package dev.rushia.verhaal_mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.rushia.verhaal_mobile.data.remote.response.LoginResult
import dev.rushia.verhaal_mobile.utils.Const
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Const.AUTH_PREFERENCES)

class AuthPreferences private constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveAuthToken(user: LoginResult) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(Const.TOKEN_KEY)] = setOf(user.token)
            preferences[stringSetPreferencesKey(Const.USER_NAME)] = setOf(user.name)
        }
    }

    fun getAuthToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(Const.TOKEN_KEY)]?.firstOrNull()
        }
    }

    fun getUserName(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(Const.USER_NAME)]?.firstOrNull()
        }
    }

    suspend fun clearAuthToken() {
        dataStore.edit { preferences ->
            preferences.remove(stringSetPreferencesKey(Const.TOKEN_KEY))
            preferences.remove(stringSetPreferencesKey(Const.USER_NAME))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}