package com.HKSHOPU.hk.ui.main.homepage.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.TopProductBean
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.util.*

class TopProductAdapter (var currency: Currency): RecyclerView.Adapter<TopProductAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<TopProductBean> = ArrayList()
    private var newData: ArrayList<TopProductBean> = ArrayList()
//    var itemClick : ((id: String) -> Unit)? = null
    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""
    fun setData(list : ArrayList<TopProductBean>){
        list?:return
        mData.clear()
        mData.addAll(list)
        newData = mData
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<TopProductBean>) {
        list?:return
        newData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_top_products,parent,false)

        return TopProductLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return newData.size
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

    override fun onBindViewHolder(holder: TopProductLinearHolder, position: Int) {
        val item = newData.get(position)
        holder.bindShop(item)
    }

    inner class TopProductLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val container = itemView.find<RelativeLayout>(R.id.layout_product)
        val container = itemView.find<RelativeLayout>(R.id.container)
        val cardvView_product = itemView.find<CardView>(R.id.cardvView_product)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val like = itemView.find<ImageView>(R.id.iv_product_like)
        val like_click = itemView.find<ImageView>(R.id.iv_product_like_click)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val price = itemView.find<TextView>(R.id.tv_price)


        fun bindShop(topProductBean : TopProductBean){

            itemView.setOnClickListener {

                val intent = Intent(itemView.context, ProductDetailedPageBuyerViewActivity::class.java)
                var bundle = Bundle()
                bundle.putString("product_id", topProductBean.product_id)
                intent.putExtra("bundle_product_id", bundle)
                itemView.context.startActivity(intent)

            }

            image.loadNovelCover(topProductBean.pic_path)
            title .text = topProductBean.product_title
            shopname.text = topProductBean.shop_title
            price.text = currency.toString()+ topProductBean.price.toString()
            if(like_inner.equals("Y")){
                like.setImageResource(R.mipmap.ic_heart_red)
                like_click.click {
                    likeClick?.invoke(topProductBean.id,"N")
                }
            }else{
                like.setImageResource(R.mipmap.ic_heart_white)
                like_click.click {
                    likeClick?.invoke(topProductBean.id,"Y")
                }
            }

            val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
            val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

            if(width.equals(1080)){

                val params_container: ViewGroup.LayoutParams = container.getLayoutParams()
                var width_scaling =  (width*168)/375

                params_container.width = width_scaling
                container.setLayoutParams(params_container)

                val params_layout_product: ViewGroup.LayoutParams = cardvView_product.getLayoutParams()
                params_layout_product.width = width_scaling
                params_layout_product.height = (width_scaling*18)/21
                cardvView_product.setLayoutParams(params_layout_product)
            }

        }
    }



}