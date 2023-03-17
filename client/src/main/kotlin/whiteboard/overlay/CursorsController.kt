package cs346.whiteboard.client.whiteboard.overlay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.helpers.getResource
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.shared.jsonmodels.Position
import java.awt.Cursor
import java.awt.Point
import java.awt.Toolkit
import java.lang.ref.WeakReference
import javax.imageio.ImageIO

enum class CursorType {
    POINTER {
        override fun icon(): CustomIcon = CustomIcon.POINTER
    },
    HAND {
        override fun icon(): CustomIcon = CustomIcon.HAND
    },
    GRAB {
        override fun icon(): CustomIcon = CustomIcon.GRAB
    },
    RESIZE_LEFT {
        override fun icon(): CustomIcon = CustomIcon.RESIZE_LEFT
    },
    RESIZE_RIGHT {
        override fun icon(): CustomIcon = CustomIcon.RESIZE_RIGHT
    },
    BRUSH {
        override fun icon(): CustomIcon = CustomIcon.BRUSH
        override fun point(): Point = Point(0, 18)
    },
    HIGHLIGHTER {
        override fun icon(): CustomIcon = CustomIcon.HIGHLIGHTER
    },
    SHAPE {
        override fun icon(): CustomIcon = CustomIcon.SHAPE
    },
    TEXTFIELD {
        override fun icon(): CustomIcon = CustomIcon.TEXTFIELD
    },
    ERASER {
        override fun icon(): CustomIcon = CustomIcon.ERASER
        override fun point(): Point = Point(0, 18)
    };

    abstract fun icon(): CustomIcon
    open fun point(): Point {
        return Point(12, 12)
    }


}

class CursorsController(
    private val username: String,
    private val handler: WeakReference<WebSocketEventHandler>
) {

    private var ownCursorPosition: Offset = Offset.Zero
    val friendCursorPositions = mutableStateMapOf<String, Animatable<Offset, AnimationVector2D>>()

    private val sendSuffix: String = ".updateCursor"

    var currentCursor by mutableStateOf(CursorType.POINTER)

    suspend fun handleCursorMessage(newOffset: Offset, userIdentifier: String) {
        // If the message was about the user's own cursor, we ignore
        if (userIdentifier == username) return

        if (!friendCursorPositions.containsKey(userIdentifier)) {
            friendCursorPositions[userIdentifier] =
                Animatable(newOffset, Offset.VectorConverter)
        } else {
            friendCursorPositions[userIdentifier]?.animateTo(newOffset)
        }
    }

    fun handleUsersUpdate(usersUpdate: Set<String>) {
        friendCursorPositions.keys.removeAll { it !in usersUpdate }
        usersUpdate.forEach { username ->
            if (username == this.username || friendCursorPositions.containsKey(username)) return@forEach
                friendCursorPositions[username] = Animatable(Offset(0f, 0f), Offset.VectorConverter)
        }
    }


    fun updateCursor(newCoordinate: Offset) {
        ownCursorPosition = newCoordinate
        handler.get()?.let {
            val body = Position(ownCursorPosition.x, ownCursorPosition.y)
            it.send(sendSuffix, body = body, serializationStrategy = Position.serializer())
        }
    }

    fun getCurrentCursor(): Cursor {
        return Toolkit.getDefaultToolkit().createCustomCursor(currentCursor.icon().image(), currentCursor.point(), "cursor")
    }
}