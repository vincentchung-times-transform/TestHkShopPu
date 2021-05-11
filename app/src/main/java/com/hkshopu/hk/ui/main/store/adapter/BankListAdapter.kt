package com.hkshopu.hk.ui.main.store.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R

import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.utils.extension.inflate


import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class BankListAdapter : RecyclerView.Adapter<BankListAdapter.BankListLinearHolder>(){

    private var selected = -1
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListLinearHolder {
        val v = parent.context.inflate(R.layout.item_bankaccount,parent,false)

        return BankListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: BankListLinearHolder, position: Int) {
        val viewHolder: BankListLinearHolder = holder
        val item = mData.get(position)
        viewHolder.code.text = item.code
        viewHolder.name.text = item.name
        viewHolder.number.text = item.account
    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class BankListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val code = itemView.find<TextView>(R.id.tv_code)
        var name = itemView.find<TextView>(R.id.tv_name)
        var number = itemView.find<TextView>(R.id.tv_number)


    }



}