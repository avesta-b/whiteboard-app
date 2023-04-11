/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

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
    UPDATE_COMPONENT("UPDATE_COMPONENT"),
    DELETE_COMPONENT("DELETE_COMPONENT"),
    GET_FULL_STATE("GET_FULL_STATE"),
    SEND_MESSAGE("SEND_MESSAGE"),
    SEND_PING("SEND_PING")
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
    SHAPE("SHAPE"),
    AI_IMAGE("AI_IMAGE")
}

@Serializable
enum class ComponentColor {
    BLACK, RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, WHITE
}

@Serializable
enum class PathType {
    BRUSH, HIGHLIGHTER, PAINT
}

@Serializable
enum class PathThickness {
    THIN, THICK, EXTRA_THICK
}

@Serializable
enum class ShapeType {
    SQUARE, RECTANGLE, TRIANGLE, CIRCLE
}

@Serializable
enum class ShapeFill {
    FILL, OUTLINE
}

@Serializable
enum class TextFont {
    DEFAULT, COMIC, MONO
}

@Serializable
enum class TextSize {
    SMALL, MEDIUM, LARGE
}

@Serializable
enum class AccessLevel {
    LOCKED, UNLOCKED
}

@Serializable
data class AIImageData(
    val prompt: String? = null,
    val url: String? = null
)

@Serializable
data class ComponentState(
    // Shared
    var uuid: String = "",
    var componentType: ComponentType = ComponentType.SHAPE,
    var size: Size = Size(),
    var position: Position = Position(),
    var depth: Float = 0f,
    var color: ComponentColor = ComponentColor.BLACK,
    var owner: String = "",
    var accessLevel: AccessLevel = AccessLevel.UNLOCKED,
    // Path component
    var path: List<Position>? = null,
    var pathType: PathType? = null,
    var pathThickness: PathThickness? = null,
    // Shape component
    var shapeType: ShapeType? = null,
    var shapeFill: ShapeFill? = null,
    // Textbox component
    var text: String? = null,
    var textFont: TextFont? = null,
    var textSize: TextSize? = null,
    // AI Image component
    var imageData: AIImageData? = null
)

@Serializable
data class ComponentUpdate(
    var username: String? = null,
    val uuid: String = "", // UUID of component being updated
    val updateUUID: String = UUID.randomUUID().toString(), // UUID corresponding to the update event
    val size: Size? = null,
    val position: Position? = null,
    val color: ComponentColor? = null,
    var accessLevel: AccessLevel? = null,
    // Path component
    val path: List<Position>? = null,
    val pathType: PathType? = null,
    val pathThickness: PathThickness? = null,
    // Shape component
    val shapeType: ShapeType? = null,
    val shapeFill: ShapeFill? = null,
    // Textbox component
    val text: String? = null,
    val textFont: TextFont? = null,
    val textSize: TextSize? = null,
    // AI Image component
    val imageData: AIImageData? = null
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
    val updateComponent: ComponentUpdate? = null,
    val deleteComponent: DeleteComponent? = null,
    val getFullState: WhiteboardState? = null,
    val chatMessage: ChatMessage? = null,
    val ping: Ping? = null
)

@Serializable
data class DeleteComponent(
    val uuid: String = "",
    val username: String? = null
)

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

@Serializable
enum class EmojiPing {
    THUMBS, SMILE, SKULL, THINK
}

@Serializable
data class Ping(
    val sender: String = "",
    val emojiPing: EmojiPing = EmojiPing.THUMBS,
    val position: Position = Position(0f, 0f),
    val uuid: String = UUID.randomUUID().toString()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ping) return false
        return uuid == other.uuid
    }
}
