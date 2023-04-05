package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import cs346.whiteboard.client.MenuBarState
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.helpers.toColor
import cs346.whiteboard.client.helpers.toFloat
import cs346.whiteboard.client.helpers.toTextStyle
import cs346.whiteboard.client.ui.textSelectionColors
import cs346.whiteboard.client.websocket.ComponentEventController
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.shared.jsonmodels.*
import java.lang.ref.WeakReference
import java.util.*

val defaultTextBoxSize = Size(500f, 250f)
val defaultFont = TextFont.DEFAULT
val defaultFontSize = TextSize.SMALL

class TextBox(
    uuid: String = UUID.randomUUID().toString(),
    private val controller: WeakReference<ComponentEventController?>,
    override var coordinate: AttributeWrapper<Offset>,
    override var size: AttributeWrapper<Size> = attributeWrapper(defaultTextBoxSize, controller, uuid),
    override var color: AttributeWrapper<ComponentColor> = attributeWrapper(defaultComponentColor, controller, uuid),
    override var depth: Float,
    override var owner: String,
    override var accessLevel: AttributeWrapper<AccessLevel> = attributeWrapper(defaultAccessLevel, controller, uuid),
    var font: AttributeWrapper<TextFont> = attributeWrapper(defaultFont, controller, uuid),
    var fontSize: AttributeWrapper<TextSize> = attributeWrapper(defaultFontSize, controller, uuid),
    initialWord: String = "",
) : Component(uuid) {

    val text = attributeWrapper(TextFieldValue(initialWord), controller, uuid)

    override val editPaneAttributes = listOf(
        EditPaneAttribute.COLOR,
        EditPaneAttribute.TEXT_FONT,
        EditPaneAttribute.TEXT_SIZE,
        EditPaneAttribute.ACCESS_LEVEL
    )

    override fun getComponentType(): ComponentType = ComponentType.TEXT_BOX

    override suspend fun applyServerUpdate(update: ComponentUpdate) {
        super.applyServerUpdate(update)
        update.username?.let { user ->
            update.text?.let {
                text.setFromServer(TextFieldValue(it), update.updateUUID, user)
            }
            update.textFont?.let {
                font.setFromServer(it, update.updateUUID, user)
            }
            update.textSize?.let {
                fontSize.setFromServer(it, update.updateUUID, user)
            }
        }
    }


    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.text = text.getValue().text
        res.textFont = font.getValue()
        res.textSize = fontSize.getValue()
        return res
    }

    override fun smallestPossibleSize(): Size {
        return Size(250f, 100f)
    }

    private fun getFontSize(scale: Float): Float {
        return scale * fontSize.getValue().toFloat()
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        ComposableTextField(
            text = text.getMutableState(),
            scale = controller.whiteboardZoom,
            modifier = getModifier(controller)
                .onFocusChanged {
                    WhiteboardEventHandler.isEditingText = it.isFocused
                    MenuBarState.isToolEnabled = !it.isFocused
                }
        )
    }

    @Composable
    private fun ComposableTextField(
        text: MutableState<TextFieldValue>,
        scale: Float,
        modifier: Modifier
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            BasicTextField(
                value = text.value,
                onValueChange = {
                    this.text.setLocally(it)
                },

                enabled = isFocused.value && isEditable(),
                modifier = modifier,
                textStyle = font.getValue().toTextStyle(getFontSize(scale)).copy(color = color.getValue().toColor()),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                singleLine = false
            )
        }
    }

    override fun clone(): Component {
        val newUUID = UUID.randomUUID().toString()
        return TextBox(
            uuid = newUUID,
            controller = controller,
            coordinate = attributeWrapper(Offset(coordinate.getValue().x, coordinate.getValue().y), controller, newUUID),
            size = attributeWrapper(size.getValue(), controller, newUUID),
            color = attributeWrapper(color.getValue(), controller, newUUID),
            depth = depth,
            owner = UserManager.getUsername() ?: "default_user",
            accessLevel = attributeWrapper(AccessLevel.UNLOCKED, controller, newUUID),
            font = attributeWrapper(font.getValue(), controller, newUUID),
            fontSize = attributeWrapper(fontSize.getValue(), controller, newUUID),
            initialWord = text.getValue().text
        )
    }
}
