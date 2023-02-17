package cs346.whiteboard.client

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cs346.whiteboard.shared.jsonmodels.CursorPosition
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Dimension

const val WINDOW_KEY = "window"
object WindowManager{
    @Serializable
    data class WindowSize(
        val width: Int,
        val height: Int
    )
    private var windowSize : WindowSize
        get() {
            PreferencesManager.readFromPreferences(WINDOW_KEY)?.let {
                return Json.decodeFromString(WindowSize.serializer(), it)
            }
            return WindowSize(1200, 800)
        }
        set(size) {
            PreferencesManager.writeToPreferencesWithKey(WINDOW_KEY, Json.encodeToString(size))
        }

    fun getWindowSize(): DpSize {
        return DpSize(windowSize.width.dp, windowSize.height.dp)
    }

    fun setWindowSize(dimension: Dimension){
        windowSize = WindowSize(dimension.width, dimension.height)
    }

    fun isOutOfBounds(position: CursorPosition): Boolean {
        return position.x < 0 || position.y > windowSize.width || position.y < 0 || position.y > windowSize.height
    }
}