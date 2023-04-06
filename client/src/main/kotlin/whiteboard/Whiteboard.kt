package cs346.whiteboard.client.whiteboard

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.ui.TextInputDialogWithAcceptAndCancel
import cs346.whiteboard.client.whiteboard.edit.EditPane
import cs346.whiteboard.client.whiteboard.edit.QueryBox
import cs346.whiteboard.client.whiteboard.edit.SelectionBox
import cs346.whiteboard.client.whiteboard.interaction.*
import cs346.whiteboard.client.whiteboard.overlay.Background
import cs346.whiteboard.client.whiteboard.overlay.Cursors
import cs346.whiteboard.client.whiteboard.overlay.Pings
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object WhiteboardLayerZIndices {
    const val background: Float = 0f
    const val cursors: Float = 1f
    const val pings: Float = 1f
    const val selectionBox: Float = 2f
    const val queryBox: Float = 3f
    const val editPane: Float = 4f
    const val zoomControl: Float = 4f
    const val toolbar: Float = 4f
    const val topBar: Float = 4f
    const val chat: Float = 4f
    const val pingWheel: Float = 5f
}

enum class WhiteboardViewState {
    WHITEBOARD, SHARING_DIALOG
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun Whiteboard(
    whiteboardController: WhiteboardController,
    modifier: Modifier
) {
    val initialTransitionState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    val coroutineScope = rememberCoroutineScope()

    var whiteboardState by remember { mutableStateOf(WhiteboardViewState.WHITEBOARD) }

    val shareInputState = remember { mutableStateOf(TextFieldValue("")) }

    when(whiteboardState) {
        WhiteboardViewState.SHARING_DIALOG -> {
            AnimatedVisibility(
                visible = whiteboardState == WhiteboardViewState.SHARING_DIALOG,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TextInputDialogWithAcceptAndCancel(
                        modifier = Modifier.width(400.dp).align(Alignment.Center),
                        onAccept = {
                            coroutineScope.launch {
                                UserManager.shareWhiteboards(
                                    roomId=whiteboardController.getRoomId(),
                                    userToBeSharedWith = shareInputState.value.text
                                )
                                shareInputState.value = TextFieldValue("")
                                whiteboardState = WhiteboardViewState.WHITEBOARD
                            }
                        },
                        onCancel = { whiteboardState = WhiteboardViewState.WHITEBOARD },
                        text = shareInputState,
                        placeholder = "Username",
                        smallTitle = "Share With User",
                        acceptText = "Share"
                    )
                }
            }
        }
        WhiteboardViewState.WHITEBOARD -> {
            Box(modifier = modifier.onPointerEvent(PointerEventType.Scroll) { WhiteboardEventHandler.onScrollEventHandler(it) }) {
                Box(modifier = modifier
                    // handle drag gestures
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                whiteboardController.handleOnDragGestureStart(it)
                            },
                            onDrag = { change, dragAmount ->
                                whiteboardController.handleOnDragGesture(change, dragAmount)
                            },
                            onDragEnd = {
                                whiteboardController.handleOnDragGestureEnd()
                            }
                        )
                    }
                    // handle tap gestures
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                whiteboardController.handleOnTapGesture(it)
                            }
                        )
                    }
                    .onGloballyPositioned {
                        whiteboardController.whiteboardSize = it.size.toSize()
                    }
                    .pointerInput(Unit) {
                        coroutineScope {
                            while (true) {
                                val position = awaitPointerEventScope {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.first().position
                                }
                                launch {
                                    whiteboardController.cursorsController.updateCursor(whiteboardController.viewToWhiteboardCoordinate(position))
                                    whiteboardController.handlePointerPosition(position)
                                }
                            }
                        }
                    }
                    .pointerHoverIcon(PointerIcon(whiteboardController.cursorsController.getCurrentCursor()))
                ) {
                    // Background
                    Background(whiteboardController)

                    // Cursors
                    Cursors(whiteboardController)

                    // Pings
                    Pings(whiteboardController)

                    // Components
                    whiteboardController.components.forEach { (_, component) ->
                        component.drawComposableComponent(whiteboardController)
                    }

                    // Selection box
                    whiteboardController.editController.selectionBoxData?.let {
                        SelectionBox(whiteboardController, it)
                    }

                    // Query box
                    whiteboardController.queryBoxController.queryBoxData?.let {
                        QueryBox(whiteboardController, it)
                    }
                }

                // Edit pane
                whiteboardController.editController.selectionBoxData?.let {
                    EditPane(whiteboardController, it, Modifier.align(Alignment.TopStart).padding(top = 50.dp))
                }

                AnimatedVisibility(
                    visibleState = initialTransitionState,
                    enter = slideInVertically(initialOffsetY = { 2 * it }) + fadeIn(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    // Toolbar
                    WhiteboardToolbar(whiteboardController)
                }

                AnimatedVisibility(
                    visibleState = initialTransitionState,
                    enter = slideInVertically(initialOffsetY = { 2 * it }) + fadeIn(),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    // Zoom control
                    WhiteboardZoomControl(whiteboardController)
                }

                // Top bar
                WhiteboardTopBar(whiteboardController, Modifier.align(Alignment.TopCenter), onShareClick = {
                    whiteboardState = WhiteboardViewState.SHARING_DIALOG
                })

                // Ping Wheel
                whiteboardController.pingController.pingWheelData?.let {
                    PingMenu(whiteboardController, it)
                }

                if (!whiteboardController.webSocketEventHandler.isDrawingAlone()) {
                    AnimatedVisibility(
                        visibleState = initialTransitionState,
                        enter = slideInVertically(initialOffsetY = { 2 * it }) + fadeIn(),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        // ChatBar
                        ChatBar(whiteboardController.webSocketEventHandler.chatController)
                    }
                }
            }
        }
    }

}
