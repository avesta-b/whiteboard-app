/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.helpers.toDp
import cs346.whiteboard.client.ui.EmojiPingText
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import cs346.whiteboard.shared.jsonmodels.EmojiPing

@Composable
fun PingMenu(controller: WhiteboardController, data: PingWheelData) {
    val coordinate = data.coordinate
    val size = Size(250f, 250f)
    var thumbsPingScale by remember { mutableStateOf(1f) }
    var smilePingScale by remember { mutableStateOf(1f) }
    var skullPingScale by remember { mutableStateOf(1f) }
    var thinkPingScale by remember { mutableStateOf(1f) }
    if (data.selectedPing == EmojiPing.THUMBS) thumbsPingScale = 2f else thumbsPingScale = 1f
    if (data.selectedPing == EmojiPing.SMILE) smilePingScale = 2f else smilePingScale = 1f
    if (data.selectedPing == EmojiPing.SKULL) skullPingScale = 2f else skullPingScale = 1f
    if (data.selectedPing == EmojiPing.THINK) thinkPingScale = 2f else thinkPingScale = 1f
    val animateThumbsPingScale by animateFloatAsState(
        targetValue = thumbsPingScale,
        animationSpec = tween(durationMillis = 200)
    )
    val animateSmilePingScale by animateFloatAsState(
        targetValue = smilePingScale,
        animationSpec = tween(durationMillis = 200)
    )
    val animateSkullPingScale by animateFloatAsState(
        targetValue = skullPingScale,
        animationSpec = tween(durationMillis = 200)
    )
    val animateThinkPingScale by animateFloatAsState(
        targetValue = thinkPingScale,
        animationSpec = tween(durationMillis = 200)
    )
    Column(
        modifier = Modifier
                    .offset((coordinate.x - size.width / 2).toDp(), (coordinate.y - size.height / 2).toDp())
                    .size(size.width.toDp(),size.height.toDp())
                    .border((0.5).dp, WhiteboardColors.secondaryVariant, Shapes.large)
                    .shadow(16.dp, Shapes.large, true)
                    .background(WhiteboardColors.background)
                    .clip(Shapes.large)
                    .zIndex(WhiteboardLayerZIndices.pingWheel)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size((size.width / 2).toDp(), (size.height / 2).toDp())
                    .border((0.5).dp, WhiteboardColors.secondaryVariant),
                contentAlignment = Alignment.Center) {
                EmojiPingText(EmojiPing.THUMBS, animateThumbsPingScale)
            }
            Box(
                modifier = Modifier
                    .size((size.width / 2).toDp(), (size.height / 2).toDp())
                    .border((0.5).dp, WhiteboardColors.secondaryVariant),
                contentAlignment = Alignment.Center) {
                EmojiPingText(EmojiPing.SMILE, animateSmilePingScale)
            }
        }
        Row {
            Box(
                modifier = Modifier
                    .size((size.width / 2).toDp(), (size.height / 2).toDp())
                    .border((0.5).dp, WhiteboardColors.secondaryVariant),
                contentAlignment = Alignment.Center) {
                EmojiPingText(EmojiPing.SKULL, animateSkullPingScale)
            }
            Box(
                modifier = Modifier
                    .size((size.width / 2).toDp(), (size.height / 2).toDp())
                    .border((0.5).dp, WhiteboardColors.secondaryVariant),
                contentAlignment = Alignment.Center) {
                EmojiPingText(EmojiPing.THINK, animateThinkPingScale)
            }
        }
    }
}