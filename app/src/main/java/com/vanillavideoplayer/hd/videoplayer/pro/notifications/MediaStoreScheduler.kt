package com.vanillavideoplayer.hd.videoplayer.pro.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.harshil258.adplacer.utils.Logger
import java.util.Calendar



class MediaStoreScheduler(private val context: Context) {

    private var alarmIntent: PendingIntent? = null
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMediaStoreObserver() {
        // Cancel any existing alarms to ensure only one is active
        cancelAlarm()

        val intent = Intent(context, MediaStoreBroadcastReceiverPro::class.java)
        alarmIntent = PendingIntent.getBroadcast(
            context, 0, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        // Set the alarm to start at approximately 24 hours from now
        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.MINUTE, 1)
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent!!
        )

        Logger.d("MediaStoreObserver", "Scheduled MediaStoreObserver")
    }

    private fun cancelAlarm() {
        alarmIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            alarmIntent = null
            Logger.d("MediaStoreObserver", "Cancelled previous alarm")
        }
    }
}