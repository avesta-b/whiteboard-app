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
        @DestinationVariable roomId: Long,
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
        @DestinationVariable roomId: Long,
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
        @DestinationVariable roomId: Long
    ): WebSocketEvent {
        return WebSocketEvent(
            WebSocketEventType.GET_FULL_STATE,
            getFullState = stateManager.getWhiteboard(roomId) ?: WhiteboardState()
        )
    }

    @MessageMapping("/whiteboard.addComponent/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun addComponent(
        @DestinationVariable roomId: Long,
        componentState: ComponentState
    ) : WebSocketEvent {
        stateManager.addComponent(roomId, componentState)
        return WebSocketEvent(
            WebSocketEventType.ADD_COMPONENT,
            addComponent = componentState
        )
    }

    @MessageMapping("/whiteboard.updateComponent/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun updateComponent(
        @DestinationVariable roomId: Long,
        componentUpdate: ComponentUpdate
    ) : WebSocketEvent {
        val success = stateManager.updateComponent(roomId, componentUpdate)
        return WebSocketEvent(
            WebSocketEventType.UPDATE_COMPONENT,
            updateComponent = if (success) componentUpdate else null
        )
    }

    @MessageMapping("/whiteboard.deleteComponent/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun deleteComponent(
        @DestinationVariable roomId: Long,
        deleteComponent: DeleteComponent
    ) : WebSocketEvent {
        val success = stateManager.deleteComponent(roomId, deleteComponent)
        return WebSocketEvent(
            WebSocketEventType.DELETE_COMPONENT,
            deleteComponent = if (success) deleteComponent else null
        )
    }

    @MessageMapping("/whiteboard.sendMessage/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun sendMessage(
        @DestinationVariable roomId: Long,
        chatMessage: ChatMessage,
        headerAccessor: SimpMessageHeaderAccessor
    ) : WebSocketEvent {
        val userJwt = headerAccessor.sessionAttributes?.get("userJwt").toString()

        val decodedJwt = JWT.decode(userJwt)
        val username = decodedJwt.claims["username"]?.asString()

        return WebSocketEvent(
            eventType = WebSocketEventType.SEND_MESSAGE,
            chatMessage = if (username == chatMessage.sender || chatMessage.content.isNotEmpty()) { chatMessage } else { null }
        )
    }

    @MessageMapping("/whiteboard.sendPing/{roomId}")
    @SendTo("/topic/whiteboard/{roomId}")
    fun sendPing(
        @DestinationVariable roomId: Long,
        ping: Ping,
        headerAccessor: SimpMessageHeaderAccessor
    ) : WebSocketEvent {
        val userJwt = headerAccessor.sessionAttributes?.get("userJwt").toString()

        val decodedJwt = JWT.decode(userJwt)
        val username = decodedJwt.claims["username"]?.asString()

        return WebSocketEvent(
            eventType = WebSocketEventType.SEND_PING,
            ping = if (username == ping.sender) { ping } else { null }
        )
    }
}