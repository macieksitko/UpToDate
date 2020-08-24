package com.example.uptodate.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.uptodate.R
import com.example.uptodate.activities.MainActivity
import com.example.uptodate.db.ProductDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DateOfExpiryService:Service() {

    var textContent = ""
    private var notificationId = 1

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == "com.example.uptodate.receivers.DateOfExpiryBroadcastReceiver" ){
            val productId = intent.getLongExtra("submittedProductId",-1)
            val productName = intent.getStringExtra("submittedProductName")
            val notificationMode = intent.getIntExtra("id",-1)
            notificationId++
            Log.d("TAG","serwis sie rozpoaczal, przyjal id o numerze...$notificationMode")
            when (notificationMode){
                1 -> textContent = "Your product $productName is up-to-date in 1 day. Hurry up"
                2 -> textContent = "Your product $productName is up-to-date"
            }
            if (notificationMode==2) deactivation(productId)
        }else Log.d("TAG","No data received in DateOfExpiryService")

        showNotification()
        return START_STICKY
    }

    private fun deactivation(productId: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            ProductDatabase.getDatabase(this@DateOfExpiryService).productDao().setActive(productId,false)
        }
    }

    private fun showNotification(){
        val startMyActivity = Intent(this, MainActivity::class.java)
        val myIntent = PendingIntent.getActivity(this, 0, startMyActivity, 0)

        Log.d("TAG","Pokazano czas powiadomienie o id...... $notificationId")

        val notification = Notification.Builder(this, getString(R.string.channelId))
            .setSmallIcon(R.drawable.leaf)
            .setContentTitle(getString(R.string.notificationTextTitle))
            .setContentText(textContent)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setContentIntent(myIntent)
            .build()
        startForeground(notificationId,notification)
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}