package cs346.whiteboard.client.whiteboard.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.TextFieldValue
import cs346.whiteboard.client.websocket.ComponentEventController
import cs346.whiteboard.shared.jsonmodels.*
import java.lang.ref.WeakReference

fun <T> makeComponentUpdate(t: T, componentUUID: String, username: String): ComponentUpdate? {
    return when (t) {
        is androidx.compose.ui.geometry.Size -> ComponentUpdate(uuid = componentUUID, username= username, size = cs346.whiteboard.shared.jsonmodels.Size(t.width, t.height))
        is Offset -> ComponentUpdate(uuid = componentUUID, position = Position(t.x, t.y), username= username)
        is ComponentColor -> ComponentUpdate(uuid = componentUUID, color = t, username= username)
        is PathType -> ComponentUpdate(uuid = componentUUID, pathType = t, username= username)
        is PathThickness -> ComponentUpdate(uuid = componentUUID, pathThickness = t, username= username)
        is ShapeType -> ComponentUpdate(uuid = componentUUID, shapeType = t, username= username)
        is ShapeFill -> ComponentUpdate(uuid = componentUUID, shapeFill = t, username= username)
        is TextFieldValue -> ComponentUpdate(uuid = componentUUID, text = t.text, username= username)
        is TextFont -> ComponentUpdate(uuid = componentUUID, textFont = t, username= username)
        is TextSize -> ComponentUpdate(uuid = componentUUID, textSize = t, username= username)
        else -> null
    }
}

fun <T> attributeWrapper(t: T,
                         controller: WeakReference<ComponentEventController?> = WeakReference(null),
                         componentUUID: String = ""): AttributeWrapper<T> {
    return AttributeWrapper(t, controller, componentUUID)
}
class AttributeWrapper<T>(
    t: T,
    private val controller: WeakReference<ComponentEventController?>,
    private val componentUUID: String
) {
    private var value = mutableStateOf(t)

    fun getValue(): T = value.value

    fun getMutableState(): MutableState<T> = value

    private var confirmed: Boolean = true

    private var mostRecentUpdateId: String? = null

    fun setFromServer(newValue: T, updateUUID: String, username: String = "") {
        if (confirmed && username != controller.get()?.username && username != "") {
            value.value = newValue
            return
        }
        if (updateUUID == mostRecentUpdateId) {
            confirmed = true
            mostRecentUpdateId = null
            return
        }
    }

    fun setWithoutConfirm(newValue: T) {
        value.value = newValue
        confirmed = true
        mostRecentUpdateId = null
    }

    fun setLocally(newValue: T) {
        value.value  = newValue

        val update = makeComponentUpdate(newValue, componentUUID, controller?.get()?.username ?: "")
        update?.let {
            confirmed = false
            mostRecentUpdateId = update.updateUUID
            controller?.get()?.update(it)
        }
    }

    fun isConfirmed(): Boolean = confirmed
}

class IterableAttributeWrapper(private val controller: WeakReference<ComponentEventController?>,
                               private val componentUUID: String) {
    private var value = mutableStateListOf<Offset>()
    fun getValue(): List<Offset> = value

    private var confirmed: Boolean = true

    private var mostRecentUpdateId: String? = null

    fun addWithoutConfirm(item: Offset) {
        value.add(item)
        confirmed = true
        mostRecentUpdateId = null
    }

    // Only ever called locally
    fun addLocally(item: Offset) {
        value.add(item)
        val componentUpdate = ComponentUpdate(
            uuid = componentUUID,
            path = value.map { Position(it.x, it.y) }
        )
        confirmed = false
        mostRecentUpdateId = componentUpdate.updateUUID
        controller?.get()?.update(componentUpdate)
    }

    fun setLocally(newValue: List<Offset>) {
        value.clear()
        newValue.forEach { value.add(it) }
        val componentUpdate = ComponentUpdate(
            uuid = componentUUID,
            path = value.map { Position(it.x, it.y) }
        )
        confirmed = false
        mostRecentUpdateId = componentUpdate.updateUUID
        controller?.get()?.update(componentUpdate)
    }

    // Called by move/resizing methods on Point data class
    fun setFromServer(list: List<Offset>, updateUUID: String, username: String = "") {
        if (confirmed && username != controller.get()?.username && username != "") {
            value.clear()
            list.forEach { value.add(it) }
        }

        if (updateUUID == mostRecentUpdateId) {
            confirmed = true
            mostRecentUpdateId = null
        }
    }

    fun setIndex(newValue: Offset, index: Int) {
        if (index > value.size) return
        value[index] = newValue
    }

    fun batchUpdate() {
        val componentUpdate = ComponentUpdate(
            uuid = componentUUID,
            path = value.map { Position(it.x, it.y) }
        )
        confirmed = false
        mostRecentUpdateId = componentUpdate.updateUUID
        controller?.get()?.update(componentUpdate)
    }
}
