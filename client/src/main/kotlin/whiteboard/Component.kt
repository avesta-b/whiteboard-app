package cs346.whiteboard.client.whiteboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

interface Component {

    var coordinate: MutableState<Offset>

    var size: MutableState<Size>

    @Composable
    fun drawComposableComponent(modifier: Modifier, controller: WhiteboardController)

}