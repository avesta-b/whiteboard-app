package cs346.whiteboard.client.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cs346.whiteboard.client.constants.*
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.helpers.getUserColor

@Composable
fun ChatToolHeader(modifier: Modifier, expandedState: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        PrimarySubtitleText(
            "Chat",
            modifier = Modifier
                .padding(20.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f)
        )
        if (expandedState) {
            CustomIcon(icon = CustomIcon.COLLAPSE)
        } else {
            CustomIcon(icon = CustomIcon.EXPAND)
        }
    }
}

@Composable
fun ChatUserName(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = Typography.body1,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun ChatTime(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.secondary,
        style = Typography.bodySmall,
        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
    )
}

@Composable
fun ChatText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = Typography.body1,
        fontWeight = FontWeight.Light
    )
}

@Composable
fun ChatMessage(userName: String, time: String, text: String, modifier: Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        UserIconText(
            userName.first().uppercaseChar().toString(),
            getUserColor(userName)
        )

        Column(modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp)) {
            Row(
                modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                ChatUserName(userName)
                ChatTime(time)
            }
            Row(modifier = Modifier.padding(0.dp, 7.dp, 0.dp, 0.dp)) {
                ChatText(text)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatTextFieldWithButton(
    text: MutableState<TextFieldValue>,
    placeholder: String,
    icon: CustomIcon = CustomIcon.SEND,
    modifier: Modifier,
    textFieldModifier: Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = WhiteboardColors.primary
                ),
                modifier = textFieldModifier
                    .height(40.dp)
                    .weight(1f)
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .onKeyEvent { event ->
                        when (event.key) {
                            Key.Enter -> {
                                onClick()
                                true
                            }
                            else -> false
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                ),
                cursorBrush = SolidColor(WhiteboardColors.primary)
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = WhiteboardColors.secondaryVariant,
                            shape = Shapes.small
                        )
                        .padding(10.dp, 12.dp, 10.dp, 10.dp)
                ) {
                    if (text.value.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.body1,
                            color = WhiteboardColors.secondary
                        )
                    }
                    SelectionContainer {
                        innerTextField()
                    }
                }
            }
            CustomIconButton(
                icon = icon,
                onClick = onClick,
                modifier = buttonModifier.size(40.dp),
                shape = Shapes.small,
                iconPadding = 12.dp,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = WhiteboardColors.primary,
                    contentColor = WhiteboardColors.background,
                )
            )
        }
    }
}