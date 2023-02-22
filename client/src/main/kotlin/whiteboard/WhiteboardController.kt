package cs346.whiteboard.client.whiteboard

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange

class WhiteboardController {

    internal val componentList = mutableStateListOf<Component>()
    internal var currentTool by mutableStateOf(WhiteboardToolbarOptions.PEN)
    internal var whiteboardOffset by mutableStateOf(Offset(0f, 0f))
    internal var whiteboardZoom by mutableStateOf(1f)
    internal var whiteboardSize by mutableStateOf(Size.Zero)

    private fun viewToWhiteboardCoordinate(point: Offset): Offset {
        val zoomOrigin = Offset(whiteboardSize.width / 2, whiteboardSize.height / 2)
        return Offset(
            zoomOrigin.x + (point.x - zoomOrigin.x) / whiteboardZoom - whiteboardOffset.x,
            zoomOrigin.y + (point.y - zoomOrigin.y) / whiteboardZoom - whiteboardOffset.y
        )
    }

    fun whiteboardToViewCoordinate(point: Offset): Offset {
        val zoomOrigin = Offset(whiteboardSize.width / 2, whiteboardSize.height / 2)
        return Offset(
            zoomOrigin.x - (zoomOrigin.x - point.x - whiteboardOffset.x) * whiteboardZoom,
            zoomOrigin.y - (zoomOrigin.y - point.y - whiteboardOffset.y) * whiteboardZoom
        )
    }

    fun zoomIn() {
        if (whiteboardZoom < 1.5f) {
            whiteboardZoom += 0.1f
        }
    }

    fun zoomOut() {
        if (whiteboardZoom > 0.5f) {
            whiteboardZoom -= 0.1f
        }
    }

    fun handleOnDragGestureStart(startPoint: Offset) {
        if (currentTool == WhiteboardToolbarOptions.PEN) {
            val whiteboardStartPoint = viewToWhiteboardCoordinate(startPoint)
            val path = Path(mutableStateOf(whiteboardStartPoint), mutableStateOf(Size.Zero))
            path.insertPoint(whiteboardStartPoint)
            componentList.add(path)
        }
    }

    fun handleOnDragGesture(change: PointerInputChange, dragAmount: Offset) {
        when(currentTool) {
            WhiteboardToolbarOptions.PAN -> {
                whiteboardOffset = whiteboardOffset.plus(dragAmount)
            }
            WhiteboardToolbarOptions.PEN -> {
                componentList.lastOrNull()?.let {
                    if (it !is Path) return
                    it.insertPoint(viewToWhiteboardCoordinate(change.position))
                }
            }
            else -> { return }
        }
    }

    fun handleOnTapGesture(point: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(point)
        when(currentTool) {
            WhiteboardToolbarOptions.PEN -> {
                val path = Path(mutableStateOf(whiteboardPoint), mutableStateOf(Size.Zero))
                path.insertPoint(whiteboardPoint)
                path.insertPoint(whiteboardPoint)
                componentList.add(path)
            }
            WhiteboardToolbarOptions.SQUARE -> {
                val square = Shape(mutableStateOf(whiteboardPoint), mutableStateOf(Size(250f, 250f)), ShapeTypes.SQUARE)
                componentList.add(square)
            }
            WhiteboardToolbarOptions.CIRCLE -> {
                val circle = Shape(mutableStateOf(whiteboardPoint), mutableStateOf(Size(250f, 250f)), ShapeTypes.CIRCLE)
                componentList.add(circle)
            }
            WhiteboardToolbarOptions.TEXT -> {
                val textBox = TextBox(mutableStateOf(whiteboardPoint), mutableStateOf(Size(350f, 250f)))
                componentList.add(textBox)
            }
            else -> {
                return
            }
        }
    }
}