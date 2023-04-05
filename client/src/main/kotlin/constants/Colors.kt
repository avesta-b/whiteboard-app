package cs346.whiteboard.client.constants

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cs346.whiteboard.client.MenuBarState

object WhiteboardColors {
    var isDarkMode by mutableStateOf(MenuBarState.menuBarPreferences.isDarkMode)
    val primary
        get() = if (isDarkMode) Color.White else Color.Black
    val secondary
        get() = if (isDarkMode) Color(0xFF64748b) else Color(0xFF94A3B8)
    val secondaryVariant
        get() = if (isDarkMode) Color(0xFF475569) else Color(0xFFCBD5E1)
    val textfieldSelectColor = secondaryVariant.copy(alpha = 0.6f)
    val background
        get() = if (isDarkMode) Color(0xFF1e293b) else Color.White
    val error = Color.Red
    val backgroundDotColor
        get() = if (isDarkMode) Color(0xFF334155) else Color(0xFFE6E6E6)
    val queryBoxColor = secondaryVariant.copy(alpha = 0.2f)
    val selectionNodeColor
        get() = if (isDarkMode) Color(0xFF64748b) else Color(0xFFf8fafc)
    val highlightedIconButtonColor = secondaryVariant.copy(alpha = 0.4f)
    val tooltipBackgroundColor = Color.Black
}
