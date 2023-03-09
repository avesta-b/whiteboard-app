package cs346.whiteboard.client.whiteboard

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.zIndex
import cs346.whiteboard.client.helpers.toDp
import java.util.*

abstract class Component {

    internal var uuid: String = UUID.randomUUID().toString()
    var isFocused: MutableState<Boolean> = mutableStateOf(false)

    abstract var depth: Float

    abstract var coordinate: MutableState<Offset>

    abstract var size: MutableState<Size>

    @Composable
    fun getModifier(controller: WhiteboardController): Modifier {
        val componentViewCoordinate = controller.whiteboardToViewCoordinate(coordinate.value)
        return Modifier
            .wrapContentSize(Alignment.TopStart, true)
            .offset(componentViewCoordinate.x.toDp(), componentViewCoordinate.y.toDp())
            .size((size.value.width * controller.whiteboardZoom).toDp(),
                (size.value.height * controller.whiteboardZoom).toDp())
            .zIndex(depth)
    }

    @Composable
    abstract fun drawComposableComponent(controller: WhiteboardController)

    abstract fun clone(): Component

    open fun isResizeable(): Boolean {
        return true
    }

    open fun move(amount: Offset) {
        coordinate.value = coordinate.value.plus(amount)
    }

    open fun smallestPossibleSize(): Size {
        return Size(40f, 40f)
    }

    open fun resize(resizeMultiplier: Float, resizeNodeAnchor: ResizeNode, anchorPoint: Offset) {
        var componentAnchorPoint =
            when (resizeNodeAnchor) {
                ResizeNode.TOP_LEFT -> coordinate.value
                ResizeNode.TOP_RIGHT -> Offset(
                    coordinate.value.x + size.value.width,
                    coordinate.value.y
                )
                ResizeNode.BOTTOM_LEFT -> Offset(
                    coordinate.value.x,
                    coordinate.value.y + size.value.height
                )
                ResizeNode.BOTTOM_RIGHT -> Offset(
                    coordinate.value.x + size.value.width,
                    coordinate.value.y + size.value.height
                )
            }
        var componentRelativeAnchorPoint =
            when (resizeNodeAnchor) {
                ResizeNode.TOP_LEFT -> Offset(
                    anchorPoint.x + (componentAnchorPoint.x - anchorPoint.x) * resizeMultiplier,
                    anchorPoint.y + (componentAnchorPoint.y - anchorPoint.y) * resizeMultiplier
                )
                ResizeNode.TOP_RIGHT -> Offset(
                    anchorPoint.x - (anchorPoint.x - componentAnchorPoint.x) * resizeMultiplier,
                    anchorPoint.y + (componentAnchorPoint.y - anchorPoint.y) * resizeMultiplier
                )
                ResizeNode.BOTTOM_LEFT -> Offset(
                    anchorPoint.x + (componentAnchorPoint.x - anchorPoint.x) * resizeMultiplier,
                    anchorPoint.y - (anchorPoint.y - componentAnchorPoint.y) * resizeMultiplier
                )
                ResizeNode.BOTTOM_RIGHT -> Offset(
                    anchorPoint.x - (anchorPoint.x - componentAnchorPoint.x) * resizeMultiplier,
                    anchorPoint.y - (anchorPoint.y - componentAnchorPoint.y) * resizeMultiplier
                )
            }
        val newSize = size.value.times(resizeMultiplier)
        val newCoordinate =
            when (resizeNodeAnchor) {
                ResizeNode.TOP_LEFT -> componentRelativeAnchorPoint
                ResizeNode.TOP_RIGHT -> Offset(
                    componentRelativeAnchorPoint.x - newSize.width,
                    componentRelativeAnchorPoint.y
                )
                ResizeNode.BOTTOM_LEFT -> Offset(
                    componentRelativeAnchorPoint.x,
                    componentRelativeAnchorPoint.y - newSize.height
                )
                ResizeNode.BOTTOM_RIGHT -> Offset(
                    componentRelativeAnchorPoint.x - newSize.width,
                    componentRelativeAnchorPoint.y - newSize.height
                )
            }
        coordinate.value = newCoordinate
        if (isResizeable()) {
            size.value = newSize
        }
    }

}