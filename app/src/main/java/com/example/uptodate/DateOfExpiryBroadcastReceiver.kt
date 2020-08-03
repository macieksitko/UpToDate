package com.example.uptodate

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DateOfExpiryBroadcastReceiver: BroadcastReceiver() {

    val notificationId = 200
    val channel_id = "com.example.uptodate"
    val textTitle = "Up to date warning"
    val textContent = "Your product date has expired"
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Expiration is comingg",
            Toast.LENGTH_LONG).show();
        showNotification(context)
    }

    private fun showNotification(context: Context){
        val startMyActivity = Intent(context, MainActivity::class.java)
        val myIntent = PendingIntent.getActivity(context, 0, startMyActivity, 0)

        var builder = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.drawable.leaf)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(myIntent)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }
}