package cs346.whiteboard.client.commands

import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardToolbarOptions

enum class CommandTypes {
    CUT, COPY, PASTE, DELETE, ZOOMIN, ZOOMOUT, SETTOOL,
}

object CommandFactory {
    var whiteboardController: WhiteboardController? = null
    fun create(command: CommandTypes): Command {
        return when (command) {
            CommandTypes.CUT -> CutCommand(whiteboardController)
            CommandTypes.COPY -> CopyCommand(whiteboardController)
            CommandTypes.PASTE -> PasteCommand(whiteboardController)
            CommandTypes.DELETE -> DeleteCommand(whiteboardController)
            CommandTypes.ZOOMIN -> ZoomInCommand(whiteboardController)
            CommandTypes.ZOOMOUT -> ZoomOutCommand(whiteboardController)
            else -> NothingCommand(whiteboardController)
        }
    }

    fun create(command: CommandTypes, tool: WhiteboardToolbarOptions = WhiteboardToolbarOptions.SELECT): Command {
        return when (command) {
            CommandTypes.SETTOOL -> SetToolCommand(whiteboardController, tool)
            else -> NothingCommand(whiteboardController)
        }
    }
}