package cs346.whiteboard.client.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.BaseUrlProvider
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.components.CursorUserNameText
import cs346.whiteboard.shared.jsonmodels.CursorPosition
import cs346.whiteboard.shared.jsonmodels.CursorPositionUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import kotlin.math.abs
import kotlin.math.roundToInt

fun getUsernameColor(inputString: String): Color {
    // Hash the input string to generate a unique number
    val hash = abs(inputString.hashCode())

    // Calculate the RGB values of the color
    val red = hash % 256
    val green = (hash / 256) % 256
    val blue = (hash / 65536) % 256

    // Create a new Color object with the calculated RGB values
    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f)
}

internal class CursorViewModel(private var cursorPosition: CursorPosition,
                               private val userName: String,
                               private val scope: CoroutineScope,
                               private val roomId: String
) {
    // Im a bit stupid so idk what the keyword `by` does but it makes otherCursorPositions of type CursorPosition
    var otherCursorPositions = mutableStateMapOf<String, Animatable<Offset, AnimationVector2D>>()

    private val baseUrl: String = "ws://" + BaseUrlProvider.HOST + "/ws"
    private val sendPath: String = "/app/whiteboard/${roomId}"
    private val subscribePath: String = "/topic/whiteboard/${roomId}"

    private var session: StompSession? = null

    // TODO: Pass in actual params
    constructor(user: String, ctx: CoroutineScope, room: String) : this(
        CursorPosition(-100000f, -100000f),
        user,
        ctx,
        room
    )

    init {
        scope.launch {
            connect()
        }
    }

    private suspend fun connect() {
        session = StompClient(KtorWebSocketClient()).connect(baseUrl)

        session?.withJsonConversions()?.let {
            it.convertAndSend(sendPath,
                CursorPositionUpdate(userName, cursorPosition),
                CursorPositionUpdate.serializer())

            it.use { s->
                val messages: Flow<CursorPositionUpdate> = s.subscribe(subscribePath,
                    CursorPositionUpdate.serializer())

                messages.collect { msg ->
                    if (msg.userIdentifier != userName) {
                        scope.launch {
                            val newOffset = Offset(msg.userCursorPosition.x, msg.userCursorPosition.y)
                            if (!otherCursorPositions.containsKey(msg.userIdentifier)) {
                                otherCursorPositions[msg.userIdentifier] =
                                    Animatable(newOffset, Offset.VectorConverter)
                            } else {
                                otherCursorPositions[msg.userIdentifier]?.animateTo(newOffset)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun updateCursor(newPosition: CursorPosition) {
        cursorPosition = newPosition

        session?.withJsonConversions()?.let {
            it.convertAndSend(sendPath,
                CursorPositionUpdate(userName, cursorPosition),
                CursorPositionUpdate.serializer())
        }
        if (!this.otherCursorPositions.containsKey(userName)) {
            this.otherCursorPositions[userName] = Animatable(Offset(newPosition.x, newPosition.y), Offset.VectorConverter)
        } else {
            this.otherCursorPositions[userName] = Animatable(Offset(newPosition.x, newPosition.y), Offset.VectorConverter)
        }
    }
}

// TODO: Use non-experimental
@OptIn(ExperimentalTextApi::class)
@Composable
fun CursorView(modifier: Modifier, roomId: String) {
    val coroutineScope = rememberCoroutineScope()
    var model = remember { CursorViewModel(UserManager.getUsername() ?: "A", coroutineScope, roomId) }
    // Draw circles for each item in positions map
    Box(modifier.pointerInput(Unit) {
        coroutineScope {
            while (true) {
                val position = awaitPointerEventScope {
                    val offset = awaitPointerEvent(PointerEventPass.Initial).changes.first().position
                    CursorPosition(offset.x, offset.y)
                }
                launch {
                    model.updateCursor(position)
                }
            }
        }
    }) {
        model.otherCursorPositions.forEach { (id, offset) ->
            Column(Modifier.size(300.dp).offset{offset.value.toIntOffset()}) {
                Box(Modifier.size(15.dp).clip(CircleShape).background(getUsernameColor(id)))
                CursorUserNameText(id, getUsernameColor(id))
            }
        }
    }
}

private fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
