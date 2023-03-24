package whiteboard.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.helpers.Quadruple
import cs346.whiteboard.client.whiteboard.components.Component
import cs346.whiteboard.client.whiteboard.components.Shape
import cs346.whiteboard.client.whiteboard.components.attributeWrapper
import cs346.whiteboard.client.whiteboard.edit.EditController
import cs346.whiteboard.client.whiteboard.edit.ResizeNode
import cs346.whiteboard.client.whiteboard.edit.SelectionBoxData
import cs346.whiteboard.shared.jsonmodels.ComponentColor
import cs346.whiteboard.shared.jsonmodels.ShapeFill
import cs346.whiteboard.shared.jsonmodels.ShapeType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import java.lang.ref.WeakReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class EditControllerTest {
    private lateinit var controller: EditController
    private lateinit var selectionBoxData: SelectionBoxData
    private lateinit var component: Component

    @BeforeEach
    fun setUp() {
        controller = EditController()
        selectionBoxData = SelectionBoxData(
            emptyList(),
            Offset(0f, 0f),
            Size(50f, 50f),
            null,
            true
        )
        component = Shape(
            "",
            WeakReference(null),
            attributeWrapper(Offset(0f, 0f)),
            attributeWrapper(Size(250f, 250f)),
            attributeWrapper(ComponentColor.BLACK),
            0f,
            attributeWrapper(ShapeType.SQUARE),
            attributeWrapper(ShapeFill.OUTLINE)
        )
    }

    @Test
    fun testPointInResizeNodeWithNoSelectionBox() {
        assertNull(controller.pointInResizeNode(Offset(0.0F, 0F), true))
        assertNull(controller.pointInResizeNode(Offset(10F, 10F), true))

        controller.selectedSingleComponent(component)
        val resizeNode = controller.pointInResizeNode(Offset(8f, 8f), false)
        Assertions.assertEquals(ResizeNode.TOP_LEFT, resizeNode)
    }

    @org.junit.jupiter.api.Test
    fun `test moveSelectedComponents`() {
        controller.selectedSingleComponent(component)
        controller.moveSelectedComponents(Offset(20f, 20f))
        Assertions.assertEquals(Offset(20f, 20f), controller.selectionBoxData?.coordinate)
    }

    @org.junit.jupiter.api.Test
    fun `test isPointInSelectionBox`() {
        controller.selectedSingleComponent(component)
        val pointInBox = controller.isPointInSelectionBox(Offset(60f, 60f))

        Assertions.assertEquals(true, pointInBox)
    }

    @org.junit.jupiter.api.Test
    fun `test selectedSingleComponent`() {
        controller.selectedSingleComponent(component)

        Assertions.assertEquals(1, controller.selectionBoxData?.selectedComponents?.size)
        Assertions.assertEquals(component, controller.selectionBoxData?.selectedComponents?.first())
        Assertions.assertEquals(true, controller.selectionBoxData?.isResizable)
    }

    @org.junit.jupiter.api.Test
    fun `test selectedComponents with list`() {
        val componentList = listOf(component)
        controller.selectedComponents(componentList)

        Assertions.assertEquals(componentList, controller.selectionBoxData?.selectedComponents)
    }

    @org.junit.jupiter.api.Test
    fun `test clearSelectionBox`() {
        controller.selectedSingleComponent(component)
        controller.clearSelectionBox()

        Assertions.assertNull(controller.selectionBoxData)
    }

    @Test
    fun testGetSelectionBoxResizeNodeCoordinates() {
        val size = Size(100f, 50f)
        val coordinate = Offset(0f, 0f)
        val resizeNodeSize = Size(10f, 10f)
        val selectionBoxData = SelectionBoxData(
            emptyList(),
            coordinate,
            size,
            ResizeNode.BOTTOM_RIGHT,
            true,
            resizeNodeSize
        )
        val expected = Quadruple(
            coordinate.minus(Offset(5f, 5f)),
            Offset(coordinate.x + size.width, coordinate.y).minus(Offset(5f, 5f)),
            Offset(coordinate.x, coordinate.y + size.height).minus(Offset(5f, 5f)),
            Offset(coordinate.x + size.width, coordinate.y + size.height).minus(Offset(5f, 5f))
        )
        val actual = controller.getSelectionBoxResizeNodeCoordinates(selectionBoxData)
        assertEquals(expected, actual)
    }
    @Test
    fun testPointInResizeNodeShouldNotSetAnchorNode() {
        val size = Size(100f, 50f)
        val coordinate = Offset(0f, 0f)
        val resizeNodeSize = Size(10f, 10f)
        val selectionBoxData = SelectionBoxData(
            emptyList(),
            coordinate,
            size,
            null,
            true,
            resizeNodeSize
        )
        val expected = null
        val actual = controller.pointInResizeNode(Offset(5f, 5f), false)
        assertEquals(expected, actual)
        assertNull(controller.selectionBoxData?.resizeNodeAnchor)
    }


}