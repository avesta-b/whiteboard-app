package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.RoomUpdate
import cs346.whiteboard.shared.jsonmodels.WebSocketEvent
import cs346.whiteboard.shared.jsonmodels.WebSocketEventType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserRoomManager(
    @Autowired val whiteboardStateManager: WhiteboardStateManagerInterface
) {
    private var rooms: MutableMap<Long, MutableSet<String>> = mutableMapOf()

    fun userJoinedRoom(user: String, roomId: Long) {
        rooms.getOrPut(roomId) { mutableSetOf() }.add(user)
    }

    fun userLeftRoom(user: String, roomId: Long) {
        rooms[roomId]?.remove(user)
        if (rooms[roomId]?.isEmpty() == true) {
            rooms.remove(roomId)

            whiteboardStateManager.persistRoomToDb(roomId)
        }
    }

    private fun getUsersInRoom(roomId: Long) : Set<String> {
        return rooms[roomId] ?: emptySet()
    }

    fun makeRoomEvent(roomId: Long): WebSocketEvent {

        return WebSocketEvent(
            eventType = WebSocketEventType.UPDATE_ROOM,
            roomUpdate = RoomUpdate(getUsersInRoom(roomId))
        )
    }
}