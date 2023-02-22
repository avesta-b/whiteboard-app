package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import cs346.whiteboard.client.helpers.distanceBetween

class Path(override var coordinate: MutableState<Offset>, override var size: MutableState<Size>) : Component {

    private var points = mutableStateListOf<Offset>()

    @Composable
    override fun drawComposableComponent(modifier: Modifier, controller: WhiteboardController) {
        Canvas(modifier) {
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

    fun insertPoint(point: Offset) {
        // NOTE(avesta): Rate limiting the number of points that can be added on the theory that adding less points
        // that are close together will help us smoothen our existing points.

        // TODO: Let's ask users to rate these against each other. We will do 1 control (the old algo)
        // Another one will be a bezier curve with this rate limit, another one will be a bezier curve with no
        // rate limit. The final one will once again be this bezier curve with the rate limit.
        val lastPoint = points.lastOrNull()
        if (lastPoint == null) {
            points.add(point)
            return
        }
        if (distanceBetween(lastPoint, point) < 1.5) return;
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
        val firstPoint = controller.whiteboardToViewCoordinate(points.first()).minus(controller.whiteboardToViewCoordinate(coordinate.value))
        path.moveTo(firstPoint.x, firstPoint.y)

        // Loop through each set of 3 points, creating a Bezier curve for each segment
        for (i in 1 until points.size-1 step 1) {
            val curPoint = controller.whiteboardToViewCoordinate(points[i])
            .minus(controller.whiteboardToViewCoordinate(coordinate.value))
            if (i + 1 < points.size) {
                // Calculate the control points
                val controlPoint1 = controller.whiteboardToViewCoordinate(Offset(
                    (points[i].x + points[i-1].x) / 2,
                    (points[i].y + points[i-1].y) / 2))
                    .minus(controller.whiteboardToViewCoordinate(coordinate.value))
                val controlPoint2 = controller.whiteboardToViewCoordinate(Offset(
                    (points[i].x + points[i+1].x) / 2,
                    (points[i].y + points[i+1].y) / 2))
                    .minus(controller.whiteboardToViewCoordinate(coordinate.value))
                // Draw the curve segment
                path.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    curPoint.x, curPoint.y
                )
            } else {
                // Draw a line segment to the final point if there are an odd number of points
                path.lineTo(curPoint.x, curPoint.y)
            }
        }
        return path
    }
}