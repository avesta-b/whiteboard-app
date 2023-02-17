package cs346.whiteboard.client.views

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.components.PrimaryButton

// TODO: replace TestView

enum class TestUiState {
    CURSOR_DEMO, DRAW_DEMO, MENU
}

@Composable
fun TestView(modifier: Modifier, onSignOut: () -> Unit) {
    var testUiState by remember { mutableStateOf(TestUiState.MENU)}
    var roomId by remember { mutableStateOf( "") }

    Box(modifier, Alignment.Center) {
        Crossfade(testUiState) { state ->
            when (state) {
                TestUiState.MENU -> {
                    Box(modifier, Alignment.Center) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            PrimaryButton(Modifier.size(280.dp, 50.dp), "Sign out", true) {
                                UserManager.signOut()
                                onSignOut()
                            }
                            PrimaryButton(Modifier.size(280.dp, 50.dp), "Draw", true) {
                                testUiState = TestUiState.DRAW_DEMO
                            }
                            PrimaryButton(Modifier.size(280.dp, 50.dp),
                                text="Connect to a room",
                                enabled=roomId.isNotEmpty()) {
                                testUiState = TestUiState.CURSOR_DEMO
                            }
                            TextField(
                                value = roomId,
                                onValueChange = {roomId = it},
                                placeholder = { Text(text="Enter a room ID to connect to")}
                            )
                        }
                    }
                }
                TestUiState.DRAW_DEMO -> {
                    Column(modifier = modifier, horizontalAlignment = Alignment.Start){
                        IconButton(onClick = {
                            testUiState = TestUiState.MENU
                        }) {
                            Icon(Icons.Filled.ArrowBack, "back")
                        }
                        WhiteboardView(modifier)
                    }
                }
                TestUiState.CURSOR_DEMO -> {
                    Column(modifier = modifier, horizontalAlignment = Alignment.Start){
                        IconButton(onClick = {
                            testUiState = TestUiState.MENU
                            roomId=""
                        }) {
                            Icon(Icons.Filled.ArrowBack, "back")
                        }
                        // Cursor demo view here
                        CursorView(modifier, roomId)
                    }
                }
            }
        }
    }
}