package cs346.whiteboard.client.helpers

import cs346.whiteboard.client.constants.WhiteboardColors
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

fun getResource(name: String): InputStream = object {}.javaClass.getResourceAsStream(name)

enum class CustomIcon {
    POINTER {
        override fun fileName(): String = "pointer.png"
    },
    HAND {
        override fun fileName(): String = "hand.png"
    },
    GRAB {
        override fun fileName(): String = "grab.png"
    },
    RESIZE_LEFT {
        override fun fileName(): String = "resize-left.png"
    },
    RESIZE_RIGHT {
        override fun fileName(): String = "resize-right.png"
    },
    BRUSH {
        override fun fileName(): String = "brush.png"
    },
    HIGHLIGHTER {
        override fun fileName(): String = "highlighter.png"
    },
    PAINT {
        override fun fileName(): String = "paint.png"
    },
    SQUARE {
        override fun fileName(): String = "square.png"
    },
    RECTANGLE {
        override fun fileName(): String = "rectangle.png"
    },
    TRIANGLE {
        override fun fileName(): String = "triangle.png"
    },
    CIRCLE {
        override fun fileName(): String = "circle.png"
    },
    TEXTFIELD {
        override fun fileName(): String = "textfield.png"
    },
    ERASER {
        override fun fileName(): String = "eraser.png"
    },
    MINUS {
        override fun fileName(): String = "minus.png"
    },
    PLUS {
        override fun fileName(): String = "plus.png"
    },
    BACK {
        override fun fileName(): String = "back.png"
    },
    COPY {
        override fun fileName(): String = "copy.png"
    },
    DELETE {
        override fun fileName(): String = "delete.png"
    },
    LOCK {
        override fun fileName(): String = "lock.png"
    },
    UNLOCK {
        override fun fileName(): String = "unlock.png"
    },
    EXPAND {
        override fun fileName(): String = "expand.png"
    },
    COLLAPSE {
        override fun fileName(): String = "collapse.png"
    },
    SEND {
        override fun fileName(): String = "send.png"
    },
    OPENAI {
        override fun fileName(): String = "openai.png"
    },
    IMAGE {
        override fun fileName(): String = "image.png"
    },
    ARROW
    {
        override fun fileName(): String = "arrow.png"
    },
    FILE {
        override fun fileName(): String = "file.png"
    },
    SHARED {
        override fun fileName(): String = "shared.png"
    };

    abstract fun fileName(): String

    fun path(): String {
        var fileName = fileName()
        var indexOfDot = fileName.indexOf('.')
        if (WhiteboardColors.isDarkMode) {
            fileName = "${fileName.substring(0, indexOfDot)}-dark${fileName.substring(indexOfDot)}"
        }
        return "/icons/$fileName"
    }
    fun image(): BufferedImage {
        return ImageIO.read(getResource(path()))
    }
}

val imageCache = mutableMapOf<String, ImageBitmap>()
