/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.ui.CustomIconButton
import cs346.whiteboard.client.ui.PrimarySubtitleText
import cs346.whiteboard.client.ui.VerticalDivider
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import java.awt.Cursor
import kotlin.math.roundToInt

@Composable
fun WhiteboardZoomControl(controller: WhiteboardController) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(16.dp)
            .border(1.dp, WhiteboardColors.secondaryVariant, Shapes.small)
            .shadow(16.dp, Shapes.small, true)
            .background(WhiteboardColors.background)
            .clip(Shapes.small)
            .zIndex(WhiteboardLayerZIndices.zoomControl)
            .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor())),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomIconButton(
            icon = CustomIcon.MINUS,
            onClick = {
                controller.zoomOut()
            }
        )
        VerticalDivider(color = WhiteboardColors.secondaryVariant)
        Box(Modifier.padding(8.dp).width(48.dp)) {
            PrimarySubtitleText(
                text = "${(100 * controller.whiteboardZoom).roundToInt()}%",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        VerticalDivider(color = WhiteboardColors.secondaryVariant)
        CustomIconButton(
            icon = CustomIcon.PLUS,
            onClick = {
                controller.zoomIn()
            }
        )
    }
}