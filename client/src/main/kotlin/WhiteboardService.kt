package cs346.whiteboard.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*

object BaseUrlProvider {
    const val REMOTEHOST = "lobster-app-laueo.ondigitalocean.app"
    const val LOCALHOST = "localhost:80"
    var HOST = if(MenuBarState.isLocal) LOCALHOST else REMOTEHOST
    fun toggleLocalHost(){
        HOST = if(HOST == REMOTEHOST) LOCALHOST else REMOTEHOST
    }
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

    suspend fun postRequest(path: String, body: String, token: String? = null): String {
        val response = client.request {
            method = HttpMethod.Post
            url {
                protocol = if(MenuBarState.isLocal) URLProtocol.HTTP else URLProtocol.HTTPS
                host = BaseUrlProvider.HOST
                path(path)
            }
            headers {
                contentType(ContentType("application", "json"))
                token?.let {
                    bearerAuth(it)
                }
            }
            setBody(body)
        }
        return response.body()
    }
}