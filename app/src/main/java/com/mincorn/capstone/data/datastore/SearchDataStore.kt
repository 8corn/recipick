package com.mincorn.capstone.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchDataStore (
    private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "search_prefs")
    private val searchHistoryKey = stringPreferencesKey("search_history")
    private val gson = Gson()

    val getRecentSearches: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[searchHistoryKey] ?: ""

        if (json.isEmpty()) {
            emptyList()
        } else {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        }
    }

    suspend fun saveSearch(keyword: String) {
        val cleanKeyword = keyword.trim()
        if (cleanKeyword.isEmpty()) return

        context.dataStore.edit { prefs ->
            val currentJson = prefs[searchHistoryKey] ?: ""
            val currentList: MutableList<String> =
                if (currentJson.isEmpty()) {
                    mutableListOf()
                } else {
                    gson.fromJson(currentJson, object : TypeToken<List<String>>() {}.type)
                }

            currentList.remove(cleanKeyword)
            currentList.add(0, cleanKeyword)

            val limitedList = currentList.take(5)
            prefs[searchHistoryKey] = gson.toJson(limitedList)
        }
    }

    suspend fun deleteSearch(keyword: String) {
        context.dataStore.edit { prefs ->
            val currentJson = prefs[searchHistoryKey] ?: ""
            if (currentJson.isNotEmpty()) {
                val currentList: MutableList<String> = gson.fromJson(currentJson, object : TypeToken<List<String>>() {}.type)

                currentList.remove(keyword)
                prefs[searchHistoryKey] = gson.toJson(currentList)
            }
        }
    }
}