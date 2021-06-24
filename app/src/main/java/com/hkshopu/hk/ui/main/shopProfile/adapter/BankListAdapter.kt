package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ItemData
import com.HKSHOPU.hk.data.bean.ShopBankAccountBean
import com.HKSHOPU.hk.utils.extension.inflate


import org.jetbrains.anko.find
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
            viewHolder.container_bank_item.setBackgroundResource(R.drawable.customborder_onboard_16dp_down)
        }else{
            viewHolder.preset.visibility = View.GONE
            viewHolder.container_bank_item.setBackgroundResource(R.drawable.customborder_onboard_8dp)
        }


        viewHolder.preset.setOnClickListener {
            toPresetClick?.invoke("go")
        }

        if (cancel_inner) {
            if(item.is_default.isEmpty()||item.is_default.equals("N")) {
                viewHolder.cancel.visibility = View.VISIBLE
            }else{
                viewHolder.cancel.visibility = View.GONE
            }

        } else {
            viewHolder.cancel.visibility = View.GONE
        }

        viewHolder.cancel.setOnClickListener {
            removeItem(position)
            cancelClick?.invoke(item.id)
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
        var container_bank_item = itemView.find<LinearLayout>(R.id.container_bank_item)


    }



}