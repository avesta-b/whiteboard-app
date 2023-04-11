/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.websocket

import androidx.compose.runtime.snapshotFlow
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.client.network.BaseUrlProvider
import cs346.whiteboard.client.settings.MenuBarState
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.interaction.ChatController
import cs346.whiteboard.client.whiteboard.interaction.PingController
import cs346.whiteboard.client.whiteboard.overlay.CursorsController
import cs346.whiteboard.shared.jsonmodels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationStrategy
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

/// Routes events to appropriate controllers
/// Provides simple API for controllers to send events to

class WebSocketEventHandler(private val username: String,
                            private val coroutineScope: CoroutineScope,
                            private val roomId: String,
                            private val whiteboardController: WhiteboardController
) {

    private val localbaseUrl: String = "ws://" + BaseUrlProvider.HOST + "/ws"
    private val remoteUrl: String = "wss://" + BaseUrlProvider.HOST + "/ws"
    private var baseUrl: String = if(MenuBarState.isLocal) localbaseUrl else remoteUrl
    private val subscribePath: String = "/topic/whiteboard/${roomId}"
    private var session: StompSession? = null
    private var stompClient: StompClient? = null

    // Objects to route events to
    val cursorsController: CursorsController = CursorsController(username, WeakReference(this))

    val userLobbyController: UserLobbyController = UserLobbyController(username, WeakReference(this))

    val componentEventController: ComponentEventController = ComponentEventController(roomId, WeakReference(this), username)

    val chatController: ChatController = ChatController(username, WeakReference(this))

    val pingController: PingController = PingController(username, WeakReference(this))

    init {
        coroutineScope.launch {
            connect()
        }
        snapshotFlow { MenuBarState.isLocal }
            .onEach {
                if(MenuBarState.isLocal) baseUrl = localbaseUrl
                else baseUrl = remoteUrl
            }
            .launchIn(coroutineScope)
    }

    fun isDrawingAlone(): Boolean {
        return roomId.isEmpty()
    }

    private suspend fun connect() {
        if (isDrawingAlone()) return

        val headers = mapOf(
            "jwt" to (UserManager?.jwt ?: "no-name"),
            "roomId" to roomId.toString()
        )

        val subscribeHeaders = StompSubscribeHeaders(
            destination = subscribePath,
            customHeaders = headers
        )

        if (stompClient == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .pingInterval(10L, TimeUnit.SECONDS)
                .build()


            val wsClient = OkHttpWebSocketClient(okHttpClient)

            stompClient = StompClient(wsClient) {
                connectionTimeout = 10.seconds
                gracefulDisconnect = false
                heartBeat = HeartBeat(10.seconds, 30.seconds)
                heartBeatTolerance = HeartBeatTolerance(5.seconds, 5.seconds)
            }
        }

        try {
            session = stompClient?.connect(baseUrl, customStompConnectHeaders = headers)
        } catch (err: Exception) {
        }

        session?.withJsonConversions()?.let {
            it.use { s->
                try {
                    val messages: Flow<WebSocketEvent> = s.subscribe(subscribeHeaders, WebSocketEvent.serializer())

                    // CALL THIS BEFORE COLLECTING BUT AFTER SUBSCRIBING
                    // UserLobbyController is responsible for adding ourselves to a room
                    userLobbyController.addSelfToLobby()
                    componentEventController.requestFullState()

                    messages.collect { msg ->
                        coroutineScope.launch {
                            routeEvent(msg)
                        }
                    }
                } catch(err: Exception) {
                    /// TODO: CATCH ERROR (MIGHT BE INCORRECT PLACEMENT)
//                    println("GET THIS ERROR: $err")
                }

            }
        }
    }

    fun disconnect() {
        coroutineScope.launch {
            session?.disconnect()
        }
    }

    private suspend fun routeEvent(event: WebSocketEvent) {
        when (event.eventType) {
            WebSocketEventType.UPDATE_ROOM -> {
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
            WebSocketEventType.ADD_COMPONENT -> {
                val newComponent = event.addComponent ?: return
                whiteboardController.addComponent(newComponent)
            }
            WebSocketEventType.DELETE_COMPONENT -> {
                val deleteComponent = event.deleteComponent ?: return
                whiteboardController.deleteComponent(deleteComponent)
            }
            WebSocketEventType.GET_FULL_STATE -> {
                val state: WhiteboardState = event.getFullState ?: return
                whiteboardController.setState(state)
            }
            WebSocketEventType.SEND_MESSAGE -> {
                val chatMessage: ChatMessage = event.chatMessage ?: return
                chatController.receiveMessage(chatMessage)
            }
            WebSocketEventType.UPDATE_COMPONENT -> {
                val componentUpdate = event.updateComponent ?: return
                whiteboardController.applyServerUpdate(componentUpdate)
            }
            WebSocketEventType.SEND_PING -> {
                val ping: Ping = event.ping ?: return
                pingController.receivePing(ping)
            }
        }
    }

    // Send an event to server
    fun <T: Any> send(sendSuffix: String, body: T?, serializationStrategy: SerializationStrategy<T>) {
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