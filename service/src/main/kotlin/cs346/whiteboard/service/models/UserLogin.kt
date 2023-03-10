package cs346.whiteboard.service.models

import javax.persistence.*

@Entity
@Table(name = "LOGINS")
data class UserLogin(
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    val password: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
    constructor() : this(".", ".", -1)
}
