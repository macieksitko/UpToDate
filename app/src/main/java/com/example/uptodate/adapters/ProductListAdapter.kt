package com.example.uptodate.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.uptodate.R
import com.example.uptodate.models.Product


private var selectedPosition = -1

class ProductListAdapter(
    private val listener: OnProductListener
)  : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {


    private var products = emptyList<Product>() // Cached copy of products

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
                R.id.prodImg ->{
                    listener.onImageClick(position)
                    imageViewProduct.setImageResource(R.drawable.prod_img)
                    itemBackground.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    selectedPosition = -1
                }
                R.id.itemBackground ->{
                    listener.onProductClick(position)
                }
            }
        }
        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            selectedPosition = position

            if (position == selectedPosition){
                imageViewProduct.setImageResource(R.drawable.baseline_delete_black_18dp)
                itemBackground.setBackgroundColor(Color.parseColor("#ff6090"))
            }else{
                imageViewProduct.setImageResource(R.drawable.prod_img)
                itemBackground.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            setOnLongClickAnimation(itemBackground,imageViewProduct)
            listener.onProductLongClick(position)
            return true
        }

    }

    private fun setOnLongClickAnimation(itemBackground: RelativeLayout, imageViewProduct: ImageView) {
        animateProductIcon(imageViewProduct)
        animateItemBackground(itemBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = products[position]
        holder.textViewProductName.text = current.product_name
        holder.textViewDateOfExpiring.text = current.date_of_expiry

        if (!current.isActive){
            holder.itemBackground.alpha = 0.5f
        }else holder.itemBackground.alpha = 1.0f
    }

    private fun setFadeAnimation(viewToAnimate: View) {
        TODO()
    }

    internal fun setProducts(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun getItemCount() = products.size

    fun getProductAtPosition(position: Int): Product {
        return products[position]
    }

    private fun animateProductIcon(imageViewProduct:View){
        YoYo.with(Techniques.RubberBand)
            .duration(800)
            .repeat(1)
            .playOn(imageViewProduct)
    }

    private fun animateItemBackground(itemBackground:View){
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