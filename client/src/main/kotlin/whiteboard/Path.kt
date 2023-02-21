package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import cs346.whiteboard.client.helpers.distanceBetween

class Path(override var coordinate: Offset, override var size: Size) : Component {

    private var points = mutableStateListOf<Offset>()

    override fun drawCanvasComponent(drawScope: DrawScope) {
        drawScope.drawPath(
            createPathFromPoints(points),
            color = Color.Black,
            alpha = 1f,
            style = Stroke(
                width = 5f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.cornerPathEffect(10f)
            )
        )
    }

    @Composable
    override fun drawComposableComponent(boxScope: BoxScope) {

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
    }

    private fun createPathFromPoints(points: List<Offset>): Path {
        if (points.isEmpty()) return Path()
        // Start at the first point
        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        // Loop through each set of 3 points, creating a Bezier curve for each segment
        for (i in 1 until points.size-1 step 1) {
            if (i + 1 < points.size) {
                // Calculate the control points
                val controlPoint1X = (points[i].x + points[i-1].x) / 2
                val controlPoint1Y = (points[i].y + points[i-1].y) / 2
                val controlPoint2X = (points[i].x + points[i+1].x) / 2
                val controlPoint2Y = (points[i].y + points[i+1].y) / 2

                // Draw the curve segment
                path.cubicTo(
                    controlPoint1X, controlPoint1Y,
                    controlPoint2X, controlPoint2Y,
                    points[i].x, points[i].y
                )
            } else {
                // Draw a line segment to the final point if there are an odd number of points
                path.lineTo(points[i].x, points[i].y)
            }
        }
        return path
    }
}