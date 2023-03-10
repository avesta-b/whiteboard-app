package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.ComponentType
import cs346.whiteboard.shared.jsonmodels.Position
import java.util.*

class Path(
    override var coordinate: MutableState<Offset>,
    override var size: MutableState<Size>,
    override var depth: Float,
    uuid: String = UUID.randomUUID().toString()
) : Component(uuid) {

    private var points = mutableStateListOf<Offset>()

    override fun getComponentType(): ComponentType = ComponentType.PATH

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.path = points.map { Position(it.x, it.y) }
        return res
    }

    override fun setState(newState: ComponentState) {
        super.setState(newState)
        points.clear()
        newState.path?.map { it.toOffset() }?.forEach { points.add(it) }
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        Canvas(getModifier(controller)) {
            drawPath(
                createPathFromPoints(points, controller),
                color = Color.Black,
                alpha = 1f,
                style = Stroke(
                    width = 10f * controller.whiteboardZoom,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.cornerPathEffect(10f * controller.whiteboardZoom)
                )
            )
        }
    }

    override fun clone(): Component {
        val component = Path(
            mutableStateOf(Offset(coordinate.value.x, coordinate.value.y)),
            mutableStateOf(size.value),
            depth
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
