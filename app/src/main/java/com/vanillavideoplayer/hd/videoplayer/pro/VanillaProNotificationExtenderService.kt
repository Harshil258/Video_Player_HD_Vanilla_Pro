package com.vanillavideoplayer.hd.videoplayer.pro

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.harshil258.adplacer.utils.Logger
import com.onesignal.notifications.IDisplayableMutableNotification
import com.onesignal.notifications.INotificationReceivedEvent
import com.onesignal.notifications.INotificationServiceExtension
import com.vanillavideoplayer.hd.videoplayer.pro.VanillaPlayerProApp.Companion.context
import com.vanillavideoplayer.videoplayer.feature.player.VanillaPlayerActivityPro
import org.json.JSONObject

class VanillaProNotificationExtenderService() : INotificationServiceExtension {
    override fun onNotificationReceived(event: INotificationReceivedEvent) {
        val notification: IDisplayableMutableNotification = event.notification
        context?.let { context ->
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                val additionalData: JSONObject? = notification.additionalData
                Logger.d(Logger.TAG, "initOneSignal: additionalData $additionalData")
                Logger.d(Logger.TAG, "initOneSignal: optString ${additionalData?.optString("actionType")}")
                if (additionalData != null && additionalData.optString("actionType") == "MOVIE_REMINDER") {
                    Logger.d(Logger.TAG, "initOneSignal: optString inside movie reminder")

                    event.preventDefault()

                    val remindedMovie = GlobalPreferences().getRemindedVideos(context)
                    val movie = GlobalPreferences().getLastPlayedVideos(context).firstOrNull { remindedMovie.contains(it).not() || true }
                    Logger.d(Logger.TAG, "initOneSignal:2")

                    if (movie == null) {
                        return
                    }
                    Logger.d(Logger.TAG, "initOneSignal:3")

                    remindedMovie.add(movie)
                    GlobalPreferences().setRemindedVideos(context, remindedMovie)

                    var title = notification.title ?: "Resume Your Epic Journey!"
                    var body = notification.body ?: "You were so close! Continue watching and see what happens next."

                    val movieUri = movie.uriString

                    title = title.replace("[MOVIE_NAME]", movie.displayName)
                    body = body.replace("[MOVIE_NAME]", movie.displayName)

                    Logger.d(Logger.TAG, "onReceive: title : $title")
                    Logger.d(Logger.TAG, "onReceive: body : $body")
                    Logger.d(Logger.TAG, "onReceive: movieUri : $movieUri")

                    if (movieUri.isNotEmpty()) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            movieUri.toUri(),
                            context,
                            VanillaPlayerActivityPro::class.java
                        )
                        val pendingIntent = PendingIntent.getActivity(context, 1045, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                        Glide.with(context)
                            .asBitmap()
                            .load(movieUri)
                            .listener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                                    showCustomNotification(context, title, body, null, pendingIntent)
                                    return false
                                }

                                override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                    showCustomNotification(context, title, body, resource, pendingIntent)
                                    return false
                                }
                            }
                            ).submit()
                    }
                } else {
                    event.preventDefault()
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context, CHANNEL_ID: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelDescription = "Vanilla Movie Reminder Notification Channel : To remind about the uncompleted movies!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance)
            channel.description = channelDescription
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showCustomNotification(context: Context, title: String, body: String, bitmap: Bitmap?, pendingIntent: PendingIntent) {
        val CHANNEL_ID = "vanilla_movie_reminder_channel"
        val notificationId = 1154

        createNotificationChannel(context, CHANNEL_ID)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(com.vanillavideoplayer.videoplayer.core.ui.R.drawable.ic_notification)
            if (bitmap != null) {
                setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setBigContentTitle(title)
                        .setSummaryText(body)
                )
            } else {
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                )
            }
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        }
    }
}
