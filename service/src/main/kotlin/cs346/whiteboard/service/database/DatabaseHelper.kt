package cs346.whiteboard.service.database

import cs346.whiteboard.service.database.tables.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

// TODO: Refactor once more knowledge of Kotlin has been acquired
object DatabaseHelper {
    fun init() {
        // in build file so git doesn't catch it, we can put it elsewhere if preferred
        val jdbcUrl = "jdbc:sqlite:build/whiteboard.db"

        Database.connect(jdbcUrl)

        // Add loggers and create our tables
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(UserTable)
        }
    }
}