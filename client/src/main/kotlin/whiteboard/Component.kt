package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

interface Component {

    var coordinate: Offset

    var size: Size
    fun drawCanvasComponent(drawScope: DrawScope)

    @Composable
    fun drawComposableComponent(boxScope: BoxScope)

}