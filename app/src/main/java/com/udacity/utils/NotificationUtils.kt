package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

//Notification params
private const val NOTIFICATION_ID = 19
private const val REQUEST_CODE = 19
private const val FLAGS = 19


//Send notifications
fun NotificationManager.sendNotification(
    messageBody: String, applicationContext: Context, status: Boolean
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    when (status) {
        true -> contentIntent.putExtra(applicationContext.getString(R.string.success), status)
        false -> contentIntent.putExtra(applicationContext.getString(R.string.failed), status)
    }
    contentIntent.putExtra(applicationContext.getString(R.string.key), messageBody)


    //pending intent to show notification
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT// update already shown notification
    )

    //Build notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.app_notification_channel_id)
    )
        //notification icon
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        //notification title
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        //notification text
        .setContentText(messageBody)

        //content intent for tap action
        .setContentIntent(pendingIntent)
        //notification closes on tap
        .setAutoCancel(true)

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    //deliver notification
    notify(NOTIFICATION_ID, builder.build())


}

//Cancel notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}