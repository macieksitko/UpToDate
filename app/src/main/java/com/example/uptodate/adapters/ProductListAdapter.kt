package com.example.uptodate.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.uptodate.R
import com.example.uptodate.models.Product
import java.util.*


var selectedPosition = -1

class ProductListAdapter(
    private val listener: OnProductListener
)  : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>(),
    Filterable
{


    private var products :MutableList<Product> = arrayListOf() // Cached copy of products
    private var productsCopy :MutableList<Product> = arrayListOf()
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener{
        val textViewProductName: TextView = itemView.findViewById(R.id.prod_name)
        val textViewDateOfExpiring: TextView = itemView.findViewById(R.id.date_of_expiring)
        val imageViewProduct: ImageView = itemView.findViewById(R.id.prodImg)
        val itemBackground: RelativeLayout = itemView.findViewById(R.id.itemBackground)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            imageViewProduct.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            when(v?.id){
                R.id.prodImg -> {
                    listener.onImageClick(position)
                    selectedPosition = -1
                }
                R.id.itemBackground -> {
                    listener.onProductClick(position)
                }
            }
        }
        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            selectedPosition = position
            listener.onProductLongClick(position)
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ProductViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = products[position]
        holder.textViewProductName.text = current.getProductName()
        holder.textViewDateOfExpiring.text = current.getDateOfExpiry()
        if (position == selectedPosition){
            holder.imageViewProduct.setImageResource(R.drawable.baseline_delete_black_18dp)
            holder.itemBackground.setBackgroundColor(Color.parseColor("#ff6090"))
            setOnLongClickAnimation(holder.itemBackground, holder.imageViewProduct)
        }else{
            holder.imageViewProduct.setImageResource(R.drawable.prod_img)
            holder.itemBackground.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        if (!current.isActive){
            holder.itemBackground.alpha = 0.5f
        }else holder.itemBackground.alpha = 1.0f
    }

    private fun setOnLongClickAnimation(itemBackground: RelativeLayout, imageViewProduct: ImageView) {
        animateProductIcon(imageViewProduct)
        animateItemBackground(itemBackground)
    }

    internal fun setProducts(products: MutableList<Product>) {
        this.products = products
        productsCopy = products
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList:MutableList<Product> = arrayListOf()
                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(productsCopy)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (item in productsCopy) {
                        if (item.getProductName().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                products = results.values as MutableList<Product>
                notifyDataSetChanged()
            }
        }
    }
    fun getProductAtPosition(position: Int): Product {
        return products[position]
    }

    private fun animateProductIcon(imageViewProduct: View){
        YoYo.with(Techniques.RubberBand)
            .duration(800)
            .repeat(1)
            .playOn(imageViewProduct)
    }

    private fun animateItemBackground(itemBackground: View){
        YoYo.with(Techniques.Shake)
            .duration(500)
            .playOn(itemBackground)
    }

    interface OnProductListener{
        fun onProductClick(position: Int)
        fun onProductLongClick(position: Int)
        fun onImageClick(position: Int)
    }
}