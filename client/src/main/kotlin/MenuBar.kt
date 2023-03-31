package cs346.whiteboard.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.*
import cs346.whiteboard.client.commands.CommandFactory
import cs346.whiteboard.client.commands.CommandTypes
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.helpers.Toolkit
import cs346.whiteboard.client.ui.*
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions
import java.awt.event.*

object MenuBarState{
    private val _isLocal = mutableStateOf(false)
    var isLocal: Boolean by _isLocal
}
@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun createMenuBar(state: WindowState, frameScope: FrameWindowScope){
    frameScope.MenuBar {
        Menu("Tools", mnemonic = 'T') {
            Item("Select", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SELECT).execute() }, shortcut = KeyShortcut(Key.V), enabled = WhiteboardEventHandler.isEditingText)
            Item("Pan", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PAN).execute() }, shortcut = KeyShortcut(Key.D), enabled = WhiteboardEventHandler.isEditingText)
            Separator()
            Item("Pen", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PEN).execute() }, shortcut = KeyShortcut(Key.P), enabled = WhiteboardEventHandler.isEditingText)
            Item("Square", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SQUARE).execute() }, shortcut = KeyShortcut(Key.U), enabled = WhiteboardEventHandler.isEditingText)
            Item("Circle", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.CIRCLE).execute() }, shortcut = KeyShortcut(Key.C), enabled = WhiteboardEventHandler.isEditingText)
            Item("Text", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.TEXT).execute() }, shortcut = KeyShortcut(Key.T), enabled = WhiteboardEventHandler.isEditingText)
            Separator()
            Item("Eraser", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.ERASE).execute() }, shortcut = KeyShortcut(Key.X), enabled = WhiteboardEventHandler.isEditingText)
        }
        Menu("Edit", mnemonic = 'E') {
            Item("Delete", onClick = { CommandFactory.create(CommandTypes.DELETE).execute() }, shortcut = KeyShortcut(Key.Backspace))
            Separator()
            Item("Cut", onClick = { CommandFactory.create(CommandTypes.CUT).execute() }, shortcut = KeyShortcut(Key.X, ctrl = !isMacOS(), meta = isMacOS()))
            Item("Copy", onClick = { CommandFactory.create(CommandTypes.COPY).execute() }, shortcut = KeyShortcut(Key.C, ctrl = !isMacOS(), meta = isMacOS()))
            Item("Paste", onClick = { CommandFactory.create(CommandTypes.PASTE).execute() }, shortcut = KeyShortcut(Key.V, ctrl = !isMacOS(), meta = isMacOS()))
        }
        Menu("View", mnemonic = 'V') {
            CheckboxItem("Dark mode", checked = WhiteboardColors.isDarkMode, onCheckedChange = {
                WhiteboardColors.isDarkMode = !WhiteboardColors.isDarkMode
            })
            Separator()
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
        Menu("Developer", mnemonic = 'D'){
            CheckboxItem("Toggle localhost", checked = MenuBarState.isLocal, onCheckedChange = {
                BaseUrlProvider.toggleLocalHost()
                MenuBarState.isLocal = !MenuBarState.isLocal
            })
        }
    }
}
