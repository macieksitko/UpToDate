package com.example.uptodate.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.uptodate.db.ProductDatabase
import com.example.uptodate.services.DateOfExpiryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DateOfExpiryBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intent1 = Intent(context, DateOfExpiryService::class.java)
        if (intent.action !=null){
            val productId = intent.getLongExtra("submittedProductId",-1)
            val productName = intent.getStringExtra("submittedProductName")
            val notificationMode = intent.getIntExtra("id",-1)

            intent1.putExtra("submittedProductId",productId)
            intent1.putExtra("submittedProductName",productName)
            intent1.putExtra("id",notificationMode)

        }else Log.d("TAG","No data received in DateOfExpiryBroadcastReceiver")
        context.startService(intent1)
    }


}