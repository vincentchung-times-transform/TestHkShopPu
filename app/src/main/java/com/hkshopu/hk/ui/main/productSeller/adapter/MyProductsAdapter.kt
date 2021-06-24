package com.HKSHOPU.hk.ui.main.shopProfile.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.MyProductBean
import com.HKSHOPU.hk.ui.main.productSeller.activity.ProductDetailForSalerActivity
import com.HKSHOPU.hk.ui.main.productSeller.fragment.EditProductRemindForFragmentDialogFragment
import com.HKSHOPU.hk.ui.main.productSeller.fragment.ProductActiveApplyDialogFragment
import com.HKSHOPU.hk.ui.main.productSeller.fragment.ProductDeleteApplyDialogFragment
import com.HKSHOPU.hk.ui.main.productSeller.fragment.ProductDraftApplyDialogFragment
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.find
import java.util.*


class MyProductsAdapter(var fragment: Fragment, var product_type: String) : RecyclerView.Adapter<MyProductsAdapter.ProductInfoLinearHolder>(){
    private var mData: ArrayList<MyProductBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null


    var MMKV_product_id: String =  ""
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

            MMKV.mmkvWithID("http").putString("ProductId", MMKV_product_id)

            val intent = Intent(fragment.context, ProductDetailForSalerActivity::class.java)
            fragment.context?.startActivity(intent)
        }

        holder.btn_edit_pro.setOnClickListener {

            MMKV_product_id = mData.get(holder.adapterPosition).id

            MMKV.mmkvWithID("http").putString("ProductId", MMKV_product_id)

            EditProductRemindForFragmentDialogFragment(this.fragment, MMKV_product_id).show(fragment.activity!!.supportFragmentManager, "MyCustomFragment")


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
        val iv_delete_btn_space = itemView.find<ImageView>(R.id.iv_delete_btn_space)
        var container_my_product: LinearLayout = itemView.find<LinearLayout>(R.id.container_my_product)
        var container_my_product_p: LinearLayout.LayoutParams = itemView.find<LinearLayout>(R.id.container_my_product).layoutParams as LinearLayout.LayoutParams


        @SuppressLint("ResourceType")
        fun bindShop(bean : MyProductBean){


            if(position.equals(mData.size-1)){
                setMargin(itemView.context, container_my_product, container_my_product_p,
                15,0,15,16)
            }

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
                iv_delete_btn_space.visibility = View.VISIBLE


            } else {
                btn_deletePro.visibility = View.GONE
                iv_delete_btn_space.visibility = View.GONE
            }


        }

    }


    //更新資料用
    fun onOff_editStatus(status: Boolean) {
        editStatus = status
        this.notifyDataSetChanged()
    }

    fun setMargin(con: Context, view: LinearLayout, params: ViewGroup.LayoutParams,
                  dp_l:Int, dp_t: Int, dp_r:Int, dp_b:Int) {
        val scale: Float = con.getResources().getDisplayMetrics().density
        // convert the DP into pixel
        val pixel_l = (dp_l * scale + 0.5f).toInt()
        val pixel_t = (dp_t * scale + 0.5f).toInt()
        val pixel_r = (dp_r * scale + 0.5f).toInt()
        val pixel_b = (dp_b * scale + 0.5f).toInt()
        val s = params as MarginLayoutParams
        s.setMargins(pixel_l , pixel_t, pixel_r, pixel_b)
        view.setLayoutParams(params)
    }

}