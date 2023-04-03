package cs346.whiteboard.client.views

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.MenuBarState
import cs346.whiteboard.client.ui.PrimaryButton
import cs346.whiteboard.client.ui.TextFieldWithButton

// TODO: replace TestView

enum class TestUiState {
    DRAW, MENU
}

private fun getWhiteboardButtonText(roomId: String): String {
    return if (roomId.isEmpty()) "Draw alone" else "Draw with friends"
}

@Composable
fun TestView(modifier: Modifier, onSignOut: () -> Unit) {
    var testUiState by remember { mutableStateOf(TestUiState.MENU)}
    var roomId = remember { mutableStateOf( TextFieldValue("")) }

    Box(modifier, Alignment.Center) {
        Crossfade(testUiState) { state ->
            when (state) {
                TestUiState.MENU -> {
                    MenuBarState.isToolEnabled = false
                    Box(modifier, Alignment.Center) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            PrimaryButton(Modifier.size(280.dp, 50.dp), "Sign out", true) {
                                onSignOut()
                            }
                            TextFieldWithButton(
                                text = roomId,
                                buttonText = getWhiteboardButtonText(roomId.value.text),
                                modifier = Modifier.size(280.dp, 50.dp),
                                enabled = true,
                                onClick = { testUiState = TestUiState.DRAW },
                                placeholder = "Enter a room ID to connect to"
                            )
                        }
                    }
                }
                TestUiState.DRAW -> {
                    MenuBarState.isToolEnabled = true
                    WhiteboardView(modifier, roomId.value.text, onExit = {
                        testUiState = TestUiState.MENU
                    })
                }
            }
        }
    }
}