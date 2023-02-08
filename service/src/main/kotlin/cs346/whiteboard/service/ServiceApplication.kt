package cs346.whiteboard.service

import cs346.whiteboard.service.database.DatabaseHelper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServiceApplication

fun main(args: Array<String>) {
    DatabaseHelper.init()
    runApplication<ServiceApplication>(*args)
}

