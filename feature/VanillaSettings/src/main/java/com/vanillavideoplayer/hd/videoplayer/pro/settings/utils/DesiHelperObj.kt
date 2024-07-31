package com.vanillavideoplayer.hd.videoplayer.pro.settings.utils

import java.util.Locale

object DesiHelperObj {
    fun getAvailableDesis(): List<Pair<String, String>> {
        return try {
            Locale.getAvailableLocales().map {
                val key = it.isO3Language
                val language = it.displayLanguage
                Pair(language, key)
            }.distinctBy { it.second }.sortedBy { it.first }
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    fun getDesiDisplayLang(key: String): String {
        return try {
            Locale.getAvailableLocales().first { it.isO3Language == key }.displayLanguage
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
