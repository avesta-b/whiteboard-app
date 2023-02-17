package cs346.whiteboard.service.controllers

import cs346.whiteboard.shared.jsonmodels.CursorPositionUpdate
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class CursorUpdateController {

    @MessageMapping("/whiteboard/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun updateCursor(@DestinationVariable roomId: String,
                     newUserPosition: CursorPositionUpdate): CursorPositionUpdate {

        return newUserPosition
    }
}