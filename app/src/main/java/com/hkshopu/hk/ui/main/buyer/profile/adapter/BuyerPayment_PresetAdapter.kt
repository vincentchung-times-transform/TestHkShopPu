package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerPaymentBean

import com.HKSHOPU.hk.data.bean.ItemData
import com.HKSHOPU.hk.utils.extension.inflate


import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import java.util.*

class BuyerPayment_PresetAdapter : RecyclerView.Adapter<BuyerPayment_PresetAdapter.PaymentPresetListLinearHolder>(){

    private var selected = 0

    private var mData: ArrayList<BuyerPaymentBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<BuyerPaymentBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentPresetListLinearHolder {
        val v = parent.context.inflate(R.layout.item_fpsaccount_preset,parent,false)

        return PaymentPresetListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }



    var presetClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: PaymentPresetListLinearHolder, position: Int) {
        val viewHolder: PaymentPresetListLinearHolder = holder
        val item = mData.get(position)
        viewHolder.code.text = item.bank_code
        viewHolder.name.text = item.bank_name
        viewHolder.account.text = item.bank_account_name

        if(item.is_default.equals("N")) {
            viewHolder.code.textColor = Color.parseColor("#48484A")
            viewHolder.name.textColor = Color.parseColor("#48484A")
            viewHolder.account.textColor = Color.parseColor("#48484A")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_16dp)

        }else{
            viewHolder.code.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.account.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_16dp)
        }
        viewHolder.layout_preset.setOnClickListener {
            selected = position
            notifyDataSetChanged()

        }
        if(selected==position){
            viewHolder.code.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.account.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_16dp)
            presetClick?.invoke(item.id)
        }else{
            viewHolder.code.textColor = Color.parseColor("#48484A")
            viewHolder.name.textColor = Color.parseColor("#48484A")
            viewHolder.account.textColor = Color.parseColor("#48484A")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_16dp)

        }

    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class PaymentPresetListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout_preset = itemView.find<RelativeLayout>(R.id.layout_paymentpreset)
        var code = itemView.find<TextView>(R.id.tv_bankcode)
        var name = itemView.find<TextView>(R.id.tv_bankname)
        var account = itemView.find<TextView>(R.id.tv_bankaccount)


    }



}