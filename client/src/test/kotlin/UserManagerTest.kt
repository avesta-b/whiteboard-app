/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client

import cs346.whiteboard.client.network.WhiteboardService
import cs346.whiteboard.client.settings.UserManager
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import io.mockk.*
import org.junit.jupiter.api.BeforeEach

class UserManagerTest {
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `attemptSignUp with valid credentials should return true`() {
        runBlocking {
            val mockResponse = "{\"jwtToken\": \"mock_token\"}"
            mockkObject(WhiteboardService)
            coEvery { WhiteboardService.postRequest(any(), any()) } returns mockResponse

            val result = UserManager.attemptSignUp("valid_username", "valid_password")

            assertTrue(result)
        }
    }

    @Test
    fun `attemptSignUp with invalid credentials should return false`() {
        runBlocking {
            val result = UserManager.attemptSignUp("_invalid_username", "valid_password")

            assertFalse(result)
        }
    }

    @Test
    fun `attemptSignIn with valid credentials should return true`() {
        runBlocking {
            val mockResponse = "{\"jwtToken\": \"mock_token\"}"
            mockkObject(WhiteboardService)
            coEvery { WhiteboardService.postRequest(any(), any()) } returns mockResponse

            val result = UserManager.attemptSignIn("valid_username", "valid_password")

            assertTrue(result)
        }
    }

    @Test
    fun `attemptSignIn with invalid credentials should return false`() {
        runBlocking {
            val result = UserManager.attemptSignIn("_invalid_username", "valid_password")

            assertFalse(result)
        }
    }

}