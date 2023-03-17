package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.helpers.Quadruple
import cs346.whiteboard.client.helpers.overlap
import cs346.whiteboard.client.helpers.toList
import cs346.whiteboard.client.whiteboard.overlay.CursorType
import cs346.whiteboard.client.whiteboard.components.Component
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

data class SelectionBoxData(
    val selectedComponents: List<Component>,
    var coordinate: Offset,
    val size: Size,
    val resizeNodeAnchor: ResizeNode?,
    val isResizable: Boolean,
    val resizeNodeSize: Size = Size(20f, 20f)
)
class SelectionBoxController {
    var selectionBoxData by mutableStateOf<SelectionBoxData?>(null)
        private set

    fun getSelectionBoxResizeNodeCoordinates(data: SelectionBoxData):
            Quadruple<Offset, Offset, Offset, Offset> {
        val offset = Offset(
            data.resizeNodeSize.width.div(2f).times(-1f),
            data.resizeNodeSize.height.div(2f).times(-1f)
        )
        val topLeft = data.coordinate.plus(offset)
        val topRight = Offset(data.coordinate.x + data.size.width, data.coordinate.y).plus(offset)
        val bottomLeft = Offset(data.coordinate.x, data.coordinate.y + data.size.height).plus(offset)
        val bottomRight = Offset(
            data.coordinate.x + data.size.width,
            data.coordinate.y + data.size.height).plus(offset)
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
            val resizeNodeAnchor = data.resizeNodeAnchor?.let { it } ?: return
            val anchorPoint =
                when (resizeNodeAnchor) {
                    ResizeNode.TOP_LEFT -> data.coordinate
                    ResizeNode.TOP_RIGHT -> Offset(data.coordinate.x + data.size.width, data.coordinate.y)
                    ResizeNode.BOTTOM_LEFT -> Offset(data.coordinate.x, data.coordinate.y + data.size.height)
                    ResizeNode.BOTTOM_RIGHT -> Offset(data.coordinate.x + data.size.width, data.coordinate.y + data.size.height)
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
                        (anchorPoint.x - position.x).absoluteValue / data.size.width +
                        (anchorPoint.y - position.y).absoluteValue / data.size.height
                    ) / 2
            for (component in data.selectedComponents) {
                // Prevent shrinking components beyond min size
                if (component.size.value.height * resizeMultiplier * scale <= component.smallestPossibleSize().height * scale
                    && component.size.value.width * resizeMultiplier * scale <= component.smallestPossibleSize().width * scale
                    && component.isResizeable()
                    && resizeMultiplier <= 1f) {
                    return
                }
            }
            val newSize = data.size.times(resizeMultiplier)
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
            val newCoordinate =
                when (resizeNodeAnchor) {
                    ResizeNode.TOP_LEFT -> anchorPoint
                    ResizeNode.TOP_RIGHT -> Offset(
                        anchorPoint.x - newSize.width,
                        anchorPoint.y
                    )
                    ResizeNode.BOTTOM_LEFT -> Offset(
                        anchorPoint.x,
                        anchorPoint.y - newSize.height
                    )
                    ResizeNode.BOTTOM_RIGHT -> Offset(
                        anchorPoint.x - newSize.width,
                        anchorPoint.y - newSize.height
                    )
                }
            selectionBoxData = data.copy(coordinate = newCoordinate, size = newSize)
            //
        }
    }

    fun moveSelectedComponents(dragAmount: Offset) {
        selectionBoxData?.let {
            for (component in it.selectedComponents) {
                component.move(dragAmount)
            }
            selectionBoxData = it.copy(coordinate = it.coordinate.plus(dragAmount))
        }
    }

    fun isPointInSelectionBox(point: Offset): Boolean {
        selectionBoxData?.let {
            return overlap(
                point.minus(Offset(5f, 5f)),
                Size(10f, 10f),
                it.coordinate,
                it.size)
        }
        return false
    }

    fun selectedSingleComponent(component: Component) {
        selectionBoxData = SelectionBoxData(
            mutableListOf(component),
            component.coordinate.value,
            component.size.value,
            null,
            component.isResizeable()
        )
        component.isFocused.value = true
    }

    fun selectedComponents(components: List<Component>, minCoordinate: Offset, maxCoordinate: Offset) {
        val size = Size(maxCoordinate.x - minCoordinate.x, maxCoordinate.y - minCoordinate.y)
        val isResizable: Boolean = !(components.size == 1 && !components.first().isResizeable())
        selectionBoxData = SelectionBoxData(
            components,
            minCoordinate,
            size,
            null,
            isResizable
        )
        if (components.size == 1) {
            components.first().isFocused.value = true
        }
    }

    fun selectedComponents(components: List<Component>) {
        var minCoordinate = Offset(
            components.minOf { it.coordinate.value.x },
            components.minOf { it.coordinate.value.y }
        )

        var maxCoordinate = Offset(
            components.maxOf { it.coordinate.value.x + it.size.value.width },
            components.maxOf { it.coordinate.value.y + it.size.value.height }
        )

        selectedComponents(components, minCoordinate, maxCoordinate)
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