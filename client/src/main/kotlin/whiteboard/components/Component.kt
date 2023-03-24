package cs346.whiteboard.client.whiteboard.components

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
import cs346.whiteboard.client.helpers.toOffset
import cs346.whiteboard.client.helpers.toSize
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.client.whiteboard.edit.ResizeNode
import cs346.whiteboard.shared.jsonmodels.*
import java.util.*

val defaultComponentColor = ComponentColor.BLACK

abstract class Component(val uuid: String = UUID.randomUUID().toString()) {

    var isFocused: MutableState<Boolean> = mutableStateOf(false)

    abstract var depth: Float

    abstract var coordinate: AttributeWrapper<Offset>

    abstract var size: AttributeWrapper<Size>

    abstract var color: AttributeWrapper<ComponentColor>

    abstract val editPaneAttributes: List<EditPaneAttribute>

    abstract fun getComponentType(): ComponentType

    open fun toComponentState(): ComponentState {
        return ComponentState(
            uuid=uuid,
            depth=depth,
            color = color.getValue(),
            componentType = getComponentType(),
            size = cs346.whiteboard.shared.jsonmodels.Size(size.getValue().width, size.getValue().height),
            position = Position(coordinate.getValue().x, coordinate.getValue().y)
        )
    }

    open suspend fun applyServerUpdate(update: ComponentUpdate) {
        update.username?.let {user ->
            update.size?.let {
                size.setFromServer(it.toSize(), update.updateUUID, user)
            }
            update.position?.let {
                coordinate.setFromServer(it.toOffset(), update.updateUUID, user)
            }
            update.color?.let {
                color.setFromServer(it, update.updateUUID, user)
            }
        }
    }

    @Composable
    open fun getModifier(controller: WhiteboardController): Modifier {
        val componentViewCoordinate = controller.whiteboardToViewCoordinate(coordinate.getValue())
        return Modifier
            .wrapContentSize(Alignment.TopStart, true)
            .offset(componentViewCoordinate.x.toDp(), componentViewCoordinate.y.toDp())
            .size((size.getValue().width * controller.whiteboardZoom).toDp(),
                (size.getValue().height * controller.whiteboardZoom).toDp())
            .zIndex(depth)
    }

    @Composable
    abstract fun drawComposableComponent(controller: WhiteboardController)

    abstract fun clone(): Component

    open fun isResizeable(): Boolean {
        return true
    }

    open fun move(amount: Offset) {
        coordinate.setLocally(coordinate.getValue().plus(amount))
    }

    open fun smallestPossibleSize(): Size {
        return Size(40f, 40f)
    }

    open fun resize(resizeMultiplier: Float, resizeNodeAnchor: ResizeNode, anchorPoint: Offset) {
        val newSize = size.getValue().times(resizeMultiplier)
        var componentAnchorPoint =
            when (resizeNodeAnchor) {
                ResizeNode.TOP_LEFT -> coordinate.getValue()
                ResizeNode.TOP_RIGHT -> Offset(
                    coordinate.getValue().x + size.getValue().width,
                    coordinate.getValue().y
                )
                ResizeNode.BOTTOM_LEFT -> Offset(
                    coordinate.getValue().x,
                    coordinate.getValue().y + size.getValue().height
                )
                ResizeNode.BOTTOM_RIGHT -> Offset(
                    coordinate.getValue().x + size.getValue().width,
                    coordinate.getValue().y + size.getValue().height
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
        coordinate.setLocally(newCoordinate)
        if (isResizeable()) {
            size.setLocally(newSize)
        }
    }

}
