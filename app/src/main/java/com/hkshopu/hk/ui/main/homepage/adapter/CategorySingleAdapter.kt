package com.HKSHOPU.hk.ui.main.homepage.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventToProductSearch
import com.HKSHOPU.hk.component.EventToShopSearch

import com.HKSHOPU.hk.data.bean.ItemData
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.ui.main.homepage.activity.MerchanCategorySearchActivity
import com.HKSHOPU.hk.ui.main.homepage.activity.SearchActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.util.*

class CategorySingleAdapter() : RecyclerView.Adapter<CategorySingleAdapter.CategoryLinearHolder>() {

    private var selected = -1
    private var mData: ArrayList<ShopCategoryBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list: ArrayList<ShopCategoryBean>) {
        list ?: return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryLinearHolder {
        val v = parent.context.inflate(R.layout.item_shopcategory_homepage, parent, false)

        return CategoryLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
//    var intentClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: CategoryLinearHolder, position: Int) {
        val viewHolder: CategoryLinearHolder = holder
        val item = mData.get(position)
        viewHolder.title.text = item.c_shop_category
        var image_url = ApiConstants.IMG_HOST + item.selected_shop_category_icon
        Glide.with(viewHolder.image)
            .load(image_url)
            .apply(RequestOptions.centerCropTransform())
            .into(viewHolder.image)

        holder.itemView.setOnClickListener {

            MMKV.mmkvWithID("http")
                .putString("keyword",item.c_shop_category)
                .putString("product_category_id", item.id)
                .putString("sub_product_category_id", "")

            val intent = Intent(holder.itemView.context, SearchActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, bean: ItemData)
    }

    inner class CategoryLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.find<TextView>(R.id.tv_shopcategory)
        var image = itemView.find<ImageView>(R.id.iv_shopcategory_sel)

    }


}