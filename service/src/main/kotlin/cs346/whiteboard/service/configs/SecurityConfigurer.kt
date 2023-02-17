package cs346.whiteboard.service.configs


import cs346.whiteboard.service.services.UserLoginDetailsService
import cs346.whiteboard.service.util.JWTFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class SecurityConfigurer : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var filter: JWTFilter

    @Autowired
    private lateinit var userLoginDetailsService: UserLoginDetailsService

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .httpBasic()
            .and()
            .cors()
            .and()
            .authorizeHttpRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/user/**").hasRole("USER")
            // TODO:  Note fix this
            .antMatchers("/ws/**").permitAll()
            .and()
            .userDetailsService(userLoginDetailsService)
            .exceptionHandling()
            .authenticationEntryPoint { request: HttpServletRequest?,
                                        response: HttpServletResponse,
                                        authException: AuthenticationException? ->

                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized"
                )
            }
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}