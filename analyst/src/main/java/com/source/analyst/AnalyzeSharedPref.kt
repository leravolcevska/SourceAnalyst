package com.source.analyst

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AnalyzeSharedPref {
    val sharedName = "AnalyzeSharedPref"
    val cacheExtra = "cache"

    private var isAlreadyCached = false

    suspend fun saveCache(context: Context, link: String) {
        if(isAlreadyCached) return
        isAlreadyCached = true

        val cache = getCache(context)

        if(!cache.isNullOrBlank()) return

        withContext(Dispatchers.IO) {
            val sharedPref = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPref.edit()

            editor.putString(cacheExtra, link)

            editor.apply()
        }
    }

    suspend fun getCache(context: Context): String? {
        return withContext(Dispatchers.IO) {
            val sharedPref = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE)

            val cache = sharedPref.getString(cacheExtra, null)

            if(!cache.isNullOrBlank()) isAlreadyCached = true

            cache
        }
    }
}
