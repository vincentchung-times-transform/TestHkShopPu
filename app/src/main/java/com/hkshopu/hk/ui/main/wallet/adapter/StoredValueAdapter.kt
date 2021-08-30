package com.HKSHOPU.hk.ui.main.wallet.adapter


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.AddValueHistoryBean
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.wallet.activity.AddValueDetailedInfoActivity
import com.HKSHOPU.hk.ui.main.wallet.activity.TransactionDetailedInfoForAdActivity
import com.HKSHOPU.hk.ui.main.wallet.activity.TransactionDetailedInfoForSponsorActivity
import com.HKSHOPU.hk.utils.extension.inflate
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class StoredValueAdapter (): RecyclerView.Adapter<StoredValueAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<AddValueHistoryBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null

    fun setData(list : ArrayList<AddValueHistoryBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<AddValueHistoryBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_stored_value_record,parent,false)

        return TopProductLinearHolder(v)
    }
    override fun getItemCount(): Int {
        return mData.size
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
    override fun onBindViewHolder(holder: TopProductLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }
    inner class TopProductLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_product_icon = itemView.find<ImageView>(R.id.iv_product_icon)
        var tv_desc = itemView.find<TextView>(R.id.tv_desc)
        val tv_date = itemView.find<TextView>(R.id.tv_date)
        val tv_stored_value = itemView.find<TextView>(R.id.tv_stored_value)
        var tv_add_value_order_status = itemView.find<TextView>(R.id.tv_add_value_order_status)
        var tv_slash = itemView.find<TextView>(R.id.tv_slash)
        var tv_click = itemView.find<TextView>(R.id.tv_click)

        fun bindShop(storedValueBean : AddValueHistoryBean){

            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val actual_finished_at: Date = format.parse(storedValueBean.created_at.toString())
            var actual_finished_at_result =  SimpleDateFormat("dd/MM/yyyy").format(actual_finished_at)
            tv_date.setText(actual_finished_at_result.toString())

            tv_stored_value.setText(storedValueBean.change.toString().replace("-", ""))

            when(storedValueBean.action){
                "錢包儲值"->{
                    tv_slash.visibility = View.GONE
                    tv_click.visibility = View.GONE

                    when(storedValueBean.change.toString()){
                        "788"->{
                            iv_product_icon.setImageResource(R.mipmap.petit_podium)
                        }
                        "398"->{
                            iv_product_icon.setImageResource(R.mipmap.petit_coins)
                        }
                        "158"->{
                            iv_product_icon.setImageResource(R.mipmap.petit_coin)
                        }
                        else->{

                        }
                    }

                    when(storedValueBean.status.toString()){
                        "0"->{
                            tv_add_value_order_status.visibility = View.VISIBLE
                            tv_add_value_order_status.setText("繼續付款")
                            tv_add_value_order_status.setTextColor(ContextCompat.getColor(itemView.context,R.color.hkshop_color))
                        }
                        "1"->{
                            tv_add_value_order_status.visibility = View.VISIBLE
                            tv_add_value_order_status.setText("審查中")
                            tv_add_value_order_status.setTextColor(ContextCompat.getColor(itemView.context,R.color.purple_7B61FF))
                        }
                        "2"->{
                            tv_add_value_order_status.visibility = View.VISIBLE
                            tv_add_value_order_status.setText("付款失敗")
                            tv_add_value_order_status.setTextColor(ContextCompat.getColor(itemView.context,R.color.bright_red))
                        }
                        "3"->{
                            tv_add_value_order_status.visibility = View.GONE
                        }
                    }
                }
                "贊助扣值"->{
                    when(storedValueBean.description.toString()){
                        "尊榮贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_honor)
                        }
                        "至尊贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_supreme)
                        }
                        "榮耀贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_glory)
                        }
                        "卓越贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_excel)
                        }
                        "冥王星贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_pluto)
                        }
                        "天王星贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_uranus)
                        }
                        "金星贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_venus)
                        }
                        "水星贊助商"->{
                            iv_product_icon.setImageResource(R.mipmap.transaction_sponsor_mercury)
                        }
                    }

                    tv_add_value_order_status.visibility = View.GONE
                    tv_slash.visibility = View.GONE
                    tv_click.visibility = View.GONE

                }
                "購買廣告"->{
                    tv_desc.setText(storedValueBean.description.toString())
                    iv_product_icon.setImageResource(R.mipmap.transaction_product_keyword_ad)

                    tv_add_value_order_status.visibility = View.GONE
                    tv_slash.visibility = View.VISIBLE
                    tv_click.visibility = View.VISIBLE
                }

            }

            itemView.setOnClickListener {
                when(storedValueBean.action) {
                    "錢包儲值" -> {
                        val intent =
                            Intent(itemView.context, AddValueDetailedInfoActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("orderNumber", storedValueBean.order_number.toString())
                        intent.putExtra("bundle", bundle)
                        itemView.context.startActivity(intent)
                    }
                    "購買贊助" -> {
                        val intent =
                            Intent(itemView.context, TransactionDetailedInfoForSponsorActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("orderNumber", storedValueBean.order_number.toString())
                        intent.putExtra("bundle", bundle)
                        itemView.context.startActivity(intent)
                    }
                    "購買廣告" -> {
                        val intent =
                            Intent(itemView.context, TransactionDetailedInfoForAdActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("orderNumber", storedValueBean.order_number.toString())
                        intent.putExtra("bundle", bundle)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }
}