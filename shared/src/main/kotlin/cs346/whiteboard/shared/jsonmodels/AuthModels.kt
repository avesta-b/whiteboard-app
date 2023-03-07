package cs346.whiteboard.shared.jsonmodels

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsRequest(val username: String, val password: String) {
    constructor() : this("", "")
}
@Serializable
data class SerializedJWT(val jwtToken: String) {
    constructor() : this("")
}