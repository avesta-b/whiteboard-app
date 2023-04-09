package cs346.whiteboard.service.configs

import com.auth0.jwt.JWT
import cs346.whiteboard.service.services.UserRoomManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

data class SessionData(
    val username: String,
    val roomId: Long
)

@Component
class WebSocketEventListener(private val messagingTemplate: SimpMessagingTemplate,
                             @Autowired private val roomManager: UserRoomManager) {


    private val sessionData: MutableMap<String, SessionData> = mutableMapOf()

    @EventListener
    fun handleSessionConnected(event: SessionConnectedEvent) {
        val sessionInfo = StompHeaderAccessor.wrap(event.message)
        val sessionId = sessionInfo.sessionId ?: return
        val connectHeader = sessionInfo.getHeader(StompHeaderAccessor.CONNECT_MESSAGE_HEADER)
                as? GenericMessage<MutableMap<String, String>> ?: return

        val nativeHeader = connectHeader.headers[StompHeaderAccessor.NATIVE_HEADERS] as? Map<String, List<String>>
            ?: return

        val userJwt = nativeHeader["jwt"]?.first()?.toString() ?: return
        val roomId = nativeHeader["roomId"]?.first()?.toString()?.toLong() ?: return

        val decodedJwt = JWT.decode(userJwt)
        val username: String = decodedJwt.claims["username"]?.asString() ?: "no-name"

        sessionData[sessionId] = SessionData(username = username, roomId = roomId)
        roomManager.userJoinedRoom(username, roomId)
    }

    @EventListener
    fun handleSessionDisconnected(event: SessionDisconnectEvent) {
        val sessionInfo = StompHeaderAccessor.wrap(event.message)
        val sessionId = sessionInfo.sessionId ?: return

        val data = sessionData[sessionId] ?: return

        roomManager.userLeftRoom(data.username, data.roomId)

        sessionData.remove(sessionId)

        messagingTemplate.convertAndSend(
            "/topic/whiteboard/${data.roomId}",
            roomManager.makeRoomEvent(data.roomId)
        )
    }

}