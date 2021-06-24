package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ItemData
import com.HKSHOPU.hk.data.bean.ShopBankAccountBean
import com.HKSHOPU.hk.utils.extension.inflate


import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import java.util.*

class BankPresetAdapter : RecyclerView.Adapter<BankPresetAdapter.BankListLinearHolder>(){

    private var selected = 0
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListLinearHolder {
        val v = parent.context.inflate(R.layout.item_bankaccount_preset,parent,false)

        return BankListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    var presetClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: BankListLinearHolder, position: Int) {
        val viewHolder: BankListLinearHolder = holder
        val item = mData.get(position)
        viewHolder.code.text = item.code
        viewHolder.name.text = item.name
        viewHolder.number.text = item.account
        if(item.is_default.equals("Y")){
            viewHolder.code.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.number.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_16dp)
        }
        viewHolder.layout_preset.setOnClickListener {
            selected = position
            notifyDataSetChanged()
        }
        if(selected==position){
            viewHolder.code.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.number.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_16dp)
            presetClick?.invoke(item.id)
        }else{
            viewHolder.code.textColor = Color.parseColor("#48484A")
            viewHolder.name.textColor = Color.parseColor("#48484A")
            viewHolder.number.textColor = Color.parseColor("#48484A")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_16dp)
        }
    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class BankListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout_preset = itemView.find<RelativeLayout>(R.id.layout_bankaccountpreset)
        val code = itemView.find<TextView>(R.id.tv_code)
        var name = itemView.find<TextView>(R.id.tv_name)
        var number = itemView.find<TextView>(R.id.tv_number)


    }



}