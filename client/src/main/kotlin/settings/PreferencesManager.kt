/*
 * Copyright (c) 2023 Avesta Barzegar, York Wei, Mikail Rahman, Edward Wang
 */

package cs346.whiteboard.client.settings

import  java.util.prefs.*
object PreferencesManager {

    private val preferences: Preferences = Preferences.userNodeForPackage(PreferencesManager.javaClass)
    fun writeToPreferencesWithKey(key: String, content: String) {
        preferences.put(key, content)
    }
    fun readFromPreferences(key: String): String? {
        val content = preferences.get(key, "")
        return if (content.isEmpty()) null else content
    }

    fun removeFromPreferences(key: String) {
        preferences.remove(key)
    }
}