package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerOrderDetailBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseListPendingPaymentActivity
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.load
import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response


import org.jetbrains.anko.find
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class BuyerOrderList_PendingPaymentAdapter : RecyclerView.Adapter<BuyerOrderList_PendingPaymentAdapter.BuyerPendingPaymentLinearHolder>(){

    private var selected = -1
    private var cancel_inner:Boolean = false
    private var mData: ArrayList<BuyerOrderDetailBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<BuyerOrderDetailBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyerPendingPaymentLinearHolder {
        val v = parent.context.inflate(R.layout.item_pending_payment,parent,false)

        return BuyerPendingPaymentLinearHolder(v)
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

    var intentClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: BuyerPendingPaymentLinearHolder, position: Int) {
        val viewHolder: BuyerPendingPaymentLinearHolder = holder
        val item = mData.get(position)
        viewHolder.product.load(item.product_pic)
        viewHolder.store.load(item.shop_icon)
        viewHolder.name.text = item.shop_title
        viewHolder.quantity.text = item.count.toString()
        viewHolder.price.text = "HKD$ "+item.sub_total.toString()

//        holder.getCheckFpsStatus(item.order_id)


        viewHolder.itemView.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, BuyerPurchaseListPendingPaymentActivity::class.java)
            val bundle = Bundle()
            bundle.putString("order_id", item.order_id)
            intent.putExtra("bundle", bundle)
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,address_id:String)
    }

    inner class BuyerPendingPaymentLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val product = itemView.find<ImageView>(R.id.iv_product)
        val store = itemView.find<ImageView>(R.id.iv_store)
        var name = itemView.find<TextView>(R.id.tv_store_name)
        var quantity = itemView.find<TextView>(R.id.tv_product_quantity)
        val price = itemView.find<TextView>(R.id.tv_product_price)
        val btn_continue_payment = itemView.find<ImageView>(R.id.iv_btn_continue_payment)
        val tv_state = itemView.find<TextView>(R.id.tv_state)


//        fun getCheckFpsStatus(order_id:String) {
//
//            val url = ApiConstants.API_HOST+"user_detail/${order_id}/fps_check/"
//            val web = Web(object : WebListener {
//                override fun onResponse(response: Response) {
//                    var resStr: String? = ""
//                    try {
//                        resStr = response.body()!!.string()
//                        val json = JSONObject(resStr)
//                        val ret_val = json.get("ret_val")
//                        val status = json.get("status")
//                        Log.d("getCheckFpsStatus", "返回資料 resStr：" + resStr)
//                        Log.d("getCheckFpsStatus", "返回資料 ret_val：" + ret_val)
//                        if (status == 0) {
//                            runOnUiThread {
//                                btn_continue_payment.visibility = View.VISIBLE
//                                tv_state.setText(R.string.tobepaid)
//                            }
//
//                        }else if(status == -1) {
//                            runOnUiThread {
//                                btn_continue_payment.visibility = View.INVISIBLE
//                                tv_state.setText(R.string.add_value_reviewing_status)
//                            }
//
//                        }
//                    } catch (e: JSONException) {
//                        Log.d("getCheckFpsStatus", "JSONException：" + e.toString())
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                        Log.d("getCheckFpsStatus", "IOException：" + e.toString())
//                    }
//                }
//
//                override fun onErrorResponse(ErrorResponse: IOException?) {
//                    Log.d("getCheckFpsStatus", "ErrorResponse：" + ErrorResponse.toString())
//                }
//            })
//            web.getCheckFpsStatus(url)
//        }

    }
}