package cs346.whiteboard.service.services

import cs346.whiteboard.service.models.toJsonString
import cs346.whiteboard.service.models.toWhiteboardState
import cs346.whiteboard.service.repositories.WhiteboardTableRepository
import cs346.whiteboard.shared.jsonmodels.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface WhiteboardStateManagerInterface {

    @Transactional
    fun persistRoomToDb(roomId: Long)

    fun getWhiteboard(roomId: Long): WhiteboardState

    fun addComponent(roomId: Long, component: ComponentState)

    fun deleteComponent(roomId: Long, deleteComponent: DeleteComponent): Boolean
    fun updateComponent(roomId: Long, update: ComponentUpdate): Boolean
}


@Service
class WhiteboardStateManager(
    @Autowired private val whiteboardTableRepository: WhiteboardTableRepository
) : WhiteboardStateManagerInterface {


    private var states: MutableMap<Long, WhiteboardState> = mutableMapOf()

    @Transactional
    override fun persistRoomToDb(roomId: Long) {
        val state = getWhiteboard(roomId)

        val whiteboard = whiteboardTableRepository.findByRoomId(roomId)
        if (whiteboard != null) {
            whiteboard.state = state.toJsonString()
            val savedState = whiteboardTableRepository.save(whiteboard)
            states.remove(roomId)
        }
    }

    @Transactional
    override fun getWhiteboard(roomId: Long): WhiteboardState {
        val state = states[roomId]
        if (state != null) {
            return state
        }

        // Get the whiteboard from the database
        val whiteboard = whiteboardTableRepository.findByRoomId(roomId)
        val dbState = whiteboard?.state?.toWhiteboardState()

        if (dbState != null) {
            states[roomId] = dbState
            return dbState
        }

        return states.getOrPut(roomId) { WhiteboardState(mutableMapOf()) }
    }

    override fun addComponent(roomId: Long, component: ComponentState) {
        val whiteboard = getWhiteboard(roomId)
        whiteboard.components[component.uuid] = component
    }

    // Returns true on success
    override fun deleteComponent(roomId: Long, deleteComponent: DeleteComponent): Boolean {
        val whiteboard = getWhiteboard(roomId)
        val componentState = whiteboard.components[deleteComponent.uuid] ?: return false
        if (componentState.accessLevel == AccessLevel.LOCKED && deleteComponent.username != componentState.owner) return false
        whiteboard.components.remove(deleteComponent.uuid)
        return true
    }

    // Returns true on success
    override fun updateComponent(roomId: Long, update: ComponentUpdate): Boolean {
        val whiteboard = getWhiteboard(roomId)
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