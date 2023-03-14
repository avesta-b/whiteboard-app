package cs346.whiteboard.client.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography

val textSelectionColors = TextSelectionColors(
    handleColor = Colors.primary,
    backgroundColor = Colors.secondaryVariant
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

@Composable
fun TextFieldWithButton(
    text: MutableState<TextFieldValue>,
    buttonText: String,
    modifier: Modifier,
    placeholder: String,
    enabled: Boolean,
    onClick: () -> Unit) {
    // TODO: separate this
    Column {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = Shapes.small,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Colors.primary,
                contentColor = Colors.background,
                disabledBackgroundColor = Colors.secondary,
                disabledContentColor = Colors.background
            )) {
            PrimaryButtonText(buttonText)
        }
    }
    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
        OutlinedTextField(
            value = text.value,
            onValueChange = { text.value = it },
            modifier = Modifier.size(280.dp, 60.dp),
            textStyle = Typography.subtitle1,
            label = { TextFieldPlaceholderText(placeholder) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false),
            singleLine = true,
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