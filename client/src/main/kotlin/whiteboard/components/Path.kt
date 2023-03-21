package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import cs346.whiteboard.client.helpers.toColor
import cs346.whiteboard.client.helpers.toFloat
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.client.whiteboard.edit.ResizeNode
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.shared.jsonmodels.*
import java.util.*

val defaultPathThickness = PathThickness.THIN

class Path(
    override var coordinate: MutableState<Offset>,
    override var size: MutableState<Size>,
    override var color: MutableState<ComponentColor> = mutableStateOf(defaultComponentColor),
    override var depth: Float,
    var type: MutableState<PathType>,
    var thickness: MutableState<PathThickness> = mutableStateOf(defaultPathThickness),
    uuid: String = UUID.randomUUID().toString()
) : Component(uuid) {

    override val editPaneAttributes = listOf(
        EditPaneAttribute.COLOR,
        EditPaneAttribute.PATH_TYPE,
        EditPaneAttribute.PATH_THICKNESS
    )

    private var points = mutableStateListOf<Offset>()

    override fun getComponentType(): ComponentType = ComponentType.PATH

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.path = points.map { Position(it.x, it.y) }
        res.pathType = type.value
        res.pathThickness = thickness.value
        return res
    }

    override fun setState(newState: ComponentState) {
        super.setState(newState)
        points.clear()
        newState.path?.map { it.toOffset() }?.forEach { points.add(it) }
        newState.pathType?.let { type.value = it }
        newState.pathThickness?.let { thickness.value = it }
    }

    private fun drawPath(scope: DrawScope, controller: WhiteboardController) {
        when (type.value) {
            PathType.BRUSH -> {
                scope.drawPath(
                    createPathFromPoints(points, controller),
                    color = color.value.toColor(),
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
                    createPathFromPoints(points, controller),
                    color = color.value.toColor(),
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
                    createPathFromPoints(points, controller),
                    brush = Brush.horizontalGradient(
                        0.0f to color.value.toColor(),
                        0.25f to Color.Red,
                        0.5f to Color.Green,
                        0.75f to Color.Blue,
                        1.0f to color.value.toColor(),
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
        return scale * getStrokeMultiplier() * thickness.value.toFloat()
    }

    private fun getStrokeMultiplier(): Float {
        return when(type.value) {
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
        val component = Path(
            mutableStateOf(Offset(coordinate.value.x, coordinate.value.y)),
            mutableStateOf(size.value),
            mutableStateOf(color.value),
            depth,
            mutableStateOf(type.value),
            mutableStateOf(thickness.value)
        )

        points.forEach {
            component.points.add(it)
        }

        return component
    }

    override fun isResizeable(): Boolean {
        // A dot is not resizable
        return !(points.size == 1 || points.size == 2 && points[0] == points[1])
    }

    override fun move(amount: Offset) {
        super.move(amount)
        for (i in 0 until points.size) {
            points[i] = points[i].plus(amount)
        }
    }

    override fun smallestPossibleSize(): Size {
        return Size(1f, 1f)
    }

    override fun resize(resizeMultiplier: Float, resizeNodeAnchor: ResizeNode, anchorPoint: Offset) {
        super.resize(resizeMultiplier, resizeNodeAnchor, anchorPoint)
        for (i in 0 until points.size) {
            points[i] =
                when (resizeNodeAnchor) {
                    ResizeNode.TOP_LEFT -> Offset(
                        anchorPoint.x + (points[i].x - anchorPoint.x) * resizeMultiplier,
                        anchorPoint.y + (points[i].y - anchorPoint.y) * resizeMultiplier
                    )

                    ResizeNode.TOP_RIGHT -> Offset(
                        anchorPoint.x - (anchorPoint.x - points[i].x) * resizeMultiplier,
                        anchorPoint.y + (points[i].y - anchorPoint.y) * resizeMultiplier
                    )

                    ResizeNode.BOTTOM_LEFT -> Offset(
                        anchorPoint.x + (points[i].x - anchorPoint.x) * resizeMultiplier,
                        anchorPoint.y - (anchorPoint.y - points[i].y) * resizeMultiplier
                    )

                    ResizeNode.BOTTOM_RIGHT -> Offset(
                        anchorPoint.x - (anchorPoint.x - points[i].x) * resizeMultiplier,
                        anchorPoint.y - (anchorPoint.y - points[i].y) * resizeMultiplier
                    )
                }
        }
    }

    fun insertPoint(point: Offset) {
        // NOTE(avesta): Rate limiting the number of points that can be added on the theory that adding less points
        // that are close together will help us smoothen our existing points.

        // TODO: Let's ask users to rate these against each other. We will do 1 control (the old algo)
        // Another one will be a bezier curve with this rate limit, another one will be a bezier curve with no
        // rate limit. The final one will once again be this bezier curve with the rate limit.
        // TODO: revisit path drawing
        val lastPoint = points.lastOrNull()
        if (lastPoint == null) {
            points.add(point)
            return
        }
        points.add(point)

        // TODO: There's got to be a better way to do this... works in the meantime -M
        // If it ain't broke don't fix it :D -Y
        if (point.x > coordinate.value.x && point.x - coordinate.value.x > size.value.width) {
            size.value = Size(point.x - coordinate.value.x, size.value.height)
        }
        if (point.y > coordinate.value.y && point.y - coordinate.value.y > size.value.height) {
            size.value = Size(size.value.width, point.y - coordinate.value.y)
        }
        if (point.x < coordinate.value.x) {
            size.value = Size(coordinate.value.x - point.x + size.value.width, size.value.height)
            coordinate.value = Offset(point.x, coordinate.value.y)
        }
        if (point.y < coordinate.value.y) {
            size.value = Size(size.value.width, coordinate.value.y - point.y + size.value.height)
            coordinate.value = Offset(coordinate.value.x, point.y)
        }
    }

    private fun createPathFromPoints(points: List<Offset>, controller: WhiteboardController): Path {
        if (points.isEmpty()) return Path()
        // Start at the first point
        val path = Path()
        val firstPoint = controller.whiteboardToViewCoordinate(points.first())
            .minus(controller.whiteboardToViewCoordinate(coordinate.value))
        path.moveTo(firstPoint.x, firstPoint.y)

        for (i in 1 until points.size) {
            val curPoint = controller.whiteboardToViewCoordinate(points[i])
                .minus(controller.whiteboardToViewCoordinate(coordinate.value))
            path.lineTo(curPoint.x, curPoint.y)
        }
        return path
    }
}
