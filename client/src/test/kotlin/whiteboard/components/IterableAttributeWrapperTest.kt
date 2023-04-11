/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package whiteboard.components

import androidx.compose.ui.geometry.Offset
import cs346.whiteboard.client.websocket.ComponentEventController
import cs346.whiteboard.client.whiteboard.components.IterableAttributeWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference

class IterableAttributeWrapperTest {

    private lateinit var wrapper: IterableAttributeWrapper
    private val componentUUID = "testUUID"

    @BeforeEach
    fun setup() {
        val controllerRef = WeakReference<ComponentEventController?>(null)
        wrapper = IterableAttributeWrapper(controllerRef, componentUUID)
    }

    @Test
    fun testAddWithoutConfirm() {
        val item = Offset(10f, 20f)
        wrapper.addWithoutConfirm(item)

        val value = wrapper.getValue()
        assertEquals(1, value.size)
        assertEquals(item, value[0])
    }

    @Test
    fun testAddLocally() {
        val item = Offset(30f, 40f)
        wrapper.addLocally(item)

        val value = wrapper.getValue()
        assertEquals(1, value.size)
        assertEquals(item, value[0])
    }

    @Test
    fun testSetLocally() {
        val newValue = listOf(Offset(50f, 60f), Offset(70f, 80f))
        val updateUUID = "testUpdateUUID1"
        wrapper.setLocally(newValue)

        val value = wrapper.getValue()
        assertEquals(2, value.size)
        assertEquals(newValue[0], value[0])
        assertEquals(newValue[1], value[1])
    }

    @Test
    fun testSetIndex() {
        wrapper.addWithoutConfirm(Offset(190f, 200f))
        wrapper.addWithoutConfirm(Offset(210f, 220f))

        val newValue = Offset(230f, 240f)
        wrapper.setIndex(newValue, 1)

        val value = wrapper.getValue()
        assertEquals(2, value.size)
        assertEquals(newValue, value[1])
    }

    @Test
    fun testBatchUpdate() {
        wrapper.addWithoutConfirm(Offset(250f, 260f))
        wrapper.addWithoutConfirm(Offset(270f, 280f))

        wrapper.batchUpdate()

        val value = wrapper.getValue()
        assertEquals(2, value.size)
        assertEquals(Offset(250f, 260f), value[0])
        assertEquals(Offset(270f, 280f), value[1])
    }
}