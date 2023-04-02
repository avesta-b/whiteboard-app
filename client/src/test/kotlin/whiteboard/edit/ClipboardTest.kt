package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.whiteboard.components.Component
import cs346.whiteboard.client.whiteboard.components.Shape
import cs346.whiteboard.client.whiteboard.components.attributeWrapper
import cs346.whiteboard.shared.jsonmodels.AccessLevel
import cs346.whiteboard.shared.jsonmodels.ComponentColor
import cs346.whiteboard.shared.jsonmodels.ShapeFill
import cs346.whiteboard.shared.jsonmodels.ShapeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference

class ClipboardTest {
    private lateinit var component1: Component
    private lateinit var component2: Component
    private lateinit var component3: Component

    @BeforeEach
    fun setUp() {
        component1 = Shape(
            "",
            WeakReference(null),
            attributeWrapper(Offset(0f, 0f)),
            attributeWrapper(Size(250f, 250f)),
            attributeWrapper(ComponentColor.BLACK),
            0f,
            "",
            attributeWrapper(AccessLevel.UNLOCKED),
            attributeWrapper(ShapeType.SQUARE),
            attributeWrapper(ShapeFill.OUTLINE)
        )
        component2 = Shape(
            "",
            WeakReference(null),
            attributeWrapper(Offset(20f, 20f)),
            attributeWrapper(Size(250f, 250f)),
            attributeWrapper(ComponentColor.BLACK),
            0f,
            "",
            attributeWrapper(AccessLevel.UNLOCKED),
            attributeWrapper(ShapeType.SQUARE),
            attributeWrapper(ShapeFill.OUTLINE)
        )
        component3 = Shape(
            "",
            WeakReference(null),
            attributeWrapper(Offset(40f, 40f)),
            attributeWrapper(Size(250f, 250f)),
            attributeWrapper(ComponentColor.BLACK),
            0f,
            "",
            attributeWrapper(AccessLevel.UNLOCKED),
            attributeWrapper(ShapeType.SQUARE),
            attributeWrapper(ShapeFill.OUTLINE)
        )
    }

    @Test
    fun testCopy() {
        runBlocking {
            val components = listOf(component1, component2, component3)
            Clipboard.copy(components)
            val pastedComponents = Clipboard.paste()
            delay(100)
            assertEquals(3, pastedComponents.size)
            assertEquals(component1.coordinate.getValue().plus(Offset(10f, 10f)), pastedComponents[0].coordinate.getValue())
            assertEquals(component1.size.getValue(), pastedComponents[0].size.getValue())
            assertEquals(component1.depth, pastedComponents[0].depth)
            assertEquals(component2.coordinate.getValue().plus(Offset(10f, 10f)), pastedComponents[1].coordinate.getValue())
            assertEquals(component2.size.getValue(), pastedComponents[1].size.getValue())
            assertEquals(component2.depth, pastedComponents[1].depth)
            assertEquals(component3.coordinate.getValue().plus(Offset(10f, 10f)), pastedComponents[2].coordinate.getValue())
            assertEquals(component3.size.getValue(), pastedComponents[2].size.getValue())
            assertEquals(component3.depth, pastedComponents[2].depth)
        }
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
        runBlocking {
            val components = listOf(component1, component2, component3)
            Clipboard.copy(components)
            val pastedComponents1 = Clipboard.paste()
            delay(100)
            assertEquals(Offset(10f, 10f), pastedComponents1[0].coordinate.getValue())
            assertEquals(Offset(30f, 30f), pastedComponents1[1].coordinate.getValue())
            assertEquals(Offset(50f, 50f), pastedComponents1[2].coordinate.getValue())

            val pastedComponents2 = Clipboard.paste()
            delay(100)
            assertEquals(Offset(20f, 20f), pastedComponents2[0].coordinate.getValue())
            assertEquals(Offset(40f, 40f), pastedComponents2[1].coordinate.getValue())
            assertEquals(Offset(60f, 60f), pastedComponents2[2].coordinate.getValue())
        }
    }
}