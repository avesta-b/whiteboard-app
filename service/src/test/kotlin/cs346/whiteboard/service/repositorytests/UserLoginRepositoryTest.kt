package cs346.whiteboard.service.repositorytests

import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.repositories.UserLoginRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserLoginRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserLoginRepository
) {

    @Test
    fun `When findByUsername can find then return UserLogin`() {
        val johnDoe = UserLogin("johnDoe", "John")
        entityManager.persist(johnDoe)
        entityManager.flush()
        val user = userRepository.findByUsername(johnDoe.username)
        assertEquals(johnDoe, user)
    }

    @Test
    fun `When findByUsername queries a username that does not exist`() {
        entityManager.clear()
        entityManager.flush()
        val user = userRepository.findByUsername("ben11@gmail.com")
        assertEquals(user, null)
    }
}