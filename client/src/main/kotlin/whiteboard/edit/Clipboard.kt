/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.ui.geometry.Offset
import cs346.whiteboard.client.whiteboard.components.Component

object Clipboard {
    private var selectionData = mutableListOf<Component>()
    private var offset = Offset(10f, 10f)

    internal fun copy(components: List<Component>) {
        if (components.isNotEmpty()) {
            selectionData.clear()
            offset = Offset(10f, 10f)
            components.forEach {
                selectionData.add(it.clone())
            }
        }
    }

    internal fun paste(): List<Component> {
        val components = mutableListOf<Component>()
        selectionData.forEach {
            val component = it.clone()
            component.move(offset, true)
            components.add(component)
        }
        offset = offset.plus(Offset(10f, 10f))
        return components
    }
}