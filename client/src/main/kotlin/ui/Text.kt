/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

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
import cs346.whiteboard.client.helpers.toText
import cs346.whiteboard.shared.jsonmodels.EmojiPing

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
    Box(modifier.clip(Shapes.small).background(WhiteboardColors.tooltipBackgroundColor)) {
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
fun EmojiPingText(ping: EmojiPing, scale: Float) {
    Text(
        text = ping.toText(),
        color = WhiteboardColors.primary,
        style = Typography.h2(scale)
    )
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
        color = Color.White,
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
fun TextFieldPlaceholderText(text: String, style: TextStyle = Typography.subtitle1) {
    Text(
        text = text,
        style = style
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
fun PrimaryBodyText(text: String) {
    Text(
        text = text,
        color = WhiteboardColors.primary,
        style = Typography.body1
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
fun ErrorText(text: String, isVisible: Boolean = true) {
    Text(
        text = text,
        color = if (isVisible) WhiteboardColors.error else Color.Transparent,
        style = Typography.subtitle2
    )
}