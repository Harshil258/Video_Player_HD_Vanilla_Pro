package com.vanillavideoplayer.hd.videoplayer.pro.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Looper
import android.provider.MediaStore
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.Logger.TAG
import com.vanillavideoplayer.hd.videoplayer.pro.GlobalPreferences
import com.vanillavideoplayer.hd.videoplayer.pro.MainActivity
import com.vanillavideoplayer.videoplayer.R
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class MediaStoreBroadcastReceiverPro : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d("MediaStoreObserver", "MediaStoreBroadcastReceiver onReceive")

        val currentTime = System.currentTimeMillis()
        val time24HoursAgo = currentTime - TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Video.Media.DATE_ADDED} >= ?"
        val selectionArgs = arrayOf((time24HoursAgo / 1000).toString())
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        val bitmaps = mutableListOf<Bitmap>()
        val videoNames = mutableListOf<String>()

        cursor?.use { cursor1 ->
            val idIndex = cursor1.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameIndex = cursor1.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateAddedIndex = cursor1.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            Logger.d("MediaStoreObserver", "Latest videos added in last 24 hours:")
            var count = 0
            var cursorCount = cursor1.count
            while (cursor1.moveToNext() && count < 4) {
                val id = cursor1.getLong(idIndex)
                val name = cursor1.getString(nameIndex)
                val list = GlobalPreferences().getNewVideosReminderList(context)
                Logger.d(TAG, "onReceive: list : $list")
                if (!list.contains(id)) {
                    Logger.d(TAG, "onReceive: Passed The Condition!")
                    Logger.d(TAG, "onResourceReady: id : $id")
                    list.add(id)
                    GlobalPreferences().setNewVideosReminderList(context, list)

                    Glide.with(context)
                        .asBitmap()
                        .load(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id))
                        .override(300, 300)
                        .centerCrop()
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                                if (resource.width > 0 && resource.height > 0) {
                                    bitmaps.add(resource)
                                    videoNames.add(name)

                                    if (bitmaps.size == min(4, cursorCount)) {
                                        Logger.d(TAG, "onReceive: Show Notification!")
                                        createAndSendNotification(context, bitmaps, videoNames)
                                    }
                                } else {
                                    Logger.e(TAG, "Invalid bitmap dimensions: width=${resource.width}, height=${resource.height}")
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                super.onLoadFailed(errorDrawable)
                                Logger.e(TAG, "onLoadFailed: $errorDrawable")
                            }

                        })

                    count++
                }
            }
        }
    }

    private fun createAndSendNotification(context: Context, bitmaps: List<Bitmap>, videoNames: List<String>) {
        val bounds = Rect(0, 0, 1000, 1000) // Define the bounds as needed
        val drawable = createMultiDrawableOrBitmap(ArrayList(bitmaps), bounds)

        // Delayed post to ensure all bitmaps are loaded before sending notification
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val multiDrawable = drawable

            // Prepare the notification title
            val notificationTitle = prepareNotificationTitle(context, videoNames)

            // Send notification with combined drawable and titles
            sendNotificationWithDrawable(context, multiDrawable, notificationTitle)
        }, 2000)
    }

    private fun prepareNotificationTitle(context: Context, videoNames: List<String>): String {
        // Prepare the notification title based on the number of videos
        if (videoNames.isEmpty()) {
            return "No videos found"
        } else {
            val numVideos = videoNames.size
            val builder = StringBuilder()
            builder.append("Video ")
            if (numVideos <= 3) {
                // List all video names
                for (i in 0 until numVideos) {
                    builder.append(videoNames[i])
                    if (i < numVideos - 1) {
                        builder.append(", ")
                    }
                }
            } else {
                // List first three video names and mention the count of remaining videos
                for (i in 0 until 3) {
                    builder.append(videoNames[i])
                    if (i < 2) {
                        builder.append(", ")
                    }
                }
                builder.append(" and ${numVideos - 3} more...")
            }
            return builder.toString()
        }
    }

    private fun sendNotificationWithDrawable(context: Context, multiDrawable: Bitmap, title: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(context)

        val pendingIntent = if (runningActivity != null) {
            val notificationIntent = Intent(context, NotificationActionReceiver::class.java)
            notificationIntent.setAction("Notification Click")
            PendingIntent.getBroadcast(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val notificationIntent = Intent(context, MainActivity::class.java)
            PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        val customHeadsUpView = RemoteViews(context.packageName, R.layout.heads_up)
        customHeadsUpView.setTextViewText(R.id.notification_title, title)
//        customHeadsUpView.setImageViewBitmap(R.id.notification_image, multiDrawable)

        val notificationBuilder = NotificationCompat.Builder(context, "channel_id234")
            .setSmallIcon(R.mipmap.ic_launcher) // Set your small icon here
            .setContentTitle("New videos added from 24 hour...") // Main notification title
            .setContentText(title) // Main notification text
            .setLargeIcon(multiDrawable) // Large icon displayed in notification
//            .setCustomHeadsUpContentView(customHeadsUpView) // Set custom heads-up view
//            .setCustomContentView(customHeadsUpView)
//            .setCustomBigContentView(customHeadsUpView)
            .setAutoCancel(true)
//            .setOngoing(true)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(multiDrawable)) // Big picture style for expanded view
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority for heads-up notification
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Default notification options (sound, vibration, etc.)
//            .setFullScreenIntent(pendingIntent,false)
            .setContentIntent(pendingIntent) // Set content intent (required for backwards compatibility)

        notificationManager.notify(2352343, notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "channel_id234"
            val channelName = "Vanilla Playerr"
            val channelDescription = "Vanilla Video player notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scaleCenterCrop(source: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val scaleX = newWidth.toFloat() / source.width.toFloat()
        val scaleY = newHeight.toFloat() / source.height.toFloat()
        val scale = max(scaleX, scaleY)

        val scaledWidth = (source.width * scale).toInt()
        val scaledHeight = (source.height * scale).toInt()

        return Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)
    }

    fun createMultiDrawableOrBitmap(bitmaps: ArrayList<Bitmap>, bounds: Rect): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val items = ArrayList<PhotoItem>()

        // Function to create PhotoItem with position and size depending on count of images
        fun init() {
            items.clear()
            when (bitmaps.size) {
                1 -> {
                    val bitmap = scaleCenterCrop(bitmaps[0], bounds.width(), bounds.height())
                    items.add(PhotoItem(bitmap, Rect(0, 0, bounds.width(), bounds.height())))
                }

                2 -> {
                    val bitmap1 = scaleCenterCrop(bitmaps[0], bounds.width(), bounds.height() / 2)
                    val bitmap2 = scaleCenterCrop(bitmaps[1], bounds.width(), bounds.height() / 2)
                    items.add(PhotoItem(bitmap1, Rect(0, 0, bounds.width() / 2, bounds.height())))
                    items.add(PhotoItem(bitmap2, Rect(bounds.width() / 2, 0, bounds.width(), bounds.height())))
                }

                3 -> {
                    val bitmap1 = scaleCenterCrop(bitmaps[0], bounds.width(), bounds.height() / 2)
                    val bitmap2 = scaleCenterCrop(bitmaps[1], bounds.width() / 2, bounds.height() / 2)
                    val bitmap3 = scaleCenterCrop(bitmaps[2], bounds.width() / 2, bounds.height() / 2)
                    items.add(PhotoItem(bitmap1, Rect(0, 0, bounds.width() / 2, bounds.height())))
                    items.add(PhotoItem(bitmap2, Rect(bounds.width() / 2, 0, bounds.width(), bounds.height() / 2)))
                    items.add(PhotoItem(bitmap3, Rect(bounds.width() / 2, bounds.height() / 2, bounds.width(), bounds.height())))
                }

                4 -> {
                    val bitmap1 = scaleCenterCrop(bitmaps[0], bounds.width() / 2, bounds.height() / 2)
                    val bitmap2 = scaleCenterCrop(bitmaps[1], bounds.width() / 2, bounds.height() / 2)
                    val bitmap3 = scaleCenterCrop(bitmaps[2], bounds.width() / 2, bounds.height() / 2)
                    val bitmap4 = scaleCenterCrop(bitmaps[3], bounds.width() / 2, bounds.height() / 2)



                    items.add(PhotoItem(bitmap1, Rect(0, 0, bounds.width() / 2, bounds.height() / 2)))
                    items.add(PhotoItem(bitmap2, Rect(0, bounds.height() / 2, bounds.width() / 2, bounds.height())))
                    items.add(PhotoItem(bitmap3, Rect(bounds.width() / 2, 0, bounds.width(), bounds.height() / 2)))
                    items.add(PhotoItem(bitmap4, Rect(bounds.width() / 2, bounds.height() / 2, bounds.width(), bounds.height())))
                }
            }
        }

        // Function to scale and center crop image
        fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
            return ThumbnailUtils.extractThumbnail(source, newWidth, newHeight)
        }

        // Initialize items based on bitmaps when the function is called
        init()

        // Draw method to draw each PhotoItem onto the canvas
        fun draw(canvas: Canvas) {
            Logger.d("MediaStoreObserver", "draw  ")
            items.forEach {
                canvas.drawBitmap(it.bitmap, null, it.position, paint)
                Logger.d("MediaStoreObserver", "draw   ${it.bitmap.width}   position   ${it.position}")
            }
        }

        // Create a Bitmap to return
        val bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        Logger.d("MediaStoreObserver", "1231312   draw   ${bitmap.width}  ")

        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    /**
     * Data class for storing bitmap and position
     */
    data class PhotoItem(val bitmap: Bitmap, val position: Rect)

}


