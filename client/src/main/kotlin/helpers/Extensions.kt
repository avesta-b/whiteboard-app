package cs346.whiteboard.client.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.client.whiteboard.components.*
import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.ComponentType
import cs346.whiteboard.shared.jsonmodels.Position
import cs346.whiteboard.shared.jsonmodels.Size
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

@Composable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())

fun Position.toOffset() : Offset {
    return Offset(x, y)
}

fun Size.toSize(): androidx.compose.ui.geometry.Size {
    return androidx.compose.ui.geometry.Size(width, height)
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

fun ComponentState.toComponent(eventHandler: WebSocketEventHandler): Component {
    when(componentType) {
        ComponentType.TEXT_BOX -> {
            return TextBox(
                uuid = uuid,
                coordinate = mutableStateOf(position.toOffset()),
                size = mutableStateOf(size.toSize()),
                depth = depth,
                initialWord = text ?: "",
                webSocketEventHandler = WeakReference(eventHandler)
            )
        }

        ComponentType.PATH -> {
            val path = Path(
                coordinate = mutableStateOf(position.toOffset()),
                size = mutableStateOf(size.toSize()),
                depth = depth,
                uuid = uuid
            )
            this.path?.forEach { path.insertPoint(it.toOffset()) }
            return path
        }

        else -> { // Square and Circle case
            val type: ShapeTypes = if (componentType == ComponentType.SQUARE) { ShapeTypes.SQUARE }
            else { ShapeTypes.CIRCLE }
            return Shape(
                uuid = uuid,
                coordinate = mutableStateOf(position.toOffset()),
                size = mutableStateOf(size.toSize()),
                depth = depth,
                type = type
            )
        }

    }
}