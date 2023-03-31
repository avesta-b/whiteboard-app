package cs346.whiteboard.client.whiteboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.websocket.ChatController
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.ui.*
import kotlinx.coroutines.launch
import java.awt.Cursor

@Composable
fun ChatBar(chatController: ChatController) {
    val messageState = remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 0f else 180f
    )

    fun sendMessage() {
        if (messageState.value.text.isEmpty()) return

        chatController.sendMessage(messageState.value.text)
        messageState.value = TextFieldValue("")

        coroutineScope.launch {
            scrollState.animateScrollToItem(chatController.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .width(400.dp)
            .padding(16.dp)
            .border(1.dp, WhiteboardColors.secondaryVariant, Shapes.small)
            .shadow(16.dp, Shapes.small, true)
            .background(WhiteboardColors.background)
            .clip(Shapes.small)
            .zIndex(WhiteboardLayerZIndices.chat)
            .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor()))
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChatToolHeader(
                titleModifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedState = !expandedState
                    },
                arrowModifier = Modifier
                    .alpha(ContentAlpha.medium)
                    .rotate(rotationState),
                onClick = {
                    expandedState = !expandedState
                }
            )

            if (expandedState) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(20.dp, 0.dp)
                ) {

                    items(chatController.messages, key = { it }) {
                        ChatMessage(
                            it.sender,
                            it.time,
                            it.content,
                            Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp)
                        )
                    }
                }

                ChatTextFieldWithButton(
                    text = messageState,
                    placeholder = "Message",
                    enabled = true,
                    modifier = Modifier
                        .padding(20.dp),
                    textFieldModifier = Modifier
                        .onFocusChanged {
                            WhiteboardEventHandler.isEditingText = it.isFocused
                        },
                    onClick = ::sendMessage
                )
            }
        }
    }
}