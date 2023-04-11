/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.views

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.ui.*
import cs346.whiteboard.client.whiteboardmanager.MyWhiteboardManager
import kotlinx.coroutines.launch

enum class HomeUiState {
    DRAW, MENU, CREATE_WHITEBOARD
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeView(modifier: Modifier, onSignOut: () -> Unit) {
    var homeUiState by remember { mutableStateOf(HomeUiState.MENU) }

    var isShowingOwnWhiteboard = remember { mutableStateOf(true) }

    var createNewWhiteboard = remember { mutableStateOf(TextFieldValue("")) }

    var coroutineScope = rememberCoroutineScope()

    var whiteboardsManagers = remember { mutableStateOf(MyWhiteboardManager(coroutineScope)) }

    BoxWithConstraints(modifier, Alignment.Center) {
        AnimatedVisibility(
            visible = homeUiState == HomeUiState.DRAW,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val item = whiteboardsManagers.value.selectedWhiteboardItem
            if (item == null) {
                homeUiState = HomeUiState.MENU
            }
            WhiteboardView(modifier, item?.name ?: "", roomId = item?.id ?: -1, onExit = {
                whiteboardsManagers.value.selectedWhiteboardItem = null
                homeUiState = HomeUiState.MENU
                whiteboardsManagers.value.onLaunch()
            })
        }
        AnimatedVisibility(
            visible = homeUiState == HomeUiState.MENU,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Logo(modifier = Modifier.size(59.dp, 37.dp))

                    Spacer(modifier = Modifier.weight(1f))

                    TwoTextButton("My Whiteboards", "Shared With Me",
                        isShowingOwnWhiteboard.value,
                        onClick = {
                            isShowingOwnWhiteboard.value = !isShowingOwnWhiteboard.value
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(Modifier.height(40.dp), "Sign out", true) {
                        onSignOut()
                    }
                }

                // Box for scrollable portion
                Box(modifier = Modifier.weight(1f)) {
                    // Scrollable content
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isShowingOwnWhiteboard.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        whiteboardsManagers.value.OwnWhiteboardList {
                            whiteboardsManagers.value.onDisconnect()
                            homeUiState = HomeUiState.DRAW
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isShowingOwnWhiteboard.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        whiteboardsManagers.value.SharedWhiteboardList {
                            whiteboardsManagers.value.onDisconnect()
                            homeUiState = HomeUiState.DRAW
                        }
                    }
                }

                if (isShowingOwnWhiteboard.value) {
                    PrimaryButton(
                        Modifier.size(280.dp, 90.dp).padding(bottom = 20.dp, top = 20.dp),
                        "Create New Whiteboard",
                        enabled = true
                    ) {
                        homeUiState = HomeUiState.CREATE_WHITEBOARD
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = homeUiState == HomeUiState.CREATE_WHITEBOARD,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                TextInputDialogWithAcceptAndCancel(
                    modifier = Modifier.width(400.dp).align(Alignment.Center),
                    onAccept = {
                        coroutineScope.launch {
                            whiteboardsManagers.value.createNewWhiteboard(
                                createNewWhiteboard.value.text
                            )
                            createNewWhiteboard.value = TextFieldValue("")
                            homeUiState = HomeUiState.MENU
                        }
                    },
                    onCancel = { homeUiState = HomeUiState.MENU },
                    text = createNewWhiteboard,
                    placeholder = "Whiteboard Name",
                    smallTitle = "Create New Whiteboard",
                    acceptText = "Create",
                    showError = false
                )
            }
        }
    }
}