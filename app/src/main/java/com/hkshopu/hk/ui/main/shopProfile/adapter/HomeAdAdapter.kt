package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.HomeAdBean

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click


import org.jetbrains.anko.find
import java.util.*

class HomeAdAdapter : RecyclerView.Adapter<HomeAdAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<HomeAdBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null
    var deleteClick : ((id: Int) -> Unit)? = null
    private var cancel_inner:Boolean = false
    fun setData(list : ArrayList<HomeAdBean>){
        list?:return
        this.mData = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_store_ad,parent,false)

        return ShopInfoLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val image = itemView.find<ImageView>(R.id.iv_homeAd)

        fun bindShop(bean : HomeAdBean){

            image.loadNovelCover(bean.pic_path)

            image.click {
                itemClick?.invoke(bean.shop_id)
            }
        }
    }



}