package com.hkshopu.hk.ui.main.store.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.hkshopu.hk.data.bean.MyProductBean

import com.hkshopu.hk.ui.main.product.activity.MerchandiseActivity
import com.hkshopu.hk.ui.main.product.fragment.ProductActiveApplyDialogFragment
import com.hkshopu.hk.ui.main.product.fragment.ProductDeleteApplyDialogFragment
import com.hkshopu.hk.ui.main.product.fragment.ProductDraftApplyDialogFragment
import com.hkshopu.hk.ui.main.product.fragment.EditProductRemindForFragmentDialogFragment
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.util.*

class MyProductsAdapter(var fragment: Fragment, var product_type: String) : RecyclerView.Adapter<MyProductsAdapter.ProductInfoLinearHolder>(){
    private var mData: ArrayList<MyProductBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null


    var MMKV_product_id: Int = 1
    var productType = product_type


    private var editStatus: Boolean = false

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

        holder.btn_deletePro.setOnClickListener {

            MMKV_product_id = mData.get(holder.adapterPosition).id


//            removeAt(holder.adapterPosition)
            ProductDeleteApplyDialogFragment(MMKV_product_id, productType).show(
                fragment.fragmentManager!!,
                "MyCustomFragment")

        }

        holder.itemView.setOnClickListener{

            MMKV_product_id = mData.get(holder.adapterPosition).id

            MMKV.mmkvWithID("http").putInt("ProductId", MMKV_product_id)

            val intent = Intent(fragment.context, MerchandiseActivity::class.java)
            fragment.context?.startActivity(intent)
        }

        holder.btn_edit_pro.setOnClickListener {

            MMKV_product_id = mData.get(holder.adapterPosition).id

            MMKV.mmkvWithID("http").putInt("ProductId", MMKV_product_id)

            EditProductRemindForFragmentDialogFragment(this.fragment).show(fragment.activity!!.supportFragmentManager, "MyCustomFragment")


        }
        holder.btn_draftOrActive.setOnClickListener {

            MMKV_product_id = mData.get(holder.adapterPosition).id

            when(product_type){
                "active"->{

                    ProductDraftApplyDialogFragment(MMKV_product_id, product_type, fragment).show(
                        fragment.fragmentManager!!,
                        "MyCustomFragment")



                }
                "draft"->{
                    ProductActiveApplyDialogFragment(MMKV_product_id, product_type, fragment).show(
                        fragment.fragmentManager!!,
                        "MyCustomFragment")
                }
            }

        }

    }

    inner class ProductInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_Icon = itemView.find<ImageView>(R.id.iv_Icon)
        val tv_productName = itemView.find<TextView>(R.id.tv_productName)
        val tv_priceRange = itemView.find<TextView>(R.id.tv_priceRange)
        val btn_edit_pro = itemView.find<ImageView>(R.id.btn_edit_pro)
        val btn_draftOrActive = itemView.find<ImageView>(R.id.btn_draftOrActive)
        val btn_deletePro = itemView.find<ImageView>(R.id.btn_deletePro)





        @SuppressLint("ResourceType")
        fun bindShop(bean : MyProductBean){
//            iv_Icon.click {
//                itemClick?.invoke(bean.id)
//            }

//            MMKV_product_id = bean.id

            if(bean.product_price.equals(-1)){
                tv_priceRange.text = "${bean.min_price}-${bean.max_price}"
            }else{
                tv_priceRange.text = bean.product_price.toString()
            }

            iv_Icon.loadNovelCover(bean.pic_path)
            tv_productName.text = bean.product_title

           when(product_type){
               "active"->{
                   btn_draftOrActive.setImageResource(R.mipmap.btn_draft_pros)
               }
               "draft"->{
                   btn_draftOrActive.setImageResource(R.mipmap.btn_active_pros)
               }
           }


            if (editStatus) {
                btn_deletePro.visibility = View.VISIBLE

            } else {
                btn_deletePro.visibility = View.GONE

            }


        }

    }


    //更新資料用
    fun onOff_editStatus(status: Boolean) {
        editStatus = status
        this.notifyDataSetChanged()
    }


}