package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.ui.textSelectionColors
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography
import cs346.whiteboard.client.helpers.toColor
import cs346.whiteboard.client.helpers.toFloat
import cs346.whiteboard.client.helpers.toTextStyle
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.shared.jsonmodels.*
import java.awt.Rectangle
import java.lang.ref.WeakReference
import java.util.*

val defaultTextBoxSize = Size(500f, 250f)
val defaultFont = TextFont.DEFAULT
val defaultFontSize = TextSize.SMALL

class TextBox(
    override var coordinate: MutableState<Offset>,
    override var size: MutableState<Size> = mutableStateOf(defaultTextBoxSize),
    override var color: MutableState<ComponentColor> = mutableStateOf(defaultComponentColor),
    override var depth: Float,
    var font: MutableState<TextFont> = mutableStateOf(defaultFont),
    var fontSize: MutableState<TextSize> = mutableStateOf(defaultFontSize),
    initialWord: String = "",
    uuid: String = UUID.randomUUID().toString(),
    private val webSocketEventHandler: WeakReference<WebSocketEventHandler>
) : Component(uuid) {

    val text = mutableStateOf(TextFieldValue(initialWord))

    override val editPaneAttributes = listOf(
        EditPaneAttribute.COLOR,
        EditPaneAttribute.TEXT_FONT,
        EditPaneAttribute.TEXT_SIZE
    )

    override fun getComponentType(): ComponentType = ComponentType.TEXT_BOX
    override fun setState(newState: ComponentState) {
        super.setState(newState)
        newState.text?.let { text.value = TextFieldValue(it) }
        newState.textFont?.let { font.value = it }
        newState.textSize?.let { fontSize.value = it }
    }

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.text = text.value.text
        res.textFont = font.value
        res.textSize = fontSize.value
        return res
    }

    override fun smallestPossibleSize(): Size {
        return Size(250f, 100f)
    }

    private fun getFontSize(scale: Float): Float {
        return scale * fontSize.value.toFloat()
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        ComposableTextField(
            text = text,
            scale = controller.whiteboardZoom,
            modifier = getModifier(controller)
                .onFocusChanged {
                    if (it.isFocused) {
                        WhiteboardEventHandler.isEditingText = true
                    } else {
                        WhiteboardEventHandler.isEditingText = false
                        webSocketEventHandler.get()?.let { ws ->
                            ws.componentEventController.add(this)
                        }
                    }
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
                    text.value = it
                    //  TODO: Figure out how to update TextBox component as the text changes
//                                webSocketEventHandler.get()?.let { ws ->
//                                    ws.componentEventController.add(this)
//                                }
                },

                enabled = isFocused.value,
                modifier = modifier,
                textStyle = font.value.toTextStyle(getFontSize(scale)).copy(color = color.value.toColor()),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                singleLine = false
            )
        }
    }

    override fun clone(): Component {
        return TextBox(
            mutableStateOf(Offset(coordinate.value.x, coordinate.value.y)),
            mutableStateOf(size.value),
            mutableStateOf(color.value),
            depth = depth,
            font = mutableStateOf(font.value),
            fontSize = mutableStateOf(fontSize.value),
            initialWord = text.value.text,
            webSocketEventHandler = webSocketEventHandler
        )
    }
}
