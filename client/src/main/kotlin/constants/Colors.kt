package cs346.whiteboard.client.constants

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val Colors = Colors(
    primary = Color.Black,
    primaryVariant = Color.Black,
    secondary = Color(0xFF94A3B8),
    secondaryVariant= Color(0xFFCBD5E1),
    background = Color.White,
    surface = Color.White,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
    isLight = false
)

val Colors.backgroundDotColor: Color
    get() = Color(0xFFE6E6E6)

val Colors.queryBoxColor: Color
    get() = Colors.secondaryVariant.copy(alpha = 0.2f)

val Colors.highlightedIconButtonColor: Color
    get() = Colors.secondaryVariant.copy(alpha = 0.4f)