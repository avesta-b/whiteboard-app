package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.triangle
import cs346.whiteboard.client.helpers.toColor
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.shared.jsonmodels.*
import java.util.*

val defaultShapeSize = Size(250f, 250f)
val defaultShapeFill = ShapeFill.OUTLINE

class Shape(
    override var coordinate: MutableState<Offset>,
    override var size: MutableState<Size> = mutableStateOf(defaultShapeSize),
    override var color: MutableState<ComponentColor> = mutableStateOf(defaultComponentColor),
    override var depth: Float,
    var type: MutableState<ShapeType>,
    var fill: MutableState<ShapeFill> = mutableStateOf(defaultShapeFill),
    uuid: String = UUID.randomUUID().toString()
) : Component(uuid) {

    init {
        if (type.value == ShapeType.RECTANGLE && size.value == defaultShapeSize) {
            size.value = Size(defaultShapeSize.width * 2, defaultShapeSize.height)
        }
    }

    override fun getComponentType(): ComponentType {
        return ComponentType.SHAPE
    }

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.shapeType = type.value
        res.shapeFill = fill.value
        return res
    }

    @Composable
    override fun getModifier(controller: WhiteboardController): Modifier {
        var modifier = super.getModifier(controller).clip(getShape())
        modifier = when (fill.value) {
            ShapeFill.FILL -> modifier.background(color.value.toColor())
            ShapeFill.OUTLINE -> modifier.border((4 * controller.whiteboardZoom).dp, color.value.toColor(), getShape())
        }
        return modifier
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        Box(getModifier(controller))
    }

    override fun clone(): Component {
        return Shape(
            coordinate = mutableStateOf(Offset(coordinate.value.x, coordinate.value.y)),
            size = mutableStateOf(size.value),
            color = mutableStateOf(color.value),
            depth = depth,
            type = mutableStateOf(type.value),
            fill = mutableStateOf(fill.value)
        )
    }

    private fun getShape(): androidx.compose.ui.graphics.Shape {
        return when(type.value) {
            ShapeType.SQUARE, ShapeType.RECTANGLE, -> RectangleShape
            ShapeType.CIRCLE -> CircleShape
            ShapeType.TRIANGLE -> Shapes.triangle
        }
    }
}
