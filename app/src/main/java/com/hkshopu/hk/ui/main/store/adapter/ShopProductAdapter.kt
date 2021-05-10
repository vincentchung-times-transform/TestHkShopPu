package com.hkshopu.hk.ui.main.store.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R

import com.hkshopu.hk.data.bean.ShopProductBean
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.widget.view.click


import org.jetbrains.anko.find
import java.util.*

class ShopProductAdapter : RecyclerView.Adapter<ShopProductAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<ShopProductBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null

    fun setData(list : ArrayList<ShopProductBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_products,parent,false)

        return ShopInfoLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.layout_newproduct)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val price = itemView.find<TextView>(R.id.tv_currentnumber)
        val sold = itemView.find<TextView>(R.id.tv_sold)
        val amount = itemView.find<TextView>(R.id.tv_amount)
        val heart = itemView.find<TextView>(R.id.tv_heart)
        val eye = itemView.find<TextView>(R.id.tv_eye)
        fun bindShop(bean : ShopProductBean){
            container.click {
                itemClick?.invoke(bean.id)
            }
            image.loadNovelCover(bean.pic_path)
            title.text = bean.product_title
            price.text = bean.product_price.toString()
            sold.text = "已賣出"+bean.sold_quantity
            amount.text = "數量"+bean.quantity
            heart.text = "讚"+bean.like
            eye.text = "檢視"+bean.seen

        }
    }



}