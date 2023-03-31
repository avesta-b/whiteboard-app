package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.helpers.bottomBorder
import cs346.whiteboard.client.helpers.getUserColor
import cs346.whiteboard.client.ui.*
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import java.awt.Cursor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WhiteboardTopBar(whiteboardController: WhiteboardController, modifier: Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(WhiteboardColors.background)
        .bottomBorder(1.dp, WhiteboardColors.secondaryVariant)
        .zIndex(WhiteboardLayerZIndices.topBar)
        .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor()))) {
        Row(modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            CustomIconButton(
                icon = CustomIcon.BACK,
                shape = Shapes.large,
                onClick = {
                    whiteboardController.exitWhiteboard()
                }
            )
            Spacer(Modifier.weight(1f))

            LazyRow {
                items(whiteboardController.userLobbyController.usersInLobby, key = { it }) {
                    TooltipArea(
                        tooltip = {
                            TooltipText(it)
                        },
                        delayMillis = 100,
                        tooltipPlacement = TooltipPlacement.ComponentRect(
                            offset = DpOffset((-32).dp, 8.dp)
                        ),
                        modifier = Modifier.onClick {
                            whiteboardController.teleportToUser(it)
                        }
                    ) {
                        UserIconText(
                            it.first().uppercaseChar().toString(),
                            getUserColor(it)
                        )
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
        }

        PrimarySubtitleText(text = whiteboardController.getWhiteboardTitle(), modifier = Modifier.align(Alignment.Center))
    }
}