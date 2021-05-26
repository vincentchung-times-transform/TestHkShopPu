package com.hkshopu.hk.ui.main.store.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.application.App

import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ShopAddressListBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.activity.AdvertisementActivity
import com.hkshopu.hk.ui.main.store.activity.BankListActivity
import com.hkshopu.hk.ui.main.store.activity.BankPresetActivity
import com.hkshopu.hk.ui.main.store.activity.ShopAddressPresetActivity
import com.hkshopu.hk.utils.extension.inflate
import com.tencent.mmkv.MMKV
import okhttp3.Response


import org.jetbrains.anko.find
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ShopAddressListAdapter : RecyclerView.Adapter<ShopAddressListAdapter.BankListLinearHolder>(){

    private var selected = -1
    private var cancel_inner:Boolean = false
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

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListLinearHolder {
        val v = parent.context.inflate(R.layout.item_shopaddress,parent,false)

        return BankListLinearHolder(v)
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
    var cancelClick: ((id: String) -> Unit)? = null
    var intentClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: BankListLinearHolder, position: Int) {
        val viewHolder: BankListLinearHolder = holder
        val item = mData.get(position)

        viewHolder.name.text = item.name
        val phone = item.country_code+item.phone
        viewHolder.phone.text = phone
        val address = item.area + item.district + item.road + item.number + item.other + item.floor + item.room
        viewHolder.address.text = address

        if(item.is_default.equals("Y")){

            viewHolder.preset.visibility = View.VISIBLE

        }
        viewHolder.preset.setOnClickListener {

            intentClick?.invoke(item.id)
        }

//        if(item.is_default.isEmpty()||item.is_default.equals("null")) {
            if (cancel_inner) {
                if(item.is_default.equals("N")){
                    viewHolder.cancel.visibility = View.VISIBLE
                }
            } else {
                viewHolder.cancel.visibility = View.GONE
            }
            viewHolder.cancel.setOnClickListener {
               removeItem(position)
                cancelClick?.invoke(item.id)
            }
//        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,address_id:String)
    }

    inner class BankListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val preset = itemView.find<ImageView>(R.id.iv_preset)
        val cancel = itemView.find<ImageView>(R.id.iv_cancel)
        var name = itemView.find<TextView>(R.id.tv_shopname)
        var phone = itemView.find<TextView>(R.id.tv_shopphone)
        val address = itemView.find<TextView>(R.id.tv_shopaddress)


    }



}