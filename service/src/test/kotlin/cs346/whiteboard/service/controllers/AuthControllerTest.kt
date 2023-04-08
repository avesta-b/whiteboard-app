package cs346.whiteboard.service.controllers

import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.repositories.UserLoginRepository
import cs346.whiteboard.service.util.JWTUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito
import org.mockito.Mockito.never
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userLoginRepository: UserLoginRepository

    @MockBean
    private lateinit var passwordEncoder: PasswordEncoder

    @MockBean
    private lateinit var jwtUtil: JWTUtil

    @MockBean
    private lateinit var authManager: AuthenticationManager

    @BeforeEach
    fun setUp() {
        val username = "testuser"
        val password = "testpassword"
        val encodedPassword = "encodedPassword"
        val token = "token"

        BDDMockito.given(userLoginRepository.findByUsername(username)).willReturn(null)
        BDDMockito.given(passwordEncoder.encode(password)).willReturn(encodedPassword)
        BDDMockito.given(jwtUtil.generateToken(username)).willReturn(token)
    }

    @Test
    fun `registerHandler should save the user information and return a token`() {
        val username = "testuser"
        val password = "testpassword"
        val request = """{"username":"$username","password":"$password"}"""

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.jwtToken").value("token"))

        BDDMockito.verify(userLoginRepository).findByUsername(username)
        BDDMockito.verify(passwordEncoder).encode(password)
        val userLogin = UserLogin(username, "encodedPassword")
        BDDMockito.verify(userLoginRepository).save(userLogin)
        BDDMockito.verify(jwtUtil).generateToken(username)
    }

    @Test
    fun `registerHandler should return 409 Conflict if username is already taken`() {
        val username = "testuser"
        val password = "testpassword"
        val request = """{"username":"$username","password":"$password"}"""

        BDDMockito.given(userLoginRepository.findByUsername(username)).willReturn(UserLogin(username, password))

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isConflict)

        BDDMockito.verify(userLoginRepository).findByUsername(username)
    }

    @Test
    fun `registerHandler should return 409 Conflict if the username is already in use`() {
        val username = "testuser"
        val password = "testpassword"
        val request = """{"username":"$username","password":"$password"}"""

        BDDMockito.given(userLoginRepository.findByUsername(username)).willReturn(UserLogin(username, "encodedPassword"))

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isConflict)

        BDDMockito.verify(userLoginRepository).findByUsername(username)
        BDDMockito.verify(passwordEncoder, never()).encode(anyString())
        BDDMockito.verify(userLoginRepository, never()).save(any())
        BDDMockito.verify(jwtUtil, never()).generateToken(anyString())
    }

    @Test
    fun `registerHandler should return 400 Bad Request if the username or password is empty`() {
        val request = """{"username":"","password":""}"""

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isBadRequest)

        BDDMockito.verify(userLoginRepository, never()).findByUsername(anyString())
        BDDMockito.verify(passwordEncoder, never()).matches(anyString(), anyString())
        BDDMockito.verify(authManager, never()).authenticate(any())
        BDDMockito.verify(jwtUtil, never()).generateToken(anyString())
    }

    @Test
    fun `loginHandler should return 401 Unauthorized if the username is invalid`() {
        val username = "invaliduser"
        val password = "testpassword"
        val request = """{"username":"$username","password":"$password"}"""

        BDDMockito.given(userLoginRepository.findByUsername(username)).willReturn(null)

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isUnauthorized)

        BDDMockito.verify(userLoginRepository).findByUsername(username)
        BDDMockito.verify(passwordEncoder, never()).matches(anyString(), anyString())
        BDDMockito.verify(authManager, never()).authenticate(any())
        BDDMockito.verify(jwtUtil, never()).generateToken(anyString())
    }

    @Test
    fun `loginHandler should return 401 Unauthorized if the credentials do not match`() {
        val username = "testuser"
        val password = "testpassword"
        val request = """{"username":"$username","password":"$password"}"""

        BDDMockito.given(userLoginRepository.findByUsername(username)).willReturn(UserLogin(username, "encodedPassword"))
        BDDMockito.given(passwordEncoder.matches(password, "encodedPassword")).willReturn(false)


        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isUnauthorized)

        BDDMockito.verify(userLoginRepository).findByUsername(username)
        BDDMockito.verify(passwordEncoder, never()).encode(anyString())
        BDDMockito.verify(userLoginRepository, never()).save(any())
        BDDMockito.verify(jwtUtil, never()).generateToken(anyString())
    }

    @Test
    fun `loginHandler should return 400 Bad Request if the username or password is empty`() {
        val request = """{"username":"","password":""}"""

        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isBadRequest)

        BDDMockito.verify(userLoginRepository, never()).findByUsername(anyString())
        BDDMockito.verify(passwordEncoder, never()).matches(anyString(), anyString())
        BDDMockito.verify(authManager, never()).authenticate(any())
        BDDMockito.verify(jwtUtil, never()).generateToken(anyString())
    }

}
