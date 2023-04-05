package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.triangle
import cs346.whiteboard.client.helpers.toColor
import cs346.whiteboard.client.websocket.ComponentEventController
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.shared.jsonmodels.*
import java.lang.ref.WeakReference
import java.util.*

val defaultShapeSize = Size(250f, 250f)
val defaultShapeFill = ShapeFill.OUTLINE

class Shape(
    uuid: String = UUID.randomUUID().toString(),
    private val controller: WeakReference<ComponentEventController?>,
    override var coordinate: AttributeWrapper<Offset>,
    override var size: AttributeWrapper<Size> = attributeWrapper(defaultShapeSize, controller, uuid),
    override var color: AttributeWrapper<ComponentColor> = attributeWrapper(defaultComponentColor, controller, uuid),
    override var depth: Float,
    override var owner: String,
    override var accessLevel: AttributeWrapper<AccessLevel> = attributeWrapper(defaultAccessLevel, controller, uuid),
    var type: AttributeWrapper<ShapeType>,
    var fill: AttributeWrapper<ShapeFill> = attributeWrapper(defaultShapeFill, controller, uuid),
) : Component(uuid) {

    override val editPaneAttributes = listOf(
        EditPaneAttribute.COLOR,
        EditPaneAttribute.SHAPE_FILL,
        EditPaneAttribute.ACCESS_LEVEL
    )

    init {
        if (type.getValue() == ShapeType.RECTANGLE && size.getValue() == defaultShapeSize) {
            size.setLocally(Size(defaultShapeSize.width * 2, defaultShapeSize.height))
        }
    }

    override suspend fun applyServerUpdate(update: ComponentUpdate) {
        super.applyServerUpdate(update)
        update.username?.let {user ->
            update.shapeType?.let {
                type.setFromServer(it, update.updateUUID, user)
            }
            update.shapeFill?.let {
                fill.setFromServer(it, update.updateUUID, user)
            }
        }
    }

    override fun getComponentType(): ComponentType {
        return ComponentType.SHAPE
    }

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.shapeType = type.getValue()
        res.shapeFill = fill.getValue()
        return res
    }

    @Composable
    override fun getModifier(controller: WhiteboardController): Modifier {
        var modifier = super.getModifier(controller).clip(getShape())
        modifier = when (fill.getValue()) {
            ShapeFill.FILL -> modifier.background(color.getValue().toColor())
            ShapeFill.OUTLINE -> modifier.border((4 * controller.whiteboardZoom).dp, color.getValue().toColor(), getShape())
        }
        return modifier
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        Box(getModifier(controller))
    }

    override fun clone(): Component {
        val newUUID = UUID.randomUUID().toString()
        return Shape(
            uuid=newUUID,
            controller=controller,
            coordinate = attributeWrapper(Offset(coordinate.getValue().x, coordinate.getValue().y), controller, newUUID),
            size = attributeWrapper(size.getValue(), controller, newUUID),
            color = attributeWrapper(color.getValue(), controller, newUUID),
            depth = depth,
            owner = UserManager.getUsername() ?: "default_user",
            accessLevel = attributeWrapper(AccessLevel.UNLOCKED, controller, newUUID),
            type = attributeWrapper(type.getValue(), controller, newUUID),
            fill = attributeWrapper(fill.getValue(), controller, newUUID)
        )
    }

    private fun getShape(): androidx.compose.ui.graphics.Shape {
        return when(type.getValue()) {
            ShapeType.SQUARE, ShapeType.RECTANGLE, -> RectangleShape
            ShapeType.CIRCLE -> CircleShape
            ShapeType.TRIANGLE -> Shapes.triangle
        }
    }
}
