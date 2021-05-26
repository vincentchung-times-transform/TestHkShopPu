package com.hkshopu.hk.ui.main.store.adapter

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.application.App

import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ShopAddressListBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.ui.main.store.activity.AdvertisementActivity
import com.hkshopu.hk.ui.main.store.activity.BankPresetActivity
import com.hkshopu.hk.utils.extension.inflate


import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*

class ShopAddressPresetAdapter : RecyclerView.Adapter<ShopAddressPresetAdapter.ShopAddressListLinearHolder>(){

    private var selected = 0

    private var mData: ArrayList<ShopAddressListBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<ShopAddressListBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopAddressListLinearHolder {
        val v = parent.context.inflate(R.layout.item_shopaddress_preset,parent,false)

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
        val phone = item.country_code+item.phone
        viewHolder.phone.text = phone
        val address = item.area + item.district + item.road + item.number + item.other + item.floor + item.room
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
        val layout_preset = itemView.find<LinearLayout>(R.id.layout_shopaddresspreset)
        var name = itemView.find<TextView>(R.id.tv_shopname)
        var phone = itemView.find<TextView>(R.id.tv_shopphone)
        val address = itemView.find<TextView>(R.id.tv_shopaddress)


    }



}