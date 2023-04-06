package cs346.whiteboard.client.whiteboard

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.whiteboard.components.Shape
import cs346.whiteboard.client.whiteboard.components.attributeWrapper
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions
import cs346.whiteboard.client.whiteboard.overlay.CursorType
import cs346.whiteboard.shared.jsonmodels.AccessLevel
import cs346.whiteboard.shared.jsonmodels.ComponentColor
import cs346.whiteboard.shared.jsonmodels.ShapeFill
import cs346.whiteboard.shared.jsonmodels.ShapeType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference
import kotlin.test.assertEquals

const val ROOM_ID: Long = 54269818
const val ROOM_NAME = "FOOOOOOO"
class WhiteboardControllerTest {

    private var testCoroutineScope = TestCoroutineScope()
    private lateinit var whiteboardController: WhiteboardController

    private var bin = true
    private fun toggleBin() { bin = !bin }

    @BeforeEach
    fun setup() {
        whiteboardController = WhiteboardController(ROOM_NAME, ROOM_ID, testCoroutineScope, ::toggleBin)
    }

    @Test
    fun `exitWhiteboard invokes onExit`() {
        bin = true
        whiteboardController.exitWhiteboard()
        assertEquals(false, bin)
        whiteboardController.exitWhiteboard()
        assertEquals(true, bin)
    }

    @Test
    fun `getWhiteboardTitle returns correct title`() {
        assertEquals(ROOM_NAME, whiteboardController.getWhiteboardTitle())
    }

    @Test
    fun `teleportToUser rejects invalid users`() {
        whiteboardController.teleportToUser("")
        assertEquals(Offset(0f,0f), whiteboardController.whiteboardOffset)
    }

    // TODO("Add test for teleportToUser with valid user")

    @Test
    fun `test zoomIn`() {
        whiteboardController.zoomIn()
        assertEquals(1.1f, whiteboardController.whiteboardZoom)
    }

    @Test
    fun `test zoomOut`() {
        whiteboardController.zoomOut()
        assertEquals(0.9f, whiteboardController.whiteboardZoom)
    }

    @Test
    fun `test viewToWhiteboardCoordinate`() {
        whiteboardController.whiteboardOffset = Offset(10f, 10f)
        whiteboardController.whiteboardZoom = 2f

        val viewCoordinate = Offset(50f, 50f)
        val expectedWhiteboardCoordinate = Offset(15f, 15f)
        val actualWhiteboardCoordinate = whiteboardController.viewToWhiteboardCoordinate(viewCoordinate)

        assertEquals(expectedWhiteboardCoordinate, actualWhiteboardCoordinate)
    }

    @Test
    fun `test whiteboardToViewCoordinate`() {
        whiteboardController.whiteboardOffset = Offset(10f, 10f)
        whiteboardController.whiteboardZoom = 2f

        val whiteboardCoordinate = Offset(15f, 15f)
        val expectedViewCoordinate = Offset(50f, 50f)
        val actualViewCoordinate = whiteboardController.whiteboardToViewCoordinate(whiteboardCoordinate)

        assertEquals(expectedViewCoordinate, actualViewCoordinate)
    }

    @Test
    fun `test whiteboardToViewSize`() {
        whiteboardController.whiteboardZoom = 2f

        val whiteboardSize = Size(10f, 10f)
        val expectedViewSize = Size(20f, 20f)
        val actualViewSize = whiteboardController.whiteboardToViewSize(whiteboardSize)

        assertEquals(expectedViewSize, actualViewSize)
    }

    @Test
    fun `test pasteFromClipboard`() {
        val square = Shape(
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

        whiteboardController.clipboard.copy(listOf(square))
        whiteboardController.pasteFromClipboard()
        assertEquals(1, whiteboardController.components.size)
        assertTrue(whiteboardController.components.values.first() is Shape)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test deleteSelected`() = runBlockingTest {
        val square = Shape(
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

        whiteboardController.components[square.uuid] = square
        whiteboardController.editController.selectedSingleComponent(square)
        whiteboardController.deleteSelected()
        assertTrue(whiteboardController.components.isEmpty())
    }

    @Test
    fun testHandlePointerPosition() = runBlocking {
        whiteboardController.currentTool = WhiteboardToolbarOptions.SELECT
        val initialCursor = whiteboardController.cursorsController.currentCursor

        // Test when pointer is within resize node
        whiteboardController.handlePointerPosition(Offset(10f, 10f))
        val expectedCursor = CursorType.POINTER // Change to the expected cursor type
        assertEquals(expectedCursor, whiteboardController.cursorsController.currentCursor)

        // Test when pointer is outside resize node
        whiteboardController.handlePointerPosition(Offset(200f, 200f))
        assertEquals(initialCursor, whiteboardController.cursorsController.currentCursor)
    }

}