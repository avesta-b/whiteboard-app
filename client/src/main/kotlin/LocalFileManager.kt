package cs346.whiteboard.client

import java.io.File

const val LOCAL_DIR = ".whiteboard"
object LocalFileManager {
    fun writeToFileWithString(fileName: String, content: String) {
        File(LOCAL_DIR).mkdir()
        File("$LOCAL_DIR/$fileName").writeText(content)
    }
    fun readFromFile(fileName: String): String? {
        return if (!File("$LOCAL_DIR/$fileName").isFile) null else File("$LOCAL_DIR/$fileName").readText()
    }

    fun removeFile(fileName: String) {
        File("$LOCAL_DIR/$fileName").delete()
    }
}