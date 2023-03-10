package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.backgroundDotColor
import java.lang.Float.min

@Composable
fun Background(controller: WhiteboardController) {
    val center = remember { controller.viewToWhiteboardCoordinate(Offset(controller.whiteboardSize.width / 2, controller.whiteboardSize.height / 2)).plus(controller.whiteboardOffset) }
    Canvas(Modifier
        .fillMaxSize()
        .background(Colors.background)
        .zIndex(WhiteboardLayerZIndices.background)
    ) {
        val minPoint = controller.viewToWhiteboardCoordinate(Offset(0f, 0f))
        val maxPoint = controller.viewToWhiteboardCoordinate(Offset(size.width, size.height))
        val radius = 3f * controller.whiteboardZoom
        val distance = 40
        val drawDot: (Int, Int) -> Unit = { x, y ->
            if (x in minPoint.x.toInt()..maxPoint.x.toInt() && y in minPoint.y.toInt()..maxPoint.y.toInt()) {
                drawCircle(
                    brush = SolidColor(Colors.backgroundDotColor),
                    radius = radius,
                    center = controller.whiteboardToViewCoordinate(Offset(x.toFloat(), y.toFloat()))
                )
            }
        }
        val drawDotWithX: (Int) -> Unit = { x ->
            for (y in center.y.toInt() downTo center.y.toInt() - (size.height / min(controller.whiteboardZoom, 1f)).toInt() - controller.whiteboardOffset.y.toInt() step distance) {
                drawDot(x, y)
            }
            for (y in center.y.toInt() until center.y.toInt() + (size.height / min(controller.whiteboardZoom, 1f)).toInt() - controller.whiteboardOffset.y.toInt() step distance) {
                drawDot(x, y)
            }
        }
        // Splitting into two for-loops so we keep each dot's position relative to the center dot
        for (x in center.x.toInt() until center.x.toInt() + (size.width / min(controller.whiteboardZoom, 1f)).toInt() - controller.whiteboardOffset.x.toInt() step distance) {
            drawDotWithX(x)
        }
        for (x in center.x.toInt() downTo center.x.toInt() - (size.width / min(controller.whiteboardZoom, 1f)).toInt() - controller.whiteboardOffset.x.toInt()  step distance) {
            drawDotWithX(x)
        }
    }
}