/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.commands

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEvent
import cs346.whiteboard.client.helpers.Toolkit
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions

object WhiteboardEventHandler {
    var isEditingText = false
    @OptIn(ExperimentalComposeUiApi::class)
    fun onKeyEventHandler(event: KeyEvent): Boolean {
        if (isEditingText) return true
        return when {
            // Panning
            (event.key == Key.Spacebar && event.type == KeyEventType.KeyDown) -> {
                if (!Toolkit.isToolHeld) {
                    Toolkit.toolHolder = CommandFactory.whiteboardController?.currentTool ?: return true
                    CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PAN).execute()
                    Toolkit.isToolHeld = true
                }
                true
            }
            (event.key == Key.Spacebar && event.type == KeyEventType.KeyUp) -> {
                CommandFactory.create(CommandTypes.SETTOOL, Toolkit.toolHolder).execute()
                Toolkit.isToolHeld = false
                true
            }
            // Clipboard
            (event.isMetaPressed && event.key == Key.X && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.CUT).execute()
                true
            }
            (event.isMetaPressed && event.key == Key.C && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.COPY).execute()
                true
            }
            (event.isMetaPressed && event.key == Key.V && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.PASTE).execute()
                true
            }
            (event.key == Key.Delete && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.DELETE).execute()
                true
            }
            // Tools
            (event.key == Key.V && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SELECT).execute()
                true
            }
            (event.key == Key.D && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PAN).execute()
                true
            }
            (event.key == Key.P && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.PEN).execute()
                true
            }
            (event.key == Key.U && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.SQUARE).execute()
                true
            }
            (event.key == Key.C && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.CIRCLE).execute()
                true
            }
            (event.key == Key.T && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.TEXT).execute()
                true
            }
            (event.key == Key.X && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.SETTOOL, WhiteboardToolbarOptions.ERASE).execute()
                true
            }
            // Theme
            (event.isMetaPressed && event.key == Key.M && event.type == KeyEventType.KeyUp) -> {
                CommandFactory.create(CommandTypes.THEME).execute()
                true
            }
            // Zooming
            (event.isMetaPressed && event.key == Key.Equals && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.ZOOMIN).execute()
                true
            }
            (event.isMetaPressed && event.key == Key.Minus && event.type == KeyEventType.KeyDown) -> {
                CommandFactory.create(CommandTypes.ZOOMOUT).execute()
                true
            }
            (event.isMetaPressed) -> {
                Toolkit.metaHolder = true
                true
            }
            (event.isShiftPressed) -> {
                Toolkit.shiftHolder = true
                true
            }
            // Toolkit clean up
            else -> {
                Toolkit.metaHolder = false
                Toolkit.shiftHolder = false
                false
            }
        }
    }

    fun onScrollEventHandler(event: PointerEvent) {
        if (Toolkit.metaHolder) {
            var delta = event.changes.first().scrollDelta.y
            if (delta < 0) {
                CommandFactory.create(CommandTypes.ZOOMIN).execute()
            } else {
                CommandFactory.create(CommandTypes.ZOOMOUT).execute()
            }
        }
    }
}
