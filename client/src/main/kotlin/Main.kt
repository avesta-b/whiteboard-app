package cs346.whiteboard.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.views.RootView
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

@Composable
fun App() {
    MaterialTheme {
        RootView(
            modifier = Modifier.fillMaxSize().background(Colors.background)
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
            size = WindowManager.getWindowSize()
        ),
    ) {
        window.minimumSize = Dimension(800, 600) // set minimum size
        //add handler for window size persistence
        window.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
                componentEvent?.let {
                    WindowManager.setWindowSize(it.component.size)
                }
            }
        })
        App()
    }
}
