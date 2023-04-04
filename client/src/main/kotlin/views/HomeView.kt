package cs346.whiteboard.client.views

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.MenuBarState
import cs346.whiteboard.client.ui.AuthenticationTextField
import cs346.whiteboard.client.ui.PrimaryButton

enum class HomeUiState {
    DRAW, MENU
}

private fun getWhiteboardButtonText(roomId: String): String {
    return if (roomId.isEmpty()) "Draw alone" else "Draw with friends"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeView(modifier: Modifier, onSignOut: () -> Unit) {
    var uiState by remember { mutableStateOf(HomeUiState.MENU)}
    var roomId = remember { mutableStateOf( TextFieldValue("")) }

    Box(modifier, Alignment.Center) {
        Crossfade(uiState) { state ->
            when (state) {
                HomeUiState.MENU -> {
                    MenuBarState.isToolEnabled = false
                    Box(modifier, Alignment.Center) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            PrimaryButton(
                                modifier = Modifier.size(280.dp, 50.dp),
                                text = "Sign out",
                                enabled = true) {
                                onSignOut()
                            }
                            PrimaryButton(
                                modifier = Modifier.size(280.dp, 50.dp),
                                text = getWhiteboardButtonText(roomId.value.text),
                                enabled = true) {
                                uiState = HomeUiState.DRAW
                            }
                            AuthenticationTextField(
                                text = roomId,
                                modifier = Modifier
                                    .size(280.dp, 60.dp)
                                    .onKeyEvent { keyEvent ->
                                        if (keyEvent.key != Key.Enter || roomId.value.text.isEmpty()) {
                                            return@onKeyEvent false
                                        }
                                        uiState = HomeUiState.DRAW
                                        true
                                    },
                                enabled = true,
                                placeholder = "Enter a room ID to connect to",
                                isSecure = false
                            )
                        }
                    }
                }
                HomeUiState.DRAW -> {
                    MenuBarState.isToolEnabled = true
                    WhiteboardView(modifier, roomId.value.text, onExit = {
                        uiState = HomeUiState.MENU
                    })
                }
            }
        }
    }
}