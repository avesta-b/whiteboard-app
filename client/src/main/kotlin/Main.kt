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
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

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
        onCloseRequest = {
            WindowManager.saveWindowSize()
            exitApplication()
        },
        title = "Whiteboard",
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(WindowManager.getSavedWindowSize().getWidth().dp,
                WindowManager.getSavedWindowSize().getHeight().dp) // get window size info
        ),
    ){
        window.minimumSize = Dimension(600, 500) // set minimum size
        //add handler for window size persistence
        window.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
                WindowManager.setWindowSize(componentEvent!!.component.width, componentEvent.component.height)
            }
        })
        App()

    }
}
