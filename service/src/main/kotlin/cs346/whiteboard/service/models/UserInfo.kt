package cs346.whiteboard.service.models

import jakarta.annotation.Nonnull
import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository

@Entity
@Table(name = "LOGINS")
data class UserLogin(
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    val password: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
    constructor() : this(".", ".", -1) {}
}
