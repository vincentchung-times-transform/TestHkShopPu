package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerPaymentBean

import com.HKSHOPU.hk.utils.extension.inflate


import org.jetbrains.anko.find
import java.util.*

class BuyerBankAccount_ListAdapter : RecyclerView.Adapter<BuyerBankAccount_ListAdapter.BankListLinearHolder>(){

    private var selected = -1
    private var cancel_inner:Boolean = false
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

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListLinearHolder {
        val v = parent.context.inflate(R.layout.item_fpsaccount,parent,false)

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
        viewHolder.bankcode.text = item.bank_code
        viewHolder.bankname.text = item.bank_name
        viewHolder.bankaccountname.text = item.bank_account_name
        var contact_type = item.contact_type
        if(contact_type.equals("phone")){
            viewHolder.layout_phone.visibility = View.VISIBLE
            viewHolder.phone.text = item.phone_country_code +" "+ item.phone_number

        }else{
            viewHolder.layout_email.visibility = View.VISIBLE
            viewHolder.email.text = item.contact_email

        }

        if(item.is_default.equals("Y")){

            viewHolder.preset.visibility = View.VISIBLE
            viewHolder.space.visibility = View.VISIBLE

        }else{

            viewHolder.preset.visibility = View.GONE
            viewHolder.space.visibility = View.GONE

        }
        viewHolder.preset.setOnClickListener {

            intentClick?.invoke(item.id)
        }

//        if(item.is_default.isEmpty()||item.is_default.equals("null")) {
            if (cancel_inner) {
                if(item.is_default.equals("N")){
                    viewHolder.cancel.visibility = View.VISIBLE
                    viewHolder.dummy.visibility = View.VISIBLE
                }
            } else {
                viewHolder.dummy.visibility = View.GONE
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
        val dummy = itemView.find<ImageView>(R.id.iv_dummy)
        var bankcode = itemView.find<TextView>(R.id.tv_bankcode)
        var bankname = itemView.find<TextView>(R.id.tv_bankname)
        var bankaccountname = itemView.find<TextView>(R.id.tv_bankaccountname)
        val layout_phone = itemView.find<RelativeLayout>(R.id.layout_account_phone)
        val layout_email = itemView.find<RelativeLayout>(R.id.layout_account_email)
        val phone = itemView.find<TextView>(R.id.tv_phone)
        val email = itemView.find<TextView>(R.id.tv_email)
        var space = itemView.find<ImageView>(R.id.space)

    }



}