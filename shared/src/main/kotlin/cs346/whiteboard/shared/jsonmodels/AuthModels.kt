package cs346.whiteboard.shared.jsonmodels

data class LoginCredentialsRequest(val username: String, val password: String) {
    constructor() : this("", "")
}
data class LoginCredentialsResponse(val jwtToken: String) {
    constructor() : this("")
}