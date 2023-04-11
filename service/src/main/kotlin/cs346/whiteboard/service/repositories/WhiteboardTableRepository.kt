/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.service.repositories

import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.models.WhiteboardTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface WhiteboardTableRepository : JpaRepository<WhiteboardTable, Long> {
    fun findByRoomId(roomId: Long): WhiteboardTable?

    @Query("SELECT b FROM WhiteboardTable b WHERE b.creator = :creator")
    fun findByCreator(creator: UserLogin): List<WhiteboardTable>
}