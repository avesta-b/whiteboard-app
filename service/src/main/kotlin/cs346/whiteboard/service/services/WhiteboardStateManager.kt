package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.ComponentState
import cs346.whiteboard.shared.jsonmodels.ComponentUpdate
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

    fun updateComponent(roomId: String, update: ComponentUpdate) {
        val whiteboard = states[roomId] ?: return
        var componentState = whiteboard.components[update.uuid] ?: return
        update.size?.let {
            componentState.size = it
        }
        update.position?.let {
            componentState.position = it
        }
        update.color?.let {
            componentState.color = it
        }
        update.path?.let {
            componentState.path = it
        }
        update.pathType?.let {
            componentState.pathType = it
        }
        update.pathThickness?.let {
            componentState.pathThickness = it
        }
        update.shapeType?.let {
            componentState.shapeType = it
        }
        update.shapeFill?.let {
            componentState.shapeFill = it
        }
        update.text?.let {
            componentState.text = it
        }
        update.textFont?.let {
            componentState.textFont = it
        }
        update.textSize?.let {
            componentState.textSize = it
        }
        whiteboard.components[update.uuid] = componentState
    }
}