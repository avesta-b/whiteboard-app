/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.commands

import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions

enum class CommandTypes {
    CUT, COPY, PASTE, DELETE, ZOOMIN, ZOOMOUT, SETTOOL, THEME
}

object CommandFactory {
    var whiteboardController: WhiteboardController? = null
    fun create(command: CommandTypes, tool: WhiteboardToolbarOptions = WhiteboardToolbarOptions.SELECT): Command {
        return when (command) {
            CommandTypes.CUT -> CutCommand(whiteboardController)
            CommandTypes.COPY -> CopyCommand(whiteboardController)
            CommandTypes.PASTE -> PasteCommand(whiteboardController)
            CommandTypes.DELETE -> DeleteCommand(whiteboardController)
            CommandTypes.ZOOMIN -> ZoomInCommand(whiteboardController)
            CommandTypes.ZOOMOUT -> ZoomOutCommand(whiteboardController)
            CommandTypes.THEME -> ThemeCommand(whiteboardController)
            CommandTypes.SETTOOL -> SetToolCommand(whiteboardController, tool)
        }
    }
}