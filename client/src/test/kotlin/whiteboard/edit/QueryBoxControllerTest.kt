package whiteboard.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.whiteboard.components.Component
import cs346.whiteboard.client.whiteboard.components.Shape
import cs346.whiteboard.client.whiteboard.components.attributeWrapper
import cs346.whiteboard.client.whiteboard.edit.QueryBoxController
import cs346.whiteboard.shared.jsonmodels.ComponentColor
import cs346.whiteboard.shared.jsonmodels.ShapeFill
import cs346.whiteboard.shared.jsonmodels.ShapeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference

class QueryBoxControllerTest {

    private lateinit var queryBoxController: QueryBoxController
    private lateinit var components: List<Component>

    @BeforeEach
    fun setUp() {
        queryBoxController = QueryBoxController()
        components = listOf(
            Shape(
                "",
                WeakReference(null),
                attributeWrapper(Offset(0f, 0f)),
                attributeWrapper(Size(250f, 250f)),
                attributeWrapper(ComponentColor.BLACK),
                0f,
                attributeWrapper(ShapeType.SQUARE),
                attributeWrapper(ShapeFill.OUTLINE)
            ),
            Shape(
                "",
                WeakReference(null),
                attributeWrapper(Offset(20f, 20f)),
                attributeWrapper(Size(250f, 250f)),
                attributeWrapper(ComponentColor.BLACK),
                0f,
                attributeWrapper(ShapeType.SQUARE),
                attributeWrapper(ShapeFill.OUTLINE)
            ),
            Shape(
                "",
                WeakReference(null),
                attributeWrapper(Offset(40f, 40f)),
                attributeWrapper(Size(250f, 250f)),
                attributeWrapper(ComponentColor.BLACK),
                0f,
                attributeWrapper(ShapeType.SQUARE),
                attributeWrapper(ShapeFill.OUTLINE)
            )
        )
    }

    @Test
    fun startQueryBox() {
        queryBoxController.startQueryBox(Offset(10f, 10f))
        val queryBoxData = queryBoxController.queryBoxData
        assertEquals(Offset(10f, 10f), queryBoxData?.anchorPoint)
        assertEquals(Offset(10f, 10f), queryBoxData?.coordinate)
        assertEquals(Size.Zero, queryBoxData?.size)
    }

    @Test
    fun updateQueryBox() {
        queryBoxController.startQueryBox(Offset(10f, 10f))
        queryBoxController.updateQueryBox(Offset(30f, 30f))
        val queryBoxData = queryBoxController.queryBoxData
        assertEquals(Offset(10f, 10f), queryBoxData?.anchorPoint)
        assertEquals(Offset(10f, 10f), queryBoxData?.coordinate)
        assertEquals(Size(20f, 20f), queryBoxData?.size)
    }

    @Test
    fun getComponentsInQueryBoxAndMinMaxCoordinates_noOverlap() {
        queryBoxController.startQueryBox(Offset(300f, 300f))
        queryBoxController.updateQueryBox(Offset(310f, 310f))
        val componentsInQueryBox = queryBoxController.getComponentsInQueryBox(components)
        assert(componentsInQueryBox.isEmpty())
    }

    @Test
    fun getComponentsInQueryBoxAndMinMaxCoordinates_withOverlap() {
        queryBoxController.startQueryBox(Offset(0f, 0f))
        queryBoxController.updateQueryBox(Offset(100f, 100f))
        val componentsInQueryBox = queryBoxController.getComponentsInQueryBox(components)
        assertEquals(3, componentsInQueryBox.size)
    }

    @Test
    fun clearQueryBox() {
        queryBoxController.startQueryBox(Offset(10f, 10f))
        queryBoxController.clearQueryBox()
        assertNull(queryBoxController.queryBoxData)
    }
}
