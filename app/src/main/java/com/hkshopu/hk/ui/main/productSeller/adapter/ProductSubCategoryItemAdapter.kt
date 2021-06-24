package com.HKSHOPU.hk.ui.main.productSeller.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ProductChildCategoryBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.ui.main.productSeller.activity.AddNewProductActivity
import com.HKSHOPU.hk.ui.main.productSeller.activity.EditProductActivity
import com.squareup.picasso.Picasso
import com.tencent.mmkv.MMKV

class ProductSubCategoryItemAdapter(var activity: BaseActivity, var mode: String): RecyclerView.Adapter<ProductSubCategoryItemAdapter.mViewHolder>()  {


    var product_child_category_list  = mutableListOf<ProductChildCategoryBean>()

    //categoryItem基本資料變數宣告
    var id : String = ""
    var product_category_id : String = ""
    lateinit var c_product_category :String
    lateinit var c_product_sub_category : String
    lateinit var e_product_sub_category : String
    lateinit var unselected_product_sub_category_icon_image_url : String
    lateinit var selected_product_sub_category_icon_image_url : String
    lateinit var product_sub_category_background_color : String
    lateinit var product_sub_category_seq : String
    lateinit var is_delete : String
    lateinit var created_at : String
    lateinit var updated_at : String

    lateinit var selected_datas : Map<String, String>
    var c_name : String = "女生衣著"



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



        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.item_products_category, parent, false)

        return mViewHolder(example)

    }

    override fun getItemCount() = product_child_category_list.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        val sub_category_item = product_child_category_list.get(position)


        //categoryItem基本資料變數設值
        id = sub_category_item.id
        product_category_id = sub_category_item.product_category_id
        c_product_category = c_name
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

            val sub_category_item_selected = product_child_category_list.get(position)

            var id_selected = sub_category_item_selected.id
            var product_category_id_selected = sub_category_item_selected.product_category_id
            var c_product_category_selected = c_name
            var c_product_sub_category_selected = sub_category_item_selected.c_product_sub_category

            MMKV.mmkvWithID("addPro").putString(
                "product_sub_category_id",
                id_selected.toString()
            )
            MMKV.mmkvWithID("addPro").putString(
                "product_category_id",
                product_category_id_selected.toString().toString()
            )
            MMKV.mmkvWithID("addPro").putString(
                "value_textViewSeletedCategory",
                c_product_category_selected + ">" + c_product_sub_category_selected
            )


            MMKV.mmkvWithID("editPro").putString(
                "product_sub_category_id",
                id_selected.toString()
            )
            MMKV.mmkvWithID("editPro").putString(
                "product_category_id",
                product_category_id_selected.toString().toString()
            )
            MMKV.mmkvWithID("editPro").putString(
                "value_textViewSeletedCategory",
                c_product_category_selected + ">" + c_product_sub_category_selected
            )



            when(mode){
                "add"->{
                    var currentActivity: Activity = activity
                    val intent = Intent(currentActivity, AddNewProductActivity::class.java)
                    currentActivity.startActivity(intent)
                    activity.finish()
                }
                "edit"->{
                    var currentActivity: Activity = activity
                    val intent = Intent(currentActivity, EditProductActivity::class.java)
                    currentActivity.startActivity(intent)
                    activity.finish()

                }
            }




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

    fun get_selected_sub_category(): Map<String, String> {

        return selected_datas

    }

    fun set_c_name(c_name: String) {
        this.c_name = c_name
    }


}