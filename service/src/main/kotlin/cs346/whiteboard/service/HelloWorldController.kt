package cs346.whiteboard.service

import cs346.whiteboard.service.database.tables.UserTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

// TODO: Remove this file, it is meant as a placeholder and example for future developers to build upon.
// Once the first endpoint is setup, remove this file.
@RestController
class HelloWorldController {

    @GetMapping("/sample")
    fun samplePage() : String {
        return "This is a sample page"
    }

    @PutMapping("/db")
    fun writeToDb(): String {
        var result = "EMPTY"

        transaction {
            UserTable.insert {
                it[name] = "Yorkie"
                it[age] = 20
            } get UserTable.id
        }

        return result
    }
}