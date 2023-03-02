package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize

object WhiteboardLayerZIndices {
    const val background: Float = 0f
    const val selectionBox: Float = 1f
    const val queryBox: Float = 2f
    const val cursors: Float = 3f
}


@Composable
fun Whiteboard(
    whiteboardController: WhiteboardController,
    modifier: Modifier
) {
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

    ) {
        // Background
        Background(whiteboardController)

        // Cursors
        Cursors(whiteboardController)

        // Components
        whiteboardController.components.forEach { (_, component) ->
            component.drawComposableComponent(whiteboardController)
        }

        // Selection box
        whiteboardController.selectionBoxController.selectionBoxData?.let {
            SelectionBox(whiteboardController, it)
        }

        // Query box
        whiteboardController.queryBoxController.queryBoxData?.let {
            QueryBox(whiteboardController, it)
        }
    }
}