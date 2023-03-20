package whiteboard.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.whiteboard.edit.QueryBoxController
import cs346.whiteboard.client.whiteboard.edit.QueryBoxData
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QueryBoxControllerTest {
    private lateinit var controller: QueryBoxController

    @BeforeEach
    fun setUp() {
        controller = QueryBoxController()
    }

    @Test
    fun testStartQueryBox() {
        val initialPosition = Offset(10f, 10f)
        controller.startQueryBox(initialPosition)
        val expectedQueryBoxData = QueryBoxData(
            anchorPoint = initialPosition,
            coordinate = initialPosition,
            size = Size.Zero
        )
        assertEquals(expectedQueryBoxData, controller.queryBoxData)
    }

    @Test
    fun testUpdateQueryBox() {
        val initialPosition = Offset(10f, 10f)
        controller.startQueryBox(initialPosition)
        val newPosition = Offset(20f, 20f)
        controller.updateQueryBox(newPosition)
        val expectedQueryBoxData = QueryBoxData(
            anchorPoint = initialPosition,
            coordinate = Offset(10f, 10f),
            size = Size(10f, 10f)
        )
        assertEquals(expectedQueryBoxData, controller.queryBoxData)
    }

    @Test
    fun testClearQueryBox() {
        val initialPosition = Offset(10f, 10f)
        controller.startQueryBox(initialPosition)
        controller.clearQueryBox()
        assertNull(controller.queryBoxData)
    }

}
