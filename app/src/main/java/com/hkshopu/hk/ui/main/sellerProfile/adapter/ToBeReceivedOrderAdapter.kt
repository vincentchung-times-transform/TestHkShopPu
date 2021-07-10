package com.HKSHOPU.hk.ui.main.sellerProfile.adapter


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

class ToBeReceivedOrderAdapter (var currency: Currency): RecyclerView.Adapter<ToBeReceivedOrderAdapter.TopProductLinearHolder>(){
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
        val container = itemView.find<RelativeLayout>(R.id.layout_product)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val like = itemView.find<ImageView>(R.id.iv_product_like)
        val like_click = itemView.find<ImageView>(R.id.iv_product_like_click)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val price = itemView.find<TextView>(R.id.tv_price)
        fun bindShop(productSearchBean : ProductSearchBean){

            container.click {
                itemClick?.invoke(productSearchBean.id)
            }
            image.loadNovelCover(productSearchBean.pic_path)
            title .text = productSearchBean.product_title
            shopname.text = productSearchBean.shop_title
            price.text = currency.toString()+ productSearchBean.min_price.toString() + "-"+productSearchBean.max_price.toString()
            if(productSearchBean.liked.equals("Y")){
                like.setImageResource(R.mipmap.ic_heart_red)
                like_click.click {
                    likeClick?.invoke(productSearchBean.id,"N")
                    like.setImageResource(R.mipmap.ic_heart_white)
                }
            }else{
                like.setImageResource(R.mipmap.ic_heart_white)
                like_click.click {
                    likeClick?.invoke(productSearchBean.id,"Y")
                    like.setImageResource(R.mipmap.ic_heart_red)
                }
            }


        }
    }



}