package cs346.whiteboard.client.websocket

import cs346.whiteboard.client.BaseUrlProvider
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.client.whiteboard.CursorsController
import cs346.whiteboard.shared.jsonmodels.CursorUpdate
import cs346.whiteboard.shared.jsonmodels.RoomUpdate
import cs346.whiteboard.shared.jsonmodels.WebSocketEvent
import cs346.whiteboard.shared.jsonmodels.WebSocketEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationStrategy
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import java.lang.ref.WeakReference

/// Routes events to appropriate controllers
/// Provides simple API for controllers to send events to

class WebSocketEventHandler(private val username: String,
                            private val coroutineScope: CoroutineScope,
                            private val roomId: String) {

    private val baseUrl: String = "ws://" + BaseUrlProvider.HOST + "/ws"
    private val subscribePath: String = "/topic/whiteboard/${roomId}"
    private var session: StompSession? = null

    // Objects to route events to
    val cursorsController: CursorsController = CursorsController(username, WeakReference(this))

    val userLobbyController: UserLobbyController = UserLobbyController(username, WeakReference(this))

    init {
        coroutineScope.launch {
            connect()
        }
    }

    private suspend fun connect() {
        if (roomId.isEmpty()) return

        val headers = mapOf<String, String>("username" to username)
        val subscribeHeaders = StompSubscribeHeaders(
            destination = subscribePath,
            customHeaders = headers
        )

        session = StompClient(KtorWebSocketClient()).connect(baseUrl)

        // UserLobbyController is responsible for adding ourselves to a room
        userLobbyController.addSelfToLobby()

        session?.withJsonConversions()?.let {
            it.use { s->
                val messages: Flow<WebSocketEvent> = s.subscribe(subscribeHeaders, WebSocketEvent.serializer())
                messages.collect { msg ->
                    coroutineScope.launch {
                        routeEvent(msg)
                    }
                }
            }
        }
    }

    private suspend fun routeEvent(event: WebSocketEvent) {
        when (event.eventType) {
            WebSocketEventType.ROOM_UPDATE -> {
                val update: RoomUpdate = event.roomUpdate ?: return
                userLobbyController.handleUserUpdate(update.users)
                cursorsController.handleUsersUpdate(update.users)
            }

            WebSocketEventType.UPDATE_CURSOR -> {
                val update: CursorUpdate = event.cursorUpdate ?: return
                cursorsController.handleCursorMessage(
                    newOffset = update.position.toOffset(),
                    userIdentifier = update.userIdentifier
                )
            }

            WebSocketEventType.UPDATE_COMPONENT -> {
                // TODO: Handle UPDATE_COMPONENT event
            }
            WebSocketEventType.DRAW_COMPONENT -> {
                // TODO: Handle DRAW_COMPONENT event
            }
            WebSocketEventType.DELETE_COMPONENT -> {
                // TODO: Handle DELETE_COMPONENT event
            }
        }

    }

    // Send an event to server
    fun <T: Any> send(sendSuffix: String, body: T, serializationStrategy: SerializationStrategy<T>) {
        val sendPath = "/app/whiteboard$sendSuffix/$roomId"

        val jwt = UserManager.jwt
        if (jwt == null) {
            println("ERROR: Could not send user position as JWT does not exist in sendPosition()")
            return
        }

        val stompSendHeaders = StompSendHeaders(
            destination = sendPath
        )

        coroutineScope.launch {
            session?.withJsonConversions()?.let {
                it.convertAndSend(
                    headers = stompSendHeaders,
                    body = body,
                    serializer = serializationStrategy
                )
            }
        }
    }
}