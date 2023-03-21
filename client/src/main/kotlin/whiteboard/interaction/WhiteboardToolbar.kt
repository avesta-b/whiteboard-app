package cs346.whiteboard.client.whiteboard.interaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.ui.CustomIconButton
import cs346.whiteboard.client.ui.SecondaryBodyText
import cs346.whiteboard.client.ui.SmallBodyText
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import cs346.whiteboard.client.whiteboard.overlay.CursorType
import cs346.whiteboard.shared.jsonmodels.PathType
import cs346.whiteboard.shared.jsonmodels.ShapeType
import java.awt.Cursor
import javax.tools.Tool

enum class WhiteboardToolbarOptions {
    SELECT {
        override fun icon() = CustomIcon.POINTER
        override fun cursorType() = CursorType.POINTER
    },
    PAN {
        override fun icon() = CustomIcon.HAND
        override fun cursorType() = CursorType.HAND
    },
    PEN {
        override fun icon() = CustomIcon.BRUSH
        override fun cursorType() = CursorType.BRUSH
    },
    HIGHLIGHTER {
        override fun icon() = CustomIcon.HIGHLIGHTER
        override fun cursorType() = CursorType.HIGHLIGHTER
    },
    PAINT {
        override fun icon() = CustomIcon.PAINT
        override fun cursorType() = CursorType.PAINT
    },
    SQUARE {
        override fun icon() = CustomIcon.SQUARE
        override fun cursorType() = CursorType.SQUARE
    },
    RECTANGLE {
        override fun icon() = CustomIcon.RECTANGLE
        override fun cursorType() = CursorType.RECTANGLE
    },
    TRIANGLE {
        override fun icon() = CustomIcon.TRIANGLE
        override fun cursorType() = CursorType.TRIANGLE
    },
    CIRCLE {
        override fun icon() = CustomIcon.CIRCLE
        override fun cursorType() = CursorType.CIRCLE
    },
    TEXT {
        override fun icon() = CustomIcon.TEXTFIELD
        override fun cursorType() = CursorType.TEXTFIELD
    },
    ERASE {
        override fun icon() = CustomIcon.ERASER
        override fun cursorType() = CursorType.ERASER
    };

    abstract fun icon(): CustomIcon
    abstract fun cursorType(): CursorType

    fun pathTools(): List<WhiteboardToolbarOptions> = listOf(PEN, HIGHLIGHTER, PAINT)

    fun shapeTools(): List<WhiteboardToolbarOptions> = listOf(SQUARE, RECTANGLE, TRIANGLE, CIRCLE)

    fun isPathTool(): Boolean {
        return pathTools().contains(this)
    }

    fun isShapeTool(): Boolean {
        return shapeTools().contains(this)
    }

    fun getPathType(): PathType {
        assert(isPathTool())
        return when(this) {
            PEN -> PathType.BRUSH
            HIGHLIGHTER -> PathType.HIGHLIGHTER
            PAINT -> PathType.PAINT
            else -> PathType.BRUSH
        }
    }

    fun getShapeType(): ShapeType {
        assert(isShapeTool())
        return when(this) {
            SQUARE -> ShapeType.SQUARE
            RECTANGLE -> ShapeType.RECTANGLE
            TRIANGLE -> ShapeType.TRIANGLE
            CIRCLE -> ShapeType.CIRCLE
            else -> ShapeType.SQUARE
        }
    }
}

@Composable
fun WhiteboardToolbar(whiteboardController: WhiteboardController) {

    val pathToolIndex = 2
    val shapeToolIndex = 3
    val toolList = remember {
        mutableStateListOf(
            WhiteboardToolbarOptions.SELECT,
            WhiteboardToolbarOptions.PAN,
            WhiteboardToolbarOptions.PEN,
            WhiteboardToolbarOptions.SQUARE,
            WhiteboardToolbarOptions.TEXT,
            WhiteboardToolbarOptions.ERASE
        )
    }

    Column (modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (whiteboardController.currentTool.isPathTool() || whiteboardController.currentTool.isShapeTool()) {
            val toolName = if (whiteboardController.currentTool.isPathTool()) "path" else "shape"
            SmallBodyText("Click to change $toolName tool")
        }
        Row(modifier = Modifier
            .width(350.dp)
            .height(IntrinsicSize.Min)
            .padding(16.dp)
            .border(1.dp, Colors.secondaryVariant, Shapes.small)
            .shadow(16.dp, Shapes.small, true)
            .background(Colors.background)
            .clip(Shapes.small)
            .zIndex(WhiteboardLayerZIndices.toolbar)
            .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor())),
            verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1.0f))
            toolList.forEach { tool ->
                Box {
                    CustomIconButton(
                        icon = tool.icon(),
                        shape = Shapes.large,
                        isHighlighted = whiteboardController.currentTool == tool,
                        onClick = {
                            var newTool = tool
                            if (tool.isPathTool() && whiteboardController.currentTool.isPathTool()) {
                                newTool = tool.pathTools()[(tool.pathTools().indexOf(tool) + 1) % 3]
                                toolList[pathToolIndex] = newTool
                            } else if (tool.isShapeTool() && whiteboardController.currentTool.isShapeTool()) {
                                newTool = tool.shapeTools()[(tool.shapeTools().indexOf(tool) + 1) % 4]
                                toolList[shapeToolIndex] = newTool
                            }
                            whiteboardController.currentTool = newTool
                        }
                    )
                }

            }
            Spacer(Modifier.weight(1.0f))
        }
    }

}