package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.whiteboard.components.Component
import cs346.whiteboard.client.whiteboard.components.Shape
import cs346.whiteboard.shared.jsonmodels.ComponentColor
import cs346.whiteboard.shared.jsonmodels.ShapeFill
import cs346.whiteboard.shared.jsonmodels.ShapeType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClipboardTest {
    private lateinit var component1: Component
    private lateinit var component2: Component
    private lateinit var component3: Component

    @BeforeEach
    fun setUp() {
        component1 = Shape(
            mutableStateOf(Offset(0f, 0f)),
            mutableStateOf(Size(250f, 250f)),
            mutableStateOf(ComponentColor.BLACK),
            0f,
            mutableStateOf(ShapeType.SQUARE),
            mutableStateOf(ShapeFill.OUTLINE)
        )
        component2 = Shape(
            mutableStateOf(Offset(20f, 20f)),
            mutableStateOf(Size(250f, 250f)),
            mutableStateOf(ComponentColor.BLACK),
            0f,
            mutableStateOf(ShapeType.SQUARE),
            mutableStateOf(ShapeFill.OUTLINE)
        )
        component3 = Shape(
            mutableStateOf(Offset(40f, 40f)),
            mutableStateOf(Size(250f, 250f)),
            mutableStateOf(ComponentColor.BLACK),
            0f,
            mutableStateOf(ShapeType.SQUARE),
            mutableStateOf(ShapeFill.OUTLINE)
        )
    }

    @Test
    fun testCopy() {
        val components = listOf(component1, component2, component3)
        Clipboard.copy(components)
        val pastedComponents = Clipboard.paste()

        assertEquals(3, pastedComponents.size)
        assertEquals(component1.coordinate.value.plus(Offset(10f, 10f)), pastedComponents[0].coordinate.value)
        assertEquals(component1.size.value, pastedComponents[0].size.value)
        assertEquals(component1.depth, pastedComponents[0].depth)
        assertEquals(component2.coordinate.value.plus(Offset(10f, 10f)), pastedComponents[1].coordinate.value)
        assertEquals(component2.size.value, pastedComponents[1].size.value)
        assertEquals(component2.depth, pastedComponents[1].depth)
        assertEquals(component3.coordinate.value.plus(Offset(10f, 10f)), pastedComponents[2].coordinate.value)
        assertEquals(component3.size.value, pastedComponents[2].size.value)
        assertEquals(component3.depth, pastedComponents[2].depth)
    }

    @Test
    fun testPaste() {
        val components = listOf(component1, component2, component3)
        Clipboard.copy(components)
        val pastedComponents1 = Clipboard.paste()

        assertNotSame(components, pastedComponents1)
        assertEquals(3, pastedComponents1.size)

        val pastedComponents2 = Clipboard.paste()
        assertNotSame(pastedComponents1, pastedComponents2)
        assertEquals(3, pastedComponents2.size)
    }

    @Test
    fun testPasteOffset() {
        val components = listOf(component1, component2, component3)
        Clipboard.copy(components)
        val pastedComponents1 = Clipboard.paste()

        assertEquals(Offset(10f, 10f), pastedComponents1[0].coordinate.value)
        assertEquals(Offset(30f, 30f), pastedComponents1[1].coordinate.value)
        assertEquals(Offset(50f, 50f), pastedComponents1[2].coordinate.value)

        val pastedComponents2 = Clipboard.paste()
        assertEquals(Offset(20f, 20f), pastedComponents2[0].coordinate.value)
        assertEquals(Offset(40f, 40f), pastedComponents2[1].coordinate.value)
        assertEquals(Offset(60f, 60f), pastedComponents2[2].coordinate.value)
    }
}