package cs346.whiteboard.client.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.constants.Colors

@Composable
fun PrimaryButtonSpinner() {
    CircularProgressIndicator(
        modifier = Modifier.size(16.dp, 16.dp),
        color = Colors.background,
        strokeWidth = 2.dp
    )
}

@Composable
fun LargeSpinner() {
    CircularProgressIndicator(
        modifier = Modifier.size(32.dp, 32.dp),
        color = Colors.primary,
        strokeWidth = 4.dp
    )
}