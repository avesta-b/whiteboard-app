package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class Path(override var coordinate: Offset, override var size: Size) : Component {

    var points = mutableStateListOf<Offset>()

    override fun drawCanvasComponent(drawScope: DrawScope) {
        drawScope.drawPath(
            createPathFromPoints(points),
            color = Color.Black,
            alpha = 1f,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    @Composable
    override fun drawComposableComponent(boxScope: BoxScope) {

    }

    fun insertPoint(point: Offset) {
        points.add(point)
    }

    private fun createPathFromPoints(points: List<Offset>): Path {
        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
        }
        return path
    }

}