package com.hkshopu.hk.ui.main.store.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.application.App

import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.ui.main.store.activity.AdvertisementActivity
import com.hkshopu.hk.ui.main.store.activity.BankPresetActivity
import com.hkshopu.hk.utils.extension.inflate


import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class BankListAdapter : RecyclerView.Adapter<BankListAdapter.BankListLinearHolder>(){

    private var selected = -1
    private var cancel_inner:Boolean = false

    private var mData: ArrayList<ShopBankAccountBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<ShopBankAccountBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListLinearHolder {
        val v = parent.context.inflate(R.layout.item_bankaccount,parent,false)

        return BankListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    var cancelClick: ((id: String) -> Unit)? = null
    var toPresetClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: BankListLinearHolder, position: Int) {
        val viewHolder: BankListLinearHolder = holder
        val item = mData.get(position)
        viewHolder.code.text = item.code
        viewHolder.name.text = item.name
        viewHolder.number.text = item.account
        if(item.is_default.equals("Y")){
            viewHolder.preset.visibility = View.VISIBLE
        }else{
            viewHolder.preset.visibility = View.GONE
        }
        viewHolder.preset.setOnClickListener {
            toPresetClick?.invoke("go")
        }
        if(item.is_default.isEmpty()||item.is_default.equals("N")) {
            if (cancel_inner) {
                viewHolder.cancel.visibility = View.VISIBLE
            } else {
                viewHolder.cancel.visibility = View.GONE
            }
            viewHolder.cancel.setOnClickListener {
                removeItem(position)
                cancelClick?.invoke(item.id)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class BankListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val preset = itemView.find<ImageView>(R.id.iv_preset)
        val cancel = itemView.find<ImageView>(R.id.iv_cancel)
        val code = itemView.find<TextView>(R.id.tv_code)
        var name = itemView.find<TextView>(R.id.tv_name)
        var number = itemView.find<TextView>(R.id.tv_number)


    }



}