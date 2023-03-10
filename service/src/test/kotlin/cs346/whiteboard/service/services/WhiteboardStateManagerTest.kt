package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WhiteboardStateManagerTest {
    private lateinit var whiteboardStateManager: WhiteboardStateManager

    @BeforeEach
    fun setUp() {
        whiteboardStateManager = WhiteboardStateManager()
    }

    @Test
    fun `getWhiteboard returns null for nonexistent room`() {
        assertNull(whiteboardStateManager.getWhiteboard("Nonexistent Room"))
    }

    @Test
    fun `addComponent adds component to whiteboard`() {
        val roomId = "Room 1"
        val component = ComponentState(
            uuid = "Component 1",
            componentType = ComponentType.SQUARE,
            size = Size(width = 10.0f, height = 10.0f),
            position = Position(x = 10.0f, y = 20.0f),
            depth = 0f
        )
        whiteboardStateManager.addComponent(roomId, component)
        val whiteboard = whiteboardStateManager.getWhiteboard(roomId)
        assertNotNull(whiteboard)
        assertEquals(component, whiteboard!!.components[component.uuid])
    }

    @Test
    fun `addComponent adds multiple components to whiteboard`() {
        val roomId = "Room 1"
        val component1 = ComponentState(
            uuid = "Component 1",
            componentType = ComponentType.SQUARE,
            size = Size(width = 10.0f, height = 10.0f),
            position = Position(x = 10.0f, y = 20.0f),
            depth = 0f
        )
        val component2 = ComponentState(
            uuid = "Component 2",
            componentType = ComponentType.CIRCLE,
            size = Size(width = 20.0f, height = 20.0f),
            position = Position(x = 30.0f, y = 40.0f),
            depth = 1f
        )
        whiteboardStateManager.addComponent(roomId, component1)
        whiteboardStateManager.addComponent(roomId, component2)
        val whiteboard = whiteboardStateManager.getWhiteboard(roomId)
        assertNotNull(whiteboard)
        assertEquals(component1, whiteboard!!.components[component1.uuid])
        assertEquals(component2, whiteboard.components[component2.uuid])
    }

    @Test
    fun `destroy whiteboard when last component is deleted`() {
        val roomId = "Room 1"
        val component = ComponentState(
            uuid = "Component 1",
            componentType = ComponentType.SQUARE,
            size = Size(width = 10.0f, height = 10.0f),
            position = Position(x = 10.0f, y = 20.0f),
            depth = 0f
        )
        whiteboardStateManager.addComponent(roomId, component)
        whiteboardStateManager.deleteComponent(roomId, DeleteComponent(component.uuid))
        val whiteboard = whiteboardStateManager.getWhiteboard(roomId)
        assertNull(whiteboard)
    }

    @Test
    fun `addComponent updates component if it already exists`() {
        val roomId = "Room 1"
        val component = ComponentState(
            uuid = "Component 1",
            componentType = ComponentType.SQUARE,
            size = Size(width = 10.0f, height = 10.0f),
            position = Position(x = 10.0f, y = 20.0f),
            depth = 0f
        )
        val updatedComponent = component.copy(
            size = Size(width = 20.0f, height = 20.0f),
            position = Position(x = 30.0f, y = 40.0f),
            depth = 1f
        )
        whiteboardStateManager.addComponent(roomId, component)
        whiteboardStateManager.addComponent(roomId, updatedComponent)
        val whiteboard = whiteboardStateManager.getWhiteboard(roomId)
        assertNotNull(whiteboard)
        assertEquals(1, whiteboard!!.components.size)
        assertEquals(updatedComponent, whiteboard.components[component.uuid])
    }
}