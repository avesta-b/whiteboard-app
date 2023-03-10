package cs346.whiteboard.client.whiteboard

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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import cs346.whiteboard.client.components.textSelectionColors
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.ComponentType
import java.lang.ref.WeakReference
import java.util.*

class TextBox(
    override var coordinate: MutableState<Offset>,
    override var size: MutableState<Size>,
    override var depth: Float,
    initialWord: String = "",
    uuid: String = UUID.randomUUID().toString(),
    private val webSocketEventHandler: WeakReference<WebSocketEventHandler>
) : Component(uuid) {

    val text = mutableStateOf(TextFieldValue(initialWord))

    override fun getComponentType(): ComponentType = ComponentType.TEXT_BOX
    override fun setState(newState: ComponentState) {
        super.setState(newState)
        val newText: String = newState.text ?: return
        text.value = TextFieldValue(newText)
    }

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.text = text.value.text
        return res
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
        ComposableTextField(
            text = text,
            modifier = getModifier(controller)
                .onFocusChanged {
                    if (!it.isFocused) {
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
        modifier: Modifier
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            OutlinedTextField(
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
                textStyle = Typography.subtitle1,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                singleLine = false,
                shape = Shapes.small,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Colors.primary,
                    disabledTextColor = Colors.secondaryVariant,
                    cursorColor = Colors.primary,
                    focusedBorderColor = Colors.primary,
                    unfocusedBorderColor = Colors.secondaryVariant,
                    disabledBorderColor = Colors.secondaryVariant,
                    focusedLabelColor = Colors.primary,
                    unfocusedLabelColor = Colors.secondaryVariant,
                    disabledLabelColor = Colors.secondaryVariant
                ),
            )
        }
    }

    override fun clone(): Component {
        return TextBox(
            mutableStateOf(Offset(coordinate.value.x, coordinate.value.y)),
            mutableStateOf(size.value),
            depth = depth,
            initialWord = text.value.text,
            webSocketEventHandler = webSocketEventHandler
        )
    }
}
