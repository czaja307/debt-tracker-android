package com.example.debttracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for Context to create a single DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manager for handling user preferences stored in DataStore
 */
class PreferencesManager(private val context: Context) {
    
    companion object {
        // Keys for user preferences
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val CURRENCY_KEY = stringPreferencesKey("user_currency")
        private val PROFILE_PICTURE_KEY = stringPreferencesKey("profile_picture_uri")
    }
    
    /**
     * Get the stored user name
     */
    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[NAME_KEY] ?: ""
    }
    
    /**
     * Get the stored user currency
     */
    val userCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "USD"
    }
    
    /**
     * Get the stored profile picture URI
     */
    val profilePictureUri: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_PICTURE_KEY] ?: ""
    }
    
    /**
     * Save the user name
     */
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
        }
    }
    
    /**
     * Save the user currency
     */
    suspend fun saveUserCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
    
    /**
     * Save the profile picture URI
     */
    suspend fun saveProfilePictureUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_PICTURE_KEY] = uri
        }
    }
    
    /**
     * Clear all user preferences
     */
    suspend fun clearPreferences() {
        context.dataStore.edit { it.clear() }
    }
}
