package cs346.whiteboard.client.commands

import cs346.whiteboard.client.MenuBarState
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions

class CutCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        whiteboardController?.cutSelected()
    }
}

class CopyCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        whiteboardController?.copySelected()
    }
}

class PasteCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        whiteboardController?.pasteFromClipboard()
    }
}

class DeleteCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        whiteboardController?.deleteSelected()
    }
}

class ZoomInCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        whiteboardController?.zoomIn()
    }
}

class ZoomOutCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        whiteboardController?.zoomOut()
    }
}

class SetToolCommand(override var whiteboardController: WhiteboardController?, private val tool: WhiteboardToolbarOptions): Command() {
    override fun execute() {
        whiteboardController?.currentTool = tool
    }
}

class ThemeCommand(override var whiteboardController: WhiteboardController?): Command() {
    override fun execute() {
        MenuBarState.toggleDarkMode()
    }
}