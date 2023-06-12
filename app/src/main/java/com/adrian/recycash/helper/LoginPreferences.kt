package com.adrian.recycash.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = "Bearer $token"
        }
    }

    val tokenFlow: Flow<String>
        get() = dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    val isLoggedInFlow: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}