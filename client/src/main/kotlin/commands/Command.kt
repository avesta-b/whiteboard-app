/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.commands

import cs346.whiteboard.client.whiteboard.WhiteboardController

abstract class Command {
    abstract var whiteboardController: WhiteboardController?
    abstract fun execute()
}