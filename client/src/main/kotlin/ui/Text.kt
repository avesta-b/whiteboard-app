package cs346.whiteboard.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.*
import cs346.whiteboard.client.helpers.getUserColor

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = Typography.h1,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TooltipText(text: String, modifier: Modifier = Modifier) {
    Box(modifier.clip(Shapes.small).background(WhiteboardColors.primary)) {
        Text(
            text = text,
            modifier = Modifier.padding(5.dp),
            color = Color.White,
            style = Typography.subtitle2
        )
    }
}

@Composable
fun CursorUserNameText(username: String, color: Color, modifier: Modifier, scale: Float) {
    Box(modifier.clip(Shapes.small(scale)).background(color)) {
        Text(
            text = username,
            modifier = Modifier.padding((5 * scale).dp),
            color = Color.White,
            style = Typography.subtitle2(scale)
        )
    }
}

@Composable
fun UserIconText(character: String, color: Color) {
    Text(
        modifier = Modifier
            .padding(16.dp)
            .drawBehind {
                drawCircle(
                    color = color,
                    radius = this.size.maxDimension
                )
            },
        text = character,
        color = WhiteboardColors.background,
        style = Typography.subtitle2
    )
}

@Composable
fun SmallTitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = Typography.h2,
        modifier = modifier
    )
}

@Composable
fun TextFieldPlaceholderText(text: String) {
    Text(
        text = text,
        style = Typography.subtitle1
    )
}

@Composable
fun PrimaryButtonText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.background,
        style = Typography.subtitle2
    )
}

@Composable
fun SecondarySubtitleText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.secondary,
        style = Typography.subtitle2
    )
}

@Composable
fun PrimarySubtitleText(text: String, style: TextStyle = Typography.subtitle2, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = style,
        modifier = modifier
    )
}

@Composable
fun UnderlinedText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = Typography.subtitle2,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun SecondaryBodyText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.secondary,
        style = Typography.body1
    )
}

@Composable
fun SmallBodyText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.secondary,
        style = Typography.bodySmall
    )
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.error,
        style = Typography.subtitle2
    )
}

@Composable
fun ChatToolHeader(titleModifier: Modifier, arrowModifier: Modifier, onClick: () -> Unit, ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = titleModifier.fillMaxWidth()
    ) {
        PrimarySubtitleText(
            "Chat",
            modifier = Modifier
                .padding(20.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f)
        )
        IconButton(
            modifier = arrowModifier,
            onClick = { onClick() }
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = "Drop-Down Arrow",
            )
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatTextFieldWithButton(
    text: MutableState<TextFieldValue>,
    modifier: Modifier,
    textFieldModifier: Modifier,
    placeholder: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            OutlinedTextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = textFieldModifier
                    .height(60.dp)
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
                textStyle = Typography.subtitle1,
                label = { TextFieldPlaceholderText(placeholder) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
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
            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(53.dp)
                    .width(53.dp),
                enabled = enabled,
                shape = Shapes.small,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = WhiteboardColors.primary,
                    contentColor = WhiteboardColors.background,
                    disabledBackgroundColor = WhiteboardColors.secondary,
                    disabledContentColor = WhiteboardColors.background
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}