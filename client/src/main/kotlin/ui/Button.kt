package cs346.whiteboard.client.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors
import cs346.whiteboard.client.constants.Shapes
import cs346.whiteboard.client.helpers.CustomIcon

@Composable
fun PrimaryButton(modifier: Modifier,
                  text: String,
                  enabled: Boolean,
                  onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Colors.primary,
            contentColor = Colors.background,
            disabledBackgroundColor = Colors.primary,
            disabledContentColor = Colors.background
        )) {
        if (enabled) {
            PrimaryButtonText(text)
        } else {
            PrimaryButtonSpinner()
        }
    }
}

@Composable
fun OutlinedButton(modifier: Modifier,
                   text: String,
                   enabled: Boolean,
                   onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = Shapes.small,
        border = BorderStroke(1.dp, Colors.secondaryVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.secondaryVariant,
            disabledContentColor = Colors.secondaryVariant)
    ) {
        PrimarySubtitleText(text)
    }
}

@Composable
fun UnderlinedTextButton(modifier: Modifier,
                         text: String,
                         enabled: Boolean,
                         onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = Shapes.small,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.secondaryVariant,
            disabledContentColor = Colors.secondaryVariant
        )
    ) {
        UnderlinedText(text)
    }
}

@Composable
fun CustomIconButton(modifier: Modifier,
                     icon: CustomIcon,
                     onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Colors.background,
            contentColor = Colors.primary
        ),
        shape = Shapes.small,
        border = BorderStroke(1.dp, Colors.secondaryVariant),
    ) {
        Image(
            painterResource(icon.path()),
            null,
            modifier
        )
    }
}