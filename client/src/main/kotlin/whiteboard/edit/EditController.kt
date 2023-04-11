/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.client.network.WhiteboardService
import cs346.whiteboard.client.helpers.Quadruple
import cs346.whiteboard.client.helpers.overlap
import cs346.whiteboard.client.helpers.toList
import cs346.whiteboard.client.whiteboard.components.*
import cs346.whiteboard.client.whiteboard.overlay.CursorType
import cs346.whiteboard.shared.jsonmodels.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue

enum class ResizeNode {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;
    fun getOppositeNode(): ResizeNode {
        return when(this) {
            TOP_LEFT -> BOTTOM_RIGHT
            TOP_RIGHT -> BOTTOM_LEFT
            BOTTOM_LEFT -> TOP_RIGHT
            BOTTOM_RIGHT -> TOP_LEFT
        }
    }

    fun getResizeCursorType(): CursorType {
        return when(this) {
            TOP_LEFT -> CursorType.RESIZE_LEFT
            TOP_RIGHT -> CursorType.RESIZE_RIGHT
            BOTTOM_LEFT -> CursorType.RESIZE_RIGHT
            BOTTOM_RIGHT -> CursorType.RESIZE_LEFT
        }
    }
}

enum class EditPaneAttribute {
    COLOR,
    PATH_TYPE,
    PATH_THICKNESS,
    SHAPE_FILL,
    TEXT_FONT,
    TEXT_SIZE,
    IMAGE_PROMPT,
    ACCESS_LEVEL
}

data class SelectionBoxData(
    val selectedComponents: SnapshotStateList<Component>,
    val resizeNodeAnchor: ResizeNode?,
    val isResizable: Boolean,
    val resizeNodeSize: Size = Size(30f, 30f)
)
class EditController {
    var selectionBoxData by mutableStateOf<SelectionBoxData?>(null)
        private set

    private val loadingImages = mutableStateListOf<String>()
    private val failedPromptImages = mutableStateListOf<String>()

    private fun getMinMaxCoordinates(data: SelectionBoxData): Pair<Offset, Offset> {
        return data.selectedComponents.fold(Pair(
            Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            Offset(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)
        )) { (min, max), component ->
            Pair(
                Offset(
                    minOf(min.x, component.coordinate.getValue().x),
                    minOf(min.y, component.coordinate.getValue().y)
                ),
                Offset(
                    maxOf(max.x, component.coordinate.getValue().x + component.size.getValue().width),
                    maxOf(max.y, component.coordinate.getValue().y + component.size.getValue().height)
                )
            )
        }
    }

    fun getCoordinate(data: SelectionBoxData): Offset {
        return getMinMaxCoordinates(data).first
    }

    fun getSize(data: SelectionBoxData): Size {
        val (minCoordinate, maxCoordinate) = getMinMaxCoordinates(data)
        return Size(
            maxCoordinate.x - minCoordinate.x,
            maxCoordinate.y - minCoordinate.y
        )
    }

    fun getSelectionBoxResizeNodeCoordinates(data: SelectionBoxData):
            Quadruple<Offset, Offset, Offset, Offset> {
        val offset = Offset(
            data.resizeNodeSize.width.div(2f).times(-1f),
            data.resizeNodeSize.height.div(2f).times(-1f)
        )
        val coordinate = getCoordinate(data)
        val size = getSize(data)
        val topLeft = coordinate.plus(offset)
        val topRight = Offset(coordinate.x + size.width, coordinate.y).plus(offset)
        val bottomLeft = Offset(coordinate.x, coordinate.y + size.height).plus(offset)
        val bottomRight = Offset(
            coordinate.x + size.width,
            coordinate.y + size.height).plus(offset)
        return Quadruple(topLeft, topRight, bottomLeft, bottomRight)
    }

    // Side effect: sets the anchor resize node if a resize node was selected
    fun pointInResizeNode(point: Offset, shouldSetAnchorNode: Boolean): ResizeNode? {
        selectionBoxData?.let {
            if (!it.isResizable) return null
            getSelectionBoxResizeNodeCoordinates(it).toList().forEachIndexed { i, nodeCoordinate ->
                // Add hit padding to make it easier to select resize node
                if (overlap(
                        point.minus(Offset(5f, 5f)),
                        Size(10f, 10f),
                        nodeCoordinate,
                        it.resizeNodeSize)
                ) {
                    if (shouldSetAnchorNode) {
                        selectionBoxData = it.copy(resizeNodeAnchor = ResizeNode.values()[i].getOppositeNode())
                    }
                    return ResizeNode.values()[i]
                }
            }
        }
        return null
    }

