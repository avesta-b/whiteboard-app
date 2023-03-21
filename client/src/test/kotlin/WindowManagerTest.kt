package cs346.whiteboard.client

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.Dimension

class WindowManagerTest {

    @BeforeEach
    fun setUp() {
        PreferencesManager.removeFromPreferences(WINDOW_KEY)
    }

    @Test
    fun `getWindowSize returns correct DpSize`() {
        val expectedDpSize = DpSize(1200.dp, 800.dp)
        val actualDpSize = WindowManager.getWindowSize()

        assertEquals(expectedDpSize, actualDpSize)
    }

    @Test
    fun `setWindowSize updates windowSize correctly`() {
        val newDimension = Dimension(1500, 1000)
        WindowManager.setWindowSize(newDimension)

        assertEquals(newDimension.width.dp, WindowManager.getWindowSize().width)
        assertEquals(newDimension.height.dp, WindowManager.getWindowSize().height)
    }
}

