package cs346.whiteboard.client.whiteboard

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class WhiteboardController {

    val componentList = mutableStateListOf<Component>()
    private var currentTool = WhiteboardToolbarOptions.PEN

    fun handleOnDragGestureStart(startPoint: Offset) {
        if (currentTool == WhiteboardToolbarOptions.PEN) {
            val path = Path(startPoint, Size.Zero)
            path.insertPoint(startPoint)
            componentList.add(path)
        }
    }

    fun handleOnDragGesture(nextPoint: Offset) {
        if (currentTool == WhiteboardToolbarOptions.PEN) {
            componentList.lastOrNull()?.let {
                if (it !is Path) return
                it.points.add(nextPoint)
            }
        }
    }

    fun handleOnTapGesture(point: Offset) {
        when(currentTool) {
            WhiteboardToolbarOptions.PEN -> {
                val path = Path(point, Size.Zero)
                path.insertPoint(point)
                path.insertPoint(point)
                componentList.add(path)
            }
            WhiteboardToolbarOptions.SQUARE -> {
                val square = Shape(point, Size(100f, 100f), ShapeTypes.SQUARE)
                componentList.add(square)
            }
            WhiteboardToolbarOptions.CIRCLE -> {
                val circle = Shape(point, Size(100f, 100f), ShapeTypes.CIRCLE)
                componentList.add(circle)
            }
            WhiteboardToolbarOptions.TEXT -> {
                val textBox = TextBox(point, Size.Zero)
                componentList.add(textBox)
            }
        }
    }

    fun setCurrentTool(newTool: WhiteboardToolbarOptions) {
        currentTool = newTool
    }

}