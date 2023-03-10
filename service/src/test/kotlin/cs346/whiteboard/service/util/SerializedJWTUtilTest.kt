package cs346.whiteboard.service.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class SerializedJWTUtilTest {
    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Value("\${jwt_secret}")
    private val secret: String? = null

    @Value("\${jwt_issuer}")
    private val jwtIssuer: String? = null

    @Test
    fun `generating a token should produce a JWT with the correct subject, username claim, and issuer`() {
        val token = jwtUtil.generateToken("test-user")
        val decodedJwt = JWT.decode(token)

        assertEquals("User Details", decodedJwt.subject)
        assertEquals("test-user", decodedJwt.claims["username"]?.asString())
        assertEquals(jwtIssuer, decodedJwt.issuer)
    }

    @Test
    fun `validating a token and retrieving the user should return the correct username`() {
        val token = jwtUtil.generateToken("test-user")
        val username = jwtUtil.validateTokenAndRetrieveUser(token)

        assertEquals("test-user", username)
    }
}