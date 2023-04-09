package cs346.whiteboard.client.whiteboard

import androidx.compose.ui.geometry.Offset
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.client.whiteboard.overlay.CursorsController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference

class CursorsControllerTest {

    private lateinit var cursorsController: CursorsController
    private lateinit var webSocketEventHandler: WebSocketEventHandler
    private lateinit var weakEventHandler: WeakReference<WebSocketEventHandler>

    private var testCoroutineScope = TestCoroutineScope()
    private lateinit var whiteboardController: WhiteboardController

    @BeforeEach
    fun setUp() {
        whiteboardController = WhiteboardController(ROOM_NAME, ROOM_ID, testCoroutineScope) {}
        webSocketEventHandler = whiteboardController.webSocketEventHandler
        weakEventHandler = WeakReference(webSocketEventHandler)
        cursorsController = CursorsController("testUsername", weakEventHandler)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHandleCursorMessage() = runTest {
        val newOffset = Offset(10f, 20f)
        val userIdentifier = "friend"

        cursorsController.handleCursorMessage(newOffset, userIdentifier)

        val friendCursorPosition = cursorsController.friendCursorPositions[userIdentifier]
        assertEquals(newOffset, friendCursorPosition?.value)
    }

    @Test
    fun testHandleUsersUpdate() {
        val usersUpdate = setOf("user1", "user2", "testUsername")

        cursorsController.handleUsersUpdate(usersUpdate)

        assertEquals(2, cursorsController.friendCursorPositions.size)
        assert(usersUpdate.containsAll(cursorsController.friendCursorPositions.keys))
    }

    @Test
    fun testUpdateCursor() {
        val newCoordinate = Offset(100f, 200f)

        cursorsController.updateCursor(newCoordinate)

        assertEquals(newCoordinate, cursorsController.ownCursorPosition)
    }
}