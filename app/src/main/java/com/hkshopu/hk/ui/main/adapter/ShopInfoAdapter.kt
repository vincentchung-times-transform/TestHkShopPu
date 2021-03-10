package com.hkshopu.hk.ui.main.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.utils.extension.loadNovelCover


import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class ShopInfoAdapter : RecyclerView.Adapter<ShopInfoAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<ShopInfoBean> = ArrayList()
    var itemClick : ((title: String) -> Unit)? = null

    fun setData(list : ArrayList<ShopInfoBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
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
        val image = itemView.find<ImageView>(R.id.ivIcon)
        val title = itemView.find<TextView>(R.id.text)

        private val sdf = SimpleDateFormat("yyyy-MM-dd")
        fun bindShop(bean : ShopInfoBean){
            image.loadNovelCover(bean.shop_icon)
            title.text = bean.shop_title

        }
    }



}