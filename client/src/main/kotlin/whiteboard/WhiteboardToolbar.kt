package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes

enum class WhiteboardToolbarOptions {
    PEN {
        override fun icon() = Icons.Outlined.Draw
    },
    SQUARE {
        override fun icon() = Icons.Outlined.Square
    },
    CIRCLE {
        override fun icon() = Icons.Outlined.Circle
    },
    TEXT {
        override fun icon() = Icons.Outlined.TextFields
    };

    abstract fun icon(): ImageVector
}
@Composable
fun WhiteboardToolbar(whiteboardController: WhiteboardController, modifier: Modifier) {
    Row(modifier = modifier
        .border(1.dp, Colors.secondaryVariant, Shapes.medium)
        .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier.weight(1.0f))
        enumValues<WhiteboardToolbarOptions>().forEach {
            IconButton(onClick = {
                whiteboardController.setCurrentTool(it)
            }) {
                Icon(it.icon(), it.name)
            }
        }
        Spacer(modifier.weight(1.0f))
    }
}