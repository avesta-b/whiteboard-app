package cs346.whiteboard.client.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography

val textSelectionColors = TextSelectionColors(
    handleColor = WhiteboardColors.primary,
    backgroundColor = WhiteboardColors.secondaryVariant
)
@Composable
fun AuthenticationTextField(text: MutableState<TextFieldValue>,
                            modifier: Modifier,
                            enabled: Boolean,
                            placeholder: String,
                            isSecure: Boolean) {
    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        OutlinedTextField(
            value = text.value,
            onValueChange = { text.value = it },
            modifier = modifier,
            enabled = enabled,
            textStyle = Typography.subtitle1,
            label = { TextFieldPlaceholderText(placeholder) },
            visualTransformation = if (isSecure) PasswordVisualTransformation()
            else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false),
            singleLine = true,
            shape = Shapes.small,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = WhiteboardColors.primary,
                disabledTextColor = WhiteboardColors.secondaryVariant,
                cursorColor = WhiteboardColors.primary,
                focusedBorderColor = WhiteboardColors.primary,
                unfocusedBorderColor = WhiteboardColors.secondaryVariant,
                disabledBorderColor = WhiteboardColors.secondaryVariant,
                focusedLabelColor = WhiteboardColors.primary,
                unfocusedLabelColor = WhiteboardColors.secondaryVariant,
                disabledLabelColor = WhiteboardColors.secondaryVariant),
        )
    }
}