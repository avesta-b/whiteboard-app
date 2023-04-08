package cs346.whiteboard.client

import cs346.whiteboard.client.settings.PreferencesManager
import org.junit.jupiter.api.Test
import java.util.prefs.Preferences
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class PreferencesManagerTest {

    private val preferences: Preferences = Preferences.userNodeForPackage(PreferencesManager.javaClass)

    @Test
    fun testWriteToPreferencesWithKey() {
        val key = "test-key"
        val content = "test-content"
        PreferencesManager.writeToPreferencesWithKey(key, content)
        assertEquals(content, preferences.get(key, ""))
    }

    @Test
    fun testReadFromPreferences() {
        val key = "test-key"
        val content = "test-content"
        preferences.put(key, content)
        val result = PreferencesManager.readFromPreferences(key)
        assertEquals(content, result)
    }

    @Test
    fun testReadFromPreferencesNonexistentKey() {
        val key = "nonexistent-key"
        val result = PreferencesManager.readFromPreferences(key)
        assertNull(result)
    }

    @Test
    fun testRemoveFromPreferences() {
        val key = "test-key"
        val content = "test-content"
        preferences.put(key, content)
        PreferencesManager.removeFromPreferences(key)
        assertFalse(preferences.keys().contains(key))
    }
}