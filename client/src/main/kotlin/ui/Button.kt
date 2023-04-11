/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.helpers.getUserColor
import cs346.whiteboard.shared.jsonmodels.WhiteboardItem

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

@Composable
fun OwnedWhiteboardButton(
    item: WhiteboardItem,
    modifier: Modifier,
    cornerRadius: Dp = 8.dp,
    onClick: () -> Unit,
) {
    val sharedText = if (item.sharedWithOthers != false) { "Shared with others"}  else { "Only you can access" }
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier,
        border = BorderStroke(1.dp, WhiteboardColors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = WhiteboardColors.background,
            contentColor = WhiteboardColors.secondaryVariant,
            disabledContentColor = WhiteboardColors.secondaryVariant)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            ImageIcon(icon=CustomIcon.FILE, modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(24.dp))

            Column(modifier = Modifier
                .padding(horizontal = 24.dp)){
                PrimaryBodyText(item.name)
                Spacer(modifier = Modifier.height(4.dp))
                SecondaryBodyText(sharedText)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (item.sharedWithOthers != false) {
                ImageIcon(icon=CustomIcon.SHARED, modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(24.dp))
            }
        }
    }
}

@Composable
fun SharedWhiteboardButton(
    item: WhiteboardItem,
    modifier: Modifier,
    cornerRadius: Dp = 8.dp,
    onClick: () -> Unit,
) {
    val sharedText = "Shared by ${item.author}"
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier,
        border = BorderStroke(1.dp, WhiteboardColors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = WhiteboardColors.background,
            contentColor = WhiteboardColors.secondaryVariant,
            disabledContentColor = WhiteboardColors.secondaryVariant)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            ImageIcon(icon=CustomIcon.FILE, modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(24.dp))

            Column(modifier = Modifier
                .padding(horizontal = 24.dp)){
                PrimaryBodyText(item.name)
                Spacer(modifier = Modifier.height(4.dp))
                SecondaryBodyText(sharedText)
            }

            Spacer(modifier = Modifier.weight(1f))

            UserIconText(
                item.author.first().uppercaseChar().toString(),
                getUserColor(item.author)
            )
        }
    }
}


@Composable
fun TwoTextButton(
    text1: String,
    text2: String,
    isFirstSelected: Boolean,
    onClick: () -> Unit
) {
    var isText1Selected = isFirstSelected
    val backgroundColor1 = if (isText1Selected) WhiteboardColors.background else WhiteboardColors.lightBackground
    val backgroundColor2 = if (!isText1Selected) WhiteboardColors.background else WhiteboardColors.lightBackground

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(Shapes.small)
            .background(WhiteboardColors.lightBackground)
            .clickable(onClick = onClick)
            .height(52.dp)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(Shapes.small)
                .background(backgroundColor1)
                .padding(12.dp)
        ) {
            if (isText1Selected) {
                PrimaryBodyText(text1)
            } else {
                SecondaryBodyText(text1)
            }
        }
        Box(
            modifier = Modifier
                .clip(Shapes.small)
                .background(backgroundColor2)
                .padding(12.dp)
        ) {
            if (isText1Selected) {
                SecondaryBodyText(text2)
            } else {
                PrimaryBodyText(text2)
            }
        }
    }
}
