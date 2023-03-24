package cs346.whiteboard.client.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
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
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.backgroundDotColor
import cs346.whiteboard.client.constants.highlightedIconButtonColor
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
            backgroundColor = Colors.primary,
            contentColor = Colors.background,
            disabledBackgroundColor = Colors.primary,
            disabledContentColor = Colors.background
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
        border = BorderStroke(1.dp, Colors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.secondaryVariant,
            disabledContentColor = Colors.secondaryVariant)
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
        border = BorderStroke(1.dp, if (selected) Colors.primary else Colors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.primary
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
            backgroundColor = Colors.background,
            contentColor = Colors.secondaryVariant,
            disabledContentColor = Colors.secondaryVariant
        )
    ) {
        UnderlinedText(text)
    }
}

@Composable
fun OutlinedCustomIconButton(modifier: Modifier,
                     icon: CustomIcon,
                     onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.primary
        ),
        shape = Shapes.small,
        border = BorderStroke(1.dp, Colors.secondaryVariant),
    ) {
        Image(
            painterResource(icon.path()),
            null,
            modifier
        )
    }
}

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier.size(48.dp),
    icon: CustomIcon,
    iconSize: Dp = 8.dp,
    shape: Shape = RectangleShape,
    isHighlighted: Boolean = false,
    onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.primary
        ),
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier) {
            if (isHighlighted) {
                Box(
                    modifier
                        .size(16.dp)
                        .padding(8.dp)
                        .clip(shape)
                        .background(Colors.highlightedIconButtonColor)
                        .align(Alignment.Center)
                )
            }
            Image(
                painter = painterResource(icon.path()),
                contentDescription = null,
                modifier = modifier.size(iconSize).align(Alignment.Center).padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}