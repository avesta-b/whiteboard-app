/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.helpers

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

fun getUserColor(username: String): Color {
    // Hash the input string to generate a unique number
    val hash = abs(username.hashCode())

    // Calculate the RGB values of the color
    val red = hash % 256
    val green = (hash / 256) % 256
    val blue = (hash / 65536) % 256

    // Create a new Color object with the calculated RGB values
    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f)
}
