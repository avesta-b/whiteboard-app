package cs346.whiteboard.client.whiteboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cs346.whiteboard.client.settings.UserManager
import cs346.whiteboard.client.constants.Typography
import cs346.whiteboard.client.constants.WhiteboardColors
import cs346.whiteboard.client.constants.subtitle2
import cs346.whiteboard.client.helpers.CustomIcon
import cs346.whiteboard.client.ui.SmallSpinner
import cs346.whiteboard.client.websocket.ComponentEventController
import cs346.whiteboard.client.whiteboard.WhiteboardController
import cs346.whiteboard.client.whiteboard.edit.EditPaneAttribute
import cs346.whiteboard.shared.jsonmodels.*
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import java.lang.ref.WeakReference
import java.util.*

val defaultImageSize = Size(512f, 512f)
class AIGeneratedImage(
    uuid: String = UUID.randomUUID().toString(),
    private val controller: WeakReference<ComponentEventController?>,
    override var coordinate: AttributeWrapper<Offset>,
    override var size: AttributeWrapper<Size> = attributeWrapper(defaultImageSize, controller, uuid),
    override var color: AttributeWrapper<ComponentColor> = attributeWrapper(defaultComponentColor, controller, uuid),
    override var depth: Float,
    override var owner: String,
    override var accessLevel: AttributeWrapper<AccessLevel> = attributeWrapper(defaultAccessLevel, controller, uuid),
    var imageData: AttributeWrapper<AIImageData> = attributeWrapper(AIImageData(), controller, uuid)
) : Component(uuid) {

    override val editPaneAttributes = listOf(
        EditPaneAttribute.IMAGE_PROMPT,
        EditPaneAttribute.ACCESS_LEVEL
    )

    override suspend fun applyServerUpdate(update: ComponentUpdate) {
        super.applyServerUpdate(update)
        update.username?.let {user ->
            update.imageData?.let {
                imageData.setFromServer(it, update.updateUUID, user)
            }
        }
    }

    override fun getComponentType(): ComponentType {
        return ComponentType.AI_IMAGE
    }

    override fun toComponentState(): ComponentState {
        var res = super.toComponentState()
        res.imageData = imageData.getValue()
        return res
    }

    override fun isResizeable(): Boolean {
        return false
    }

    @Composable
    override fun drawComposableComponent(controller: WhiteboardController) {
                Box(
            modifier = getModifier(controller),
            contentAlignment = Alignment.Center
        ) {
            imageData.getValue().url?.let {
                KamelImage(
                    resource = lazyPainterResource(it),
                    contentDescription = null,
                    onLoading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(WhiteboardColors.secondaryVariant),
                            contentAlignment = Alignment.Center) {
                            SmallSpinner()
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    crossfade = true
                )
            } ?: run {
                Box(Modifier
                    .fillMaxSize()
                    .background(WhiteboardColors.secondaryVariant))
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(CustomIcon.IMAGE.path()),
                        contentDescription = null,
                        modifier = Modifier.size((24 * controller.whiteboardZoom).dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.size(16.dp))
                    Text(
                        text = "Enter a prompt",
                        color = WhiteboardColors.primary,
                        style = Typography.subtitle2(controller.whiteboardZoom)
                    )
                }

            }
        }
    }

    override fun clone(): Component {
        val newUUID = UUID.randomUUID().toString()
        return AIGeneratedImage(
            uuid=newUUID,
            controller=controller,
            coordinate = attributeWrapper(Offset(coordinate.getValue().x, coordinate.getValue().y), controller, newUUID),
            size = attributeWrapper(size.getValue(), controller, newUUID),
            color = attributeWrapper(color.getValue(), controller, newUUID),
            depth = depth,
            owner = UserManager.getUsername() ?: "default_user",
            accessLevel = attributeWrapper(AccessLevel.UNLOCKED, controller, newUUID),
            imageData = attributeWrapper(imageData.getValue(), controller, newUUID)
        )
    }

}