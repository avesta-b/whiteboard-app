/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.service.repositories

import cs346.whiteboard.service.models.UserAccess
import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.models.WhiteboardTable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UserAccessRepository : CrudRepository<UserAccess, Long> {
    fun findByUserAndWhiteboard(user: UserLogin, whiteboard: WhiteboardTable): UserAccess?
    fun findByUser(user: UserLogin): List<UserAccess>
    fun findByWhiteboard(whiteboard: WhiteboardTable): List<UserAccess>
    @Query("SELECT ua FROM UserAccess ua WHERE ua.user = :user AND ua.accessLevel = 'WRITE_ACCESS'")
    fun findWriteAccessWhiteboardsForUser(user: UserLogin): List<UserAccess>
}
