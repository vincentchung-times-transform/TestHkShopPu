package com.HKSHOPU.hk.ui.main.productBuyer.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ProductDetailedPageForBuyer_RatingDetailsBean

import com.HKSHOPU.hk.utils.extension.inflate


import org.jetbrains.anko.find

class ProductRatingDetailsAdapter : RecyclerView.Adapter<ProductRatingDetailsAdapter.ShopInfoLinearHolder>(){

    var itemClick : ((id: Int) -> Unit)? = null

    var MMKV_product_id: Int = 1
    var mutablelist_ratingDetailsBean: MutableList<ProductDetailedPageForBuyer_RatingDetailsBean> = mutableListOf()

    fun setData(list : MutableList<ProductDetailedPageForBuyer_RatingDetailsBean>){
        list?:return
        this.mutablelist_ratingDetailsBean = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.adapter_item_product_rating_details,parent,false)

        return ShopInfoLinearHolder(v)

    }

    override fun getItemCount(): Int {
        return mutablelist_ratingDetailsBean.size
    }
    fun removeAt(position: Int) {
        mutablelist_ratingDetailsBean.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mutablelist_ratingDetailsBean.get(position)
        holder.bind(item)


    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_rating_user_icon = itemView.find<ImageView>(R.id.iv_rating_user_icon)
        val tv_rating_user_name = itemView.find<TextView>(R.id.tv_rating_user_name)
        val tv_rating_user_comment = itemView.find<TextView>(R.id.tv_rating_user_comment)
        var tv_rating_user_average_rating = itemView.find<TextView>(R.id.tv_rating_user_average_rating)
        var iv_rating_user_star01 = itemView.find<ImageView>(R.id.iv_rating_user_star01)
        var iv_rating_user_star02 = itemView.find<ImageView>(R.id.iv_rating_user_star02)
        var iv_rating_user_star03 = itemView.find<ImageView>(R.id.iv_rating_user_star03)
        var iv_rating_user_star04 = itemView.find<ImageView>(R.id.iv_rating_user_star04)
        var iv_rating_user_star05 = itemView.find<ImageView>(R.id.iv_rating_user_star05)

        fun bind(bean : ProductDetailedPageForBuyer_RatingDetailsBean){

//            Picasso.with(itemView.context).load(bean.pic_path).into(iv_rating_user_icon)
            tv_rating_user_name.setText(bean.user_name)
            tv_rating_user_comment.setText(bean.comment)

            tv_rating_user_average_rating.setText(bean.rating.toString())
            if(bean.rating>4.25){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star_half)

            }else if (bean.rating>3.75){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>3.25){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star_half)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>2.75){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>2.25){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star_half)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>1.75){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>1.25){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star_half)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>0.75){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_fill)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else if(bean.rating>0.25){

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star_half)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }else{

                iv_rating_user_star01.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star02.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star03.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star04.setImageResource(R.mipmap.ic_star)
                iv_rating_user_star05.setImageResource(R.mipmap.ic_star)

            }

        }

    }




}