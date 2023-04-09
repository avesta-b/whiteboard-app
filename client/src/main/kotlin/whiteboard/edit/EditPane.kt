package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.settings.MenuBarState
import cs346.whiteboard.client.commands.WhiteboardEventHandler
import cs346.whiteboard.client.constants.Inter
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography
import cs346.whiteboard.client.helpers.*
import cs346.whiteboard.client.ui.*
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.WhiteboardLayerZIndices
import cs346.whiteboard.shared.jsonmodels.*
import kotlinx.coroutines.launch
import java.awt.Cursor

@Composable
fun EditPane(whiteboardController: WhiteboardController, data: SelectionBoxData, modifier: Modifier) {
    var attributes = data.selectedComponents.fold(EditPaneAttribute.values().toList()) { accumulated, component ->
        if (!component.isEditable()) accumulated else
        (accumulated.intersect(component.editPaneAttributes.toSet())).toList()
    }.toMutableList()
    if (data.selectedComponents.any { !it.isOwnedByCurrentUser() }) {
        attributes.remove(EditPaneAttribute.ACCESS_LEVEL)
    }
    val locked = data.selectedComponents.all { !it.isEditable() }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .width(350.dp)
            .height(IntrinsicSize.Min)
            .padding(16.dp)
            .border(1.dp, WhiteboardColors.secondaryVariant, Shapes.small)
            .shadow(16.dp, Shapes.small, true)
            .background(WhiteboardColors.background)
            .clip(Shapes.small)
            .padding(16.dp)
            .zIndex(WhiteboardLayerZIndices.editPane)
            .pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor())),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (!locked) {
            attributes.forEach {
                when (it) {
                    EditPaneAttribute.COLOR -> {
                        val sharedComponentColor = whiteboardController.editController.selectedComponentsSharedColor()
                        PrimarySubtitleText(text = "Color")
                        Column(verticalArrangement = Arrangement.Top) {
                            Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ComponentColor.values().toList().subList(0, 4).forEach { color ->
                                    OutlinedButton(
                                        selected = color == sharedComponentColor,
                                        onClick = {
                                            whiteboardController.editController.setColorSelectedComponents(color)
                                        }
                                    ) {
                                        Box(
                                            Modifier
                                                .size(16.dp)
                                                .border(1.dp, WhiteboardColors.secondaryVariant, CircleShape)
                                                .background(color.toColor(), CircleShape)
                                        )
                                    }
                                }
                            }
                            Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ComponentColor.values().toList().subList(4, 8).forEach { color ->
                                    OutlinedButton(
                                        selected = color == sharedComponentColor,
                                        onClick = {
                                            whiteboardController.editController.setColorSelectedComponents(color)
                                        }
                                    ) {
                                        Box(
                                            Modifier
                                                .size(16.dp)
                                                .border(1.dp, WhiteboardColors.secondaryVariant, CircleShape)
                                                .background(color.toColor(), CircleShape)
                                        )
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
                                    }
                                ) {
                                    Box(
                                        Modifier
                                            .width(16.dp)
                                            .height((thickness.toFloat() / 8).dp)
                                            .background(WhiteboardColors.primary, CircleShape)
                                    )
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
                                    }
                                ) {
                                    PrimarySubtitleText(
                                        size.description(),
                                        TextFont.DEFAULT.toTextStyle(12f + size.toFloat() / 16f),
                                        Modifier.wrapContentSize()
                                    )
                                }
                            }
                        }
                    }

                    EditPaneAttribute.IMAGE_PROMPT -> {
                        val imageData = whiteboardController.editController.selectedComponentImageData()
                        imageData?.let { imageData ->
                            imageData.prompt?.let { prompt ->
                                PrimarySubtitleText(text = "Current Prompt")
                                SecondaryBodyText(prompt)
                            }
                            PrimarySubtitleText(text = "Generate AI Image")
                            val promptText = mutableStateOf(TextFieldValue(""))
                            PromptTextFieldWithButton(
                                text = promptText,
                                placeholder = "Enter prompt",
                                enabled = !whiteboardController.editController.shouldShowImageLoading(),
                                modifier = Modifier,
                                textFieldModifier = Modifier
                                    .onFocusChanged { focusState ->
                                        WhiteboardEventHandler.isEditingText = focusState.isFocused
                                        MenuBarState.isToolEnabled = !focusState.isFocused
                                    },
                                onClick = {
                                    if (promptText.value.text.isNotEmpty()) {
                                        coroutineScope.launch {
                                            whiteboardController.editController.fetchAIImage(promptText.value.text)
                                        }
                                    }
                                }
                            )
                            ErrorText("Try again", whiteboardController.editController.shouldShowPromptFail())
                        }
                    }

                    EditPaneAttribute.ACCESS_LEVEL -> {
                        val sharedAccessLevel =
                            whiteboardController.editController.selectedComponentsSharedAccessLevel()
                        PrimarySubtitleText(text = "Edit Access")
                        Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AccessLevel.values().forEach { accessLevel ->
                                OutlinedButton(
                                    selected = accessLevel == sharedAccessLevel,
                                    onClick = {
                                        whiteboardController.editController.setAccessLevelSelectedComponents(
                                            accessLevel
                                        )
                                    }
                                ) {
                                    Image(
                                        painter = painterResource(accessLevel.toIcon().path()),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
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
            if (!locked) {
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
        if (locked) {
            Spacer(Modifier.height(16.dp))
            Row (
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(AccessLevel.LOCKED.toIcon().path()),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit
                )
                if (data.selectedComponents.size == 1) {
                    PrimarySubtitleText(text = "Component Locked By ${data.selectedComponents.first().owner}")
                } else {
                    PrimarySubtitleText(text = "Components Locked")
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun PromptTextFieldWithButton(
    text: MutableState<TextFieldValue>,
    placeholder: String,
    enabled: Boolean,
    icon: CustomIcon = CustomIcon.ARROW,
    modifier: Modifier,
    textFieldModifier: Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                singleLine = true,
                enabled = enabled,
                textStyle = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = WhiteboardColors.primary
                ),
                modifier = textFieldModifier
                    .height(40.dp)
                    .weight(1f)
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .onKeyEvent { event ->
                        when (event.key) {
                            Key.Enter -> {
                                onClick()
                                true
                            }
                            else -> false
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                ),
                cursorBrush = SolidColor(WhiteboardColors.primary)
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = WhiteboardColors.secondaryVariant,
                            shape = Shapes.small
                        )
                        .padding(10.dp, 12.dp, 10.dp, 10.dp)
                ) {
                    if (text.value.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Typography.body1,
                            color = WhiteboardColors.secondary
                        )
                    }
                    SelectionContainer {
                        innerTextField()
                    }
                }
            }
            CustomIconButton(
                icon = icon,
                onClick = onClick,
                modifier = buttonModifier.size(40.dp),
                shape = Shapes.small,
                iconPadding = 12.dp,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = WhiteboardColors.primary,
                    contentColor = WhiteboardColors.background,
                ),
                enabled = enabled
            )
        }
    }
}