package com.HKSHOPU.hk.ui.main.buyer.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerAddressListBean

import com.HKSHOPU.hk.data.bean.ItemData
import com.HKSHOPU.hk.utils.extension.inflate


import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import java.util.*

class BuyerAddressPresetAdapter : RecyclerView.Adapter<BuyerAddressPresetAdapter.ShopAddressListLinearHolder>(){

    private var selected = 0

    private var mData: ArrayList<BuyerAddressListBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<BuyerAddressListBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopAddressListLinearHolder {
        val v = parent.context.inflate(R.layout.item_useraddress_preset,parent,false)

        return ShopAddressListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }



    var presetClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: ShopAddressListLinearHolder, position: Int) {
        val viewHolder: ShopAddressListLinearHolder = holder
        val item = mData.get(position)
        viewHolder.name.text = item.name
        val phone = item.phone
        viewHolder.phone.text = phone
        val address = item.address
        viewHolder.address.text = address

        if(item.is_default.equals("N")) {
            viewHolder.address.textColor = Color.parseColor("#48484A")
            viewHolder.name.textColor = Color.parseColor("#48484A")
            viewHolder.phone.textColor = Color.parseColor("#48484A")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_16dp)

        }else{
            viewHolder.address.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.phone.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_16dp)
        }
        viewHolder.layout_preset.setOnClickListener {
            selected = position
            notifyDataSetChanged()

        }
        if(selected==position){
            viewHolder.address.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.phone.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_16dp)
            presetClick?.invoke(item.id)
        }else{
            viewHolder.address.textColor = Color.parseColor("#48484A")
            viewHolder.name.textColor = Color.parseColor("#48484A")
            viewHolder.phone.textColor = Color.parseColor("#48484A")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_16dp)

        }

    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class ShopAddressListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout_preset = itemView.find<LinearLayout>(R.id.layout_useraddresspreset)
        var name = itemView.find<TextView>(R.id.tv_username)
        var phone = itemView.find<TextView>(R.id.tv_userphone)
        val address = itemView.find<TextView>(R.id.tv_useraddress)


    }



}