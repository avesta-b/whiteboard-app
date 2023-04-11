/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client

import cs346.whiteboard.client.network.BaseUrlProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BaseUrlProviderTest {

    @Test
    fun `toggleLocalHost switches between REMOTEHOST and LOCALHOST`() {
        val initialHost = BaseUrlProvider.HOST
        BaseUrlProvider.toggleLocalHost()

        val toggledHost = if (initialHost == BaseUrlProvider.REMOTEHOST) BaseUrlProvider.LOCALHOST else BaseUrlProvider.REMOTEHOST
        assertEquals(toggledHost, BaseUrlProvider.HOST)

        BaseUrlProvider.toggleLocalHost()
        assertEquals(initialHost, BaseUrlProvider.HOST)
    }
}

// TODO("Figure out why this test works locally but not through CI/CD")

//class WhiteboardServiceTest {
//    @Test
//    fun `postRequest returns correct response body`() {
//
//        val path = "/api/auth/login"
//        val username = "postRequestTest"
//        val password = "postRequestTest"
//
//        runBlocking {
//            UserManager.attemptSignUp(username, password)
//            UserManager.attemptSignIn(username, password)
//        }
//
//        val requestBody = Json.encodeToString(LoginCredentialsRequest(username, password))
//
//        val expectedResponseBody: String
//        val response: String
//        runBlocking {
//            response = WhiteboardService.postRequest(path, requestBody)
//            expectedResponseBody = "{\"jwtToken\":\"${UserManager.jwt}\"}"
//        }
//
//        assertEquals(expectedResponseBody, response)
//    }
//}