/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.whiteboard.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import cs346.whiteboard.client.helpers.overlap
import cs346.whiteboard.client.whiteboard.components.Component

data class QueryBoxData(
    val anchorPoint: Offset,
    val coordinate: Offset,
    val size: Size
)
class QueryBoxController {
    var queryBoxData by mutableStateOf<QueryBoxData?>(null)
        private set

    fun startQueryBox(initialPosition: Offset) {
        queryBoxData = QueryBoxData(
            anchorPoint = initialPosition,
            coordinate = initialPosition,
            size = Size.Zero
        )
    }

    fun updateQueryBox(newPosition: Offset) {
        queryBoxData?.let {
            val topLeft = Offset(minOf(it.anchorPoint.x, newPosition.x), minOf(it.anchorPoint.y, newPosition.y))
            val bottomRight = Offset(maxOf(it.anchorPoint.x, newPosition.x), maxOf(it.anchorPoint.y, newPosition.y))
            val newSize = Size(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y)
            val newX = if (topLeft.x == it.anchorPoint.x) it.anchorPoint.x else it.anchorPoint.x - newSize.width
            val newY = if (topLeft.y == it.anchorPoint.y) it.anchorPoint.y else it.anchorPoint.y - newSize.height
            queryBoxData = QueryBoxData(
                anchorPoint = it.anchorPoint,
                coordinate = Offset(newX, newY),
                size = newSize
            )
        }
    }

    // Returns (list of components in query box, minCoordinate, maxCoordinate)
    fun getComponentsInQueryBox(components: List<Component>): List<Component> {
        val componentsInQueryBox = mutableListOf<Component>()
        queryBoxData?.let { queryBox ->
            components.forEach { component ->
                if (overlap(
                    queryBox.coordinate,
                    queryBox.size,
                    component.coordinate.getValue(),
                    component.size.getValue()
                )) {
                    componentsInQueryBox.add(component)
                }
            }
        }
        return componentsInQueryBox
    }

    fun clearQueryBox() {
        queryBoxData = null
    }

}