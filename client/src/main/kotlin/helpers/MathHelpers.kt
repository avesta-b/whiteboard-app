package cs346.whiteboard.client.helpers

import androidx.compose.ui.geometry.Offset
import kotlin.math.pow
import kotlin.math.sqrt


fun distanceBetween(point1: Offset, point2: Offset): Float {
    return sqrt((point1.x - point2.x).pow(2) + (point1.y - point2.y).pow(2))
}