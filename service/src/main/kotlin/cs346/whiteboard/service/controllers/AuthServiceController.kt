package cs346.whiteboard.service.controllers

import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.repositories.UserLoginRepository
import cs346.whiteboard.service.util.JWTUtil
import cs346.whiteboard.shared.jsonmodels.LoginCredentialsRequest
import cs346.whiteboard.shared.jsonmodels.SerializedJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/auth")
class AuthServiceController {
    @Autowired
    private lateinit var userLoginRepository: UserLoginRepository

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Autowired
    private lateinit var authManager: AuthenticationManager

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    // https://stackoverflow.com/questions/26587082/http-status-code-for-username-already-exists-when-registering-new-account
    // We return ERROR CODE 409 (CONFLICT) in the case a user already exists
    @Transactional
    @PostMapping("/register")
    fun registerHandler(@RequestBody registrationRequestBody: LoginCredentialsRequest): SerializedJWT {
        if (registrationRequestBody.username.isEmpty() || registrationRequestBody.password.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot use empty username or password")
        }

        if (userLoginRepository.findByUsername(registrationRequestBody.username) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Try a different username")
        }
        val encodedPass = passwordEncoder.encode(registrationRequestBody.password)
        val userInfo = UserLogin(
            username = registrationRequestBody.username,
            password = encodedPass
        )
        userLoginRepository.save(userInfo)
        val token = jwtUtil.generateToken(userInfo.username)
        return SerializedJWT(jwtToken = token)
    }

    @PostMapping("/login")
    fun loginHandler(@RequestBody loginRequestBody: LoginCredentialsRequest): SerializedJWT {
        if (loginRequestBody.username.isEmpty() || loginRequestBody.password.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing credentials")
        }

        val validUserInfo = userLoginRepository.findByUsername(loginRequestBody.username)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials, try again")
        val rawPassword = loginRequestBody.password
        val encodedPassword = validUserInfo.password
        if(!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials, try again")
        }

        val authInputToken = UsernamePasswordAuthenticationToken(loginRequestBody.username, loginRequestBody.password)
        try {
            val authentication = authManager.authenticate(authInputToken)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: AuthenticationException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials, try again")
        }

        val token = jwtUtil.generateToken(loginRequestBody.username)
        return SerializedJWT(jwtToken = token)
    }

}