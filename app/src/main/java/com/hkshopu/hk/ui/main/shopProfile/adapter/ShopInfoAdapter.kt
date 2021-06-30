package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ShopListBean
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.widget.view.click
import com.kaelli.niceratingbar.NiceRatingBar



import org.jetbrains.anko.find
import java.util.*

class ShopInfoAdapter : RecyclerView.Adapter<ShopInfoAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<ShopListBean> = ArrayList()

    var itemClick : ((id: String) -> Unit)? = null
    var deleteClick : ((id: String) -> Unit)? = null

    private var cancel_inner:Boolean = false

    fun setData(list : ArrayList<ShopListBean>){
        list?:return
        this.mData = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_shopmanage,parent,false)

        return ShopInfoLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(cancel: Boolean){
        cancel_inner =cancel
        this.notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val container:RelativeLayout = itemView.find<RelativeLayout>(R.id.container_shop_list_item)
//        val container_params:RelativeLayout.LayoutParams = itemView.find<RelativeLayout>(R.id.container_shop_list_item).layoutParams as RelativeLayout.LayoutParams
        val image = itemView.find<ImageView>(R.id.iv_Icon)
        val title = itemView.find<TextView>(R.id.tv_shopName)
        val ratingBar = itemView.find<NiceRatingBar>(R.id.ratingBar)
        val merchantNums = itemView.find<TextView>(R.id.tv_MerchantNums)
        val score = itemView.find<TextView>(R.id.tv_shopScore)
        val follower = itemView.find<TextView>(R.id.tv_LikeNums)
        val income = itemView.find<TextView>(R.id.tv_IncomeNums)
        val delete = itemView.find<ImageView>(R.id.iv_cancel)
        val dummy = itemView.find<ImageView>(R.id.iv_dummy)
        fun bindShop(bean : ShopListBean){

//            if(position.equals(mData.size-1)){
//                setMargin(itemView.context, container, container_params,
//                    15,0,15,24)
//            }

            container.click {
                itemClick?.invoke(bean.id)
            }

            image.loadNovelCover(bean.shop_icon)
            title.text = bean.shop_title
            merchantNums .text = bean.product_count
            follower.text = bean.follower
            score.text = bean.rating
            income.text = bean.income
            if (cancel_inner) {
                delete.visibility = View.VISIBLE
                dummy.visibility = View.VISIBLE
            } else {
                delete.visibility = View.GONE
                dummy.visibility = View.GONE
            }
            delete.click {
//                removeAt(absoluteAdapterPosition)
                deleteClick?.invoke(bean.id)
            }

        }
    }

//    fun setMargin(con: Context, view: RelativeLayout, params: ViewGroup.LayoutParams,
//                  dp_l:Int, dp_t: Int, dp_r:Int, dp_b:Int) {
//        val scale: Float = con.getResources().getDisplayMetrics().density
//        // convert the DP into pixel
//        val pixel_l = (dp_l * scale + 0.5f).toInt()
//        val pixel_t = (dp_t * scale + 0.5f).toInt()
//        val pixel_r = (dp_r * scale + 0.5f).toInt()
//        val pixel_b = (dp_b * scale + 0.5f).toInt()
//        val s = params as ViewGroup.MarginLayoutParams
//        s.setMargins(pixel_l , pixel_t, pixel_r, pixel_b)
//        view.setLayoutParams(params)
//    }

}