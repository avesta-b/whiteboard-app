package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Whiteboard(
    whiteboardController: WhiteboardController,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        Canvas(
            modifier = modifier
                // handle drag gestures
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            whiteboardController.handleOnDragGestureStart(it)
                        },
                        onDrag = { change, _ ->
                            whiteboardController.handleOnDragGesture(change.position)
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
        ) {
            whiteboardController.componentList.forEach {
                it.drawCanvasComponent(this)
            }
        }
        whiteboardController.componentList.forEach {
            it.drawComposableComponent(this)
        }
    }
}