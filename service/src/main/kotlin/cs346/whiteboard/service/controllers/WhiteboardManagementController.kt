package cs346.whiteboard.service.controllers

import cs346.whiteboard.service.models.AccessLevel
import cs346.whiteboard.service.models.UserAccess
import cs346.whiteboard.service.models.WhiteboardTable
import cs346.whiteboard.service.repositories.UserAccessRepository
import cs346.whiteboard.service.repositories.UserLoginRepository
import cs346.whiteboard.service.repositories.WhiteboardTableRepository
import cs346.whiteboard.service.util.JWTUtil
import cs346.whiteboard.shared.jsonmodels.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/user")
class WhiteboardManagementController(
    @Autowired private val whiteboardRepository: WhiteboardTableRepository,
    @Autowired private val userRepository: UserLoginRepository,
    @Autowired private val userAccessRepository: UserAccessRepository,
    @Autowired private val messagingTemplate: SimpMessagingTemplate,
    @Autowired private val jwtUtil: JWTUtil
) {

    // endpoint to create a new whiteboard owned by a user
    @Transactional
    @PostMapping("/{username}/whiteboards")
    fun createWhiteboard(
        @PathVariable username: String,
        @RequestBody whiteboard: WhiteboardCreationRequest,
        @RequestHeader("Authorization") authHeader: String
    ): FetchWhiteboardsResponse {
        if (whiteboard.name == "") throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No whiteboard name passed")
        val jwtToken = authHeader.substring(7)
        try {
            val usernameFromJwt = jwtUtil.validateTokenAndRetrieveUser(jwtToken)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
            if (usernameFromJwt != username) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
        } catch (error: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
        }


        val user = userRepository.findByUsername(username)
            ?: // throw appropriate error indicating the user does not exist
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Response not found") // not saying user not found
        // because bad security practice

        val newWhiteboard = WhiteboardTable(whiteboard.name, user)
        whiteboardRepository.saveAndFlush(newWhiteboard)

        // get all of the whiteboards owned by username
        val whiteboards = whiteboardRepository.findByCreator(user)?.map {
            WhiteboardItem(it.name ?: "", username, it.roomId ?: 0, it.userAccesses.isNotEmpty())
        }

        return FetchWhiteboardsResponse(whiteboards ?: emptyList())
    }

    @Transactional
    @GetMapping("/{username}/whiteboards")
    fun getWhiteboards(
        @PathVariable username: String,
        @RequestHeader("Authorization") authHeader: String
    ): FetchWhiteboardsResponse {
        val jwtToken = authHeader.substring(7)
        try {
            val usernameFromJwt = jwtUtil.validateTokenAndRetrieveUser(jwtToken)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
            if (usernameFromJwt != username) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
            }
        } catch (error: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
        }

        val user = userRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        // get all of the whiteboards owned by username, for each whiteboard see if it is shared with others
        val whiteboards = whiteboardRepository.findByCreator(user)?.map {
            WhiteboardItem(it.name ?: "", username, it.roomId ?: 0, it.userAccesses.isNotEmpty())
        }
        return FetchWhiteboardsResponse(whiteboards ?: emptyList())
    }

    // add an endpoint to share a whiteboard with another user
    @Transactional
    @PostMapping("/{owner}/whiteboards/{roomId}/share")
    fun shareWhiteboard(
        @PathVariable owner: String,
        @PathVariable roomId: Long,
        @RequestBody sharingRequest: WhiteboardSharingRequest,
        @RequestHeader("Authorization") authHeader: String
    ): FetchWhiteboardsResponse {
        if (owner == sharingRequest.userToBeSharedWith)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot share a whiteboard with self")
        val jwtToken = authHeader.substring(7)
        try {
            val usernameFromJwt = jwtUtil.validateTokenAndRetrieveUser(jwtToken)
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
            if (usernameFromJwt != owner) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
            }
        } catch (error: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized, attempt a relog")
        }

        // Make sure there is a user to share with (Error messages are intentionally vague to not reveal user info
        // to people who reverse engineer API)
        val user = userRepository.findByUsername(sharingRequest.userToBeSharedWith) ?:
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot share with user")

        // Whiteboard is not found
        val whiteboard = whiteboardRepository.findByRoomId(roomId) ?:
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Whiteboard not found")

        // We do not want to reshare a whiteboard that has already been shared with that user
        val existingAccess = userAccessRepository.findByUserAndWhiteboard(user, whiteboard)
        if (existingAccess != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Whiteboard has already been shared")
        }

        // If the user that is attempting to share does not exist, suggest a relog
        val ownerUser = userRepository.findByUsername(owner) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED,
            "Unauthorized, attempt a relog")

        val access = UserAccess(whiteboard = whiteboard, user = user, accessLevel = AccessLevel.WRITE_ACCESS, owner = ownerUser)
        userAccessRepository.save(access)

        // Send an event to the STOMP endpoint "/topic/share/{userToBeSharedWith}"
        val destination = "/topic/share/${sharingRequest.userToBeSharedWith}"
        val accessEntries = userAccessRepository.findByUser(user)
        val whiteboards = accessEntries.mapNotNull { access ->
            WhiteboardItem(
                name = access.whiteboard?.name ?: "",
                author = owner,
                id = access.whiteboard?.roomId ?: -1,
                sharedWithOthers = true)
        }
        messagingTemplate.convertAndSend(destination, FetchWhiteboardsResponse(whiteboards))

        // Return all of the Owner's whiteboards in their new state to the owner
        val ownersWhiteboards = whiteboardRepository.findByCreator(ownerUser)?.map {
            WhiteboardItem(it.name ?: "", owner, it.roomId ?: 0, it.userAccesses.isNotEmpty())
        }
        return FetchWhiteboardsResponse(ownersWhiteboards ?: emptyList())
    }

}