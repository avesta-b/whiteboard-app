/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.service.models

import cs346.whiteboard.shared.jsonmodels.WhiteboardState
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

    // Note(avesta): Needed a weird dependency for a JSON type so we decided to just use a LONGTEXT
    // performance differences are negligible especially because we do not query based on JSON fields.
    @Column(columnDefinition="LONGTEXT")
    var state: String = "{}",

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val roomId: Long = -1
)

fun String.toWhiteboardState(): WhiteboardState? {
    return try {
        Json.decodeFromString<WhiteboardState>(WhiteboardState.serializer(), this)
    } catch (_: Exception) {
        null
    }
}

fun WhiteboardState.toJsonString() : String {
    return try {
        Json.encodeToString(WhiteboardState.serializer(), this)
    } catch (err: Error) {
        "{}"
    }
}