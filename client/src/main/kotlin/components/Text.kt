package cs346.whiteboard.client.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Typography

@Composable
fun TitleText(text: String) {
    Text(text = text,
        color = Colors.primary,
        style = Typography.h1,
        textAlign = TextAlign.Center)
}

@Composable
fun SmallTitleText(text: String) {
    Text(text = text,
        color = Colors.primary,
        style = Typography.h2)
}

@Composable
fun TextFieldPlaceholderText(text: String) {
    Text(text = text,
        style = Typography.subtitle1)
}

@Composable
fun PrimaryButtonText(text: String) {
    Text(text = text,
        color = Colors.background,
        style = Typography.subtitle2)
}

@Composable
fun SecondarySubtitleText(text: String) {
    Text(text = text,
        color = Colors.secondary,
        style = Typography.subtitle2)
}

@Composable
fun PrimarySubtitleText(text: String) {
    Text(text = text,
        color = Colors.primary,
        style = Typography.subtitle2)
}

@Composable
fun UnderlinedText(text: String) {
    Text(text = text,
        color = Colors.primary,
        style = Typography.subtitle2,
        textDecoration = TextDecoration.Underline)
}

@Composable
fun SecondaryBodyText(text: String) {
    Text(
        text = text,
        color = Colors.secondary,
        style = Typography.body1
    )
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        color = Colors.error,
        style = Typography.subtitle2
    )
}