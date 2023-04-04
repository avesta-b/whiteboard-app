package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.shared.jsonmodels.ChatMessage
import kotlinx.serialization.Serializable
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class ChatMessageData(
    val sender: String = "",
    val time: String = "",
    val content: String = "",
    val uuid: String = UUID.randomUUID().toString()
)

class ChatController(
    private val username: String,
    private val handler: WeakReference<WebSocketEventHandler>
) {
    var messages = mutableStateListOf<ChatMessageData>()
    var newMessageReceived by mutableStateOf(false)

    fun receiveMessage(message: ChatMessage) {
        // if we sent the message, we will delete the current instance of message from messages
        if (message.sender == username) return
        val formatter = SimpleDateFormat("HH:mm")
        val currentTime = formatter.format(Calendar.getInstance().time)
        messages.add(ChatMessageData(message.sender, currentTime, message.content))
        newMessageReceived = true
    }

    fun sendMessage(content: String) {
        val message = ChatMessage(sender=username, content=content)
        val formatter = SimpleDateFormat("HH:mm")
        val currentTime = formatter.format(Calendar.getInstance().time)
        messages.add(ChatMessageData(message.sender, currentTime, message.content))
        handler.get()?.let {
            it.send(
                sendSuffix = ".sendMessage",
                body = message,
                serializationStrategy = ChatMessage.serializer()
            )
        }
    }





}