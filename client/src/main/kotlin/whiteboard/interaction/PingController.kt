package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.helpers.overlap
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.client.helpers.toText
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.shared.jsonmodels.ChatMessage
import cs346.whiteboard.shared.jsonmodels.EmojiPing
import cs346.whiteboard.shared.jsonmodels.Ping
import cs346.whiteboard.shared.jsonmodels.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*

data class PingWheelData(
    val coordinate: Offset,
    val whiteboardCoordinate: Offset,
    var selectedPing: EmojiPing? = null,
    val menuSize: Size = Size(250f, 250f)
)

data class PingData(
    val coordinate: Offset,
    val ping: EmojiPing,
    var isVisible: MutableTransitionState<Boolean>
)

class PingController(
    private val username: String,
    private val handler: WeakReference<WebSocketEventHandler>
) {
    var pingWheelData by mutableStateOf<PingWheelData?>(null)
        private set

    val pings = mutableStateMapOf<String, PingData>()

    private fun selectedPingAtPosition(position: Offset): EmojiPing? {
        pingWheelData?.let {
            if (overlap(
                    position.minus(Offset(1f, 1f)),
                    Size(1f, 1f),
                    it.coordinate.minus(Offset(it.menuSize.width / 2 + 2f, it.menuSize.height / 2 + 2f)),
                    it.menuSize.div(2f))
            ) {
                return EmojiPing.THUMBS
            } else if (
                overlap(
                    position.minus(Offset(1f, 1f)),
                    Size(1f, 1f),
                    it.coordinate.minus(Offset(0f, it.menuSize.height / 2)),
                    it.menuSize.div(2f))
            ) {
                return EmojiPing.SMILE
            } else if (
                overlap(
                    position.minus(Offset(1f, 1f)),
                    Size(1f, 1f),
                    it.coordinate.minus(Offset(it.menuSize.width / 2, 0f)),
                    it.menuSize.div(2f))
            ) {
                return EmojiPing.SKULL
            } else if (
                overlap(
                    position.minus(Offset(1f, 1f)),
                    Size(1f, 1f),
                    it.coordinate,
                    it.menuSize.div(2f))
            ) {
                return EmojiPing.THINK
            }
            return null
        }
        return null
    }

    fun startPingMenu(initialPosition: Offset, initialWhiteboardPosition: Offset) {
        pingWheelData = PingWheelData(initialPosition, initialWhiteboardPosition)
    }

    fun updatePing(newPosition: Offset) {
        pingWheelData?.let {
            pingWheelData = PingWheelData(
                it.coordinate,
                it.whiteboardCoordinate,
                selectedPingAtPosition(newPosition)
            )
        }
    }

    fun clearPingWheel() {
        pingWheelData = null
    }

    fun sendPingIfNeeded() {
        pingWheelData?.let { data ->
            data.selectedPing?.let { selectedPing ->
                val pingUUID = UUID.randomUUID().toString()
                showPing(
                    pingUUID,
                    PingData(
                        data.whiteboardCoordinate,
                        selectedPing,
                        MutableTransitionState(false)
                    )
                )
                handler.get()?.let {
                    val ping = Ping(
                        username,
                        selectedPing,
                        Position(data.whiteboardCoordinate.x, data.whiteboardCoordinate.y),
                        pingUUID
                    )
                    it.send(
                        sendSuffix = ".sendPing",
                        body = ping,
                        serializationStrategy = Ping.serializer()
                    )
                }
            }
        }
        clearPingWheel()
    }

    fun receivePing(ping: Ping) {
        if (ping.sender == username) return
        showPing(
            ping.uuid,
            PingData(
                ping.position.toOffset(),
                ping.emojiPing,
                MutableTransitionState(false)
            )
        )
    }

    private fun showPing(id: String, data: PingData) {
        pings[id] = data
        CoroutineScope(Dispatchers.Default).launch {
            delay(50)
            pings[id]?.isVisible?.targetState = true
            delay(2000)
            pings[id]?.isVisible?.targetState = false
        }
    }
}