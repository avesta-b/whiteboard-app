package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import cs346.whiteboard.client.components.textSelectionColors
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography

class TextBox(override var coordinate: MutableState<Offset>, override var size: MutableState<Size>) : Component {

    val text = mutableStateOf(TextFieldValue(""))

    @Composable
    override fun drawComposableComponent(modifier: Modifier, controller: WhiteboardController) {
        ComposableTextField(
            text = text,
            modifier = modifier
        )
    }

    @Composable
    private fun ComposableTextField(text: MutableState<TextFieldValue>,
                                    modifier: Modifier) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            OutlinedTextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = modifier,
                textStyle = Typography.subtitle1,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false),
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
                    disabledLabelColor = Colors.secondaryVariant),
            )
        }
    }
}