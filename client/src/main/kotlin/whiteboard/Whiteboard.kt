package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize
import cs346.whiteboard.client.helpers.toDp

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
        whiteboardController.componentList.forEach {
            val componentViewCoordinate = whiteboardController.whiteboardToViewCoordinate(it.coordinate.value)
            it.drawComposableComponent(
                Modifier
                    .wrapContentSize(Alignment.TopStart, true)
                    .offset(componentViewCoordinate.x.toDp(), componentViewCoordinate.y.toDp())
                    .size((it.size.value.width * whiteboardController.whiteboardZoom).toDp(),
                        (it.size.value.height * whiteboardController.whiteboardZoom).toDp()),
                whiteboardController
            )
        }
    }
}