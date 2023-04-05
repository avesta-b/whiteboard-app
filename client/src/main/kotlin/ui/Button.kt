package cs346.whiteboard.client.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.helpers.CustomIcon

@Composable
fun PrimaryButton(modifier: Modifier,
                  text: String,
                  enabled: Boolean,
                  onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = WhiteboardColors.primary,
            contentColor = WhiteboardColors.background,
            disabledBackgroundColor = WhiteboardColors.primary,
            disabledContentColor = WhiteboardColors.background
        )) {
        if (enabled) {
            PrimaryButtonText(text)
        } else {
            PrimaryButtonSpinner()
        }
    }
}

@Composable
fun OutlinedButton(modifier: Modifier,
                   text: String,
                   enabled: Boolean,
                   onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = Shapes.small,
        border = BorderStroke(1.dp, WhiteboardColors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = WhiteboardColors.background,
            contentColor = WhiteboardColors.secondaryVariant,
            disabledContentColor = WhiteboardColors.secondaryVariant)
    ) {
        PrimarySubtitleText(text = text)
    }
}

@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier
        .wrapContentSize(),
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable() (RowScope.() -> Unit)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = Shapes.medium,
        border = BorderStroke(1.dp, if (selected) WhiteboardColors.primary else WhiteboardColors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = WhiteboardColors.background,
            contentColor = WhiteboardColors.primary
        ),
        content = content
    )
}

@Composable
fun UnderlinedTextButton(modifier: Modifier,
                         text: String,
                         enabled: Boolean,
                         onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = Shapes.small,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = WhiteboardColors.background,
            contentColor = WhiteboardColors.secondaryVariant,
            disabledContentColor = WhiteboardColors.secondaryVariant
        )
    ) {
        UnderlinedText(text)
    }
}

@Composable
fun CustomIcon(
    modifier: Modifier = Modifier.size(48.dp),
    icon: CustomIcon,
    iconSize: Dp = 8.dp,
    iconPadding: Dp = 16.dp,
    shape: Shape = RectangleShape,
    isHighlighted: Boolean = false,
) {
    Box(modifier) {
        if (isHighlighted) {
            Box(
                modifier
                    .size(16.dp)
                    .padding(8.dp)
                    .clip(shape)
                    .background(WhiteboardColors.highlightedIconButtonColor)
                    .align(Alignment.Center)
            )
        }
        Image(
            painter = painterResource(icon.path()),
            contentDescription = null,
            modifier = modifier.size(iconSize).align(Alignment.Center).padding(iconPadding),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier.size(48.dp),
    icon: CustomIcon,
    iconSize: Dp = 8.dp,
    iconPadding: Dp = 16.dp,
    shape: Shape = RectangleShape,
    isHighlighted: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = WhiteboardColors.background,
        contentColor = WhiteboardColors.primary
    ),
    onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        if (enabled) {
            CustomIcon(
                modifier = modifier,
                icon = icon,
                iconSize = iconSize,
                iconPadding = iconPadding,
                shape = shape,
                isHighlighted = isHighlighted
            )
        } else {
            PrimaryButtonSpinner()
        }
    }
}