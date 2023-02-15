package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

enum class ShapeTypes {
    SQUARE, CIRCLE
}
class Shape(override var coordinate: Offset, override var size: Size, val type: ShapeTypes) : Component {
    override fun drawCanvasComponent(drawScope: DrawScope) {
        when(type) {
            ShapeTypes.SQUARE -> {
                drawScope.drawRect(
                    color = Color.Black,
                    topLeft = coordinate,
                    size = size,
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            ShapeTypes.CIRCLE -> {
                drawScope.drawCircle(
                    color = Color.Black,
                    radius = size.height / 2,
                    center = Offset(coordinate.x + size.height / 2, coordinate.y + size.height / 2),
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }

    @Composable
    override fun drawComposableComponent(boxScope: BoxScope) {

    }
}