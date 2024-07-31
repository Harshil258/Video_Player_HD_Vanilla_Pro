package com.vanillavideoplayer.hd.videoplayer.pro.settings

import android.content.Context
import android.content.SharedPreferences

class GlobalPrefs {
    private val PREF_NAME = "GlobalAdPrefs"

    fun getLangCode(context: Context): String {
        val mPrefs = getSP(context)
        return mPrefs.getString("vanilla_app_lang_pref", "")!!
    }

    fun setLangCode(context: Context, value: String?) {
        val mPrefs = getSP(context)
        mPrefs.edit().putString("vanilla_app_lang_pref", value).apply()
    }

    private fun getSP(context: Context): SharedPreferences {
        val mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return mPrefs
    }

    fun increaseCount(context: Context) {
        val currentCount = getCount(context)
        setCount(context, currentCount + 1)
    }

    private fun getIntPref(context: Context, key: String?, defaultValue: Int): Int {
        val mPrefs = getSP(context)
        return mPrefs.getInt(key, defaultValue)
    }

    private fun setIntPref(context: Context, key: String?, value: Int) {
        val mPrefs = getSP(context)
        mPrefs.edit().putInt(key, value).apply()
    }

    fun getCount(context: Context): Int {
        return getIntPref(context, "count", 0)
    }

    fun setCount(context: Context, count: Int) {
        setIntPref(context, "count", count)
    }

    companion object {
    }
}
