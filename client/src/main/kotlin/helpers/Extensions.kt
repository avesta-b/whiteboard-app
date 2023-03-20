package cs346.whiteboard.client.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import cs346.whiteboard.client.constants.Typography
import cs346.whiteboard.client.constants.textBoxComic
import cs346.whiteboard.client.constants.textBoxDefault
import cs346.whiteboard.client.constants.textBoxMono
import cs346.whiteboard.client.websocket.WebSocketEventHandler
import cs346.whiteboard.client.whiteboard.components.*
import cs346.whiteboard.shared.jsonmodels.*
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

fun ComponentColor.toColor(): Color {
    return when(this) {
        ComponentColor.BLACK -> Color.Black
        ComponentColor.RED -> Color.Red
        ComponentColor.ORANGE -> Color(255, 165, 0)
        ComponentColor.YELLOW -> Color.Yellow
        ComponentColor.GREEN -> Color.Green
        ComponentColor.BLUE -> Color.Blue
        ComponentColor.PURPLE -> Color(160, 32, 240)
        ComponentColor.WHITE -> Color.White
    }
}

fun TextFont.toTextStyle(size: Float): TextStyle {
    return when(this) {
        TextFont.DEFAULT -> Typography.textBoxDefault(size)
        TextFont.COMIC -> Typography.textBoxComic(size)
        TextFont.MONO -> Typography.textBoxMono(size)
    }
}

fun ComponentState.toComponent(eventHandler: WebSocketEventHandler): Component {
    when(componentType) {
        ComponentType.TEXT_BOX -> {
            return TextBox(
                uuid = uuid,
                coordinate = mutableStateOf(position.toOffset()),
                size = mutableStateOf(size.toSize()),
                color = mutableStateOf(color),
                depth = depth,
                font = mutableStateOf(textFont ?: TextFont.DEFAULT),
                fontSize = mutableStateOf(textSize ?: TextSize.SMALL),
                initialWord = text ?: "",
                webSocketEventHandler = WeakReference(eventHandler)
            )
        }
        ComponentType.PATH -> {
            val path = Path(
                coordinate = mutableStateOf(position.toOffset()),
                size = mutableStateOf(size.toSize()),
                color = mutableStateOf(color),
                depth = depth,
                uuid = uuid,
                type = mutableStateOf(pathType ?: PathType.BRUSH),
                thickness = mutableStateOf(pathThickness ?: PathThickness.THIN)
            )
            this.path?.forEach { path.insertPoint(it.toOffset()) }
            return path
        }
        ComponentType.SHAPE -> {
            return Shape(
                uuid = uuid,
                coordinate = mutableStateOf(position.toOffset()),
                size = mutableStateOf(size.toSize()),
                color = mutableStateOf(color),
                depth = depth,
                type = mutableStateOf(shapeType ?: ShapeType.SQUARE),
                fill = mutableStateOf(shapeFill ?: ShapeFill.OUTLINE)
            )
        }
    }
}