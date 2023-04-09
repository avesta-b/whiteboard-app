package cs346.whiteboard.client.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.*
import cs346.whiteboard.client.commands.CommandFactory
import cs346.whiteboard.client.commands.CommandTypes
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.isMacOS
import cs346.whiteboard.client.network.BaseUrlProvider
import cs346.whiteboard.client.ui.*
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import java.awt.event.*

const val MENUBAR_KEY = "menubar"

object MenuBarState {
    private val _isLocal: MutableState<Boolean> = mutableStateOf(menuBarPreferences.isLocal)
    var isLocal: Boolean by _isLocal
    private val _isToolEnabled: MutableState<Boolean> = mutableStateOf(false)
    var isToolEnabled: Boolean by _isToolEnabled

    @Serializable
    data class MenuBarPreferences(
        var isLocal: Boolean,
        var isDarkMode: Boolean
    )
    var menuBarPreferences : MenuBarPreferences
        get(){
            PreferencesManager.readFromPreferences(MENUBAR_KEY)?.let{
                return Json.decodeFromString(MenuBarPreferences.serializer(), it)
            }
            return MenuBarPreferences(isLocal = false, isDarkMode = false)
        }
        set(pref){
            PreferencesManager.writeToPreferencesWithKey(MENUBAR_KEY, Json.encodeToString(pref))
        }

    fun toggleIsLocal(){
        isLocal = !isLocal
        menuBarPreferences = MenuBarPreferences(isLocal, menuBarPreferences.isDarkMode)
    }

    fun toggleDarkMode(){
        WhiteboardColors.isDarkMode = !WhiteboardColors.isDarkMode
        menuBarPreferences = MenuBarPreferences(isLocal, WhiteboardColors.isDarkMode)
    }
}
@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun createMenuBar(state: WindowState, frameScope: FrameWindowScope){
    frameScope.MenuBar {
        Menu("Tools", mnemonic = 'T') {
            Item("Select", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SELECT).execute() }, shortcut = KeyShortcut(Key.V), enabled = MenuBarState.isToolEnabled)
            Item("Pan", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PAN).execute() }, shortcut = KeyShortcut(Key.D), enabled = MenuBarState.isToolEnabled)
            Separator()
            Item("Pen", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PEN).execute() }, shortcut = KeyShortcut(Key.P), enabled = MenuBarState.isToolEnabled)
            Item("Square", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SQUARE).execute() }, shortcut = KeyShortcut(Key.U), enabled = MenuBarState.isToolEnabled)
            Item("Circle", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.CIRCLE).execute() }, shortcut = KeyShortcut(Key.C), enabled = MenuBarState.isToolEnabled)
            Item("Text", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.TEXT).execute() }, shortcut = KeyShortcut(Key.T), enabled = MenuBarState.isToolEnabled)
            Separator()
            Item("Eraser", onClick = { CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.ERASE).execute() }, shortcut = KeyShortcut(Key.X), enabled = MenuBarState.isToolEnabled)
        }
        Menu("Edit", mnemonic = 'E') {
            Item("Delete", onClick = { CommandFactory.create(CommandTypes.DELETE).execute() }, shortcut = KeyShortcut(Key.Backspace))
            Separator()
            Item("Cut", onClick = { CommandFactory.create(CommandTypes.CUT).execute() }, shortcut = KeyShortcut(Key.X, ctrl = !isMacOS(), meta = isMacOS()))
            Item("Copy", onClick = { CommandFactory.create(CommandTypes.COPY).execute() }, shortcut = KeyShortcut(Key.C, ctrl = !isMacOS(), meta = isMacOS()))
            Item("Paste", onClick = { CommandFactory.create(CommandTypes.PASTE).execute() }, shortcut = KeyShortcut(Key.V, ctrl = !isMacOS(), meta = isMacOS()))
        }
        Menu("View", mnemonic = 'V') {
            CheckboxItem(
                "Dark mode",
                checked = WhiteboardColors.isDarkMode,
                onCheckedChange = { MenuBarState.toggleDarkMode() }, // TODO("Figure out why shortcut doesn't trigger this callback
                shortcut = KeyShortcut(Key.M, ctrl = !isMacOS(), meta = isMacOS()) // See commands.EventHandlers for invocation of shortcut
            )
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
                MenuBarState.toggleIsLocal()
            })
        }
    }
}
