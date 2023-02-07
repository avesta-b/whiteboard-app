package cs346.whiteboard.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedClassTests {
    @Test
    fun testSum() {
        val expected = 42
        assertEquals(expected, SharedClass().sum(40, 2))
    }
}