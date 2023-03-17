package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.shared.jsonmodels.ComponentType
import java.util.*

enum class ShapeTypes {
    SQUARE, CIRCLE
}
class Shape(
    override var coordinate: MutableState<Offset>,
    override var size: MutableState<Size>,
    override var depth: Float,
    val type: ShapeTypes,
    uuid: String = UUID.randomUUID().toString()
) :
    Component(uuid) {

    override fun getComponentType(): ComponentType {
        return when(type) {
            ShapeTypes.SQUARE -> ComponentType.SQUARE
            ShapeTypes.CIRCLE -> ComponentType.CIRCLE
        }
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        when(type) {
            ShapeTypes.SQUARE -> {
                Box(getModifier(controller)
                    .border(4.dp * controller.whiteboardZoom, Colors.primary, Shapes.medium)
                    )
            }
            ShapeTypes.CIRCLE -> {
                Box(getModifier(controller)
                    .border(4.dp * controller.whiteboardZoom, Colors.primary, CircleShape)
                )
            }
        }
    }

    override fun clone(): Component {
        return Shape(
            mutableStateOf(Offset(coordinate.value.x, coordinate.value.y)),
            mutableStateOf(size.value),
            depth, type
        )
    }
}
