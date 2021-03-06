package com.example.uptodate.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uptodate.R
import com.example.uptodate.adapters.ProductListAdapter
import com.example.uptodate.adapters.selectedPosition
import com.example.uptodate.fragments.ProductBottomSheet
import com.example.uptodate.models.ProductViewModel
import com.example.uptodate.receivers.DateOfExpiryBroadcastReceiver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_ondelete.view.*

class MainActivity : AppCompatActivity(),
    ProductListAdapter.OnProductListener
{

    private val productViewModel: ProductViewModel by viewModels()
    private val adapter = ProductListAdapter(this)
    private var onDeletePosition = -1
    private var isProductClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainActivityToolbar)
        setupProductList()
        setupListeners()
        createNotificationChannel()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.searchMenu)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search your product"
        searchView.isIconifiedByDefault = false

        searchView.setOnQueryTextListener(object :  OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                adapter.notifyDataSetChanged()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                adapter.notifyDataSetChanged()
                return false
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                setupProductList()
                return true
            }
        })
        return true
    }
    private fun setupProductList(){

        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        productViewModel.allProducts.observe(this, Observer { products ->
            products?.let { adapter.setProducts(it) }
        })
    }
    private fun setupListeners(){
        addButton.setOnClickListener{
            val intent = Intent(this, NewProductActivity::class.java)
            startActivityForResult(intent, 2)
        }

    }
    override fun onProductClick(position: Int) {
        if (isProductClicked){
            cancelDeleteModeOnClick()
        }else{
            showBottomSheetOnClick(position)
        }
    }
    private fun showBottomSheetOnClick(position: Int){
        val bottomSheetFragment =
            ProductBottomSheet()
        val product = adapter.getProductAtPosition(position)
        val productName = product.product_name
        val dateOfAdding = product.date_of_adding
        val dateOfExpiring = product.date_of_expiry
        val isActive = product.isActive
        val bundle = Bundle()
        Log.d("TAG", "product id ${product.id}")
        bundle.putString("productName", productName)
        bundle.putString("dateOfAdding", dateOfAdding)
        bundle.putString("dateOfExpiring", dateOfExpiring)
        bundle.putBoolean("activityState", isActive)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }
    private fun cancelDeleteModeOnClick(){
        selectedPosition = -1
        isProductClicked = false
        adapter.notifyDataSetChanged()
    }
    override fun onProductLongClick(position: Int) {
        isProductClicked = true
        onDeletePosition = position
        adapter.notifyDataSetChanged()
    }

    override fun onImageClick(position: Int) {
        if (isProductClicked){
            showCustomDialog()
        }
    }
    private fun deleteSelectedProduct(){

        val product= adapter.getProductAtPosition(onDeletePosition)
        productViewModel.deleteProduct(product)
        cancelNotifications(product.id)
        isProductClicked = false
    }
    private fun cancelNotifications(prodId: Long){
        val dayBeforeId = (prodId.toString()+0).toInt()
        val exactDayId = (prodId.toString()+1).toInt()
        val alarms =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dayBeforeNotificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java)
        val exactDayNotificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java)
        val dayBeforeWarning: PendingIntent =
            PendingIntent.getBroadcast(this,dayBeforeId , dayBeforeNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exactDayWarning: PendingIntent =
            PendingIntent.getBroadcast(this, exactDayId, exactDayNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarms.cancel(dayBeforeWarning)
        alarms.cancel(exactDayWarning)
    }
    private fun showCustomDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ondelete, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val mAlertDialog = mBuilder.show()

        dialogView.btnNegative.setOnClickListener{
            mAlertDialog.dismiss()
        }
        dialogView.btnPositive.setOnClickListener{
            deleteSelectedProduct()
            mAlertDialog.dismiss()
        }

    }

    private fun createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channelName)
            val descriptionText = getString(R.string.channelDescription)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channelId), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