    fun resizeSelectedComponents(newPosition: Offset, scale: Float) {
        selectionBoxData?.let { data ->
            data.selectedComponents.removeIf { !it.isEditable() }
            if (data.selectedComponents.isEmpty()) {
                clearSelectionBox()
                return
            }
            val coordinate = getCoordinate(data)
            val size = getSize(data)
            val resizeNodeAnchor = data.resizeNodeAnchor?.let { it } ?: return
            val anchorPoint =
                when (resizeNodeAnchor) {
                    ResizeNode.TOP_LEFT -> coordinate
                    ResizeNode.TOP_RIGHT -> Offset(coordinate.x + size.width, coordinate.y)
                    ResizeNode.BOTTOM_LEFT -> Offset(coordinate.x, coordinate.y + size.height)
                    ResizeNode.BOTTOM_RIGHT -> Offset(coordinate.x + size.width, coordinate.y + size.height)
                }
            val position =
                when (resizeNodeAnchor) {
                    ResizeNode.TOP_LEFT -> Offset(
                        maxOf(newPosition.x, anchorPoint.x),
                        maxOf(newPosition.y, anchorPoint.y)
                    )
                    ResizeNode.TOP_RIGHT -> Offset(
                        minOf(newPosition.x, anchorPoint.x),
                        maxOf(newPosition.y, anchorPoint.y)
                    )
                    ResizeNode.BOTTOM_LEFT -> Offset(
                        maxOf(newPosition.x, anchorPoint.x),
                        minOf(newPosition.y, anchorPoint.y)
                    )
                    ResizeNode.BOTTOM_RIGHT -> Offset(
                        minOf(newPosition.x, anchorPoint.x),
                        minOf(newPosition.y, anchorPoint.y)
                    )
                }
            // Take the resize multiplier as the average of delta x and delta y
            val resizeMultiplier = (
                        (anchorPoint.x - position.x).absoluteValue / size.width +
                        (anchorPoint.y - position.y).absoluteValue / size.height
                    ) / 2
            for (component in data.selectedComponents) {
                // Prevent shrinking components beyond min size
                if (component.size.getValue().height * resizeMultiplier * scale <= component.smallestPossibleSize().height * scale
                    && component.size.getValue().width * resizeMultiplier * scale <= component.smallestPossibleSize().width * scale
                    && component.isResizeable()
                    && resizeMultiplier <= 1f) {
                    return
                }
            }
            val newSize = size.times(resizeMultiplier)
            if (newSize.width <= 1f || newSize.height <= 1f) return
            // Don't resize on certain gestures
            if ((resizeNodeAnchor == ResizeNode.TOP_LEFT && position.x < anchorPoint.x && position.y < anchorPoint.y) ||
                (resizeNodeAnchor == ResizeNode.TOP_RIGHT && position.x > anchorPoint.x && position.y < anchorPoint.y) ||
                (resizeNodeAnchor == ResizeNode.BOTTOM_LEFT && position.x < anchorPoint.x && position.y > anchorPoint.y) ||
                (resizeNodeAnchor == ResizeNode.BOTTOM_RIGHT && position.x > anchorPoint.x && position.y > anchorPoint.y)) {
                return
            }
            for (component in data.selectedComponents) {
                component.resize(resizeMultiplier, resizeNodeAnchor, anchorPoint)
            }
        }
    }

    fun moveSelectedComponents(dragAmount: Offset) {
        selectionBoxData?.let { data ->
            data.selectedComponents.removeIf { !it.isEditable() }
            if (data.selectedComponents.isEmpty()) {
                clearSelectionBox()
                return
            }
            for (component in data.selectedComponents) {
                component.move(amount = dragAmount)
            }
        }
    }

    fun forceSelectedComponentsSizeUpdate() {
        selectionBoxData?.let {
            it.selectedComponents.forEach { component ->
                component.size.setLocally(component.size.getValue())
            }
        }
    }

    fun forceSelectedComponentsPositionUpdate() {
        selectionBoxData?.let {
            it.selectedComponents.forEach { component ->
                component.coordinate.setLocally(component.coordinate.getValue())
            }
        }
    }

