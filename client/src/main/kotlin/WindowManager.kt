package cs346.whiteboard.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Dimension

const val WINDOW_STATE_FILE = "windowState.json"
object WindowManager{
    @Serializable
    data class WindowSize(
        val width: Int = 800,
        val height: Int = 600
    )
    private var WindowSizeData : WindowSize?
        get() {
            LocalFileManager.readFromFile(WINDOW_STATE_FILE)?.let {
                return Json.decodeFromString(WindowSize.serializer(), it)
            }
            return WindowSize(800, 600)
        }
        set(WindowSizeData) {
            LocalFileManager.writeToFileWithString(WINDOW_STATE_FILE, Json.encodeToString(WindowSizeData))
        }

    fun getSavedWindowSize():Dimension{
        return Dimension(WindowSizeData!!.width, WindowSizeData!!.height)
    }
    fun saveWindowSize(){
        LocalFileManager.writeToFileWithString(WINDOW_STATE_FILE, Json.encodeToString(WindowSizeData))
    }
    fun setWindowSize(width: Int, height: Int){
        WindowSizeData = WindowSize(width, height)
    }
}