package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.RoomUpdate
import cs346.whiteboard.shared.jsonmodels.WebSocketEvent
import cs346.whiteboard.shared.jsonmodels.WebSocketEventType
import org.springframework.stereotype.Component

@Component
class UserRoomManager {
    private var rooms: MutableMap<String, MutableSet<String>> = mutableMapOf()

    fun userJoinedRoom(user: String, roomId: String) {
        rooms.getOrPut(roomId) { mutableSetOf() }.add(user)
    }

    fun userLeftRoom(user: String, roomId: String) {
        rooms[roomId]?.remove(user)
        if (rooms[roomId]?.isEmpty() == true) rooms.remove(roomId)
    }

    private fun getUsersInRoom(roomId: String) : Set<String> {
        return rooms[roomId] ?: emptySet()
    }

    fun makeRoomEvent(roomId: String): WebSocketEvent {

        return WebSocketEvent(
            eventType = WebSocketEventType.ROOM_UPDATE,
            roomUpdate = RoomUpdate(getUsersInRoom(roomId))
        )
    }
}