package com.mincorn.capstone.data.source.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getNgrokUrl(): String = prefs.getString("ngrok_url", "") ?: ""

    fun setNgrokUrl(url: String) = prefs.edit { putString("ngrok_url", url) }
}