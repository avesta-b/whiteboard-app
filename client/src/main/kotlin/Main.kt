package cs346.whiteboard.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import androidx.compose.ui.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.input.key.*
import cs346.whiteboard.client.commands.CommandFactory
import cs346.whiteboard.client.commands.CommandTypes
import cs346.whiteboard.client.commands.onKeyEventHandler
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.views.RootView
import cs346.whiteboard.client.whiteboard.WhiteboardToolbarOptions
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
        onKeyEvent = ::onKeyEventHandler
    ){
        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item("New", onClick = {}, shortcut = KeyShortcut(Key.N, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Open", onClick = {}, shortcut = KeyShortcut(Key.O, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Save", onClick = {}, shortcut = KeyShortcut(Key.S, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Save As", onClick = {}, shortcut = KeyShortcut(Key.S, shift = true, ctrl = !isMacOS(), meta = isMacOS()))
            }
            Menu("Edit", mnemonic = 'E') {
                Item("Delete", onClick = { CommandFactory.create(CommandTypes.DELETE).execute() }, shortcut = KeyShortcut(Key.Backspace))
                Separator()
                Item("Cut", onClick = { CommandFactory.create(CommandTypes.CUT).execute() }, shortcut = KeyShortcut(Key.X, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Copy", onClick = { CommandFactory.create(CommandTypes.COPY).execute() }, shortcut = KeyShortcut(Key.C, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Paste", onClick = { CommandFactory.create(CommandTypes.PASTE).execute() }, shortcut = KeyShortcut(Key.V, ctrl = !isMacOS(), meta = isMacOS()))
            }
            Menu("Tools", mnemonic = 'T') {
                Item("Select", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SELECT).execute() }, shortcut = KeyShortcut(Key.V))
                Item("Pan", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PAN).execute() }, shortcut = KeyShortcut(Key.D))
                Separator()
                Item("Pen", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PEN).execute() }, shortcut = KeyShortcut(Key.P))
                Item("Square", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SQUARE).execute() }, shortcut = KeyShortcut(Key.U))
                Item("Circle", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.CIRCLE).execute() }, shortcut = KeyShortcut(Key.C))
                Item("Text", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.TEXT).execute() }, shortcut = KeyShortcut(Key.T))
                Separator()
                Item("Eraser", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.ERASE).execute() }, shortcut = KeyShortcut(Key.X))
            }
            Menu("View", mnemonic = 'V') {
                Item("Zoom in", onClick = { CommandFactory.create(CommandTypes.ZOOMIN).execute() }, shortcut = KeyShortcut(Key.Plus, ctrl = !isMacOS(), meta = isMacOS()))
                Item("Zoom out", onClick = { CommandFactory.create(CommandTypes.ZOOMOUT).execute() }, shortcut = KeyShortcut(Key.Minus, ctrl = !isMacOS(), meta = isMacOS()))
            }
            Menu("Window", mnemonic = 'W') {
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
