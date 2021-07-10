package com.HKSHOPU.hk.ui.main.homepage.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ShopRecommendBean

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click
import com.kaelli.niceratingbar.NiceRatingBar


import org.jetbrains.anko.find
import java.util.*

class StoreRecommendAdapter : RecyclerView.Adapter<StoreRecommendAdapter.ShopRecommendLinearHolder>(){
    private var mData: ArrayList<ShopRecommendBean> = ArrayList()
    private var newData: ArrayList<ShopRecommendBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var followClick : ((id: String, follow: String) -> Unit)? = null
    private var follow_inner = ""
    fun setData(list : ArrayList<ShopRecommendBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<ShopRecommendBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopRecommendLinearHolder {
        val v = parent.context.inflate(R.layout.item_store_ranking,parent,false)

        return ShopRecommendLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(follow: String){
        follow_inner =follow
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

    override fun onBindViewHolder(holder: ShopRecommendLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopRecommendLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val container = itemView.find<LinearLayout>(R.id.layout_shopbg)
        val pic1 = itemView.find<ImageView>(R.id.iv_shop_pic1)
        val pic2 = itemView.find<ImageView>(R.id.iv_shop_pic2)
        val pic3 = itemView.find<ImageView>(R.id.iv_shop_pic3)
        val picUser = itemView.find<ImageView>(R.id.iv_user_pic)
        val shopCare = itemView.find<ImageView>(R.id.iv_shopcare)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val ratings = itemView.find<TextView>(R.id.tv_rating)
        val ratingBar = itemView.find<NiceRatingBar>(R.id.ratingBar)
        val follows = itemView.find<TextView>(R.id.tv_attentionnumber)
        fun bindShop(shopRecommendBean : ShopRecommendBean){
            itemView.click {
                itemClick?.invoke(shopRecommendBean.shop_id.toString())
            }
            picUser.loadNovelCover(shopRecommendBean.shop_icon)
            shopname.text = shopRecommendBean.shop_name
            follows.text = shopRecommendBean.follower_count.toString()

                val pic1_path = shopRecommendBean.pic_path_1
                val pic2_path = shopRecommendBean.pic_path_2
                val pic3_path = shopRecommendBean.pic_path_3
                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)
                pic3.loadNovelCover(pic3_path)
            ratings.text = shopRecommendBean.rating.toString()
            ratingBar.setRating(shopRecommendBean.rating.toFloat())
            if(shopRecommendBean.followed.equals("Y")){
                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
                shopCare.click {
                    followClick?.invoke(shopRecommendBean.shop_id.toString(),"N")
                    shopCare.setImageResource(R.mipmap.ic_addtakecare)
                }
            }else{
                shopCare.setImageResource(R.mipmap.ic_addtakecare)
                shopCare.click {
                    followClick?.invoke(shopRecommendBean.shop_id.toString(),"Y")
                    shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
                }
            }
//            if(follow_inner.equals("Y")){
//                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
//            }else{
//                shopCare.setImageResource(R.mipmap.ic_addtakecare)
//            }
        }
    }



}