package whiteboard.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.helpers.Quadruple
import cs346.whiteboard.client.whiteboard.components.Component
import cs346.whiteboard.client.whiteboard.edit.ResizeNode
import cs346.whiteboard.client.whiteboard.edit.SelectionBoxController
import cs346.whiteboard.client.whiteboard.edit.SelectionBoxData
import kotlinx.coroutines.selects.select
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class SelectionBoxControllerTest {
    private lateinit var selectionBoxController: SelectionBoxController
    private lateinit var selectionBoxData: SelectionBoxData
    @BeforeEach
    fun setUp() {
        selectionBoxController = SelectionBoxController()
        selectionBoxData = SelectionBoxData(
            emptyList(),
            Offset(0f, 0f),
            Size(50f, 50f),
            null,
            true
        )
    }

    @Test
    fun testPointInResizeNodeWithNoSelectionBox() {
        assertNull(selectionBoxController.pointInResizeNode(Offset(0.0F, 0F), true))
        assertNull(selectionBoxController.pointInResizeNode(Offset(10F, 10F), true))
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
        val actual = selectionBoxController.getSelectionBoxResizeNodeCoordinates(selectionBoxData)
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
        val actual = selectionBoxController.pointInResizeNode(Offset(5f, 5f), false)
        assertEquals(expected, actual)
        assertNull(selectionBoxController.selectionBoxData?.resizeNodeAnchor)
    }


}