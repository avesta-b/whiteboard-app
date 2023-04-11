/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.shared.jsonmodels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class WebsocketModelsTest {
    @Test
    fun testPosition() {
        val position1 = Position(1f, 2f)
        val position2 = Position(1f, 2f)
        val position3 = Position(3f, 4f)

        assertEquals(position1, position2)
        assertNotEquals(position1, position3)
    }

    @Test
    fun testWebSocketEventType() {
        assertEquals(WebSocketEventType.UPDATE_ROOM.value, "UPDATE_ROOM")
        assertEquals(WebSocketEventType.UPDATE_CURSOR.value, "CURSOR_UPDATE")
    }

    @Test
    fun testCursorUpdate() {
        val cursorUpdate1 = CursorUpdate("user1", Position(1f, 2f))
        val cursorUpdate2 = CursorUpdate("user1", Position(1f, 2f))
        val cursorUpdate3 = CursorUpdate("user2", Position(3f, 4f))

        assertEquals(cursorUpdate1, cursorUpdate2)
        assertNotEquals(cursorUpdate1, cursorUpdate3)
    }

    @Test
    fun testChatMessage() {
        val chatMessage1 = ChatMessage("user1", "Hello")
        val chatMessage2 = ChatMessage("user1", "Hello")
        val chatMessage3 = ChatMessage("user2", "Goodbye")

        assertEquals(chatMessage1.content, chatMessage2.content)
        assertEquals(chatMessage1.sender, chatMessage2.sender)
        assertNotEquals(chatMessage1.content, chatMessage3.content)
        assertNotEquals(chatMessage1.sender, chatMessage3.sender)
    }

    @Test
    fun testPing() {
        val ping1 = Ping("user1", EmojiPing.THUMBS, Position(1f, 2f))
        val ping2 = Ping("user1", EmojiPing.THUMBS, Position(1f, 2f))
        val ping3 = Ping("user2", EmojiPing.SMILE, Position(3f, 4f))

        assertEquals(ping1.sender, ping2.sender)
        assertEquals(ping1.emojiPing, ping2.emojiPing)
        assertEquals(ping1.position.x, ping2.position.x)
        assertEquals(ping1.position.y, ping2.position.y)
        assertNotEquals(ping1.sender, ping3.sender)
        assertNotEquals(ping1.emojiPing, ping3.emojiPing)
        assertNotEquals(ping1.position.x, ping3.position.x)
        assertNotEquals(ping1.position.y, ping3.position.y)
    }

    @Test
    fun testRoomUpdate() {
        val roomUpdate1 = RoomUpdate(setOf("user1", "user2"))
        val roomUpdate2 = RoomUpdate(setOf("user1", "user2"))
        val roomUpdate3 = RoomUpdate(setOf("user2", "user3"))

        assertEquals(roomUpdate1, roomUpdate2)
        assertNotEquals(roomUpdate1, roomUpdate3)
    }

    @Test
    fun testSize() {
        val size1 = Size(100f, 200f)
        val size2 = Size(100f, 200f)
        val size3 = Size(300f, 400f)

        assertEquals(size1, size2)
        assertNotEquals(size1, size3)
    }

// ...

    @Test
    fun testComponentState() {
        val componentState1 = ComponentState(
            uuid = "123",
            componentType = ComponentType.SHAPE,
            size = Size(100f, 200f),
            position = Position(1f, 2f),
            depth = 0f,
            color = ComponentColor.BLACK,
            owner = "user1",
            accessLevel = AccessLevel.UNLOCKED,
            shapeType = ShapeType.SQUARE,
            shapeFill = ShapeFill.FILL
        )

        val componentState2 = ComponentState(
            uuid = "123",
            componentType = ComponentType.SHAPE,
            size = Size(100f, 200f),
            position = Position(1f, 2f),
            depth = 0f,
            color = ComponentColor.BLACK,
            owner = "user1",
            accessLevel = AccessLevel.UNLOCKED,
            shapeType = ShapeType.SQUARE,
            shapeFill = ShapeFill.FILL
        )

        val componentState3 = ComponentState(
            uuid = "456",
            componentType = ComponentType.SHAPE,
            size = Size(300f, 400f),
            position = Position(3f, 4f),
            depth = 0f,
            color = ComponentColor.BLACK,
            owner = "user2",
            accessLevel = AccessLevel.UNLOCKED,
            shapeType = ShapeType.SQUARE,
            shapeFill = ShapeFill.FILL
        )

        assertEquals(componentState1, componentState2)
        assertNotEquals(componentState1, componentState3)
    }

    @Test
    fun testComponentUpdate() {
        val componentUpdate1 = ComponentUpdate(
            username = "user1",
            uuid = "123",
            updateUUID = "upd123",
            size = Size(100f, 200f),
            position = Position(1f, 2f),
            color = ComponentColor.BLACK,
            accessLevel = AccessLevel.UNLOCKED,
            shapeType = ShapeType.SQUARE,
            shapeFill = ShapeFill.FILL
        )

        val componentUpdate2 = ComponentUpdate(
            username = "user1",
            uuid = "123",
            updateUUID = "upd123",
            size = Size(100f, 200f),
            position = Position(1f, 2f),
            color = ComponentColor.BLACK,
            accessLevel = AccessLevel.UNLOCKED,
            shapeType = ShapeType.SQUARE,
            shapeFill = ShapeFill.FILL
        )

        val componentUpdate3 = ComponentUpdate(
            username = "user2",
            uuid = "456",
            updateUUID = "upd456",
            size = Size(300f, 400f),
            position = Position(3f, 4f),
            color = ComponentColor.BLACK,
            accessLevel = AccessLevel.UNLOCKED,
            shapeType = ShapeType.SQUARE,
            shapeFill = ShapeFill.FILL
        )

        assertEquals(componentUpdate1.size, componentUpdate2.size)
        assertEquals(componentUpdate1.position, componentUpdate2.position)
        assertEquals(componentUpdate1.color, componentUpdate2.color)
        assertNotEquals(componentUpdate1.uuid, componentUpdate3.uuid)
        assertNotEquals(componentUpdate1.updateUUID, componentUpdate3.updateUUID)
    }

    @Test
    fun testWhiteboardState() {
        val componentState1 = ComponentState(uuid = "123")
        val componentState2 = ComponentState(uuid = "456")
        val whiteboardState1 = WhiteboardState(components = mutableMapOf("123" to componentState1, "456" to componentState2))
        val whiteboardState2 = WhiteboardState(components = mutableMapOf("123" to componentState1, "456" to componentState2))

        assertEquals(whiteboardState1, whiteboardState2)
    }

    @Test
    fun testWebSocketEvent() {
        val cursorUpdate = CursorUpdate("user1", Position(1f, 2f))
        val roomUpdate = RoomUpdate(setOf("user1", "user2"))
        val addComponent = ComponentState(uuid = "123")
        val updateComponent = ComponentUpdate(username = "user1", uuid = "123", updateUUID = "upd123")
        val deleteComponent = DeleteComponent(uuid = "123", username = "user1")
        val whiteboardState = WhiteboardState(components = mutableMapOf("123" to addComponent))
        val chatMessage = ChatMessage("user1", "Hello")
        val ping = Ping("user1", EmojiPing.THUMBS, Position(1f, 2f))

        val webSocketEvent = WebSocketEvent(
            eventType = WebSocketEventType.UPDATE_CURSOR,
            cursorUpdate = cursorUpdate,
            roomUpdate = roomUpdate,
            addComponent = addComponent,
            updateComponent = updateComponent,
            deleteComponent = deleteComponent,
            getFullState = whiteboardState,
            chatMessage = chatMessage,
            ping = ping
        )

        assertEquals(webSocketEvent.eventType, WebSocketEventType.UPDATE_CURSOR)
        assertEquals(webSocketEvent.cursorUpdate, cursorUpdate)
        assertEquals(webSocketEvent.roomUpdate, roomUpdate)
        assertEquals(webSocketEvent.addComponent, addComponent)
        assertEquals(webSocketEvent.updateComponent, updateComponent)
        assertEquals(webSocketEvent.deleteComponent, deleteComponent)
        assertEquals(webSocketEvent.getFullState, whiteboardState)
        assertEquals(webSocketEvent.chatMessage, chatMessage)
        assertEquals(webSocketEvent.ping, ping)
    }
}
