package com.HKSHOPU.hk.ui.main.buyer.profile.adapter


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerOrderDetailBean
import com.HKSHOPU.hk.ui.main.seller.order.activity.SellerOrderDetailsActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import org.jetbrains.anko.find
import java.util.*

class BuyerOrderList_CanceledAdapter (): RecyclerView.Adapter<BuyerOrderList_CanceledAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<BuyerOrderDetailBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""

    var order_status = "Cancelled"

    fun setData(list : ArrayList<BuyerOrderDetailBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<BuyerOrderDetailBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_pending_cancel,parent,false)

        return TopProductLinearHolder(v)
    }
    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(like: String){
        like_inner =like
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
    override fun onBindViewHolder(holder: TopProductLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }
    inner class TopProductLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_product_icon = itemView.find<ImageView>(R.id.iv_product_icon)
        val iv_oderer_icon = itemView.find<ImageView>(R.id.iv_oderer_icon)
        val tv_orderer_name = itemView.find<TextView>(R.id.tv_orderer_name)
        val tv_order_status = itemView.find<TextView>(R.id.tv_order_status)
        val tv_product_kind_quant = itemView.find<TextView>(R.id.tv_product_kind_quant)
        val tv_priceRange = itemView.find<TextView>(R.id.tv_priceRange)

        var layout_order_cancelled = itemView.find<LinearLayout>(R.id.layout_order_cancelled)

        fun bindShop(buyerOrderDetailBean : BuyerOrderDetailBean){
            iv_product_icon.loadNovelCover(buyerOrderDetailBean.product_pic)
            iv_oderer_icon.loadNovelCover(buyerOrderDetailBean.shop_icon)
            tv_order_status.setText(itemView.context.getText(R.string.sales_tab4))
            tv_orderer_name.text = buyerOrderDetailBean.order_number
            tv_product_kind_quant.text = buyerOrderDetailBean.count.toString()
            tv_priceRange.text = buyerOrderDetailBean.sub_total.toString()

            layout_order_cancelled.visibility = View.VISIBLE

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, SellerOrderDetailsActivity::class.java)
                var bundle = Bundle()
                bundle.putString("order_id", buyerOrderDetailBean.order_id)
                bundle.putString("order_statuts", order_status)
                intent.putExtra("bundle", bundle)
                itemView.context.startActivity(intent)
            }

        }
    }
}