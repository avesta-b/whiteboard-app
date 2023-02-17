package cs346.whiteboard.shared.jsonmodels

import kotlinx.serialization.Serializable

@Serializable
data class CursorPosition(val x: Float, val y: Float) {
    constructor() : this(0f, 0f)
}

@Serializable
data class CursorPositionUpdate(val userIdentifier: String, val userCursorPosition: CursorPosition) {
    constructor() : this("", CursorPosition())
}