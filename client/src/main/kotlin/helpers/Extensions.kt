package cs346.whiteboard.client.helpers

import androidx.compose.runtime.Composable
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

fun PathType.toIcon(): CustomIcon {
    return when(this) {
        PathType.BRUSH -> CustomIcon.BRUSH
        PathType.HIGHLIGHTER -> CustomIcon.HIGHLIGHTER
        PathType.PAINT -> CustomIcon.PAINT
    }
}

fun PathThickness.toFloat(): Float {
    return when(this) {
        PathThickness.THIN -> 10f
        PathThickness.THICK -> 20f
        PathThickness.EXTRA_THICK -> 30f
    }
}

fun ShapeFill.description(): String {
    return when(this) {
        ShapeFill.FILL -> "Solid"
        ShapeFill.OUTLINE -> "Outline"
    }
}

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

fun TextSize.description(): String {
    return when(this) {
        TextSize.SMALL -> "Small"
        TextSize.MEDIUM -> "Medium"
        TextSize.LARGE -> "Large"
    }
}

fun TextSize.toFloat(): Float {
    return when(this) {
        TextSize.SMALL -> 16f
        TextSize.MEDIUM -> 32f
        TextSize.LARGE -> 64f
    }
}

fun AccessLevel.toIcon(): CustomIcon {
    return when(this) {
        AccessLevel.LOCKED -> CustomIcon.LOCK
        AccessLevel.UNLOCKED -> CustomIcon.UNLOCK
    }
}

fun EmojiPing.toText(): String {
    return when(this) {
        EmojiPing.THUMBS -> "\uD83D\uDC4D"
        EmojiPing.SMILE -> "\uD83D\uDE03"
        EmojiPing.SKULL -> "\uD83D\uDC80"
        EmojiPing.THINK -> "\uD83E\uDDD0"
    }
}

fun ComponentState.toComponent(eventHandler: WebSocketEventHandler): Component {
    val compController = WeakReference(eventHandler.componentEventController)
    when(componentType) {
        ComponentType.TEXT_BOX -> {
            return TextBox(
                uuid = uuid,
                controller = compController,
                coordinate = attributeWrapper(position.toOffset(), compController, uuid),
                size = attributeWrapper(size.toSize(), compController, uuid),
                color = attributeWrapper(color, compController, uuid),
                depth = depth,
                owner = owner,
                accessLevel = attributeWrapper(accessLevel, compController, uuid),
                font = attributeWrapper(textFont ?: TextFont.DEFAULT, compController, uuid),
                fontSize = attributeWrapper(textSize ?: TextSize.SMALL, compController, uuid),
                initialWord = text ?: ""
            )
        }
        ComponentType.PATH -> {
            val path = Path(
                uuid = uuid,
                coordinate = attributeWrapper(position.toOffset(), compController, uuid),
                controller = compController,
                size = attributeWrapper(size.toSize(), compController, uuid),
                color = attributeWrapper(color, compController, uuid),
                depth = depth,
                owner = owner,
                accessLevel = attributeWrapper(accessLevel, compController, uuid),
                type = attributeWrapper(pathType ?: PathType.BRUSH, compController, uuid),
                thickness = attributeWrapper(pathThickness ?: PathThickness.THIN, compController, uuid)
            )
            this.path?.forEach { path.insertLocalWithoutConfirm(it.toOffset()) }
            return path
        }
        ComponentType.SHAPE -> {
            return Shape(
                uuid = uuid,
                controller = compController,
                coordinate = attributeWrapper(position.toOffset(), compController, uuid),
                size = attributeWrapper(size.toSize(), compController, uuid),
                color = attributeWrapper(color, compController, uuid),
                depth = depth,
                owner = owner,
                accessLevel = attributeWrapper(accessLevel, compController, uuid),
                type = attributeWrapper(shapeType ?: ShapeType.SQUARE, compController, uuid),
                fill = attributeWrapper(shapeFill ?: ShapeFill.OUTLINE, compController, uuid)
            )
        }
    }
}