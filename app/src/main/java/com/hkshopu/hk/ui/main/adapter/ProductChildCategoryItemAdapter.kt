package com.hkshopu.hk.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.data.bean.ProductCategoryBean
import com.hkshopu.hk.data.bean.ProductChildCategoryBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.utils.rxjava.RxBus
import com.squareup.picasso.Picasso
import java.util.*

class ProductChildCategoryItemAdapter: RecyclerView.Adapter<ProductChildCategoryItemAdapter.mViewHolder>()  {

    lateinit var product_child_category_list : MutableList<ProductChildCategoryBean>

    //categoryItem基本資料變數宣告
    var id : Int = 0
    var product_category_id :Int = 0
    lateinit var c_product_sub_category : String
    lateinit var e_product_sub_category : String
    lateinit var unselected_product_sub_category_icon_image_url : String
    lateinit var selected_product_sub_category_icon_image_url : String
    lateinit var product_sub_category_background_color : String
    lateinit var product_sub_category_seq : String
    lateinit var is_delete : String
    lateinit var created_at : String
    lateinit var updated_at : String



    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        //把layout檔的元件們拉進來，指派給當地變數
        val item_unselected_icon = itemView.findViewById<ImageView>(R.id.img_unselected_icon)
        val item_selected_icon = itemView.findViewById<ImageView>(R.id.img_selected_icon)
        val item_txt = itemView.findViewById<TextView>(R.id.tv_shopcategory)

        init {

        }

        fun bind(item: ProductChildCategoryBean){

            //綁定當地變數與dataModel中的每個值
            var image_url = ApiConstants.IMG_HOST + item.unselected_product_sub_category_icon
            Log.d("ShopmenuActivity", "返回資料 List：" + image_url.toString())


        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.item_shopcategory, parent, false)

        return mViewHolder(example)

    }

    override fun getItemCount() = product_child_category_list.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        val sub_category_item = product_child_category_list.get(position)


        //categoryItem基本資料變數設值
        id = sub_category_item.id
        product_category_id
        c_product_sub_category = sub_category_item.c_product_sub_category
        e_product_sub_category = sub_category_item.e_product_sub_category
        unselected_product_sub_category_icon_image_url = ApiConstants.IMG_HOST + sub_category_item.unselected_product_sub_category_icon
        selected_product_sub_category_icon_image_url = ApiConstants.IMG_HOST + sub_category_item.selected_product_sub_category_icon
        Log.d("checkImageUrl_sub", unselected_product_sub_category_icon_image_url.toString())
        Log.d("checkImageUrl_sub", selected_product_sub_category_icon_image_url.toString())
        product_sub_category_background_color = sub_category_item.product_sub_category_background_color
        product_sub_category_seq = sub_category_item.product_sub_category_seq
        is_delete = sub_category_item.is_delete
        created_at = sub_category_item.created_at
        updated_at = sub_category_item.updated_at

        //綁定當地變數與dataModel中的每個值
        holder.item_txt.setText(sub_category_item.c_product_sub_category)
        Picasso.with(holder.itemView.context).load(unselected_product_sub_category_icon_image_url).into(holder.item_unselected_icon)
        Picasso.with(holder.itemView.context).load(selected_product_sub_category_icon_image_url).into(holder.item_selected_icon)


        holder.itemView.setOnClickListener {

            var selected_item_id = holder.adapterPosition + 1
            Toast.makeText(holder.itemView.context, selected_item_id.toString(), Toast.LENGTH_SHORT).show()
//            RxBus.getInstance().post(EventProductCatSelected(selected_item_id))

        }

    }

    //更新資料用
    fun updateList(list:MutableList<ProductChildCategoryBean>) {
        product_child_category_list = list
    }
    fun onItemDissmiss(position: Int) {
        product_child_category_list.drop(position)
        notifyItemRemoved(position)

    }





//    fun LoadImageFromWebURL(url: String?): Drawable? {
//        return try {
//            val iStream = URL(url).content as InputStream
//            Drawable.createFromStream(iStream, "src name")
//        } catch (e: Exception) {
//            null
//        }
//    }



}