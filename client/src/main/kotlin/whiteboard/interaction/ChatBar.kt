package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.settings.MenuBarState
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.ui.*
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import kotlinx.coroutines.launch
import java.awt.Cursor

@Composable
fun ChatBar(chatController: ChatController) {
    val messageState = remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var expandedState by remember { mutableStateOf(false) }

    fun sendMessage() {
        if (messageState.value.text.isEmpty()) return

        chatController.sendMessage(messageState.value.text)
        messageState.value = TextFieldValue("")
        coroutineScope.launch {
            scrollState.animateScrollToItem(chatController.messages.size - 1)
        }
    }

    if (expandedState && chatController.newMessageReceived) {
        coroutineScope.launch {
            scrollState.animateScrollToItem(chatController.messages.size - 1)
        }
        chatController.newMessageReceived = false
    }

    Box{
        Column(
            modifier = Modifier
                .width(300.dp)
                .padding(4.dp, 4.dp, 16.dp, 16.dp)
                .border(1.dp, WhiteboardColors.secondaryVariant, Shapes.small)
                .shadow(16.dp, Shapes.small, true)
                .background(WhiteboardColors.background)
                .clip(Shapes.small)
                .zIndex(WhiteboardLayerZIndices.chat)
                .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor()))
        ) {
            ChatToolHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedState = !expandedState
                    },
                expandedState = expandedState
            )

            AnimatedVisibility(visible = expandedState) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    HorizontalDivider(color = WhiteboardColors.secondaryVariant)

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(15.dp, 0.dp)
                    ) {

                        items(chatController.messages, key = { it }) {
                            ChatMessage(
                                it.sender,
                                it.time,
                                it.content,
                                Modifier.padding(0.dp, 12.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = WhiteboardColors.secondaryVariant)

                    ChatTextFieldWithButton(
                        text = messageState,
                        placeholder = "Message",
                        modifier = Modifier
                            .padding(15.dp),
                        textFieldModifier = Modifier
                            .onFocusChanged {
                                WhiteboardEventHandler.isEditingText = it.isFocused
                                MenuBarState.isToolEnabled = !it.isFocused
                            },
                        onClick = ::sendMessage
                    )
                }
            }
        }

        if (!expandedState && chatController.newMessageReceived) { Badge() }
    }
}
