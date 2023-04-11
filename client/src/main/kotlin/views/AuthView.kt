/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.views

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.client.ui.*
import kotlinx.coroutines.launch

enum class AuthUiState {
    SIGN_IN,
    SIGN_IN_ERROR,
    ATTEMPTING_SIGN_IN,
    SIGN_UP,
    SIGN_UP_ERROR,
    ATTEMPTING_SIGN_UP,
    SIGN_UP_SUCCESS
}

internal class AuthViewModel (val onSuccessfulAuth: () -> Unit) {
    private var state by mutableStateOf(AuthUiState.SIGN_IN)

    var username = mutableStateOf(TextFieldValue(""))
    var password = mutableStateOf(TextFieldValue(""))

    fun shouldShowSignIn(): Boolean {
        return state == AuthUiState.SIGN_IN
                || state == AuthUiState.SIGN_IN_ERROR
                || state == AuthUiState.ATTEMPTING_SIGN_IN
    }

    fun shouldShowSignInError(): Boolean {
        return state == AuthUiState.SIGN_IN_ERROR
    }

    fun shouldShowSignUp(): Boolean {
        return state == AuthUiState.SIGN_UP
                || state == AuthUiState.SIGN_UP_ERROR
                || state == AuthUiState.ATTEMPTING_SIGN_UP
    }

    fun shouldShowSignUpSuccess(): Boolean {
        return state == AuthUiState.SIGN_UP_SUCCESS
    }

    fun shouldShowSignUpError(): Boolean {
        return state == AuthUiState.SIGN_UP_ERROR
    }

    fun shouldEnableSignInInteraction(): Boolean {
        return state == AuthUiState.SIGN_IN
                || state == AuthUiState.SIGN_IN_ERROR
    }

    fun shouldEnableSignUpInteraction(): Boolean {
        return state == AuthUiState.SIGN_UP
                || state == AuthUiState.SIGN_UP_ERROR
    }

    suspend fun onSignInClicked() {
        state = AuthUiState.ATTEMPTING_SIGN_IN
        if (UserManager.attemptSignIn(username.value.text, password.value.text)) {
            onSuccessfulAuth()
        } else {
            state = AuthUiState.SIGN_IN_ERROR
        }
    }

    fun onCreateNewAccountClicked() {
        clearTextFieldValues()
        state = AuthUiState.SIGN_UP
    }

    fun onBackToSignInClicked() {
        clearTextFieldValues()
        state = AuthUiState.SIGN_IN
    }

    suspend fun onSignUpClicked() {
        state = AuthUiState.ATTEMPTING_SIGN_UP
        if (UserManager.attemptSignUp(username.value.text, password.value.text)) {
            state = AuthUiState.SIGN_UP_SUCCESS
        } else {
            state = AuthUiState.SIGN_UP_ERROR
        }
    }

