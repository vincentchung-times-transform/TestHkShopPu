package com.hkshopu.hk.ui.main.store.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.MyProductBean

import com.hkshopu.hk.ui.main.product.activity.EditProductActivity
import com.hkshopu.hk.ui.main.product.activity.MerchandiseActivity
import com.hkshopu.hk.ui.main.store.fragment.MerchantsOndeckFragment
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.util.*

class MyProductsAdapter(var fragment: Fragment) : RecyclerView.Adapter<MyProductsAdapter.ProductInfoLinearHolder>(){
    private var mData: ArrayList<MyProductBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null

    var MMKV_product_id: Int = 1

    fun setData(list : ArrayList<MyProductBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_my_products, parent,false)

        return ProductInfoLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ProductInfoLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)

        holder.itemView.setOnClickListener{

            MMKV.mmkvWithID("http").putInt("ProductId", MMKV_product_id)

            val intent = Intent(fragment.context, MerchandiseActivity::class.java)
            fragment.context?.startActivity(intent)
        }

        holder.btn_edit_pro.setOnClickListener {

            MMKV.mmkvWithID("http").putInt("ProductId", MMKV_product_id)

            val intent = Intent(fragment.context, EditProductActivity::class.java)
            fragment.context?.startActivity(intent)

        }
        holder.btn_draftOrActive.setOnClickListener {


        }

    }

    inner class ProductInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_Icon = itemView.find<ImageView>(R.id.iv_Icon)
        val tv_productName = itemView.find<TextView>(R.id.tv_productName)
        val tv_priceRange = itemView.find<TextView>(R.id.tv_priceRange)
        val btn_edit_pro = itemView.find<ImageView>(R.id.btn_edit_pro)
        val btn_draftOrActive = itemView.find<ImageView>(R.id.btn_draftOrActive)




        fun bindShop(bean : MyProductBean){
//            iv_Icon.click {
//                itemClick?.invoke(bean.id)
//            }

            MMKV_product_id = bean.id

            var price_range = "${bean.min_price}-${bean.max_price}"

            iv_Icon.loadNovelCover(bean.pic_path)
            tv_productName.text = bean.product_title
            tv_priceRange.text = price_range.toString()



        }
    }



}