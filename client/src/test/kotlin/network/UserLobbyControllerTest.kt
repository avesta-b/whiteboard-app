package network

import cs346.whiteboard.client.websocket.UserLobbyController
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference

class UserLobbyControllerTest {

    private lateinit var controller: UserLobbyController

    @Test
    fun `handleUserUpdate updates users in lobby`() {
        controller = UserLobbyController("foobar", WeakReference(null))
        val currentUsers = setOf("user1", "user2", "user3", "foobar")
        controller.handleUserUpdate(currentUsers)
        assertEquals(3, controller.usersInLobby.size)
        assertTrue(controller.usersInLobby.containsAll(listOf("user1", "user3", "user2")))
    }

    @Test
    fun `empty lobby lobby`() {
        controller = UserLobbyController("foobar", WeakReference(null))
        controller.handleUserUpdate(emptySet())
        assert(controller.usersInLobby.isEmpty())
    }

    @Test
    fun `handleUserUpdate removes user who left the lobby`() {
        controller = UserLobbyController("foobar", WeakReference(null))
        controller.handleUserUpdate(setOf("user1", "user2", "user3", "foobar"))
        val currentUsers = setOf("user1", "user2", "user3", "foobar", "java")
        controller.handleUserUpdate(currentUsers)
        assertEquals(4, controller.usersInLobby.size)
        assert(!controller.usersInLobby.contains("foobar"))
    }
}
