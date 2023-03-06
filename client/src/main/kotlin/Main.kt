package cs346.whiteboard.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Menu
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.ui.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.views.RootView
import cs346.whiteboard.client.views.RootViewModel
import cs346.whiteboard.client.views.TestView
import java.awt.Dimension
import java.awt.event.*


@Composable
fun App() {
    MaterialTheme {
        RootView(
            modifier = Modifier.fillMaxSize().background(Colors.background)
        )
    }
}

fun isMacOS():Boolean {
    val os = System.getProperty("os.name").lowercase()
    return os.contains("mac")
}

@OptIn(ExperimentalComposeUiApi::class)
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
    ){
        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item("New", onClick = {}, shortcut = KeyShortcut(Key.N, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Open", onClick = {}, shortcut = KeyShortcut(Key.O, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Save", onClick = {}, shortcut = KeyShortcut(Key.S, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Save As", onClick = {}, shortcut = KeyShortcut(Key.S, shift = true, ctrl = !isMacOS(), meta = isMacOS()))
            }
            Menu("Edit", mnemonic = 'E') {
                Item("Delete", onClick = {}, shortcut = KeyShortcut(Key.Backspace))
                Separator()
                Item("Cut", onClick = {}, shortcut = KeyShortcut(Key.X, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Copy", onClick = {}, shortcut = KeyShortcut(Key.C, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Paste", onClick = {}, shortcut = KeyShortcut(Key.V, ctrl = !isMacOS(), meta = isMacOS()))
            }
            Menu("Tools", mnemonic = 'T') {
                Item("Zoom in", onClick = {})
                Item("Zoom out", onClick = {})
                Separator()
                Item("Select", onClick = {})
                Item("Pan", onClick = {})
                Separator()
                Item("Pen", onClick = {})
                Item("Square", onClick = {})
                Item("Circle", onClick = {})
                Item("Text", onClick = {})
                Separator()
                Item("Eraser", onClick = {})
            }
            Menu("Window", mnemonic = 'V'){
                Item("Minimize",
                    onClick = {
                        state.isMinimized = true
                    },
                    enabled = !state.isMinimized && state.placement != WindowPlacement.Fullscreen
                )
                Item("Fullscreen",
                    onClick = {
                        state.placement = if (state.placement != WindowPlacement.Floating) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Fullscreen
                        }
                    },
                    enabled = !(state.isMinimized)
                )
            }
        }
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
