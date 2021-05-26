package com.hkshopu.hk.ui.main.store.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R

import com.hkshopu.hk.data.bean.ShopProductBean
import com.hkshopu.hk.ui.main.product.activity.MerchandiseActivity
import com.hkshopu.hk.utils.extension.inflate
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.widget.view.click
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class ShopProductAdapter(var fragment: Fragment) : RecyclerView.Adapter<ShopProductAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<ShopProductBean> = ArrayList()
    var itemClick : ((id: Int) -> Unit)? = null

    var MMKV_product_id: Int = 1

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
            MMKV.mmkvWithID("http").putInt("ProductId", MMKV_product_id)

            val intent = Intent(fragment.context, MerchandiseActivity::class.java)
            fragment.context?.startActivity(intent)
        }

    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.layout_newproduct)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val price = itemView.find<TextView>(R.id.tv_currentnumber)
        val sold = itemView.find<TextView>(R.id.tv_sold)
        val amount = itemView.find<TextView>(R.id.tv_amount)
        val heart = itemView.find<TextView>(R.id.tv_heart)
        val eye = itemView.find<TextView>(R.id.tv_eye)
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


        }
    }



}