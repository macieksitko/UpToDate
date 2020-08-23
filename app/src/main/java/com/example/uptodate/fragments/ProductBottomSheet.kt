package com.example.uptodate.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uptodate.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.product_details_bottom_sheet.*

class ProductBottomSheet() : BottomSheetDialogFragment() {

    private var fragmentView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.product_details_bottom_sheet, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView(){
        val productName = arguments?.getString("productName")
        val dateOfAdding =  arguments?.getString("dateOfAdding")
        val dateOfExpiring =  arguments?.getString("dateOfExpiring")
        val isActive =  arguments?.getBoolean("activityState")
        if (isActive!!){
            activityStateTextView.text = getString(R.string.activeState)
            activityStateTextView.setTextColor(Color.parseColor("#8bc34a"))
        } else {
            activityStateTextView.text = getString(R.string.inactiveState)
            activityStateTextView.setTextColor(Color.parseColor("#ff0033"))
        }
        productNameTextView.text = productName
        dateOfAddingTextView.text = dateOfAdding
        dateOfExpiringTextView.text = dateOfExpiring
    }
}