    fun selectedComponentsSharedColor(): ComponentColor? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            val color = it.selectedComponents.first().color.getValue()
            it.selectedComponents.forEach { component -> if (component.color.getValue() != color) return null }
            return color
        }
        return null
    }

    fun selectedComponentsSharedPathType(): PathType? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            if (it.selectedComponents.first() !is Path) return null
            val type = (it.selectedComponents.first() as Path).type.getValue()
            it.selectedComponents.forEach { component -> if (component !is Path || component.type.getValue() != type) return null }
            return type
        }
        return null
    }

    fun selectedComponentsSharedThickness(): PathThickness? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            if (it.selectedComponents.first() !is Path) return null
            val thickness = (it.selectedComponents.first() as Path).thickness.getValue()
            it.selectedComponents.forEach { component -> if (component !is Path || component.thickness.getValue() != thickness) return null }
            return thickness
        }
        return null
    }

    fun selectedComponentsSharedFill(): ShapeFill? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            if (it.selectedComponents.first() !is Shape) return null
            val fill = (it.selectedComponents.first() as Shape).fill.getValue()
            it.selectedComponents.forEach { component -> if (component !is Shape || component.fill.getValue() != fill) return null }
            return fill
        }
        return null
    }

    fun selectedComponentsSharedFont(): TextFont? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            if (it.selectedComponents.first() !is TextBox) return null
            val font = (it.selectedComponents.first() as TextBox).font.getValue()
            it.selectedComponents.forEach { component -> if (component !is TextBox || component.font.getValue() != font) return null }
            return font
        }
        return null
    }

    fun selectedComponentsSharedFontSize(): TextSize? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            if (it.selectedComponents.first() !is TextBox) return null
            val fontSize = (it.selectedComponents.first() as TextBox).fontSize.getValue()
            it.selectedComponents.forEach { component -> if (component !is TextBox || component.fontSize.getValue() != fontSize) return null }
            return fontSize
        }
        return null
    }

    fun selectedComponentsSharedAccessLevel(): AccessLevel? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            val accessLevel = it.selectedComponents.first().accessLevel.getValue()
            it.selectedComponents.forEach { component -> if (component.accessLevel.getValue() != accessLevel) return null }
            return accessLevel
        }
        return null
    }

    fun selectedComponentImageData(): AIImageData? {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return null
            if (it.selectedComponents.first() !is AIGeneratedImage) return null
            if (it.selectedComponents.size > 1) return null
            return (it.selectedComponents.first() as AIGeneratedImage).imageData.getValue()
        }
        return null
    }

    fun shouldShowImageLoading(): Boolean {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return false
            if (it.selectedComponents.first() !is AIGeneratedImage) return false
            if (it.selectedComponents.size > 1) return false
            return loadingImages.contains(it.selectedComponents.first().uuid)
        }
        return false
    }

    fun shouldShowPromptFail(): Boolean {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return false
            if (it.selectedComponents.first() !is AIGeneratedImage) return false
            if (it.selectedComponents.size > 1) return false
            return failedPromptImages.contains(it.selectedComponents.first().uuid)
        }
        return false
    }

    fun setColorSelectedComponents(color: ComponentColor) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                component.color.setLocally(color)
            }
        }
    }

    fun setPathTypeSelectedComponents(type: PathType) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                if (component !is Path) return
                component.type.setLocally(type)
            }
        }
    }

    fun setThicknessSelectedComponents(thickness: PathThickness) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                if (component !is Path) return
                component.thickness.setLocally(thickness)
            }
        }
    }

    fun setFillSelectedComponents(fill: ShapeFill) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                if (component !is Shape) return
                component.fill.setLocally(fill)
            }
        }
    }

    fun setFontSelectedComponents(font: TextFont) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                if (component !is TextBox) return
                component.font.setLocally(font)
            }
        }
    }

    fun setFontSizeSelectedComponents(fontSize: TextSize) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                if (component !is TextBox) return
                component.fontSize.setLocally(fontSize)
            }
        }
    }

    fun setAccessLevelSelectedComponents(accessLevel: AccessLevel) {
        selectionBoxData?.let {
            it.selectedComponents.forEach skip@ { component ->
                if (!component.isEditable()) return@skip
                component.accessLevel.setLocally(accessLevel)
            }
        }
    }

    suspend fun fetchAIImage(prompt: String) {
        selectionBoxData?.let {
            if (it.selectedComponents.isEmpty()) return
            if (it.selectedComponents.first() !is AIGeneratedImage) return
            if (it.selectedComponents.size > 1) return
            val imageComponent = it.selectedComponents.first() as AIGeneratedImage
            failedPromptImages.remove(imageComponent.uuid)
            loadingImages.add(imageComponent.uuid)
            try {
                val requestBody = Json.encodeToString(ImageGenerationRequest(prompt))
                val responseBody = WhiteboardService.postRequest(
                    path = "api/image/generate",
                    body = requestBody,
                    token = UserManager.jwt
                )
                val imageGenerationResponse = Json.decodeFromString(ImageGenerationResponse.serializer(), responseBody)
                imageComponent.imageData.setLocally(AIImageData(imageGenerationResponse.prompt, imageGenerationResponse.imageUrl))
            } catch (_: Exception) {
                failedPromptImages.add(imageComponent.uuid)
            }
            loadingImages.remove(imageComponent.uuid)
        }
    }

    fun isPointInSelectionBox(point: Offset): Boolean {
        selectionBoxData?.let {
            val coordinate = getCoordinate(it)
            val size = getSize(it)
            return overlap(
                point.minus(Offset(5f, 5f)),
                Size(10f, 10f),
                coordinate,
                size)
        }
        return false
    }

    fun selectedSingleComponent(component: Component) {
        selectionBoxData = SelectionBoxData(
            mutableStateListOf(component),
            null,
            component.isResizeable()
        )
        component.isFocused.value = true
    }

    fun selectedComponents(components: List<Component>) {
        val sortedComponents = components.sortedBy { it.depth }
        val allNotResizable = !sortedComponents.fold(false) { acc, component ->
            acc || component.isResizeable()
        }
        selectionBoxData = SelectionBoxData(
            sortedComponents.toMutableStateList(),
            null,
            !allNotResizable
        )
        if (sortedComponents.size == 1) {
            sortedComponents.first().isFocused.value = true
        }
    }

    fun clearSelectionBox() {
        selectionBoxData?.let {
            if (it.selectedComponents.size == 1) {
                it.selectedComponents.first().isFocused.value = false
            }
        }
        selectionBoxData = null
    }
}