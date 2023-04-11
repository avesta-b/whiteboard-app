/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.service.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTUtil {
    @Value("\${jwt_secret}")
    private val secret: String? = null

    @Value("\${jwt_issuer}")
    private val jwtIssuer: String? = null

    val oneDayInMilliseconds: Long = 86400L * 1000L
    @Throws(IllegalArgumentException::class, JWTCreationException::class)
    fun generateToken(username: String): String {
        val utc = TimeZone.getTimeZone("UTC")
        val now = Calendar.getInstance(utc).time
        val expiresAt = Calendar.getInstance(utc).apply {
            time = now
            add(Calendar.MILLISECOND, oneDayInMilliseconds.toInt())
        }.time

        return JWT.create()
            .withSubject("User Details")
            .withClaim("username", username)
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .withIssuer(jwtIssuer)
            .sign(Algorithm.HMAC512(secret))
    }

    @Throws(JWTVerificationException::class)
    fun validateTokenAndRetrieveUser(token: String): String? {

        val verifier: JWTVerifier = JWT.require(Algorithm.HMAC512(secret))
            .withSubject("User Details")
            .withIssuer(jwtIssuer)
            .build()
        val jwt: DecodedJWT = verifier.verify(token)
        return jwt.getClaim("username").asString()
    }
}
