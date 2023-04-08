package cs346.whiteboard.client.whiteboard

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.client.commands.CommandFactory
import cs346.whiteboard.client.helpers.Toolkit
import cs346.whiteboard.client.helpers.overlap
import cs346.whiteboard.client.helpers.toComponent
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.client.whiteboard.components.*
import cs346.whiteboard.client.whiteboard.edit.Clipboard
import cs346.whiteboard.client.whiteboard.edit.EditController
import cs346.whiteboard.client.whiteboard.edit.QueryBoxController
import cs346.whiteboard.client.whiteboard.interaction.WhiteboardToolbarOptions
import cs346.whiteboard.client.whiteboard.overlay.CursorType
import cs346.whiteboard.shared.jsonmodels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference
import java.util.*

class WhiteboardController(
    private val roomName: String,
    private val roomId: Long,
    private val coroutineScope: CoroutineScope,
    private val onExit: () -> Unit
) {

    internal val components = mutableStateMapOf<String, Component>()
    internal var currentTool by mutableStateOf(WhiteboardToolbarOptions.SELECT)
    internal var whiteboardOffset by mutableStateOf(Offset(0f, 0f))
    internal var whiteboardZoom by mutableStateOf(1f)
    internal var whiteboardSize by mutableStateOf(Size.Zero)
    internal var queryBoxController by mutableStateOf(QueryBoxController())
    internal var editController by mutableStateOf(EditController())

    internal val webSocketEventHandler = WebSocketEventHandler(
        username = UserManager.getUsername() ?: "default_user",
        roomId = "$roomId",
        coroutineScope = coroutineScope,
        whiteboardController = this
    )

    internal var cursorsController by mutableStateOf(webSocketEventHandler.cursorsController)
    internal var userLobbyController by mutableStateOf(webSocketEventHandler.userLobbyController)
    internal var pingController by mutableStateOf(webSocketEventHandler.pingController)

    private var lastComponentId = ""
    private var currentDepth = 0f
    internal var isDraggingSelectionBox = false
    internal var isResizingSelectionBox = false

    internal val clipboard = Clipboard

    init {
        CommandFactory.whiteboardController = this
        snapshotFlow { currentTool }
            .onEach {
                cursorsController.currentCursor = it.cursorType()
            }
            .launchIn(coroutineScope)
    }

    fun exitWhiteboard() {
        onExit()
    }

    fun getWhiteboardTitle(): String {
        if (roomName.isNotEmpty()) return roomName
        return "Whiteboard"
    }

    fun teleportToUser(user: String) {
        cursorsController.friendCursorPositions[user]?.let {
            val userPosition = it.value
            val center = Offset(whiteboardSize.width / 2, whiteboardSize.height / 2)
            val newOffset = center.minus(userPosition)
            whiteboardZoom = 1f
            whiteboardOffset = newOffset
        }
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

    fun cutSelected() {
        copySelected()
        deleteSelected()
    }

    fun copySelected() {
        editController.selectionBoxData?.let {
            clipboard.copy(it.selectedComponents)
        }
    }

    fun pasteFromClipboard() {
        val selectionData = clipboard.paste()
        selectionData.forEach {
            it.depth = preIncrementCurrentDepth()
            components[it.uuid] = it
            webSocketEventHandler.componentEventController.add(it)
        }
        editController.clearSelectionBox()
        editController.selectedComponents(selectionData)
    }

    fun deleteSelected() {
        editController.selectionBoxData?.selectedComponents?.forEach {
            if (it.isEditable()) {
                components.remove(it.uuid)
                webSocketEventHandler.componentEventController.delete(it.uuid)
            }
        }
        editController.clearSelectionBox()
    }

    private fun preIncrementCurrentDepth(): Float {
        currentDepth = (components.size + 1) * Float.MIN_VALUE
        return currentDepth
    }

    fun handlePointerPosition(point: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(point)
        when (currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                editController.pointInResizeNode(whiteboardPoint, false)?.let {
                    if (!isResizingSelectionBox) {
                        cursorsController.currentCursor = it.getResizeCursorType()
                    }
                } ?: run {
                    if (!isResizingSelectionBox) {
                        cursorsController.currentCursor = CursorType.POINTER
                    }
                }
            }
            else -> return
        }
    }

    fun handleOnDragGestureStart(startPoint: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(startPoint)
        // Show emoji wheel
        if (Toolkit.shiftHolder) {
            pingController.startPingMenu(startPoint, whiteboardPoint)
            return
        }
        when (currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                if (editController.pointInResizeNode(whiteboardPoint, true) != null) {
                    isResizingSelectionBox = true
                } else if (editController.isPointInSelectionBox(whiteboardPoint)) {
                    isDraggingSelectionBox = true
                } else {
                    editController.clearSelectionBox()
                    getComponentAtPoint(whiteboardPoint)?.let {
                        editController.selectedSingleComponent(it)
                        isDraggingSelectionBox = true
                    } ?: run {
                        queryBoxController.startQueryBox(whiteboardPoint)
                    }
                }
            }
            WhiteboardToolbarOptions.PEN, WhiteboardToolbarOptions.HIGHLIGHTER, WhiteboardToolbarOptions.PAINT -> {
                editController.clearSelectionBox()
                val whiteboardStartPoint = viewToWhiteboardCoordinate(startPoint)
                val compController = WeakReference(webSocketEventHandler.componentEventController)
                val componentUUID = UUID.randomUUID().toString()
                val path = Path(
                    uuid= componentUUID,
                    controller=WeakReference(webSocketEventHandler.componentEventController),
                    coordinate = attributeWrapper(whiteboardStartPoint, compController, componentUUID),
                    size = attributeWrapper(Size(1f, 1f), compController, componentUUID ),
                    depth = preIncrementCurrentDepth(),
                    owner = UserManager.getUsername() ?: "default_user",
                    type = attributeWrapper(currentTool.getPathType(), compController, componentUUID)
                )
                path.insertPoint(whiteboardStartPoint)
                components[path.uuid] = path
                webSocketEventHandler.componentEventController.add(path)
                lastComponentId = path.uuid
            }
            WhiteboardToolbarOptions.PAN -> {
                cursorsController.currentCursor = CursorType.GRAB
            }
            WhiteboardToolbarOptions.ERASE -> {
                editController.clearSelectionBox()
                useEraser(getComponentAtPoint(whiteboardPoint)?.uuid)
            }
            else -> {
                editController.clearSelectionBox()
                return
            }
        }
    }

    fun handleOnDragGesture(change: PointerInputChange, dragAmount: Offset) {
        val whiteboardPoint = viewToWhiteboardCoordinate(change.position)
        if (Toolkit.shiftHolder && pingController.pingWheelData != null) {
            pingController.updatePing(change.position)
            return
        }
        pingController.clearPingWheel()

        when(currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                if (isResizingSelectionBox) {
                    editController.resizeSelectedComponents(whiteboardPoint, whiteboardZoom)
                } else if (isDraggingSelectionBox) {
                    editController.moveSelectedComponents(dragAmount.div(whiteboardZoom))
                } else {
                    queryBoxController.updateQueryBox(whiteboardPoint)
                }
            }
            WhiteboardToolbarOptions.PAN -> {
                whiteboardOffset = whiteboardOffset.plus(dragAmount.div(whiteboardZoom))
            }
            WhiteboardToolbarOptions.PEN, WhiteboardToolbarOptions.HIGHLIGHTER, WhiteboardToolbarOptions.PAINT -> {
                components[lastComponentId]?.let {
                    if (it !is Path) return
                    it.insertPoint(whiteboardPoint)
                }
            }
            WhiteboardToolbarOptions.ERASE -> {
                useEraser(getComponentAtPoint(whiteboardPoint)?.uuid)
            }
            else -> { return }
        }
    }

    fun handleOnDragGestureEnd() {
        if (Toolkit.shiftHolder && pingController.pingWheelData != null) {
            pingController.sendPingIfNeeded()
            return
        }
        pingController.clearPingWheel()
        when(currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                if (isResizingSelectionBox) {
                    editController.forceSelectedComponentsSizeUpdate()
                } else if (isDraggingSelectionBox) {
                    editController.forceSelectedComponentsPositionUpdate()
                }
                isResizingSelectionBox = false
                isDraggingSelectionBox = false
                val queriedComponents = queryBoxController.getComponentsInQueryBox(components.values.toList())
                if (queriedComponents.isNotEmpty()) {
                    editController.selectedComponents(queriedComponents)
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
        editController.clearSelectionBox()
        when(currentTool) {
            WhiteboardToolbarOptions.SELECT -> {
                val selectedComponent = getComponentAtPoint(whiteboardPoint)
                selectedComponent?.let {
                    editController.selectedSingleComponent(it)
                }
            }
            WhiteboardToolbarOptions.PEN,
            WhiteboardToolbarOptions.HIGHLIGHTER,
            WhiteboardToolbarOptions.PAINT -> {
                val compController = WeakReference(webSocketEventHandler.componentEventController)
                val componentUUID = UUID.randomUUID().toString()
                val path = Path(
                    uuid= componentUUID,
                    controller=WeakReference(webSocketEventHandler.componentEventController),
                    coordinate = attributeWrapper(whiteboardPoint, compController, componentUUID),
                    size = attributeWrapper(Size(1f, 1f), compController, componentUUID ),
                    depth = preIncrementCurrentDepth(),
                    owner = UserManager.getUsername() ?: "default_user",
                    type = attributeWrapper(currentTool.getPathType(), compController, componentUUID)
                )
                path.insertPoint(whiteboardPoint)
                path.insertPoint(whiteboardPoint) // no updateUUID needed because we are adding
                components[path.uuid] = path
                webSocketEventHandler.componentEventController.add(path) // we add
            }
            WhiteboardToolbarOptions.SQUARE,
            WhiteboardToolbarOptions.RECTANGLE,
            WhiteboardToolbarOptions.TRIANGLE,
            WhiteboardToolbarOptions.CIRCLE -> {
                val compController = WeakReference(webSocketEventHandler.componentEventController)
                val componentUUID = UUID.randomUUID().toString()
                val shape = Shape(
                    uuid = componentUUID,
                    controller = compController,
                    coordinate = attributeWrapper(whiteboardPoint, compController, componentUUID),
                    depth = preIncrementCurrentDepth(),
                    owner = UserManager.getUsername() ?: "default_user",
                    type = attributeWrapper(currentTool.getShapeType(), compController, componentUUID)
                )
                components[shape.uuid] = shape
                editController.selectedSingleComponent(shape)
                currentTool = WhiteboardToolbarOptions.SELECT
                webSocketEventHandler.componentEventController.add(shape)
            }
            WhiteboardToolbarOptions.TEXT -> {
                val compController = WeakReference(webSocketEventHandler.componentEventController)
                val componentUUID = UUID.randomUUID().toString()
                val textBox = TextBox(
                    uuid = componentUUID,
                    controller = compController,
                    coordinate = attributeWrapper(whiteboardPoint, compController, componentUUID),
                    depth = preIncrementCurrentDepth(),
                    owner = UserManager.getUsername() ?: "default_user",
                    initialWord = ""
                )
                components[textBox.uuid] = textBox
                editController.selectedSingleComponent(textBox)
                currentTool = WhiteboardToolbarOptions.SELECT
                webSocketEventHandler.componentEventController.add(textBox)
            }
            WhiteboardToolbarOptions.AI_IMAGE -> {
                val compController = WeakReference(webSocketEventHandler.componentEventController)
                val componentUUID = UUID.randomUUID().toString()
                val image = AIGeneratedImage(
                    uuid = componentUUID,
                    controller = compController,
                    coordinate = attributeWrapper(whiteboardPoint, compController, componentUUID),
                    depth = preIncrementCurrentDepth(),
                    owner = UserManager.getUsername() ?: "default_user"
                )
                components[image.uuid] = image
                editController.selectedSingleComponent(image)
                currentTool = WhiteboardToolbarOptions.SELECT
                webSocketEventHandler.componentEventController.add(image)
            }
            WhiteboardToolbarOptions.ERASE -> {
                useEraser(getComponentAtPoint(whiteboardPoint)?.uuid)
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
                    component.coordinate.getValue(),
                    component.size.getValue())
            ) {
                componentsAtPoint.add(component)
            }
        }
        return componentsAtPoint.maxByOrNull { it.depth }
    }

    fun addComponent(state: ComponentState) {
        var component: Component? = components[state.uuid]
        if (component == null) {
            component = state.toComponent(webSocketEventHandler)
            components[state.uuid] = component
            return
        }
    }

    private fun useEraser(componentUUID: String?) {
        components[componentUUID]?.let {
            if (it.isEditable()) {
                components.remove(it.uuid)
                webSocketEventHandler.componentEventController.delete(it.uuid)
            }
        }
    }

    fun deleteComponent(deleteComponent: DeleteComponent) {
        components.remove(deleteComponent.uuid)
        editController.selectionBoxData?.let {
            if (it.selectedComponents.size == 1 && it.selectedComponents.first().uuid == deleteComponent.uuid) {
                editController.clearSelectionBox()
            } else {
                it.selectedComponents.removeIf { selected ->
                    selected.uuid == deleteComponent.uuid
                }
            }
        }
    }

    suspend fun applyServerUpdate(componentUpdate: ComponentUpdate) {
        var component: Component = components[componentUpdate.uuid] ?: return

        component.applyServerUpdate(componentUpdate)
    }

    fun setState(state: WhiteboardState) {
        components.clear()

        state.components.forEach {
            components[it.key] = it.value.toComponent(webSocketEventHandler)
        }
    }

    fun getRoomId() : Long = roomId
}