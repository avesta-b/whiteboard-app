package cs346.whiteboard.client.views

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.components.LargeSpinner
import kotlinx.coroutines.launch

enum class RootUiState {
    SIGNING_IN, NOT_SIGNED_IN, SIGNED_IN
}
internal class RootViewModel {

    var state by mutableStateOf(initialState())

    private fun initialState(): RootUiState {
        return if (UserManager.shouldAttemptSignIn()) RootUiState.SIGNING_IN else RootUiState.NOT_SIGNED_IN
    }

    fun onSuccessfulAuth() {
        state = RootUiState.SIGNED_IN
    }

    fun onSignOut() {
        state = RootUiState.NOT_SIGNED_IN
    }

    suspend fun attemptSignIn() {
        if (UserManager.attemptSignInWithStoredCredentials()) {
            onSuccessfulAuth()
        } else {
            onSignOut()
        }
    }
}
@Composable
fun RootView(modifier: Modifier) {
    val model = remember { RootViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier, Alignment.Center) {
        Crossfade(model.state) { state ->
            when (state) {
                RootUiState.NOT_SIGNED_IN -> {
                    AuthView(modifier) {
                        model.onSuccessfulAuth()
                    }
                }
                RootUiState.SIGNING_IN -> {
                    Box(modifier, Alignment.Center) {
                        LargeSpinner().also {
                            coroutineScope.launch {
                                model.attemptSignIn()
                            }
                        }
                    }
                }
                RootUiState.SIGNED_IN -> {
                    TestView(modifier) {
                        model.onSignOut()
                    }
                }
            }
        }
    }
}