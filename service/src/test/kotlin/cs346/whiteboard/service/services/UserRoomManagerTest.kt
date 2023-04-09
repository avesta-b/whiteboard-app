package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.ComponentUpdate
import cs346.whiteboard.shared.jsonmodels.DeleteComponent
import cs346.whiteboard.shared.jsonmodels.WhiteboardState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// dummy class for test
class WhiteboardStateManagerMock : WhiteboardStateManagerInterface {
    override fun persistRoomToDb(roomId: Long) {
    }

    override fun getWhiteboard(roomId: Long): WhiteboardState {
        return WhiteboardState()
    }

    override fun addComponent(roomId: Long, component: ComponentState) {
    }

    override fun deleteComponent(roomId: Long, deleteComponent: DeleteComponent): Boolean {
        return true
    }

    override fun updateComponent(roomId: Long, update: ComponentUpdate): Boolean {
        return true
    }

}

class UserRoomManagerTest {
    private lateinit var userRoomManager: UserRoomManager

    @BeforeEach
    fun setUp() {
        userRoomManager = UserRoomManager(WhiteboardStateManagerMock())
    }

    @Test
    fun `userJoinedRoom adds user to room`() {
        userRoomManager.userJoinedRoom("Alice", 1)
        assertEquals(setOf("Alice"), userRoomManager.makeRoomEvent(1).roomUpdate!!.users)
    }

    @Test
    fun `userJoinedRoom adds multiple users to same room`() {
        userRoomManager.userJoinedRoom("Alice", 1)
        userRoomManager.userJoinedRoom("Bob", 1)
        assertEquals(setOf("Alice", "Bob"), userRoomManager.makeRoomEvent(1).roomUpdate!!.users)
    }

    @Test
    fun `userJoinedRoom adds users to different rooms`() {
        userRoomManager.userJoinedRoom("Alice", 1)
        userRoomManager.userJoinedRoom("Bob", 2)
        assert(setOf("Alice") != userRoomManager.makeRoomEvent(2).roomUpdate!!.users)
        assert(setOf("Bob") != userRoomManager.makeRoomEvent(1).roomUpdate!!.users)
    }

    @Test
    fun `userLeftRoom removes user from room`() {
        userRoomManager.userJoinedRoom("Alice", 1)
        userRoomManager.userLeftRoom("Alice", 1)
        assertEquals(emptySet<String>(), userRoomManager.makeRoomEvent(1).roomUpdate!!.users)
    }

    @Test
    fun `userLeftRoom removes room if empty`() {
        userRoomManager.userJoinedRoom("Alice", 1)
        userRoomManager.userLeftRoom("Alice", 1)
        assert(userRoomManager.makeRoomEvent(1).roomUpdate!!.users.isEmpty())
    }
}
