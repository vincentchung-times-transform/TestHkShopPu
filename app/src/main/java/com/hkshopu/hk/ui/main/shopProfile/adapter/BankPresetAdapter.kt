package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

        if(position.equals(mData.size-1)){
            setMargin(holder.itemView.context, viewHolder.layout_preset, viewHolder.layout_preset_params,
                15,0,15,16)
        }


        viewHolder.code.text = item.code
        viewHolder.name.text = item.name
        viewHolder.number.text = item.account
        if(item.is_default.equals("Y")){
            viewHolder.code.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.number.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_8dp)
        }
        viewHolder.layout_preset.setOnClickListener {
            selected = position
            notifyDataSetChanged()
        }
        if(selected==position){
            viewHolder.code.textColor = Color.parseColor("#FFFFFF")
            viewHolder.name.textColor = Color.parseColor("#FFFFFF")
            viewHolder.number.textColor = Color.parseColor("#FFFFFF")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_turquise_8dp)
            presetClick?.invoke(item.id)
        }else{
            viewHolder.code.textColor = Color.parseColor("#48484A")
            viewHolder.name.textColor = Color.parseColor("#48484A")
            viewHolder.number.textColor = Color.parseColor("#48484A")
            viewHolder.layout_preset.setBackgroundResource(R.drawable.customborder_onboard_8dp)
        }
    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class BankListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout_preset = itemView.find<LinearLayout>(R.id.layout_bankaccountpreset)
        val layout_preset_params = itemView.find<LinearLayout>(R.id.layout_bankaccountpreset).layoutParams as LinearLayout.LayoutParams
        val code = itemView.find<TextView>(R.id.tv_code)
        var name = itemView.find<TextView>(R.id.tv_name)
        var number = itemView.find<TextView>(R.id.tv_number)




    }

    fun setMargin(con: Context, view: LinearLayout, params: ViewGroup.LayoutParams,
                  dp_l:Int, dp_t: Int, dp_r:Int, dp_b:Int) {
        val scale: Float = con.getResources().getDisplayMetrics().density
        // convert the DP into pixel
        val pixel_l = (dp_l * scale + 0.5f).toInt()
        val pixel_t = (dp_t * scale + 0.5f).toInt()
        val pixel_r = (dp_r * scale + 0.5f).toInt()
        val pixel_b = (dp_b * scale + 0.5f).toInt()
        val s = params as ViewGroup.MarginLayoutParams
        s.setMargins(pixel_l , pixel_t, pixel_r, pixel_b)
        view.setLayoutParams(params)
    }



}