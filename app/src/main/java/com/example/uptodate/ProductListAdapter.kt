package com.example.uptodate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var products = emptyList<Product>() // Cached copy of words

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewProductName: TextView = itemView.findViewById(R.id.prod_name)
        val textViewDateOfExpiring: TextView = itemView.findViewById(R.id.date_of_expiring)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = products[position]
        holder.textViewProductName.text = current.product_name
        holder.textViewDateOfExpiring.text = current.date_of_expiry
    }

    internal fun setProducts(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size
}