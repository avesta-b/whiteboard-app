/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.constants

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(18.dp)
)

fun Shapes.small(scale: Float): RoundedCornerShape {
    return RoundedCornerShape((6 * scale).dp)
}

fun Shapes.medium(scale: Float): RoundedCornerShape {
    return RoundedCornerShape((12 * scale).dp)
}

val Shapes.triangle: GenericShape
    get() = GenericShape { size, _ ->
        // 1)
        moveTo(size.width / 2f, 0f)
    
        // 2)
        lineTo(size.width, size.height)
    
        // 3)
        lineTo(0f, size.height)
    }