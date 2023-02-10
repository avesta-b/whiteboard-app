package cs346.whiteboard.client.views

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cs346.whiteboard.client.UserManager

enum class RootUiState {
    SIGNING_IN, NOT_SIGNED_IN, SIGNED_IN
}
internal class RootViewModel {

    var state = mutableStateOf(initialState())
        private set(newState) {
            if (newState.value == RootUiState.SIGNING_IN) {
                // make auth call here to user manager
            }
            field = newState
        }
    private fun initialState(): RootUiState {
        return if (UserManager.shouldAttemptSignIn()) RootUiState.SIGNING_IN else RootUiState.NOT_SIGNED_IN
    }

    fun onSuccessfulAuth() {
        state.value = RootUiState.SIGNED_IN
    }

    fun onSignOut() {
        state.value = RootUiState.NOT_SIGNED_IN
    }
}
@Composable
fun RootView(modifier: Modifier) {
    val model = remember { RootViewModel() }

    Box(modifier, Alignment.Center) {
        Crossfade(model.state.value) { state ->
            when (state) {
                RootUiState.NOT_SIGNED_IN -> {
                    AuthView(modifier) {
                        model.onSuccessfulAuth()
                    }
                }
                RootUiState.SIGNING_IN -> {
                    CircularProgressIndicator()
                }
                RootUiState.SIGNED_IN -> {

                }
            }
        }
    }
}