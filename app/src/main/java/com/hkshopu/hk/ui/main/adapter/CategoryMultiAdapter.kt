package com.hkshopu.hk.ui.main.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.widget.view.click
import org.jetbrains.anko.find
import java.util.*


class CategoryMultiAdapter : RecyclerView.Adapter<CategoryMultiAdapter.CategoryLinearHolder>(){
    private var mData: ArrayList<ShopCategoryBean> = ArrayList()


    var itemClick: ((bean: ShopCategoryBean) -> Unit)? = null

    fun setData(list: ArrayList<ShopCategoryBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryLinearHolder {
        val v = parent.context.inflate(R.layout.item_shopcategory, parent, false)

        return CategoryLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: CategoryLinearHolder, position: Int) {
        val viewHolder: CategoryLinearHolder = holder
        val item = mData.get(position)
        viewHolder.title.text = item.c_shop_category
        viewHolder.itemView.isSelected = item.isSelect
        var image_url = ApiConstants.API_HOST+item.unselected_shop_category_icon
        var image_sel_url = ApiConstants.API_HOST+item.selected_shop_category_icon
        Log.d("ShopCategoryAdapter", "ImageUrl：" + image_url)
        Glide.with(viewHolder.image_un)
            .load(image_url)
            .apply(RequestOptions.centerCropTransform())
            .into(viewHolder.image_un)

        Glide.with(viewHolder.image_sel)
            .load(image_sel_url)
            .apply(RequestOptions.centerCropTransform())
            .into(viewHolder.image_sel)
        viewHolder.itemView.click {
            if(viewHolder.image_un.visibility == View.VISIBLE){
                viewHolder.image_un.visibility = View.INVISIBLE
                viewHolder.image_sel.visibility = View.VISIBLE
                item.isSelect = true
                itemClick?.invoke(item)
            }else{
                viewHolder.image_un.visibility = View.VISIBLE
                viewHolder.image_sel.visibility = View.INVISIBLE
                item.isSelect = false
                itemClick?.invoke(item)
            }
        }

    }


    inner class CategoryLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.find<TextView>(R.id.tv_shopcategory)
        val image_un = itemView.find<ImageView>(R.id.iv_shopcategory_un)
        val image_sel = itemView.find<ImageView>(R.id.iv_shopcategory_sel)


    }



}