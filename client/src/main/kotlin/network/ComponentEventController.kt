/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.websocket

import cs346.whiteboard.client.whiteboard.components.Component
import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.ComponentUpdate
import cs346.whiteboard.shared.jsonmodels.DeleteComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext


sealed class ComponentEvent
data class AddComponent(val component: Component) : ComponentEvent()
data class UpdateComponent(val componentUpdate: ComponentUpdate) : ComponentEvent()
data class DeleteComponentEvent(val componentId: String) : ComponentEvent()
object RequestFullState : ComponentEvent()

class ComponentEventController @OptIn(DelicateCoroutinesApi::class) constructor(
    roomId: String,
    private val handler: WeakReference<WebSocketEventHandler>,
    val username: String,
    coroutineContext: CoroutineContext = newSingleThreadContext("eventController$roomId")
) {
    private val eventChannel = Channel<ComponentEvent>(Channel.UNLIMITED)

    private val scope = CoroutineScope(coroutineContext)

    init {

        scope.launch {
            for (event in eventChannel) {
                when (event) {
                    is AddComponent -> handler.get()?.let {
                        it.send<ComponentState>(
                            sendSuffix = ".addComponent",
                            body = event.component.toComponentState(),
                            serializationStrategy = ComponentState.serializer()
                        )
                    }

                    is UpdateComponent -> handler.get()?.let {
                        it.send<ComponentUpdate>(
                            sendSuffix = ".updateComponent",
                            body = event.componentUpdate,
                            serializationStrategy = ComponentUpdate.serializer()
                        )
                    }

                    is DeleteComponentEvent -> handler.get()?.let {
                        it.send<DeleteComponent>(
                            sendSuffix = ".deleteComponent",
                            body = DeleteComponent(event.componentId, username),
                            serializationStrategy = DeleteComponent.serializer()
                        )
                    }

                    is RequestFullState -> handler.get()?.let {
                        it.send(
                            sendSuffix = ".getFullState",
                            body = null,
                            serializationStrategy = DeleteComponent.serializer()
                        )
                    }
                }
            }
        }

    }

    fun add(component: Component) {

            eventChannel.trySend(AddComponent(component))

    }

    fun update(componentUpdate: ComponentUpdate) {
            componentUpdate.username = username
            eventChannel.trySend(UpdateComponent(componentUpdate))
    }

    fun delete(component: String?) {

            component?.let {
                eventChannel.trySend(DeleteComponentEvent(it))
            }

    }

    fun requestFullState() {
        eventChannel.trySend(RequestFullState)
    }
}