package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerOrderDetailBean
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseListCompeleteActivity

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.load


import org.jetbrains.anko.find
import java.util.*

class BuyerOrderList_CompleteAdapter : RecyclerView.Adapter<BuyerOrderList_CompleteAdapter.BuyerOrderCompleteLinearHolder>(){

    private var selected = -1
    private var cancel_inner:Boolean = false
    private var mData: ArrayList<BuyerOrderDetailBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<BuyerOrderDetailBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyerOrderCompleteLinearHolder {
        val v = parent.context.inflate(R.layout.item_order_complete,parent,false)

        return BuyerOrderCompleteLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(cancel: Boolean){
        cancel_inner =cancel
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        this.mData.removeAt(position)
        notifyDataSetChanged()
    }

    var intentClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: BuyerOrderCompleteLinearHolder, position: Int) {
        val viewHolder: BuyerOrderCompleteLinearHolder = holder
        val item = mData.get(position)
        viewHolder.product.load(item.product_pic)
        viewHolder.store.load(item.shop_icon)
        viewHolder.name.text = item.shop_title
        viewHolder.quantity.text = item.count.toString()
        viewHolder.price.text = "HKD$ "+item.sub_total.toString()

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, BuyerPurchaseListCompeleteActivity::class.java)
            val bundle = Bundle()
            bundle.putString("order_id", item.order_id)
            intent.putExtra("bundle", bundle)
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,address_id:String)
    }

    inner class BuyerOrderCompleteLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val product = itemView.find<ImageView>(R.id.iv_product)
        val store = itemView.find<ImageView>(R.id.iv_store)
        var name = itemView.find<TextView>(R.id.tv_store_name)
        var quantity = itemView.find<TextView>(R.id.tv_product_quantity)
        val price = itemView.find<TextView>(R.id.tv_product_price)
        val contact = itemView.find<ImageView>(R.id.iv_contact)
        val comment = itemView.find<ImageView>(R.id.iv_comment)


    }



}