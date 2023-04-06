package cs346.whiteboard.service.models

import cs346.whiteboard.shared.jsonmodels.WhiteboardState
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.persistence.*

@Entity
@Table(name = "BOARDS")
data class WhiteboardTable(
    @Column(nullable = false)
    val name: String? = null,

    @ManyToOne
    @JoinColumn
    val creator: UserLogin? = null,

    @OneToMany(mappedBy = "whiteboard")
    val userAccesses: List<UserAccess> = emptyList(),

    @Column(columnDefinition="LONGTEXT")
    var state: String = "{}",

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val roomId: Long = -1
)
{

    fun toWhiteboardState(): WhiteboardState = Json.decodeFromString<WhiteboardState>(WhiteboardState.serializer(), state ?: "")
}

fun WhiteboardState.toJsonString() : String {
    return try {
        Json.encodeToString(WhiteboardState.serializer(), this)
    } catch (err: Error) {
        "{}"
    }
}

fun getWhiteboardStateFromString(str: String) : WhiteboardState {
    return try {
        return Json.decodeFromString(str)
    } catch (err: Error) {
        return WhiteboardState()
    }
}