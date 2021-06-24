package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ShopProductBean
import com.HKSHOPU.hk.ui.main.productSeller.activity.ProductDetailForSalerActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.math.BigDecimal
import java.util.*

class ShopProductAdapter(var fragment: Fragment) : RecyclerView.Adapter<ShopProductAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<ShopProductBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null

    var MMKV_product_id: String = ""

    fun setData(list : ArrayList<ShopProductBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_products,parent,false)

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

        holder.itemView.setOnClickListener{

            MMKV_product_id = mData.get(holder.adapterPosition).id
            MMKV.mmkvWithID("http").putString("ProductId", MMKV_product_id)

            val intent = Intent(fragment.context, ProductDetailForSalerActivity::class.java)
            fragment.context?.startActivity(intent)

        }

    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.layout_new_product)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val price = itemView.find<TextView>(R.id.tv_currentnumber)
        val sold = itemView.find<TextView>(R.id.tv_sold)
        val amount = itemView.find<TextView>(R.id.tv_amount)
        val heart = itemView.find<TextView>(R.id.tv_heart)
        val eye = itemView.find<TextView>(R.id.tv_eye)
        val cardvView_shop_product = itemView.find<CardView>(R.id.cardvView_shop_product)

        fun bindShop(bean : ShopProductBean){

            container.click {
                itemClick?.invoke(bean.id)
            }

            MMKV_product_id = bean.id
            image.loadNovelCover(bean.pic_path)
            title.text = bean.product_title

            if(bean.product_price.equals(-1)){
                price.text = "${bean.min_price}-${bean.max_price}"
            }else{
                price.text = bean.product_price.toString()
            }



            if(bean.sold_quantity.toString().length>=3){
                var one_thous = 1000
                var float = bean.sold_quantity.toDouble()/one_thous.toDouble()
                var bigDecimal = float.toBigDecimal()
                sold.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
            }else{
                sold.text = "${bean.sold_quantity.toString()}"
            }

            if(bean.sum_quantity.toString().length>=3){
                var one_thous = 1000
                var float = bean.sum_quantity.toDouble()/one_thous.toDouble()
                var bigDecimal = float.toBigDecimal()
                amount.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
            }else{
                amount.text =  "${bean.sum_quantity.toString()}"
            }

            if(bean.like.toString().length>=3){
                var one_thous = 1000
                var float = bean.like.toDouble()/one_thous.toDouble()
                var bigDecimal = float.toBigDecimal()
                heart.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
            }else{
                heart.text =  "${bean.like.toString()}"
            }

            if(bean.seen.toString().length>=3){
                var one_thous = 1000
                var float = bean.seen.toDouble()/one_thous.toDouble()
                var bigDecimal = float.toBigDecimal()
                eye.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
            }else{
                eye.text =  "${bean.seen.toString()}"
            }

            val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
            val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

            if(width.equals(1080)){

                val params_container: ViewGroup.LayoutParams = container.getLayoutParams()
                var width_scaling =  (width*168)/375

                params_container.width = width_scaling
                container.setLayoutParams(params_container)

                val params_layout_product: ViewGroup.LayoutParams = cardvView_shop_product.getLayoutParams()
                params_layout_product.width = width_scaling
                params_layout_product.height = (width_scaling*18)/21
                cardvView_shop_product.setLayoutParams(params_layout_product)
            }

        }
    }

}