package com.HKSHOPU.hk.ui.main.seller.notification.adapter


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.NotificationMessageBean
import com.HKSHOPU.hk.data.bean.ProductSearchBean
import com.HKSHOPU.hk.data.bean.SalerSaleListBean
import com.HKSHOPU.hk.ui.main.seller.order.activity.SellerOrderDetailsActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.setTextColor
import org.jetbrains.anko.find
import java.util.*

class NotificationMessageAdapter(): RecyclerView.Adapter<NotificationMessageAdapter.LinearHolder>(){
    private var mData: ArrayList<NotificationMessageBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null

    fun setData(list : ArrayList<NotificationMessageBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }

    fun add(list: ArrayList<NotificationMessageBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinearHolder {
        val v = parent.context.inflate(R.layout.item_notification_message,parent,false)
        return LinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    //更新資料用
    fun updateData(like: String){
        this.notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        val size = mData.size
        mData.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: LinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bind(item)
    }

    inner class LinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_product_icon = itemView.find<ImageView>(R.id.iv_product_icon)
        var tv_orderer_name = itemView.findViewById<TextView>(R.id.tv_orderer_name)
        var tv_message_content = itemView.findViewById<TextView>(R.id.tv_message_content)

        fun bind(salerSaleListBean : NotificationMessageBean){

            iv_product_icon.loadNovelCover(salerSaleListBean.iv_product_icon)
            tv_orderer_name.setText(salerSaleListBean.tv_orderer_name)
            tv_message_content.setText(salerSaleListBean.tv_message_content)

            itemView.setOnClickListener {

            }
        }
    }
}