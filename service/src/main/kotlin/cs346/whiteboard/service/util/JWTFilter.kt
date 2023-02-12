package cs346.whiteboard.service.util

import com.auth0.jwt.exceptions.JWTVerificationException
import cs346.whiteboard.service.services.UserLoginDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JWTFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var userDetailsService: UserLoginDetailsService

    @Autowired
    lateinit var jwtUtil: JWTUtil

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader: String? = request.getHeader("Authorization")
        if ((authorizationHeader == null) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authorizationHeader.substring(7)
        if (jwtToken.isBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val username = jwtUtil.validateTokenAndRetrieveUser(jwtToken)
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
            val authenticationToken =
                UsernamePasswordAuthenticationToken(username, userDetails.password, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authenticationToken
        } catch (exc: JWTVerificationException) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token")
        }

        filterChain.doFilter(request, response)
    }
}
