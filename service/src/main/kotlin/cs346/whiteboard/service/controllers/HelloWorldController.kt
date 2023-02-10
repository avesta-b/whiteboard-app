package cs346.whiteboard.service.controllers


import cs346.whiteboard.service.models.UserLogin
import cs346.whiteboard.service.repositories.UserLoginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

// TODO: Remove this file, it is meant as a placeholder and example for future developers to build upon.
// Once the first endpoint is setup, remove this file.
@RestController
class HelloWorldController(private val userRepository: UserLoginRepository) {

    @GetMapping("/sample")
    fun samplePage() : String {
        userRepository.save(UserLogin("Foo@gmail.com", "bar"))
        return "This is a sample page"
    }

    @PutMapping("/db")
    fun writeToDb(): String {
        var result = "EMPTY"


        return result
    }
}