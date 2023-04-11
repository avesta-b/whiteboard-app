/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.client.helpers.toColor
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.client.websocket.ComponentEventController
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.client.whiteboard.edit.ResizeNode
import cs346.whiteboard.shared.jsonmodels.*
import java.lang.ref.WeakReference
import java.util.*

val defaultPathThickness = PathThickness.THIN

class Path(
    uuid: String = UUID.randomUUID().toString(),
    private val controller: WeakReference<ComponentEventController?>,
    override var coordinate: AttributeWrapper<Offset>,
    override var size: AttributeWrapper<Size>,
    override var color: AttributeWrapper<ComponentColor> = attributeWrapper(defaultComponentColor, controller, uuid),
    override var depth: Float,
    override var owner: String,
    override var accessLevel: AttributeWrapper<AccessLevel> = attributeWrapper(defaultAccessLevel, controller, uuid),
    var type: AttributeWrapper<PathType>,
    var thickness: AttributeWrapper<PathThickness> = attributeWrapper(defaultPathThickness, controller, uuid),
    ) : Component(uuid) {

    override val editPaneAttributes = listOf(
        EditPaneAttribute.COLOR,
        EditPaneAttribute.PATH_TYPE,
        EditPaneAttribute.PATH_THICKNESS,
        EditPaneAttribute.ACCESS_LEVEL
    )

    private var points = IterableAttributeWrapper(controller, uuid)

    fun getPoints() : List<Offset> = points.getValue()

    override fun getComponentType(): ComponentType = ComponentType.PATH

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.path = getPoints().map { Position(it.x, it.y) }
        res.pathType = type.getValue()
        res.pathThickness = thickness.getValue()
        return res
    }

    override suspend fun applyServerUpdate(update: ComponentUpdate) {
        super.applyServerUpdate(update)
        update.username?.let { user ->
            update.path?.let { newPoints ->
                points.setFromServer(newPoints.map { it.toOffset() }, update.updateUUID, user)
            }
            update.pathType?.let {
                type.setFromServer(it, update.updateUUID, user)
            }
            update.pathThickness?.let {
                thickness.setFromServer(it, update.updateUUID, user)
            }
        }
    }

    fun insertLocalWithoutConfirm(point: Offset) {
        points.addWithoutConfirm(point)

        var currentSize = size.getValue()
        var currentCoordinate = coordinate.getValue()
        if (point.x > currentCoordinate.x && point.x - currentCoordinate.x > currentSize.width) {
            currentSize = Size(point.x - currentCoordinate.x, currentSize.height)
        }
        if (point.y > currentCoordinate.y && point.y - currentCoordinate.y > currentSize.height) {
            currentSize = Size(currentSize.width, point.y - currentCoordinate.y)
        }
        if (point.x < currentCoordinate.x) {
            currentSize = Size(currentCoordinate.x - point.x + currentSize.width, currentSize.height)
            currentCoordinate = Offset(point.x, currentCoordinate.y)
        }
        if (point.y < currentCoordinate.y) {
            currentSize = Size(currentSize.width, currentCoordinate.y - point.y + currentSize.height)
            currentCoordinate = Offset(currentCoordinate.x, point.y)
        }
        size.setWithoutConfirm(currentSize)
        coordinate.setWithoutConfirm(currentCoordinate)
    }


    private fun drawPath(scope: DrawScope, controller: WhiteboardController) {
        when (type.getValue()) {
            PathType.BRUSH -> {
                scope.drawPath(
                    createPathFromPoints(points.getValue(), controller),
                    color = color.getValue().toColor(),
                    alpha = 1f,
                    style = Stroke(
                        width = getStrokeWidth(controller.whiteboardZoom),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = PathEffect.cornerPathEffect(getStrokeWidth(controller.whiteboardZoom))
                    )
                )
            }
            PathType.HIGHLIGHTER -> {
                scope.drawPath(
                    createPathFromPoints(points.getValue(), controller),
                    color = color.getValue().toColor(),
                    alpha = 0.4f,
                    style = Stroke(
                        width = getStrokeWidth(controller.whiteboardZoom),
                        cap = StrokeCap.Square,
                        join = StrokeJoin.Miter,
                        pathEffect = PathEffect.cornerPathEffect(getStrokeWidth(controller.whiteboardZoom))
                    )
                )
            }
            PathType.PAINT -> {
                scope.drawPath(
                    createPathFromPoints(points.getValue(), controller),
                    brush = Brush.horizontalGradient(
                        0.0f to color.getValue().toColor(),
                        0.25f to Color.Red,
                        0.5f to Color.Green,
                        0.75f to Color.Blue,
                        1.0f to color.getValue().toColor(),
                    ),
                    alpha = 1f,
                    style = Stroke(
                        width = getStrokeWidth(controller.whiteboardZoom),
                        cap = StrokeCap.Square,
                        join = StrokeJoin.Round,
                        pathEffect = PathEffect.cornerPathEffect(getStrokeWidth(controller.whiteboardZoom))
                    )
                )
            }
        }
    }

    private fun getStrokeWidth(scale: Float): Float {
        return scale * getStrokeMultiplier() * when (thickness.getValue()) {
            PathThickness.THIN -> 10f
            PathThickness.THICK -> 20f
            PathThickness.EXTRA_THICK -> 30f
        }
    }

    private fun getStrokeMultiplier(): Float {
        return when(type.getValue()) {
            PathType.BRUSH -> 1f
            PathType.HIGHLIGHTER -> 2f
            PathType.PAINT -> 2.5f
        }
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        Canvas(getModifier(controller)) {
            drawPath(this, controller)
        }
    }

    override fun clone(): Component {
        val newUUID = UUID.randomUUID().toString()
        val component = Path(
            uuid = newUUID,
            controller = controller,
            coordinate = attributeWrapper(coordinate.getValue(), controller, newUUID),
            size = attributeWrapper(size.getValue(), controller, newUUID),
            color = attributeWrapper(color.getValue(), controller, newUUID),
            depth = depth,
            owner = UserManager.getUsername() ?: "default_user",
            accessLevel = attributeWrapper(AccessLevel.UNLOCKED, controller, newUUID),
            type = attributeWrapper(type.getValue(), controller, newUUID),
            thickness = attributeWrapper(thickness.getValue(), controller, newUUID)
        )


        points.getValue().forEach {
            component.points.addWithoutConfirm(it)
        }

        return component
    }

    override fun isResizeable(): Boolean {
        // A dot is not resizable
        return super.isResizeable() && !(points.getValue().size == 1 || points.getValue().size == 2 && points.getValue()[0] == points.getValue()[1])
    }

    override fun move(amount: Offset, force: Boolean) {
        if (!isEditable() && !force) return
        for (idx in 0 until points.getValue().size) {
            val p = points.getValue()[idx].plus(amount)
            points.setIndex(p, idx)
        }
        points.batchUpdate() // batch update all points
        coordinate.setLocally(coordinate.getValue().plus(amount))
    }

    override fun smallestPossibleSize(): Size {
        return Size(1f, 1f)
    }

    override fun resize(resizeMultiplier: Float, resizeNodeAnchor: ResizeNode, anchorPoint: Offset) {
        super.resize(resizeMultiplier, resizeNodeAnchor, anchorPoint)
        if (!isEditable()) return
        val newPoints = points.getValue().map {
            when (resizeNodeAnchor) {
                ResizeNode.TOP_LEFT -> Offset(
                    anchorPoint.x + (it.x - anchorPoint.x) * resizeMultiplier,
                    anchorPoint.y + (it.y - anchorPoint.y) * resizeMultiplier
                )

                ResizeNode.TOP_RIGHT -> Offset(
                    anchorPoint.x - (anchorPoint.x - it.x) * resizeMultiplier,
                    anchorPoint.y + (it.y - anchorPoint.y) * resizeMultiplier
                )

                ResizeNode.BOTTOM_LEFT -> Offset(
                    anchorPoint.x + (it.x - anchorPoint.x) * resizeMultiplier,
                    anchorPoint.y - (anchorPoint.y - it.y) * resizeMultiplier
                )

                ResizeNode.BOTTOM_RIGHT -> Offset(
                    anchorPoint.x - (anchorPoint.x - it.x) * resizeMultiplier,
                    anchorPoint.y - (anchorPoint.y - it.y) * resizeMultiplier
                )
            }
        }
        points.setLocally(newPoints)

    }

    fun insertPoint(point: Offset) {
        points.addLocally(point)
        var currentSize = size.getValue()
        var currentCoordinate = coordinate.getValue()
        if (point.x > currentCoordinate.x && point.x - currentCoordinate.x > currentSize.width) {
            currentSize = Size(point.x - currentCoordinate.x, currentSize.height)
        }
        if (point.y > currentCoordinate.y && point.y - currentCoordinate.y > currentSize.height) {
            currentSize = Size(currentSize.width, point.y - currentCoordinate.y)
        }
        if (point.x < currentCoordinate.x) {
            currentSize = Size(currentCoordinate.x - point.x + currentSize.width, currentSize.height)
            currentCoordinate = Offset(point.x, currentCoordinate.y)
        }
        if (point.y < currentCoordinate.y) {
            currentSize = Size(currentSize.width, currentCoordinate.y - point.y + currentSize.height)
            currentCoordinate = Offset(currentCoordinate.x, point.y)
        }
        size.setLocally(currentSize)
        coordinate.setLocally(currentCoordinate)
    }

    private fun createPathFromPoints(points: List<Offset>, controller: WhiteboardController): Path {
        if (points.isEmpty()) return Path()
        // Start at the first point
        val path = Path()
        val firstPoint = controller.whiteboardToViewCoordinate(points.first())
            .minus(controller.whiteboardToViewCoordinate(coordinate.getValue()))
        path.moveTo(firstPoint.x, firstPoint.y)

        for (i in 1 until points.size) {
            val curPoint = controller.whiteboardToViewCoordinate(points[i])
                .minus(controller.whiteboardToViewCoordinate(coordinate.getValue()))
            path.lineTo(curPoint.x, curPoint.y)
        }
        return path
    }
}
