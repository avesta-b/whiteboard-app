package cs346.whiteboard.client

import cs346.whiteboard.shared.jsonmodels.LoginCredentialsRequest
import cs346.whiteboard.shared.jsonmodels.SerializedJWT
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val CREDENTIALS_KEY = "credentials"

// 8-20 characters long, no _ or . at the beginning, no __ or _. or ._ or .. inside, allow
// letters, numbers, underscore, and period, no _ or . at the end
val USERNAME_REGEX = Regex("^(?=.{8,20}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$")
object UserManager {

    @Serializable
    data class Credentials(
        val username: String,
        val password: String
    )

    var error: String? = null

    var jwt: String? = null
        private set

    fun getUsername(): String? {
        return storedCredentials?.username
    }

    private var storedCredentials: Credentials?
        get() {
            PreferencesManager.readFromPreferences(CREDENTIALS_KEY)?.let {
                return Json.decodeFromString(Credentials.serializer(), it)
            }
            return null
        }
        set(credentials) {
            PreferencesManager.writeToPreferencesWithKey(CREDENTIALS_KEY, Json.encodeToString(credentials))
        }

    private fun isValidCredentials(username: String, password: String): Boolean {
        return username.isNotEmpty()
                && password.isNotEmpty()
                && username.matches(USERNAME_REGEX)
    }

    fun shouldAttemptSignIn(): Boolean {
        return storedCredentials != null
    }

    // Returns true on success, false on fail
    suspend fun attemptSignUp(username: String, password: String): Boolean {
        if (!isValidCredentials(username, password)) {
            error = "Empty or Invalid Username/Password"
            return false
        }
        return try {
            val requestBody = Json.encodeToString(LoginCredentialsRequest(username, password))
            // If service doesn't throw, then 2xx response -> successful sign up
            WhiteboardService.postRequest(path = "api/auth/register", body = requestBody)
            true
        } catch (e: Exception) {
            determineError(e.toString())
            false
        }
    }

    suspend fun attemptSignIn(username: String, password: String): Boolean {
        if (!isValidCredentials(username, password)) {
            error = "Empty or Invalid Username/Password"
            return false
        }
        return try {
            val requestBody = Json.encodeToString(LoginCredentialsRequest(username, password))
            val responseBody = WhiteboardService.postRequest(path = "api/auth/login", body = requestBody)
            val credentialsResponse = Json.decodeFromString(SerializedJWT.serializer(), responseBody)
            storedCredentials = Credentials(username, password)
            jwt = credentialsResponse.jwtToken
            true
        } catch (e: Exception) {
            determineError(e.toString())
            false
        }
    }

    private fun determineError(errorText: String){
        if (errorText.contains("invalid: 409")) error = "Username Taken"
        else if (errorText.contains("invalid: 400")) error = "Invalid Credentials"
        else if (errorText.contains("invalid: 401")) error = "Invalid Credentials"
        else if (errorText.contains("Failed to connect")) error = "Connection Error"
    }

    suspend fun attemptSignInWithStoredCredentials(): Boolean {
        if (!shouldAttemptSignIn())  {
            return false
        }
        storedCredentials?.let {
            return try {
                val requestBody = Json.encodeToString(LoginCredentialsRequest(it.username, it.password))
                val responseBody = WhiteboardService.postRequest(path = "api/auth/login", body = requestBody)
                val credentialsResponse = Json.decodeFromString(SerializedJWT.serializer(), responseBody)
                jwt = credentialsResponse.jwtToken
                true
            } catch (_: Exception) {
                false
            }
        }
        return false
    }

    fun signOut() {
        PreferencesManager.removeFromPreferences(CREDENTIALS_KEY)
        jwt = null
    }



}