package cs346.whiteboard.client.whiteboardmanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.BaseUrlProvider
import cs346.whiteboard.client.MenuBarState
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.WhiteboardService
import cs346.whiteboard.client.ui.OwnedWhiteboardButton
import cs346.whiteboard.client.ui.SecondaryBodyText
import cs346.whiteboard.client.ui.SharedWhiteboardButton
import cs346.whiteboard.shared.jsonmodels.FetchWhiteboardsResponse
import cs346.whiteboard.shared.jsonmodels.WhiteboardCreationRequest
import cs346.whiteboard.shared.jsonmodels.WhiteboardItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class MyWhiteboardManager(private val coroutineScope: CoroutineScope) {
    var myWhiteboards = mutableStateListOf<WhiteboardItem>()

    var sharedWhiteboards = mutableStateListOf<WhiteboardItem>()

    // No need to make this a state variable as we update the TestUiState
    var selectedWhiteboardItem: WhiteboardItem? = null


    private val localbaseUrl: String = "ws://" + BaseUrlProvider.HOST + "/ws"
    private val remoteUrl: String = "wss://" + BaseUrlProvider.HOST + "/ws"
    private var baseUrl: String = if(MenuBarState.isLocal) localbaseUrl else remoteUrl
    private val subscribePath: String = "/topic/share/${UserManager.getUsername() ?: "temp_user"}"
    private var session: StompSession? = null
    private var stompClient: StompClient? = null

    init {
        coroutineScope.launch {
            getAllWhiteboards()
            connect()
        }
    }

    fun onLaunch() {
        coroutineScope.launch {
            getAllWhiteboards()
            connect()
        }
    }

    fun onDisconnect() {
        coroutineScope.launch {
            session?.disconnect()
        }
    }

    private fun setMyWhiteboardState(state: FetchWhiteboardsResponse) {
        myWhiteboards.clear()
        state.whiteboards.forEach {
            myWhiteboards.add(it)
        }
    }

    private fun setSharedState(state: FetchWhiteboardsResponse) {
        sharedWhiteboards.clear()
        state.whiteboards.forEach {
            sharedWhiteboards.add(it)
        }
    }

    // MARK: HTTP

    suspend fun createNewWhiteboard(whiteboardName: String) {
        UserManager.jwt?.let {
            try {
                val requestBody = Json.encodeToString(WhiteboardCreationRequest(whiteboardName))
                val responseBody = WhiteboardService.postRequest(
                    path="api/user/${UserManager.getUsername() ?: ""}/whiteboards",
                    body = requestBody,
                    token = it)
                try {
                    val a: FetchWhiteboardsResponse = Json.decodeFromString(responseBody)
                    setMyWhiteboardState(a)
                } catch (_: Exception) {
                }
            } catch (_: Exception) {

            }
        }

    }

    suspend fun getAllWhiteboards() {
        UserManager.jwt?.let {
            try {
                val responseBody = WhiteboardService.getRequest(
                    path="api/user/${UserManager.getUsername() ?: ""}/whiteboards",
                    token = it
                )
                try {
                    val response: FetchWhiteboardsResponse = Json.decodeFromString(responseBody)
                    setMyWhiteboardState(response)
                } catch (_: Exception) {
                    null
                }
            } catch (_: Exception) {
                null
            }
        }

    }

    // MARK: - WEBSOCKET

    private suspend fun connect() {

        val headers = mapOf<String, String>("username" to (UserManager.getUsername() ?: ""))
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
            }
        }

        try {
            session = stompClient?.connect(baseUrl)
        } catch (err: Exception) {
            /// TODO: CATCH ERROR
        }

        session?.withJsonConversions()?.let {
            it.use { s->
                try {
                    val messages: Flow<FetchWhiteboardsResponse> = s.subscribe(subscribeHeaders, FetchWhiteboardsResponse.serializer())

                    // CALL THIS BEFORE COLLECTING BUT AFTER SUBSCRIBING
                    // UserLobbyController is responsible for adding ourselves to a room
                    sendMessageForSharedWhiteboards()
                    messages.collect { msg ->
                        coroutineScope.launch {
                            setSharedState(msg)
                        }
                    }
                } catch(err: Exception) {
                    /// TODO: CATCH ERROR (MIGHT BE INCORRECT PLACEMENT)
                }

            }
        }
    }

    suspend fun sendMessageForSharedWhiteboards() {
        val sendPath = "/app/sharing.getAll/${UserManager.getUsername() ?: ""}"

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
                    body = null,
                    serializer = WhiteboardItem.serializer()
                )
            }
        }
    }

    // MARK: - UI

    @Composable
    fun OwnWhiteboardList(onClick: () -> Unit) {
        if (myWhiteboards.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                SecondaryBodyText("No Whiteboards Yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Put switch here to swap between shared whiteboards and owned whiteboards
                items(myWhiteboards) {
                    OwnedWhiteboardButton(
                        it,
                        Modifier.width(600.dp).height(100.dp).padding(10.dp)
                    ) {
                        selectedWhiteboardItem = it
                        onClick.invoke()
                    }
                }
            }
        }
    }


    @Composable
    fun SharedWhiteboardList(onClick: () -> Unit) {
        if (sharedWhiteboards.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SecondaryBodyText("No Whiteboards Shared")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Put switch here to swap between shared whiteboards and owned whiteboards
                items(sharedWhiteboards) {
                    SharedWhiteboardButton(
                        it,
                        Modifier.width(600.dp).height(100.dp).padding(10.dp)
                    ) {
                        selectedWhiteboardItem = it
                        onClick.invoke()
                    }
                }

            }
        }
    }


}