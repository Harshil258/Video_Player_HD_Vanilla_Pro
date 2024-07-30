package com.vanillavideoplayer.hd.videoplayer.pro.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.Logger.TAG
import com.vanillavideoplayer.hd.videoplayer.pro.MainActivity

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "Notification Click") {
            Logger.d(TAG, "onReceive: Notification Click")
            if (runningActivity != null) {
                Logger.d(TAG, "onReceive: Activity Not Null")
                val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
                notificationManager?.cancel(2352343)
            } else {
                Logger.d(TAG, "onReceive: Activity Null")
                val activityIntent = Intent(context, MainActivity::class.java)
                activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(activityIntent)
            }
            return
        }
    }
}