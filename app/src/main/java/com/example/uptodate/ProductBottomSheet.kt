package com.example.uptodate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.product_details_bottom_sheet.*

class ProductBottomSheet() : BottomSheetDialogFragment() {

    private var fragmentView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        val productName = arguments?.getString("productName")
        val dateOfAdding =  arguments?.getString("dateOfAdding")
        val dateOfExpiring =  arguments?.getString("dateOfExpiring")
        productNameTextView.text = productName
        dateOfAddingTextView.text = dateOfAdding
        dateOfExpiringTextView.text = dateOfExpiring
    }
}