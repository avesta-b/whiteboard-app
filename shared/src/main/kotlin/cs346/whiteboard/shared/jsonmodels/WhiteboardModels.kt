package cs346.whiteboard.shared.jsonmodels

import kotlinx.serialization.Serializable

@Serializable
data class WhiteboardCreationRequest(
    val name: String = ""
)

@Serializable
data class WhiteboardItem(
    val name: String = "",
    val author: String = "",
    val id: Long = -1,
    val sharedWithOthers: Boolean? = null
)

@Serializable
data class FetchWhiteboardsResponse(
    val whiteboards: List<WhiteboardItem>
)

@Serializable
data class WhiteboardSharingRequest(
    val userToBeSharedWith: String = ""
)