package cs346.whiteboard.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.*

@Composable
fun TitleText(text: String) {
    Text(text = text,
        color = Colors.primary,
        style = Typography.h1,
        textAlign = TextAlign.Center)
}

@Composable
fun TooltipText(text: String, modifier: Modifier = Modifier) {
    Box(modifier.clip(Shapes.small).background(Colors.primary)) {
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
        color = Colors.background,
        style = Typography.subtitle2
    )
}

@Composable
fun SmallTitleText(text: String) {
    Text(
        text = text,
        color = Colors.primary,
        style = Typography.h2
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
        color = Colors.background,
        style = Typography.subtitle2
    )
}

@Composable
fun SecondarySubtitleText(text: String) {
    Text(
        text = text,
        color = Colors.secondary,
        style = Typography.subtitle2
    )
}

@Composable
fun PrimarySubtitleText(text: String, style: TextStyle = Typography.subtitle2, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Colors.primary,
        style = style,
        modifier = modifier
    )
}

@Composable
fun UnderlinedText(text: String) {
    Text(
        text = text,
        color = Colors.primary,
        style = Typography.subtitle2,
        textDecoration = TextDecoration.Underline
    )
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
fun SmallBodyText(text: String) {
    Text(
        text = text,
        color = Colors.secondary,
        style = Typography.bodySmall
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