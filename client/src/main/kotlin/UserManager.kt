package cs346.whiteboard.client

import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

const val CREDENTIALS_FILE = "credentials.json"
object UserManager {

    @Serializable
    data class Credentials(
        val username: String,
        val password: String
    )

    private var storedCredentials: Credentials?
        get() {
            LocalFileManager.readFromFile(CREDENTIALS_FILE)?.let {
                return Json.decodeFromString(Credentials.serializer(), it)
            }
            return null
        }
        set(credentials) {
            LocalFileManager.writeToFileWithString(CREDENTIALS_FILE, Json.encodeToString(credentials))
        }

    fun shouldAttemptSignIn(): Boolean {
        return storedCredentials != null
    }

    fun signOut() {
        LocalFileManager.removeFile(CREDENTIALS_FILE)
    }

}