package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.OrderProductBean

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.load


import org.jetbrains.anko.find
import java.util.*

class BuyerPendingDeliver_OrderDatailAdapter : RecyclerView.Adapter<BuyerPendingDeliver_OrderDatailAdapter.BuyerPendingDeliver_OrderDatailLinearHolder>(){

    private var selected = -1
    private var cancel_inner:Boolean = false
    private var mData: ArrayList<OrderProductBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<OrderProductBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyerPendingDeliver_OrderDatailLinearHolder {
        val v = parent.context.inflate(R.layout.item_order_detail,parent,false)

        return BuyerPendingDeliver_OrderDatailLinearHolder(v)
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
    override fun onBindViewHolder(holder: BuyerPendingDeliver_OrderDatailLinearHolder, position: Int) {
        val viewHolder: BuyerPendingDeliver_OrderDatailLinearHolder = holder
        val item = mData.get(position)
        viewHolder.product.load(item.product_pic)
        viewHolder.name.text = item.product_title
        viewHolder.spec1.text = item.spec_desc_1
        viewHolder.spec_item1.text = item.spec_dec_1_items
        viewHolder.spec2.text = item.spec_desc_2
        viewHolder.spec_item2.text = item.spec_dec_2_items
        viewHolder.quantity.text = "x"+item.quantity
        viewHolder.price.text = "HKD$ "+item.price

    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,address_id:String)
    }

    inner class BuyerPendingDeliver_OrderDatailLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val product = itemView.find<ImageView>(R.id.iv_product)
        var name = itemView.find<TextView>(R.id.tv_product_name)
        var spec1 = itemView.find<TextView>(R.id.tv_product_spec1)
        var spec_item1 = itemView.find<TextView>(R.id.tv_product_spec_item1)
        val spec2 = itemView.find<TextView>(R.id.tv_product_spec2)
        var spec_item2 = itemView.find<TextView>(R.id.tv_product_spec_item2)
        var quantity = itemView.find<TextView>(R.id.tv_product_quantity)
        val price = itemView.find<TextView>(R.id.tv_product_price)
    }



}