package cs346.whiteboard.shared.jsonmodels

import kotlinx.serialization.Serializable

@Serializable
data class Position(val x: Float, val y: Float) {
    constructor() : this(0f, 0f)
}

enum class WebSocketEventType(val value: String) {
    ROOM_UPDATE("USER_UPDATE"),
    UPDATE_CURSOR("CURSOR_UPDATE"),
    UPDATE_COMPONENT("COMPONENT_UPDATE"),
    DRAW_COMPONENT("DRAW_COMPONENT"),
    DELETE_COMPONENT("DELETE_COMPONENT")
}

@Serializable
data class CursorUpdate(val userIdentifier: String, val position: Position)

@Serializable
data class RoomUpdate(val users: Set<String>)

// Every event should have a user associated with it and an event type
@Serializable
data class WebSocketEvent(val eventType: WebSocketEventType,
                          val cursorUpdate: CursorUpdate? = null,
                          val roomUpdate: RoomUpdate? = null)