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
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes

enum class WhiteboardToolbarOptions {
    ZOOM_IN {
        override fun icon() = Icons.Outlined.ZoomIn
        override fun cursorType() = CursorType.POINTER
    },
    ZOOM_OUT {
        override fun icon() = Icons.Outlined.ZoomOut
        override fun cursorType() = CursorType.POINTER
    },
    SELECT {
        override fun icon() = Icons.Outlined.NearMe
        override fun cursorType() = CursorType.POINTER
    },
    PAN {
        override fun icon() = Icons.Outlined.PanTool
        override fun cursorType() = CursorType.HAND
    },
    PEN {
        override fun icon() = Icons.Outlined.Draw
        override fun cursorType() = CursorType.BRUSH
    },
    SQUARE {
        override fun icon() = Icons.Outlined.Square
        override fun cursorType() = CursorType.SHAPE
    },
    CIRCLE {
        override fun icon() = Icons.Outlined.Circle
        override fun cursorType() = CursorType.SHAPE
    },
    TEXT {
        override fun icon() = Icons.Outlined.TextFields
        override fun cursorType() = CursorType.TEXTFIELD
    };

    abstract fun icon(): ImageVector
    abstract fun cursorType(): CursorType
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
                when(it) {
                    WhiteboardToolbarOptions.ZOOM_IN -> {
                        whiteboardController.zoomIn()
                    }
                    WhiteboardToolbarOptions.ZOOM_OUT -> {
                        whiteboardController.zoomOut()
                    }
                    else -> {
                        whiteboardController.currentTool = it
                    }
                }
            }) {
                Icon(it.icon(), it.name)
            }
        }
        Spacer(modifier.weight(1.0f))
    }
}