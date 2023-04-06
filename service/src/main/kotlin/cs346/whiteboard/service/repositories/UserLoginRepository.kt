package cs346.whiteboard.service.repositories

import cs346.whiteboard.service.models.UserLogin
import org.springframework.data.repository.CrudRepository

interface UserLoginRepository: CrudRepository<UserLogin, Long> {
    fun findByUsername(username: String): UserLogin?
}