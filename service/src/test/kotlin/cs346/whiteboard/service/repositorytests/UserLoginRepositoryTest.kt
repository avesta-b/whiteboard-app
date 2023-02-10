package cs346.whiteboard.service.repositorytests

import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.repositories.UserLoginRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManagerAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserLoginRepository) {

    @Test
    fun `When findByLogin then return User`() {
        val johnDoe = UserLogin("johnDoe", "John")
        entityManager.persist(johnDoe)
        entityManager.flush()
        val user = userRepository.findByEmail(johnDoe.email)
        assertEquals(johnDoe, user)
    }

    @Test
    fun `When findByEmail when email does not exist`() {
        entityManager.clear()
        entityManager.flush()
        val user = userRepository.findByEmail("ben11@gmail.com")
        assertEquals(user, null)
    }
}