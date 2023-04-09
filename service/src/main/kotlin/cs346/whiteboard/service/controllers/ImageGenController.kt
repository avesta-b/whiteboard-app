package cs346.whiteboard.service.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import cs346.whiteboard.service.repositories.UserLoginRepository
import cs346.whiteboard.service.util.JWTUtil
import cs346.whiteboard.shared.jsonmodels.ImageGenerationRequest
import cs346.whiteboard.shared.jsonmodels.ImageGenerationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException


data class OpenAiImageGenerationRequest(
    @JsonProperty("prompt") val prompt: String,
    @JsonProperty("n") val n: Int,
    @JsonProperty("size") val size: String = "256x256"
)

data class OpenAiImageGenerationResponse (
    @JsonProperty("created") val created: Long,
    @JsonProperty("data") val data: List<Datum>
)

data class Datum (
    @JsonProperty("url") val url: String
)

@RestController
@RequestMapping("/api/image")
class ImageGenController(
    @Value("\${openai.api.key}") private val openaiApiKey: String,
    @Autowired private val restTemplate: RestTemplate,
    @Autowired private val jwtUtil: JWTUtil,
    @Autowired private val userLoginRepository: UserLoginRepository
) {

    @PostMapping("/generate")
    fun generateImage(
        @RequestBody imageGenerationRequest: ImageGenerationRequest
    ): ImageGenerationResponse {

        val openAiRequest = OpenAiImageGenerationRequest(
            prompt = imageGenerationRequest.prompt,
            n = 1
        )
        val openAiUrlList = callOpenAiApi(openAiRequest)
        val imageUrl = openAiUrlList.data?.first()?.url ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get image from OpenAI")
        return ImageGenerationResponse(imageGenerationRequest.prompt, imageUrl)
    }

    private fun callOpenAiApi(request: OpenAiImageGenerationRequest): OpenAiImageGenerationResponse {
        val url = "https://api.openai.com/v1/images/generations"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(openaiApiKey)
        }
        val requestEntity = HttpEntity(request, headers)
        val responseEntity: ResponseEntity<String> = restTemplate.postForEntity(url, requestEntity, String::class.java)
        val responseJson = responseEntity.body ?: throw RuntimeException("Failed to generate image from OpenAI API")
        return ObjectMapper().readValue(responseJson, OpenAiImageGenerationResponse::class.java)
    }
}