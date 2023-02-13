package cs346.whiteboard.client.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.UserManager
import cs346.whiteboard.client.components.PrimaryButton

// TODO: replace this
@Composable
fun TestView(modifier: Modifier, onSignOut: () -> Unit) {
    Box(modifier, Alignment.Center) {
        PrimaryButton(Modifier.size(280.dp, 50.dp), "Sign out", true) {
            UserManager.signOut()
            onSignOut()
        }
    }
}