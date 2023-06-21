package com.example.chitchat.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chitchat.MainActivity
import android.Manifest


class MyNotifications(
    private val context: Context,
    private val title: String,
    private val body: String
) {
    val channelId = "notify100"
    val channelName = "mynotification"

    val notificationManager =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: NotificationCompat.Builder

    fun showNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder = NotificationCompat.Builder(context, channelId)
        notificationBuilder.setSmallIcon(androidx.core.R.drawable.notification_bg)
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(body)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentIntent(pendingIntent)


        // checking to see if we have permission
        with(NotificationManagerCompat.from(context)) {
            if(ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                Log.d("no permission", "need permissions")
            }
            notify(100, notificationBuilder.build())
        }


        notificationManager.notify(100, notificationBuilder.build())


    }
}