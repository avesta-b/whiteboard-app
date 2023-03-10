package cs346.whiteboard.service.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRoomManagerTest {
    private lateinit var userRoomManager: UserRoomManager

    @BeforeEach
    fun setUp() {
        userRoomManager = UserRoomManager()
    }

    @Test
    fun `userJoinedRoom adds user to room`() {
        userRoomManager.userJoinedRoom("Alice", "Room 1")
        assertEquals(setOf("Alice"), userRoomManager.makeRoomEvent("Room 1").roomUpdate!!.users)
    }

    @Test
    fun `userJoinedRoom adds multiple users to same room`() {
        userRoomManager.userJoinedRoom("Alice", "Room 1")
        userRoomManager.userJoinedRoom("Bob", "Room 1")
        assertEquals(setOf("Alice", "Bob"), userRoomManager.makeRoomEvent("Room 1").roomUpdate!!.users)
    }

    @Test
    fun `userJoinedRoom adds users to different rooms`() {
        userRoomManager.userJoinedRoom("Alice", "Room 1")
        userRoomManager.userJoinedRoom("Bob", "Room 2")
        assert(setOf("Alice") != userRoomManager.makeRoomEvent("Room 2").roomUpdate!!.users)
        assert(setOf("Bob") != userRoomManager.makeRoomEvent("Room 1").roomUpdate!!.users)
    }

    @Test
    fun `userLeftRoom removes user from room`() {
        userRoomManager.userJoinedRoom("Alice", "Room 1")
        userRoomManager.userLeftRoom("Alice", "Room 1")
        assertEquals(emptySet<String>(), userRoomManager.makeRoomEvent("Room 1").roomUpdate!!.users)
    }

    @Test
    fun `userLeftRoom removes room if empty`() {
        userRoomManager.userJoinedRoom("Alice", "Room 1")
        userRoomManager.userLeftRoom("Alice", "Room 1")
        assert(userRoomManager.makeRoomEvent("Room 1").roomUpdate!!.users.isEmpty())
    }
}
