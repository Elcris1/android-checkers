package com.example.checkers.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.checkers.data.constants.Teams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.math.min

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {


    private object PreferencesKeys {
        val ALIAS_KEY = stringPreferencesKey("alias")
        val IS_WHITE_KEY = booleanPreferencesKey("team")
        val TIME_ENABLED_KEY = booleanPreferencesKey("is_time_enabled")
        val TIME_MINUTES = intPreferencesKey("minutes")
        val TIME_SECONDS = intPreferencesKey("seconds")
    }

    suspend fun saveToDataStore(alias: String, whiteTeam: Boolean, isEnabled: Boolean, minutes: Int, seconds: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.ALIAS_KEY] = alias
            it[PreferencesKeys.IS_WHITE_KEY] = whiteTeam
            it[PreferencesKeys.TIME_ENABLED_KEY] = isEnabled
            it[PreferencesKeys.TIME_MINUTES] = minutes
            it[PreferencesKeys.TIME_SECONDS] = seconds
        }
    }

    data class ConfigData(
        val alias: String,
        val isWhite: Boolean,
        val timeEnabled: Boolean,
        val minutes: Int,
        val seconds: Int
    )


    val alias: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.ALIAS_KEY] ?: ""
    }

    val isWhiteTeam: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.IS_WHITE_KEY] ?: true
    }

    val timeEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.TIME_ENABLED_KEY] ?: false
    }

    val minutes: Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.TIME_MINUTES] ?: 0
    }
    val seconds: Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.TIME_SECONDS] ?: 0
    }

    val configuration: Flow<ConfigData> = combine(
        alias,
        isWhiteTeam,
        timeEnabled,
        minutes,
        seconds
    ) { alias, team, time, min, sec ->
        ConfigData(alias, team, time, min, sec)
    }

    suspend fun setAlias(alias: String) {
        context.dataStore.edit { it[PreferencesKeys.ALIAS_KEY] = alias }
    }

    suspend fun setTeam(isWhite: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_WHITE_KEY] = isWhite }
    }

    suspend fun setTimeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.TIME_ENABLED_KEY] = enabled }
    }

    suspend fun setMinutes(time: Int) {
        context.dataStore.edit { it[PreferencesKeys.TIME_MINUTES] = time }
    }
    suspend fun setSeconds(time: Int) {
        context.dataStore.edit { it[PreferencesKeys.TIME_SECONDS] = time }

    }
}