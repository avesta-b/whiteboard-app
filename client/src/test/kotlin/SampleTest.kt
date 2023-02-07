package cs346.whiteboard.client

import kotlin.test.Test
import kotlin.test.assertEquals
import cs346.whiteboard.shared.SharedClass

class SampleTest {
    @Test
    fun testSum() {
        val expected = 42
        assertEquals(expected, 40 + 2)
    }

    @Test
    fun testShared() {
        val expected = 42
        assertEquals(expected, SharedClass().sum(40, 2))
    }
}