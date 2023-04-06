package cs346.whiteboard.service.models

import javax.persistence.*

enum class AccessLevel {
    READ_ONLY_ACCESS,
    WRITE_ACCESS,
    FULL_ACCESS
}

@Entity
@Table(name = "USER_ACCESS")
data class UserAccess(
    @ManyToOne
    @JoinColumn
    val whiteboard: WhiteboardTable? = null,

    @ManyToOne
    @JoinColumn
    val user: UserLogin? = null,

    @ManyToOne
    @JoinColumn
    val owner: UserLogin? = null,

    @Enumerated(EnumType.STRING)
    val accessLevel: AccessLevel? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)