package com.HKSHOPU.hk.ui.main.notification.adapter


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import java.text.SimpleDateFormat
import java.util.*

class NotificationMessageAdapter(): RecyclerView.Adapter<NotificationMessageAdapter.LinearHolder>(){
    private var mData: ArrayList<NotificationMessageBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
    var itemClick : ((id: String,id_notification: String, order_status: String ) -> Unit)? = null

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

    override fun onBindViewHolder(holder: LinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bind(item)
    }

    inner class LinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout_notify = itemView.find<RelativeLayout>(R.id.layout_notify)
        val iv_message_icon = itemView.find<ImageView>(R.id.iv_message_icon)
        var tv_orderer_name = itemView.findViewById<TextView>(R.id.tv_notification_name)
        var tv_message_content = itemView.findViewById<TextView>(R.id.tv_message_content)
        var tv_date = itemView.findViewById<TextView>(R.id.tv_date)
        fun bind(notificationMessageBean : NotificationMessageBean){

            iv_message_icon.loadNovelCover(notificationMessageBean.product_pic)
            tv_orderer_name.setText(notificationMessageBean.notification_title)
            tv_message_content.setText(notificationMessageBean.notification_content)
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val notify_at: Date = format.parse(notificationMessageBean.created_at)
            var notify_at_result = SimpleDateFormat("MM/dd/yyyy HH:mm").format(notify_at)
            tv_date.text = notify_at_result.toString()
            itemView.setOnClickListener {
                itemClick?.invoke(notificationMessageBean.order_id,notificationMessageBean.notitfication_id, notificationMessageBean.order_status)
                layout_notify.setBackgroundResource(R.drawable.customborder_product_evaluate_16dp)
            }
            if(notificationMessageBean.clicked.equals("Y")) {
                layout_notify.setBackgroundResource(R.drawable.customborder_product_evaluate_16dp)
            }else{
                layout_notify.setBackgroundResource(R.drawable.customborder_onboard_16dp)
            }


        }
    }
}