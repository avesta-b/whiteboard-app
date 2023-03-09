package cs346.whiteboard.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*

object BaseUrlProvider {
    const val HOST = "lobster-app-laueo.ondigitalocean.app"
}

object WhiteboardService {
    val client = HttpClient(OkHttp) {
        // Throws for non-2xx response codes
        expectSuccess = true
        engine {
            config {
                followRedirects(true)
                followSslRedirects(true)
            }
        }
    }

    suspend fun postRequest(path: String, body: String): String {
        val response = client.request {
            method = HttpMethod.Post
            url {
                protocol = URLProtocol.HTTPS
                host = BaseUrlProvider.HOST
                path(path)
            }
            headers {
                contentType(ContentType("application", "json"))
            }
            setBody(body)
        }
        return response.body()
    }
}