package cs346.whiteboard.client.whiteboard

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.helpers.overlap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WhiteboardController(private val roomId: String, private val coroutineScope: CoroutineScope) {

    internal val components = mutableStateMapOf<String, Component>()
    internal var currentTool by mutableStateOf(WhiteboardToolbarOptions.SELECT)
    internal var whiteboardOffset by mutableStateOf(Offset(0f, 0f))
    internal var whiteboardZoom by mutableStateOf(1f)
    internal var whiteboardSize by mutableStateOf(Size.Zero)
    internal var queryBoxController by mutableStateOf(QueryBoxController())
    internal var selectionBoxController by mutableStateOf(SelectionBoxController())
    internal var cursorsController by mutableStateOf(CursorsController(
        UserManager.getUsername() ?: "default_user",
        coroutineScope,
        roomId
    ))
    private var lastComponentId = ""
    private var currentDepth = 0f
    private var isDraggingSelectionBox = false
    private var isResizingSelectionBox = false

    init {
        snapshotFlow { currentTool }
            .onEach {
                cursorsController.currentCursor = it.cursorType()
            }
            .launchIn(coroutineScope)
    }

    fun viewToWhiteboardCoordinate(point: Offset): Offset {
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

    fun whiteboardToViewSize(size: Size): Size {
        return size * whiteboardZoom
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

    private fun preIncrementCurrentDepth(): Float {
        currentDepth += Float.MIN_VALUE
        return currentDepth
    }

    fun handleOnDragGestureStart(startPoint: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(startPoint)
        when (currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                if (selectionBoxController.isPointInResizeNode(whiteboardPoint)) {
                    isResizingSelectionBox = true
                } else if (selectionBoxController.isPointInSelectionBox(whiteboardPoint)) {
                    isDraggingSelectionBox = true
                } else {
                    getComponentAtPoint(whiteboardPoint)?.let {
                        selectionBoxController.selectedSingleComponent(it)
                        isDraggingSelectionBox = true
                    } ?: run {
                        selectionBoxController.clearSelectionBox()
                        queryBoxController.startQueryBox(whiteboardPoint)
                    }
                }
            }
            WhiteboardToolbarOptions.PEN -> {
                selectionBoxController.clearSelectionBox()
                val whiteboardStartPoint = viewToWhiteboardCoordinate(startPoint)
                val path = Path(mutableStateOf(whiteboardStartPoint), mutableStateOf(Size.Zero), preIncrementCurrentDepth())
                path.insertPoint(whiteboardStartPoint)
                components[path.uuid] = path
                lastComponentId = path.uuid
            }
            WhiteboardToolbarOptions.PAN -> {
                cursorsController.currentCursor = CursorType.GRAB
                selectionBoxController.clearSelectionBox()
            }
            else -> {
                selectionBoxController.clearSelectionBox()
                return
            }
        }
    }

    fun handleOnDragGesture(change: PointerInputChange, dragAmount: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(change.position)
        when(currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                if (isResizingSelectionBox) {
                    selectionBoxController.resizeSelectedComponents(whiteboardPoint, whiteboardZoom)
                } else if (isDraggingSelectionBox) {
                    selectionBoxController.moveSelectedComponents(dragAmount.div(whiteboardZoom))
                } else {
                    queryBoxController.updateQueryBox(whiteboardPoint)
                }
            }
            WhiteboardToolbarOptions.PAN -> {
                whiteboardOffset = whiteboardOffset.plus(dragAmount.div(whiteboardZoom))
            }
            WhiteboardToolbarOptions.PEN -> {
                components[lastComponentId]?.let {
                    if (it !is Path) return
                    it.insertPoint(whiteboardPoint)
                }
            }
            else -> { return }
        }
    }

    fun handleOnDragGestureEnd() {
        when(currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                isResizingSelectionBox = false
                isDraggingSelectionBox = false
                val componentsAndMinMaxCoordinates: Triple<List<Component>, Offset, Offset>? =
                    queryBoxController.getComponentsInQueryBoxAndMinMaxCoordinates(components.values.toList())
                componentsAndMinMaxCoordinates?.let { (componentsInQueryBox, minCoordinate, maxCoordinate) ->
                    selectionBoxController.selectedComponents(componentsInQueryBox, minCoordinate, maxCoordinate)
                }
                queryBoxController.clearQueryBox()
            }
            WhiteboardToolbarOptions.PAN -> {
                cursorsController.currentCursor = CursorType.HAND
            }
            else -> { return }
        }
    }

    fun handleOnTapGesture(point: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(point)
        selectionBoxController.clearSelectionBox()
        when(currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                val selectedComponent = getComponentAtPoint(whiteboardPoint)
                selectedComponent?.let {
                    selectionBoxController.selectedSingleComponent(it)
                }
            }
            WhiteboardToolbarOptions.PEN -> {
                val path = Path(mutableStateOf(whiteboardPoint), mutableStateOf(Size(1f, 1f)), preIncrementCurrentDepth())
                path.insertPoint(whiteboardPoint)
                path.insertPoint(whiteboardPoint)
                components[path.uuid] = path
            }
            WhiteboardToolbarOptions.SQUARE -> {
                val square = Shape(mutableStateOf(whiteboardPoint), mutableStateOf(Size(250f, 250f)), preIncrementCurrentDepth(), ShapeTypes.SQUARE)
                components[square.uuid] = square
                selectionBoxController.selectedSingleComponent(square)
                currentTool = WhiteboardToolbarOptions.SELECT
            }
            WhiteboardToolbarOptions.CIRCLE -> {
                val circle = Shape(mutableStateOf(whiteboardPoint), mutableStateOf(Size(250f, 250f)), preIncrementCurrentDepth(), ShapeTypes.CIRCLE)
                components[circle.uuid] = circle
                selectionBoxController.selectedSingleComponent(circle)
                currentTool = WhiteboardToolbarOptions.SELECT
            }
            WhiteboardToolbarOptions.TEXT -> {
                val textBox = TextBox(mutableStateOf(whiteboardPoint), mutableStateOf(Size(350f, 250f)), preIncrementCurrentDepth())
                components[textBox.uuid] = textBox
                selectionBoxController.selectedSingleComponent(textBox)
                currentTool = WhiteboardToolbarOptions.SELECT
            }
            else -> {
                return
            }
        }
    }

    private fun getComponentAtPoint(point: Offset): Component? {
        val componentsAtPoint = mutableListOf<Component>()
        components.forEach { (_, component) ->
            // Add hit padding to make it easier to select small components
            if (overlap(
                    point.minus(Offset(5f, 5f)),
                    Size(10f, 10f),
                    component.coordinate.value,
                    component.size.value)
            ) {
                componentsAtPoint.add(component)
            }
        }
        return componentsAtPoint.maxByOrNull { it.depth }
    }

}