package com.example.uptodate.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.example.uptodate.R
import com.example.uptodate.activities.MainActivity
import com.example.uptodate.db.ProductDatabase
import com.example.uptodate.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DateOfExpiryBroadcastReceiver: BroadcastReceiver() {

    private val channelId = "com.example.uptodate"
    private val textTitle = "Up to date warning"
    var textContent = ""
    private var notificationId = 1
    override fun onReceive(context: Context, intent: Intent) {

        val productId = intent.getLongExtra("submittedProductId",-1)
        val productName = intent.getStringExtra("submittedProductName")
        val notificationMode = intent.getIntExtra("id",-1)
        notificationId++
        Log.d("TAG","odebrano loga czas w pierwszym receiverze....$notificationMode")
        when (notificationMode){
            1 -> textContent = "Your product $productName is up-to-date in 1 day. Hurry up"
            2 -> textContent = "Your product $productName is up-to-date"
        }
        showNotification(context,productName)
        //if (notificationMode==2) deleteProduct(productId,context)
        if (notificationMode==2) deactivation(productId,context)
    }

    private fun deactivation(productId: Long,context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            ProductDatabase.getDatabase(context).productDao().setActive(productId,false)
        }
    }

    private fun showNotification(context: Context,productName: String){
        val startMyActivity = Intent(context, MainActivity::class.java)
        val myIntent = PendingIntent.getActivity(context, 0, startMyActivity, 0)

        Log.d("TAG","Pokazano czas powiadomienie o id...... $notificationId")

        val builder = NotificationCompat.Builder(context, channelId)
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

    private fun deleteProduct(productId: Long,context: Context){
        GlobalScope.launch(Dispatchers.IO) {
            val product = ProductDatabase.getDatabase(context).productDao().getProduct(productId)
            ProductDatabase.getDatabase(context).productDao().deleteProduct(product)
        }
    }

}