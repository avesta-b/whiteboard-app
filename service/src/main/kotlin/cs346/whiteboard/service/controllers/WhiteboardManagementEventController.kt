/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.service.controllers

import cs346.whiteboard.service.repositories.UserAccessRepository
import cs346.whiteboard.service.repositories.UserLoginRepository
import cs346.whiteboard.shared.jsonmodels.FetchWhiteboardsResponse
import cs346.whiteboard.shared.jsonmodels.WhiteboardItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class WhiteboardManagementEventController(
    @Autowired private val userRepository: UserLoginRepository,
    @Autowired private val userAccessRepository: UserAccessRepository,
) {

    // Get all of the whiteboards shared with me
    @MessageMapping("/sharing.getAll/{username}")
    @SendTo("/topic/share/{username}")
    fun getAllSharedWhiteboards(
        @DestinationVariable username: String
    ): FetchWhiteboardsResponse {
        val user = userRepository.findByUsername(username) ?: return FetchWhiteboardsResponse(emptyList())

        val accessEntries = userAccessRepository.findByUser(user)

        val whiteboards = accessEntries.mapNotNull { access ->
            WhiteboardItem(
                access.whiteboard?.name ?: "NO NAME",
                access.owner?.username ?: "NO OWNER",
                access?.whiteboard?.roomId ?: -1,
                true
            )
        }

        return FetchWhiteboardsResponse(whiteboards)
    }
}