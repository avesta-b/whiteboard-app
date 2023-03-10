package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.DeleteComponent
import cs346.whiteboard.shared.jsonmodels.WhiteboardState
import org.springframework.stereotype.Service

@Service
class WhiteboardStateManager {
    private var states: MutableMap<String, WhiteboardState> = mutableMapOf()

    fun getWhiteboard(roomId: String): WhiteboardState? = states[roomId]

    fun addComponent(roomId: String, component: ComponentState) {
        val whiteboard = states.getOrPut(roomId) { WhiteboardState() }
        whiteboard.components[component.uuid] = component
    }

    fun deleteComponent(roomId: String, deleteComponent: DeleteComponent) {
        val whiteboard = states[roomId] ?: return
        whiteboard.components.remove(deleteComponent.uuid)
        if (whiteboard.components.isEmpty()) {
            states.remove(roomId)
        }
    }
}