package com.hkshopu.hk.ui.main.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R

import com.hkshopu.hk.data.bean.ShopListBean
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.widget.view.click
import com.kaelli.niceratingbar.NiceRatingBar



import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class ShopInfoAdapter : RecyclerView.Adapter<ShopInfoAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<ShopListBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null

    fun setData(list : ArrayList<ShopListBean>){
        list?:return
        this.mData = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_shopmanage,parent,false)

        return ShopInfoLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.container)
        val image = itemView.find<ImageView>(R.id.iv_Icon)
        val title = itemView.find<TextView>(R.id.tv_shopName)
        val ratingBar = itemView.find<NiceRatingBar>(R.id.ratingBar)
        val merchantNums = itemView.find<TextView>(R.id.tv_MerchantNums)
        val score = itemView.find<TextView>(R.id.tv_shopScore)
        val follower = itemView.find<TextView>(R.id.tv_LikeNums)
        val income = itemView.find<TextView>(R.id.tv_IncomeNums)
        fun bindShop(bean : ShopListBean){
            container.click {
                itemClick?.invoke(bean.id)
            }
            image.loadNovelCover(bean.shop_icon)
            title.text = bean.shop_title
            merchantNums .text = bean.product_count
            follower.text = bean.follower
            score.text = bean.rating
            income.text = bean.income

        }
    }



}