package com.hkshopu.hk.ui.main.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.data.bean.ProductCategoryBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.utils.rxjava.RxBus
import com.squareup.picasso.Picasso
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

class ProductCategoryItemAdapter: RecyclerView.Adapter<ProductCategoryItemAdapter.mViewHolder>()  {

    var product_category_list = mutableListOf<ProductCategoryBean>()
    var last_position = 0

    //categoryItem基本資料變數宣告
    var id : Int = 0
    lateinit var c_product_category : String
    lateinit var e_product_category : String
    lateinit var unselected_product_category_icon_image_url : String
    lateinit var selected_product_category_icon_image_url : String
    lateinit var product_category_background_color : String
    var product_category_seq : Int = 0
    lateinit var is_delete : String
    lateinit var created_at : String
    lateinit var updated_at : String
    var isSelect : Boolean = false


    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        //把layout檔的元件們拉進來，指派給當地變數
        val item_unselected_icon = itemView.findViewById<ImageView>(R.id.img_unselected_icon)
        val item_selected_icon = itemView.findViewById<ImageView>(R.id.img_selected_icon)
        val item_txt = itemView.findViewById<TextView>(R.id.tv_shopcategory)
        val tv_dot = itemView.findViewById<TextView>(R.id.tv_dot)

        init {

            tv_dot.isVisible = false

        }

        fun bind(item: ProductCategoryBean){

        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.item_shopcategory, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = product_category_list.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        val category_item = product_category_list.get(position)

        //categoryItem基本資料變數設值
        id = category_item.id
        c_product_category = category_item.c_product_category
        e_product_category = category_item.e_product_category
        unselected_product_category_icon_image_url = ApiConstants.IMG_HOST + category_item.unselected_product_category_icon
        selected_product_category_icon_image_url = ApiConstants.IMG_HOST + category_item.selected_product_category_icon
        Log.d("checkImageUrl", unselected_product_category_icon_image_url.toString())
        Log.d("checkImageUrl", selected_product_category_icon_image_url.toString())
        product_category_background_color = category_item.product_category_background_color
        product_category_seq = category_item.product_category_seq
        is_delete = category_item.is_delete
        created_at = category_item.created_at
        updated_at = category_item.updated_at
        isSelect = category_item.isSelect

        //綁定當地變數與dataModel中的每個值
        holder.item_txt.setText(category_item.c_product_category)
        Picasso.with(holder.item_unselected_icon.context).load(selected_product_category_icon_image_url).into(holder.item_unselected_icon)
        Picasso.with(holder.item_selected_icon.context).load(unselected_product_category_icon_image_url).into(holder.item_selected_icon)

        //預設第一個項目為selected
        if(position == last_position) {

            holder.item_selected_icon.visibility = View.VISIBLE
            holder.item_unselected_icon.visibility = View.INVISIBLE
            holder.tv_dot.isVisible = true
            holder.item_txt.setTextColor(Color.parseColor("#"+ product_category_list.get(position).product_category_background_color))
            holder.tv_dot.setTextColor(Color.parseColor("#"+ product_category_list.get(position).product_category_background_color))

        }else{

            holder.item_selected_icon.visibility = View.INVISIBLE
            holder.item_unselected_icon.visibility = View.VISIBLE
            holder.tv_dot.isVisible = false
            holder.item_txt.setTextColor(Color.parseColor("#C4C4C4"))

        }

        holder.itemView.setOnClickListener {

            val category_item_selected = product_category_list.get(position)
            var c_product_category_selected = category_item_selected.c_product_category


            var selected_item_id = holder.adapterPosition + 1
            RxBus.getInstance().post(EventProductCatSelected(selected_item_id, c_product_category_selected))

            if(position != last_position) {

                holder.item_selected_icon.visibility = View.VISIBLE
                holder.item_unselected_icon.visibility = View.INVISIBLE
                holder.tv_dot.isVisible = true
                holder.item_txt.setTextColor(Color.parseColor("#"+ product_category_list.get(position).product_category_background_color))
                holder.tv_dot.setTextColor(Color.parseColor("#"+ product_category_list.get(position).product_category_background_color))

                notifyItemChanged(last_position)
                last_position = position

            }

        }

    }


    //更新資料用
    fun updateList(list:MutableList<ProductCategoryBean>) {
        product_category_list = list


    }
    fun onItemDissmiss(position: Int) {
        product_category_list.removeAt(position)
        notifyItemRemoved(position)

    }


}