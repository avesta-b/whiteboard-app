package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes

enum class ShapeTypes {
    SQUARE, CIRCLE
}
class Shape(override var coordinate: MutableState<Offset>, override var size: MutableState<Size>, val type: ShapeTypes) : Component {
    @Composable
    override fun drawComposableComponent(modifier: Modifier, controller: WhiteboardController) {
        when(type) {
            ShapeTypes.SQUARE -> {
                Box(modifier
                    .border(4.dp * controller.whiteboardZoom, Colors.primary, Shapes.medium)
                    )
            }
            ShapeTypes.CIRCLE -> {
                Box(modifier
                    .border(4.dp * controller.whiteboardZoom, Colors.primary, CircleShape)
                )
            }
        }
    }
}