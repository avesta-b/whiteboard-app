package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.components.textSelectionColors
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.constants.Typography
import kotlin.math.roundToInt

class TextBox(override var coordinate: Offset, override var size: Size) : Component {

    val text = mutableStateOf(TextFieldValue(""))
    override fun drawCanvasComponent(drawScope: DrawScope) {

    }

    @Composable
    override fun drawComposableComponent(boxScope: BoxScope) {
        boxScope.also {
            ComposableTextField(
                text = text,
                modifier = Modifier
                    .size(120.dp, 100.dp)
                    .offset(coordinate.x.div(2).roundToInt().dp, coordinate.y.div(2).roundToInt().dp)
                // TODO: fix this later. i have no idea why this is correct :)
            )
        }
    }

    @Composable
    private fun ComposableTextField(text: MutableState<TextFieldValue>,
                                    modifier: Modifier) {
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            OutlinedTextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = modifier,
                textStyle = Typography.subtitle1,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false),
                singleLine = false,
                shape = Shapes.small,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Colors.primary,
                    disabledTextColor = Colors.secondaryVariant,
                    cursorColor = Colors.primary,
                    focusedBorderColor = Colors.primary,
                    unfocusedBorderColor = Colors.secondaryVariant,
                    disabledBorderColor = Colors.secondaryVariant,
                    focusedLabelColor = Colors.primary,
                    unfocusedLabelColor = Colors.secondaryVariant,
                    disabledLabelColor = Colors.secondaryVariant),
            )
        }
    }
}