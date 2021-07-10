package com.HKSHOPU.hk.ui.main.sellerProfile.adapter


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ProductSearchBean
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click
import org.jetbrains.anko.find
import java.util.*

class CanceledOrderAdapter (var currency: Currency): RecyclerView.Adapter<CanceledOrderAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<ProductSearchBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""
    fun setData(list : ArrayList<ProductSearchBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<ProductSearchBean>) {
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
        val iv_user_icon = itemView.find<ImageView>(R.id.iv_user_icon)
        val tv_orderer_name = itemView.find<TextView>(R.id.tv_orderer_name)
        val tv_logistic_method = itemView.find<TextView>(R.id.tv_logistic_method)
        val tv_product_kind_quant = itemView.find<TextView>(R.id.tv_product_kind_quant)
        val tv_priceRange = itemView.find<TextView>(R.id.tv_priceRange)
        val tv_delivery_date = itemView.find<TextView>(R.id.tv_delivery_date)

        //待發貨 toBeDelivered
        val btn_shipping_notifying = itemView.find<ImageView>(R.id.btn_shipping_notifying)
        //待收穫 pendingGoods
        val btn_buyer_contacting_for_order_small = itemView.find<ImageView>(R.id.btn_buyer_contacting_for_order_small)

        val layout_order_unfinished =  itemView.find<LinearLayout>(R.id.layout_order_unfinished)
        val layout_order_finished =  itemView.find<LinearLayout>(R.id.layout_order_finished)

        val btn_buyer_contacting = itemView.find<ImageView>(R.id.btn_buyer_contacting)
        val btn_reviews_viewing = itemView.find<ImageView>(R.id.btn_reviews_viewing)

        fun bindShop(productSearchBean : ProductSearchBean){


            iv_product_icon.loadNovelCover(productSearchBean.pic_path)
            iv_user_icon.loadNovelCover(productSearchBean.pic_path)



        }
    }



}