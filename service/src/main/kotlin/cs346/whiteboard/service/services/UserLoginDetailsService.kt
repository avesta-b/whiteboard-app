/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.service.services

import cs346.whiteboard.service.repositories.UserLoginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserLoginDetailsService @Autowired constructor(val userLoginRepository: UserLoginRepository): UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): UserDetails {
        val userLogin = username?.let { userLoginRepository.findByUsername(it) }
            ?: throw UsernameNotFoundException("Username: $username not found")
        // Marking authorities for User as empty for the moment because we have not and current do not intend to
        // implement different auth levels for certain repositories. Subject to change.
        return User(userLogin.username, userLogin.password, emptyList())
    }
}