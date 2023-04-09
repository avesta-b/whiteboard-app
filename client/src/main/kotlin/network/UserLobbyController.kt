package cs346.whiteboard.client.websocket

import androidx.compose.runtime.mutableStateListOf
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.shared.jsonmodels.SerializedJWT
import java.lang.ref.WeakReference

class UserLobbyController(
    private val username: String,
    private val handler: WeakReference<WebSocketEventHandler>
) {
    var usersInLobby = mutableStateListOf<String>()
        private set
    private val sendSuffix: String = ".addUser"

    fun handleUserUpdate(currentUsers: Set<String>) {
        usersInLobby.clear()
        currentUsers.forEach {
            if (it == username) return@forEach
            usersInLobby.add(it)
        }
    }

    fun addSelfToLobby() {
        handler.get()?.let {
            it.send(
                sendSuffix=sendSuffix,
                body=SerializedJWT(jwtToken = UserManager.jwt ?: ""),
                serializationStrategy = SerializedJWT.serializer()
            )
        }
    }
}