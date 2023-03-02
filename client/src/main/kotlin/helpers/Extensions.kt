package cs346.whiteboard.client.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())