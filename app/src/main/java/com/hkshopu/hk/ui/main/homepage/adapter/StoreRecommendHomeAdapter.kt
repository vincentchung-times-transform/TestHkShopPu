package com.HKSHOPU.hk.ui.main.homepage.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ShopRecommendHomeBean
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click


import org.jetbrains.anko.find
import java.util.*

class StoreRecommendHomeAdapter : RecyclerView.Adapter<StoreRecommendHomeAdapter.ShopRecommendLinearHolder>(){
    private var mData: ArrayList<ShopRecommendHomeBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var followClick : ((id: String, follow: String) -> Unit)? = null
    private var follow_inner = ""
    fun setData(list : ArrayList<ShopRecommendHomeBean>){
        list?:return
        this.mData = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopRecommendLinearHolder {
        val v = parent.context.inflate(R.layout.item_store_recommend,parent,false)

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

    override fun onBindViewHolder(holder: ShopRecommendLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopRecommendLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val container = itemView.find<RelativeLayout>(R.id.layout_shopbg)
        val pic1 = itemView.find<ImageView>(R.id.iv_shop_pic1)
        val pic2 = itemView.find<ImageView>(R.id.iv_shop_pic2)
        val pic3 = itemView.find<ImageView>(R.id.iv_shop_pic3)
        val picUser = itemView.find<ImageView>(R.id.iv_user_pic)
        val shopCare = itemView.find<ImageView>(R.id.iv_shopcare)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val evaluate = itemView.find<TextView>(R.id.tv_eveluatenumber)
        fun bindShop(homeBean : ShopRecommendHomeBean){
            itemView.click {
                itemClick?.invoke(homeBean.shop_id)

            }
            picUser.loadNovelCover(homeBean.shop_icon)
            shopname.text = homeBean.shop_title
            evaluate.text = homeBean.shop_average_ratings.toString()
            if(homeBean.product_pics.size == 1) {

                val pic1_path = homeBean.product_pics[0]

                pic1.loadNovelCover(pic1_path)

            }else if(homeBean.product_pics.size == 2){
                val pic1_path = homeBean.product_pics[0]
                val pic2_path = homeBean.product_pics[1]

                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)

            }else if(homeBean.product_pics.size == 3){
                val pic1_path = homeBean.product_pics[0]
                val pic2_path = homeBean.product_pics[1]
                val pic3_path = homeBean.product_pics[2]
                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)
                pic3.loadNovelCover(pic3_path)
            }
                Log.d("StoreRecommendAdapter", "資料 shop_followed：" + homeBean.shop_followed)
            if(homeBean.shop_followed.equals("Y")){
                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
                shopCare.click {
                    followClick?.invoke(homeBean.shop_id,"N")
                    shopCare.setImageResource(R.mipmap.ic_addtakecare)
                }
            }else{
                shopCare.setImageResource(R.mipmap.ic_addtakecare)
                shopCare.click {
                    followClick?.invoke(homeBean.shop_id,"Y")
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