package cs346.whiteboard.client.websocket

import cs346.whiteboard.client.whiteboard.Component
import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.DeleteComponent
import java.lang.ref.WeakReference

class ComponentEventController(
    private val handler: WeakReference<WebSocketEventHandler>
) {

    fun add(component: Component) {
        handler.get()?.let {
            it.send<ComponentState>(
                sendSuffix=".addComponent",
                body = component.toComponentState(),
                serializationStrategy = ComponentState.serializer()
            )
        }
    }

    fun delete(component: String?) {
        val component: String = component ?: return
        handler.get()?.let {
            it.send<DeleteComponent>(
                sendSuffix=".deleteComponent",
                body = DeleteComponent(component),
                serializationStrategy = DeleteComponent.serializer()
            )
        }
    }

    fun requestFullState() {
        handler.get()?.let {
            it.send(
                sendSuffix = ".getFullState",
                body = null,
                serializationStrategy = DeleteComponent.serializer()
            )
        }
    }
}