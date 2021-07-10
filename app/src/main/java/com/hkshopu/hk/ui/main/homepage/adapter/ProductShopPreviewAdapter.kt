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

import com.HKSHOPU.hk.data.bean.ProductShopPreviewBean
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.util.*

class ProductShopPreviewAdapter (var currency: Currency): RecyclerView.Adapter<ProductShopPreviewAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<ProductShopPreviewBean> = ArrayList()
//    var itemClick : ((id: String) -> Unit)? = null
    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""
    fun setData(list : ArrayList<ProductShopPreviewBean>){
        list?:return
        this.mData = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_top_products,parent,false)

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

    override fun onBindViewHolder(holder: TopProductLinearHolder, position: Int) {
        val item = mData.get(position)
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
        fun bindShop(shopPreviewBean : ProductShopPreviewBean){

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailedPageBuyerViewActivity::class.java)
                var bundle = Bundle()
                bundle.putString("product_id", shopPreviewBean.product_id)
                intent.putExtra("bundle_product_id", bundle)
                itemView.context.startActivity(intent)

            }

            image.loadNovelCover(shopPreviewBean.pic_path)
            title .text = shopPreviewBean.product_title
            shopname.text = shopPreviewBean.shop_title
            price.text = currency.toString()+ shopPreviewBean.product_price.toString()
            if(shopPreviewBean.liked.equals("Y")){
                like.setImageResource(R.mipmap.ic_heart_red)
                like_click.click {
                    likeClick?.invoke(shopPreviewBean.product_id,"N")
                    like.setImageResource(R.mipmap.ic_heart_white)
                }
            }else{
                like.setImageResource(R.mipmap.ic_heart_white)
                like_click.click {
                    likeClick?.invoke(shopPreviewBean.product_id,"Y")
                    like.setImageResource(R.mipmap.ic_heart_red)
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