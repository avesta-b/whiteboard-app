package cs346.whiteboard.client.whiteboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import cs346.whiteboard.client.BaseUrlProvider
import cs346.whiteboard.client.helpers.getResource
import cs346.whiteboard.shared.jsonmodels.CursorPosition
import cs346.whiteboard.shared.jsonmodels.CursorPositionUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import java.awt.Cursor
import java.awt.Point
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO

enum class CursorType {
    POINTER {
        override fun fileName(): String = ""
    },
    HAND {
        override fun fileName(): String = "hand.png"
    },
    GRAB {
        override fun fileName(): String = "grab.png"
    },
    BRUSH {
        override fun fileName(): String = "brush.png"
        override fun point(): Point = Point(0, 18)
    },
    HIGHLIGHTER {
        override fun fileName(): String = "highlighter.png"
    },
    SHAPE {
        override fun fileName(): String = "shape.png"
    },
    TEXTFIELD {
        override fun fileName(): String = "textfield.png"
    };

    abstract fun fileName(): String
    open fun point(): Point {
        return Point(12, 12)
    }
}

class CursorsController(
    private val username: String,
    private val coroutineScope: CoroutineScope,
    private val roomId: String
    ) {
    var ownCursorPosition: Offset = Offset.Zero
    val friendCursorPositions = mutableStateMapOf<String, Animatable<Offset, AnimationVector2D>>()

    private val baseUrl: String = "ws://" + BaseUrlProvider.HOST + "/ws"
    private val sendPath: String = "/app/whiteboard/${roomId}"
    private val subscribePath: String = "/topic/whiteboard/${roomId}"

    private var session: StompSession? = null

    var currentCursor by mutableStateOf(CursorType.POINTER)

    init {
        coroutineScope.launch {
            connect()
        }
    }

    private suspend fun connect() {
        if (roomId.isEmpty()) return

        session = StompClient(KtorWebSocketClient()).connect(baseUrl)

        session?.withJsonConversions()?.let {
            it.convertAndSend(sendPath,
                CursorPositionUpdate(username, CursorPosition(ownCursorPosition.x, ownCursorPosition.y)),
                CursorPositionUpdate.serializer())

            it.use { s->
                val messages: Flow<CursorPositionUpdate> = s.subscribe(subscribePath,
                    CursorPositionUpdate.serializer())

                messages.collect { msg ->
                    if (msg.userIdentifier != username) {
                        coroutineScope.launch {
                            val newOffset = Offset(msg.userCursorPosition.x, msg.userCursorPosition.y)
                            if (!friendCursorPositions.containsKey(msg.userIdentifier)) {
                                friendCursorPositions[msg.userIdentifier] =
                                    Animatable(newOffset, Offset.VectorConverter)
                            } else {
                                friendCursorPositions[msg.userIdentifier]?.animateTo(newOffset)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun updateCursor(newCoordinate: Offset) {
        ownCursorPosition = newCoordinate
        session?.withJsonConversions()?.let {
            it.convertAndSend(sendPath,
                CursorPositionUpdate(username, CursorPosition(ownCursorPosition.x, ownCursorPosition.y)),
                CursorPositionUpdate.serializer())
        }
    }

    fun getCurrentCursor(): Cursor {
        if (currentCursor == CursorType.POINTER) return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        val cursorImage = ImageIO.read(getResource("/cursors/${currentCursor.fileName()}"))
        return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, currentCursor.point(), "cursor")
    }
}