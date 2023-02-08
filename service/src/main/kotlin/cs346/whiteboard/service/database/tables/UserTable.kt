package cs346.whiteboard.service.database.tables


import org.jetbrains.exposed.sql.*

// TODO: This class shall be modified to represent users.
// TODO 2: Migrate to using companion objects once we define schema

object UserTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    val age = integer("age")

    override val primaryKey = PrimaryKey(id)
}