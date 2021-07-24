package com.HKSHOPU.hk.ui.main.seller.order.adapter


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.SalerSaleListBean
import com.HKSHOPU.hk.ui.main.seller.order.activity.SellerOrderDetailsActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import org.jetbrains.anko.find
import java.util.*

class PendingGoodReceiveOrderAdapter(): RecyclerView.Adapter<PendingGoodReceiveOrderAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<SalerSaleListBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""

    var order_status = "Pending Good Receive"

    fun setData(list : ArrayList<SalerSaleListBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<SalerSaleListBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_my_sales_order,parent,false)

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

        //待發貨 toBeDelivered
        val btn_shipping_notifying = itemView.find<ImageView>(R.id.btn_shipping_notifying)
        //待收穫 pendingGoods
        val btn_buyer_contacting_for_order_small = itemView.find<ImageView>(R.id.btn_buyer_contacting_for_order_small)

        val layout_order_unfinished =  itemView.find<LinearLayout>(R.id.layout_order_unfinished)
        val layout_order_finished =  itemView.find<LinearLayout>(R.id.layout_order_finished)
        var layout_order_cancelled = itemView.find<LinearLayout>(R.id.layout_order_cancelled)

        val btn_buyer_contacting = itemView.find<ImageView>(R.id.btn_buyer_contacting)
        val btn_reviews_viewing = itemView.find<ImageView>(R.id.btn_reviews_viewing)

        fun bindShop(salerSaleListBean : SalerSaleListBean){
            tv_order_status.setText(itemView.context.getText(R.string.tobereceived))

            iv_product_icon.loadNovelCover(salerSaleListBean.product_pic)
            iv_oderer_icon.loadNovelCover(salerSaleListBean.buyer_pic)

            layout_order_unfinished.visibility = View.VISIBLE
            layout_order_finished.visibility = View.GONE
            layout_order_cancelled.visibility = View.GONE
            btn_shipping_notifying.visibility = View.GONE
            btn_buyer_contacting_for_order_small.visibility = View.VISIBLE


            itemView.setOnClickListener {
                val intent = Intent(itemView.context, SellerOrderDetailsActivity::class.java)
                var bundle = Bundle()
                bundle.putString("order_id", salerSaleListBean.order_id)
                bundle.putString("order_statuts", order_status)
                intent.putExtra("bundle", bundle)
                itemView.context.startActivity(intent)
            }

            btn_buyer_contacting_for_order_small.setOnClickListener {

            }

        }
    }
}