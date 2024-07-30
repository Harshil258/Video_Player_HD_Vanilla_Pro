package com.vanillavideoplayer.videoplayer.settings

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanillavideoplayer.videoplayer.core.model.VideoData

class GlobalPrefs {
    private val PREF_NAME = "GlobalAdPrefs"
    private val APP_DETAILS = "app_details"
    private val AD_DETAILS = "ad_details"
    private val ALL_DATA = "all_data"

    var HOW_TO_USE_DONE: String = "how_to_use_done"

    fun getLangCode(context: Context, defaultValue: String?): String {
        val mPrefs = getSP(context)
        return mPrefs.getString("vanilla_app_lang_pref", "")!!
    }

    fun setLangCode(context: Context, value: String?) {
        val mPrefs = getSP(context)
        mPrefs.edit().putString("vanilla_app_lang_pref", value).apply()
    }

    fun getIsAppPurchased(context: Context, defaultValue: Boolean = false): Boolean {
        // TODO : remove this
        val mPrefs = getSP(context)
        return mPrefs.getBoolean("IsAppPurchased", defaultValue)
//        return false
    }

    fun setIsAppPurchased(context: Context, value: Boolean) {
        val mPrefs = getSP(context)
        mPrefs.edit().putBoolean("IsAppPurchased", value).apply()
    }

    fun getIsLanguageFirstTime(context: Context): Boolean {
        val mPrefs = getSP(context)
        return mPrefs.getBoolean("IsLanguageFirstTime", true)
    }

    fun setIsLanguageFirstTime(context: Context, value: Boolean) {
        val mPrefs = getSP(context)
        mPrefs.edit().putBoolean("IsLanguageFirstTime", value).apply()
    }



    private fun getSP(context: Context): SharedPreferences {
        val mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return mPrefs
    }


    fun increaseCount(context: Context) {
        val currentCount = getCount(context) ?: 0
        setCount(context, currentCount + 1)
    }

    fun getIntPref(context: Context, key: String?, defaultValue: Int): Int {
        val mPrefs = getSP(context)
        return mPrefs.getInt(key, defaultValue)
    }

    fun getBooleanPref(context: Context, key: String?, defaultValue: Boolean): Boolean {
        val mPrefs = getSP(context)
        return mPrefs.getBoolean(key, defaultValue)
    }

    fun setIntPref(context: Context, key: String?, value: Int) {
        val mPrefs = getSP(context)
        mPrefs.edit().putInt(key, value).apply()
    }

    fun setBooleanPref(context: Context, key: String?, value: Boolean) {
        val mPrefs = getSP(context)
        mPrefs.edit().putBoolean(key, value).apply()
    }

    fun getCount(context: Context): Int {
        return getIntPref(context, "count", 0)
    }

    fun setCount(context: Context, count: Int) {
        setIntPref(context, "count", count)
    }


    fun setListener(
        context: Context,
        sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getSP(context).registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    fun removeListener(
        context: Context,
        sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getSP(context).unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }
    fun setLastPlayedVideos(context: Context, videoList: List<VideoData>) {
        val gson = Gson()
        val jsonString = gson.toJson(videoList)
        getSP(context).edit().putString("last_played_movies", jsonString).apply()
    }

    fun getLastPlayedVideos(context: Context): List<VideoData> {
        val gson = Gson()
        val jsonString = getSP(context).getString("last_played_movies", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<VideoData>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    fun setRemindedVideos(context: Context, videoList: ArrayList<VideoData>) {
        val gson = Gson()
        val jsonString = gson.toJson(videoList)
        getSP(context).edit().putString("reminded_played_movies", jsonString).apply()
    }

    fun getRemindedVideos(context: Context): ArrayList<VideoData> {
        val gson = Gson()
        val jsonString = getSP(context).getString("reminded_played_movies", null)
        return if (jsonString != null) {
            val type = object : TypeToken<ArrayList<VideoData>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            arrayListOf()
        }
    }

    companion object {
        fun LogCustomEvent(
            context: Context, eventName: String, parameterName: String, parameterValue: String
        ) {
//            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
//            val params = Bundle().apply {
//                putString(parameterName, parameterValue)
//            }
//            firebaseAnalytics.logEvent("$eventName$parameterName$parameterValue", params)
        }



    }

}
