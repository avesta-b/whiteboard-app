package cs346.whiteboard.service.services

import cs346.whiteboard.shared.jsonmodels.*
import org.springframework.stereotype.Service

@Service
class WhiteboardStateManager {
    private var states: MutableMap<String, WhiteboardState> = mutableMapOf()

    fun getWhiteboard(roomId: String): WhiteboardState? = states[roomId]

    fun addComponent(roomId: String, component: ComponentState) {
        val whiteboard = states.getOrPut(roomId) { WhiteboardState() }
        whiteboard.components[component.uuid] = component
    }

    // Returns true on success
    fun deleteComponent(roomId: String, deleteComponent: DeleteComponent): Boolean {
        val whiteboard = states[roomId] ?: return false
        val componentState = whiteboard.components[deleteComponent.uuid] ?: return false
        if (componentState.accessLevel == AccessLevel.LOCKED && deleteComponent.username != componentState.owner) return false
        whiteboard.components.remove(deleteComponent.uuid)
        if (whiteboard.components.isEmpty()) {
            states.remove(roomId)
        }
        return true
    }

    // Returns true on success
    fun updateComponent(roomId: String, update: ComponentUpdate): Boolean {
        val whiteboard = states[roomId] ?: return false
        var componentState = whiteboard.components[update.uuid] ?: return false
        if (componentState.accessLevel == AccessLevel.LOCKED && update.username != componentState.owner) return false
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
        update.accessLevel?.let {
            componentState.accessLevel = it
        }
        update.imageData?.let {
            componentState.imageData = it
        }
        whiteboard.components[update.uuid] = componentState
        return true
    }
}