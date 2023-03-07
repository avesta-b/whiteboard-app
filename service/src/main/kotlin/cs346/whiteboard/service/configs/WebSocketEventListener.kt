package cs346.whiteboard.service.configs

import com.auth0.jwt.JWT
import cs346.whiteboard.service.services.UserRoomManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent

@Component
class WebSocketEventListener(private val messagingTemplate: SimpMessagingTemplate,
                             @Autowired private val roomManager: UserRoomManager) {

    @EventListener
    fun handleSessionUnsubscribed(event: SessionUnsubscribeEvent) {
        val sessionInfo = StompHeaderAccessor.wrap(event.message)
        val roomId = sessionInfo.sessionAttributes?.get("roomId").toString()
        val userJwt = sessionInfo.sessionAttributes?.get("userJwt").toString()

        val decodedJwt = JWT.decode(userJwt)
        val username: String = decodedJwt.claims["username"]?.asString() ?: "no-name"

        roomManager.userLeftRoom(username, roomId)

        messagingTemplate.convertAndSend(
            "/topic/whiteboard/$roomId",
            roomManager.makeRoomEvent(roomId)
        )
    }
}