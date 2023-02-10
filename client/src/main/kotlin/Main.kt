package cs346.whiteboard.client

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cs346.whiteboard.client.views.RootView

@Composable
fun App() {
    MaterialTheme {
        RootView(
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Whiteboard",
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(1500.dp, 1000.dp)
            ),
        ) {
        App()
    }
}
