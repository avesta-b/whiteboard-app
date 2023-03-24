package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.helpers.*
import cs346.whiteboard.client.ui.OutlinedButton
import cs346.whiteboard.client.ui.PrimarySubtitleText
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import cs346.whiteboard.shared.jsonmodels.*
import java.awt.Cursor

@Composable
fun EditPane(whiteboardController: WhiteboardController, data: SelectionBoxData, modifier: Modifier) {
    val attributes = data.selectedComponents.fold(EditPaneAttribute.values().toList()) { accumulated, component ->
        (accumulated.intersect(component.editPaneAttributes.toSet())).toList()
    }
    Column(
        modifier = modifier
            .width(350.dp)
            .height(IntrinsicSize.Min)
            .padding(16.dp)
            .border(1.dp, Colors.secondaryVariant, Shapes.small)
            .shadow(16.dp, Shapes.small, true)
            .background(Colors.background)
            .clip(Shapes.small)
            .padding(16.dp)
            .zIndex(WhiteboardLayerZIndices.editPane)
            .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor())),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        attributes.forEach {
            when (it) {
                EditPaneAttribute.COLOR -> {
                    val sharedComponentColor = whiteboardController.editController.selectedComponentsSharedColor()
                    PrimarySubtitleText(text = "Color")
                    Column (verticalArrangement = Arrangement.Top) {
                        Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ComponentColor.values().toList().subList(0, 4).forEach { color ->
                                OutlinedButton(
                                    selected = color == sharedComponentColor,
                                    onClick = {
                                        whiteboardController.editController.setColorSelectedComponents(color)
                                        data.selectedComponents.forEach { component ->
                                            whiteboardController.sendComponentUpdate(component)
                                        }
                                    }
                                ) {
                                    Box(Modifier
                                        .size(16.dp)
                                        .border(1.dp, Colors.secondaryVariant, CircleShape)
                                        .background(color.toColor(), CircleShape))
                                }
                            }
                        }
                        Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ComponentColor.values().toList().subList(4, 8).forEach { color ->
                                OutlinedButton(
                                    selected = color == sharedComponentColor,
                                    onClick = {
                                        whiteboardController.editController.setColorSelectedComponents(color)
                                        data.selectedComponents.forEach { component ->
                                            whiteboardController.sendComponentUpdate(component)
                                        }
                                    }
                                ) {
                                    Box(Modifier
                                        .size(16.dp)
                                        .border(1.dp, Colors.secondaryVariant, CircleShape)
                                        .background(color.toColor(), CircleShape))
                                }
                            }
                        }
                    }
                }
                EditPaneAttribute.PATH_TYPE -> {
                    val sharedPathType = whiteboardController.editController.selectedComponentsSharedPathType()
                    PrimarySubtitleText(text = "Path Type")
                    Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PathType.values().forEach { type ->
                            OutlinedButton(
                                selected = type == sharedPathType,
                                onClick = {
                                    whiteboardController.editController.setPathTypeSelectedComponents(type)
                                    data.selectedComponents.forEach { component ->
                                        whiteboardController.sendComponentUpdate(component)
                                    }
                                }
                            ) {
                                Image(
                                    painter = painterResource(type.toIcon().path()),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
                EditPaneAttribute.PATH_THICKNESS -> {
                    val sharedThickness = whiteboardController.editController.selectedComponentsSharedThickness()
                    PrimarySubtitleText(text = "Thickness")
                    Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PathThickness.values().forEach { thickness ->
                            OutlinedButton(
                                selected = thickness == sharedThickness,
                                onClick = {
                                    whiteboardController.editController.setThicknessSelectedComponents(thickness)
                                    data.selectedComponents.forEach { component ->
                                        whiteboardController.sendComponentUpdate(component)
                                    }
                                }
                            ) {
                                Box(Modifier
                                    .width(16.dp)
                                    .height((thickness.toFloat() / 8).dp)
                                    .background(Colors.primary, CircleShape))
                            }
                        }
                    }
                }
                EditPaneAttribute.SHAPE_FILL -> {
                    val sharedFill = whiteboardController.editController.selectedComponentsSharedFill()
                    PrimarySubtitleText(text = "Fill")
                    Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ShapeFill.values().forEach { fill ->
                            OutlinedButton(
                                selected = fill == sharedFill,
                                onClick = {
                                    whiteboardController.editController.setFillSelectedComponents(fill)
                                    data.selectedComponents.forEach { component ->
                                        whiteboardController.sendComponentUpdate(component)
                                    }
                                }
                            ) {
                                PrimarySubtitleText(text = fill.description(), modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
                EditPaneAttribute.TEXT_FONT -> {
                    val sharedFont = whiteboardController.editController.selectedComponentsSharedFont()
                    PrimarySubtitleText(text = "Font")
                    Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextFont.values().forEach { font ->
                            OutlinedButton(
                                modifier = Modifier.height(56.dp),
                                selected = font == sharedFont,
                                onClick = {
                                    whiteboardController.editController.setFontSelectedComponents(font)
                                    data.selectedComponents.forEach { component ->
                                        whiteboardController.sendComponentUpdate(component)
                                    }
                                }
                            ) {
                                PrimarySubtitleText("Aa", font.toTextStyle(16f), Modifier.wrapContentSize())
                            }
                        }
                    }
                }
                EditPaneAttribute.TEXT_SIZE -> {
                    val sharedFontSize = whiteboardController.editController.selectedComponentsSharedFontSize()
                    PrimarySubtitleText(text = "Font Size")
                    Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextSize.values().forEach { size ->
                            OutlinedButton(
                                modifier = Modifier.height(56.dp),
                                selected = size == sharedFontSize,
                                onClick = {
                                    whiteboardController.editController.setFontSizeSelectedComponents(size)
                                    data.selectedComponents.forEach { component ->
                                        whiteboardController.sendComponentUpdate(component)
                                    }
                                }
                            ) {
                                PrimarySubtitleText(size.description(), TextFont.DEFAULT.toTextStyle(12f + size.toFloat() / 16f), Modifier.wrapContentSize())
                            }
                        }
                    }
                }
            }
        }
        PrimarySubtitleText(text = "Actions")
        Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                selected = false,
                onClick = {
                    whiteboardController.copySelected()
                    whiteboardController.pasteFromClipboard()
                }
            ) {
                Image(
                    painter = painterResource(CustomIcon.COPY.path()),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
            OutlinedButton(
                selected = false,
                onClick = {
                    whiteboardController.deleteSelected()
                }
            ) {
                Image(
                    painter = painterResource(CustomIcon.DELETE.path()),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}