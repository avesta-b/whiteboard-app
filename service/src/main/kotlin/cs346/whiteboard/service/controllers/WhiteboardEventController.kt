package cs346.whiteboard.service.controllers

import com.auth0.jwt.JWT
import cs346.whiteboard.service.services.UserRoomManager
import cs346.whiteboard.service.services.WhiteboardStateManager
import cs346.whiteboard.shared.jsonmodels.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

@Controller
class WhiteboardEventController(
    @Autowired private val roomManager: UserRoomManager,
    @Autowired private val stateManager: WhiteboardStateManager
) {

    @MessageMapping("/whiteboard.updateCursor/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun updateCursor(
        @DestinationVariable roomId: String,
        newUserPosition: Position,
        headerAccessor: SimpMessageHeaderAccessor
    ): WebSocketEvent {
        val userJwt = headerAccessor.sessionAttributes?.get("userJwt").toString()

        val decodedJwt = JWT.decode(userJwt)
        val username = decodedJwt.claims["username"]?.asString()

        if (username.isNullOrBlank()) {
            // TODO: ignore request
        }

        return WebSocketEvent(
            eventType = WebSocketEventType.UPDATE_CURSOR,
            cursorUpdate = CursorUpdate(username ?: "no-name", newUserPosition)
        )
    }

    @MessageMapping("/whiteboard.addUser/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun addUser(
        @DestinationVariable roomId: String,
        userJwt: SerializedJWT,
        headerAccessor: SimpMessageHeaderAccessor
    ): WebSocketEvent {
        headerAccessor.sessionAttributes?.set("userJwt", userJwt.jwtToken)
        headerAccessor.sessionAttributes?.set("roomId", roomId)

        val decodedJwt = JWT.decode(userJwt.jwtToken)
        val username = decodedJwt.claims["username"]?.asString()

        // Update room state
        roomManager.userJoinedRoom(username ?: "no-name", roomId)

        return roomManager.makeRoomEvent(roomId)
    }

    @MessageMapping("/whiteboard.getFullState/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun getFullState(
        @DestinationVariable roomId: String
    ): WebSocketEvent {
        return WebSocketEvent(
            WebSocketEventType.GET_FULL_STATE,
            getFullState = stateManager.getWhiteboard(roomId) ?: WhiteboardState()
        )
    }

    @MessageMapping("/whiteboard.addComponent/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun addComponent(
        @DestinationVariable roomId: String,
        componentState: ComponentState
    ) : WebSocketEvent {
        stateManager.addComponent(roomId, componentState)
        return WebSocketEvent(
            WebSocketEventType.ADD_COMPONENT,
            addComponent = componentState
        )
    }

    @MessageMapping("/whiteboard.deleteComponent/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun deleteComponent(
        @DestinationVariable roomId: String,
        deleteComponent: DeleteComponent
    ) : WebSocketEvent {
        stateManager.deleteComponent(roomId, deleteComponent)
        return WebSocketEvent(
            WebSocketEventType.DELETE_COMPONENT,
            deleteComponent = deleteComponent
        )
    }
}