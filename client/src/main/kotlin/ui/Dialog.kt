package cs346.whiteboard.client.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.settings.UserManager

@Composable
fun Dialog(
    modifier: Modifier,
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Box(modifier = modifier.border(1.dp, WhiteboardColors.secondaryVariant, Shapes.small)) {
        Column(
            modifier = modifier.padding(32.dp).align(Alignment.Center),
            horizontalAlignment = Alignment.Start
        ) {
            SmallTitleText(title)
            Spacer(Modifier.height(8.dp))
            SecondaryBodyText(description)
            Spacer(Modifier.height(16.dp))
            Row {
                Spacer(modifier.weight(1.0f))
                PrimaryButton(
                    modifier = Modifier.size(100.dp, 40.dp),
                    text = buttonText,
                    enabled = true,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun TextInputDialogWithAcceptAndCancel(
    modifier: Modifier,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    text: MutableState<TextFieldValue>,
    placeholder: String,
    smallTitle: String,
    acceptText: String,
    showError: Boolean = false
) {
    Box(
        modifier = modifier.border(1.dp, WhiteboardColors.secondaryVariant, Shapes.small)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp).align(Alignment.Center)
        ) {
            SmallTitleText(smallTitle)
            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                OutlinedTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    modifier = Modifier.padding(bottom = 20.dp, top = 10.dp).height(56.dp).fillMaxWidth(),
                    textStyle = Typography.body2,
                    label = { TextFieldPlaceholderText(placeholder) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false
                    ),
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
                        disabledLabelColor = WhiteboardColors.secondaryVariant
                    ),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showError) {
                    UserManager.error?.let {
                        ErrorText(it)
                    }
                }
                Spacer(Modifier.weight(1.0f))
                OutlinedButton(
                    modifier = Modifier.height(40.dp),
                    text = "Cancel",
                    enabled = true,
                    onClick = onCancel
                )
                Spacer(Modifier.width(8.dp))
                PrimaryButton(
                    modifier = Modifier.height(40.dp),
                    text = acceptText,
                    enabled = true,
                    onClick = if (text.value.text.isNotEmpty()) onAccept else ({})
                )
            }
        }
    }
}
