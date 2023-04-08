package cs346.whiteboard.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.window.*
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.settings.WindowManager
import cs346.whiteboard.client.settings.createMenuBar
import cs346.whiteboard.client.views.RootView
import java.awt.Dimension
import java.awt.event.*


@Composable
fun App() {
    MaterialTheme {
        RootView(
            modifier = Modifier.fillMaxSize().background(WhiteboardColors.background)
        )
    }
}

fun isMacOS():Boolean {
    val os = System.getProperty("os.name").lowercase()
    return os.contains("mac")
}

fun main() = application {
    val state = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center),
        size = WindowManager.getWindowSize()
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "Whiteboard",
        state = state,
        onKeyEvent = WhiteboardEventHandler::onKeyEventHandler
    ){
        createMenuBar(state, this)
        window.minimumSize = Dimension(1000, 800) // set minimum size
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
