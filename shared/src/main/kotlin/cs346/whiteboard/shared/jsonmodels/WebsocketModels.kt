package cs346.whiteboard.shared.jsonmodels

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Position(val x: Float = 0f, val y: Float = 0f)

@Serializable
enum class WebSocketEventType(val value: String) {
    UPDATE_ROOM("UPDATE_ROOM"),
    UPDATE_CURSOR("CURSOR_UPDATE"),
    ADD_COMPONENT("COMPONENT_UPDATE"),
    DELETE_COMPONENT("DELETE_COMPONENT"),
    GET_FULL_STATE("GET_FULL_STATE"),
    SEND_MESSAGE("SEND_MESSAGE"),
}

@Serializable
data class CursorUpdate(val userIdentifier: String, val position: Position)

@Serializable
data class RoomUpdate(val users: Set<String>)

@Serializable
data class Size(val width: Float = 0f, val height: Float = 0f)

@Serializable
enum class ComponentType(val value: String) {
    TEXT_BOX("TEXT_BOX"),
    PATH("PATH"),
    SQUARE("SQUARE"),
    CIRCLE("CIRCLE")
}

@Serializable
data class ComponentState(
    var uuid: String = "",
    var componentType: ComponentType = ComponentType.SQUARE,
    var size: Size = Size(),
    var position: Position = Position(),
    var depth: Float = 0f,
    var path: List<Position>? = null, // exists for path component
    var text: String? = null // exists for text box
)

@Serializable
data class WhiteboardState(
    var components: MutableMap<String, ComponentState> = mutableMapOf()
)

// Every event should have a user associated with it and an event type
@Serializable
data class WebSocketEvent(
    val eventType: WebSocketEventType,
    val cursorUpdate: CursorUpdate? = null,
    val roomUpdate: RoomUpdate? = null,
    val addComponent: ComponentState? = null,
    val deleteComponent: DeleteComponent? = null,
    val getFullState: WhiteboardState? = null,
    val chatMessage: ChatMessage? = null
)

@Serializable
data class DeleteComponent(val uuid: String = "")

@Serializable
data class ChatMessage(
    val sender: String = "",
    val content: String = "",
    val uuid: String = UUID.randomUUID().toString()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatMessage) return false
        return uuid == other.uuid
    }
}