    private fun clearTextFieldValues() {
        username.value = TextFieldValue("")
        password.value = TextFieldValue("")
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun AuthView(modifier: Modifier, onSuccessfulAuth: () -> Unit) {
    val model = remember { AuthViewModel(onSuccessfulAuth) }
    val coroutineScope = rememberCoroutineScope()
    val signInErrorAlpha: Float by animateFloatAsState(
        targetValue = if (model.shouldShowSignInError()) 1f else 0f,
        animationSpec = tween(
            durationMillis = 250,
            easing = EaseInOut
        )
    )
    val signUpErrorAlpha: Float by animateFloatAsState(
        targetValue = if (model.shouldShowSignUpError()) 1f else 0f,
        animationSpec = tween(
            durationMillis = 250,
            easing = EaseInOut
        )
    )

    BoxWithConstraints(modifier, Alignment.Center) {
        AnimatedVisibility(
            visible = model.shouldShowSignIn(),
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { -2*it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -2*it })) {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Logo(Modifier.size(120.dp, 78.dp))
                Spacer(Modifier.height(32.dp))
                if (this@BoxWithConstraints.maxHeight > 700.dp) {
                    TitleText("Whiteboard")
                    Spacer(Modifier.height(64.dp))
                }
                AuthenticationTextField(
                    text = model.username,
                    modifier = Modifier.size(280.dp, 60.dp),
                    enabled = model.shouldEnableSignInInteraction(),
                    placeholder = "Username",
                    isSecure = false)
                Spacer(Modifier.height(8.dp))
                AuthenticationTextField(
                    text = model.password,
                    modifier = Modifier
                        .size(280.dp, 60.dp)
                        .onKeyEvent {keyEvent ->
                            if (keyEvent.key != Key.Enter) {
                                return@onKeyEvent false
                            }
                            coroutineScope.launch {
                                model.onSignInClicked()
                            }
                            true
                        },
                    enabled = model.shouldEnableSignInInteraction(),
                    placeholder = "Password",
                    isSecure = true,)
                Spacer(Modifier.height(64.dp))
                PrimaryButton(
                    modifier = Modifier.size(280.dp, 50.dp),
                    text = "Sign in",
                    enabled = model.shouldEnableSignInInteraction(),
                    onClick = {
                        coroutineScope.launch {
                            model.onSignInClicked()
                        }
                    })
                Spacer(Modifier.height(32.dp))
                Box(Modifier.alpha(signInErrorAlpha)) {
                    ErrorText("Error: " + UserManager.error)
                }
                Spacer(Modifier.height(32.dp))
                SecondarySubtitleText("Don't have an account?")
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    modifier = Modifier.size(280.dp, 50.dp),
                    text = "Create new account",
                    enabled = model.shouldEnableSignInInteraction(),
                    onClick = {
                        model.onCreateNewAccountClicked()
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = model.shouldShowSignUp(),
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { 2*it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { 2*it })) {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                if (this@BoxWithConstraints.maxHeight > 700.dp) {
                    Logo(Modifier.size(120.dp, 78.dp))
                    Spacer(Modifier.height(32.dp))
                }
                TitleText("Create new\naccount")
                Spacer(Modifier.height(64.dp))
                AuthenticationTextField(
                    text = model.username,
                    modifier = Modifier.size(280.dp, 60.dp),
                    enabled = model.shouldEnableSignUpInteraction(),
                    placeholder = "Username",
                    isSecure = false)
                Spacer(Modifier.height(8.dp))
                AuthenticationTextField(
                    text = model.password,
                    modifier = Modifier
                        .size(280.dp, 60.dp)
                        .onKeyEvent {keyEvent ->
                            if (keyEvent.key != Key.Enter) {
                                return@onKeyEvent false
                            }
                            coroutineScope.launch {
                                model.onSignUpClicked()
                            }
                            true
                        },
                    enabled = model.shouldEnableSignUpInteraction(),
                    placeholder = "Password",
                    isSecure = true)
                Spacer(Modifier.height(64.dp))
                PrimaryButton(
                    modifier = Modifier.size(280.dp, 50.dp),
                    text = "Create account",
                    enabled = model.shouldEnableSignUpInteraction(),
                    onClick = {
                        coroutineScope.launch {
                            model.onSignUpClicked()
                        }
                    }
                )
                Spacer(Modifier.height(32.dp))
                Box(Modifier.alpha(signUpErrorAlpha)) {
                    ErrorText("Error: " + UserManager.error)
                }
                Spacer(Modifier.height(32.dp))
                UnderlinedTextButton(
                    modifier = Modifier,
                    text = "Back to sign in",
                    enabled = model.shouldEnableSignUpInteraction(),
                    onClick = {
                        model.onBackToSignInClicked()
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = model.shouldShowSignUpSuccess(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Dialog(
                modifier = Modifier.size(512.dp, 170.dp),
                title = "Account successfully created.",
                description = "Please login with your credentials.",
                buttonText = "Continue",
                onClick = {
                    model.onBackToSignInClicked()
                }
            )
        }
    }
}