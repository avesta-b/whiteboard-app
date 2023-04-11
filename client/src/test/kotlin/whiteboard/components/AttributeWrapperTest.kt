/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package whiteboard.components

import cs346.whiteboard.client.whiteboard.components.AttributeWrapper
import cs346.whiteboard.client.whiteboard.components.attributeWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AttributeWrapperTest {
    private lateinit var attributeWrapper: AttributeWrapper<String>
    @BeforeEach
    fun setUp() {
        attributeWrapper = attributeWrapper("initial")
    }

    @Test
    fun testGetValue() {
        assertEquals("initial", attributeWrapper.getValue())
    }
}
