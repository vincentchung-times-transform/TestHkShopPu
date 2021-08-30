package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshHomepage
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.data.bean.StoreFollowBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.click
import com.kaelli.niceratingbar.NiceRatingBar
import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response


import org.jetbrains.anko.find
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class BuyerProfile_StoreFollowAdapter(var userId: String) : RecyclerView.Adapter<BuyerProfile_StoreFollowAdapter.ShopFollowLinearHolder>(){
    private var mData: ArrayList<StoreFollowBean> = ArrayList()
    private var newData: ArrayList<StoreFollowBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var followClick : ((id: String, follow: String) -> Unit)? = null
//    private var follow_inner = ""
    fun setData(list : ArrayList<StoreFollowBean>){
        list?:return
        mData.clear()
        mData.addAll(list)
        newData = mData
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<StoreFollowBean>) {
        list?:return
        newData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopFollowLinearHolder {
        val v = parent.context.inflate(R.layout.item_store_ranking,parent,false)

        return ShopFollowLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return newData.size
    }
    //更新資料用
//    fun updateData(follow: String){
//        follow_inner =follow
//        this.notifyDataSetChanged()
//    }

    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        val size = newData.size
        newData.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: ShopFollowLinearHolder, position: Int) {
        val item = newData.get(position)
        holder.bindShop(item)
    }

    inner class ShopFollowLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<LinearLayout>(R.id.layout_shopbg)
        val pic1 = itemView.find<ImageView>(R.id.iv_shop_pic1)
        val pic2 = itemView.find<ImageView>(R.id.iv_shop_pic2)
        val pic3 = itemView.find<ImageView>(R.id.iv_shop_pic3)
        val picUser = itemView.find<ImageView>(R.id.iv_user_pic)
        val btn_shopcare = itemView.find<LinearLayout>(R.id.btn_shopcare)
        val tv_add = itemView.find<TextView>(R.id.tv_add)
        val tv_attention_status = itemView.find<TextView>(R.id.tv_attention_status)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val ratings = itemView.find<TextView>(R.id.tv_rating)
        val ratingBar = itemView.find<NiceRatingBar>(R.id.ratingBar)
        val follows = itemView.find<TextView>(R.id.tv_attentionnumber)
        val iv_badge = itemView.find<ImageView>(R.id.iv_badge)
        var followed_status = "Y"
        fun bindShop(storeFollowBean : StoreFollowBean){

            when(storeFollowBean.identity){
                "尊榮"->{

                    when(storeFollowBean.background_is_show){
                        "Y"->{
                            container.setBackgroundResource(R.drawable.sponsor_honorable_gradual_bg_8dp)
                            if(followed_status.equals("Y")){
                                btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                tv_add.visibility = View.GONE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                            }else{
                                btn_shopcare.setBackgroundResource(R.drawable.sponsor_honorable_gradual_btn_bg_8dp)
                                tv_add.visibility = View.VISIBLE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))
                            }

                        }
                        "N"->{
                            container.setBackgroundResource(R.drawable.customborder_addproduct)
                        }
                    }
                    when(storeFollowBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_honor)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(storeFollowBean.frame_is_show){
                        "Y"->{

                        }
                        "N"->{

                        }
                    }

                }
                "至尊"->{
                    when(storeFollowBean.background_is_show){
                        "Y"->{
                            container.setBackgroundResource(R.drawable.sponsor_supreme_gradual_bg_8dp)
                            if(followed_status.equals("Y")){
                                btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                tv_add.visibility = View.GONE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                            }else{
                                btn_shopcare.setBackgroundResource(R.drawable.sponsor_supreme_gradual_btn_bg_8dp)
                                tv_add.visibility = View.VISIBLE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))
                            }

                        }
                        "N"->{
                            container.setBackgroundResource(R.drawable.customborder_addproduct)
                        }
                    }
                    when(storeFollowBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_supreme)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(storeFollowBean.frame_is_show){
                        "Y"->{

                        }
                        "N"->{

                        }
                    }
                }
                "榮耀"->{
                    when(storeFollowBean.background_is_show){
                        "Y"->{
                            container.setBackgroundResource(R.drawable.sponsor_glory_bg_8dp)
                            if(followed_status.equals("Y")){
                                btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                tv_add.visibility = View.GONE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                            }else{
                                btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                                tv_add.visibility = View.VISIBLE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))
                            }

                        }
                        "N"->{
                            container.setBackgroundResource(R.drawable.customborder_addproduct)
                        }
                    }
                    when(storeFollowBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_glory)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(storeFollowBean.frame_is_show){
                        "Y"->{

                        }
                        "N"->{

                        }
                    }
                }
                "卓越"->{

                    when(storeFollowBean.background_is_show){
                        "Y"->{
                            container.setBackgroundResource(R.drawable.sponsor_excellence_bg_8dp)
                            if(followed_status.equals("Y")){
                                btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                tv_add.visibility = View.GONE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                            }else{
                                btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                                tv_add.visibility = View.VISIBLE
                                tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))
                            }
                        }
                        "N"->{
                            container.setBackgroundResource(R.drawable.customborder_addproduct)
                        }
                    }
                    when(storeFollowBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_excel)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(storeFollowBean.frame_is_show){
                        "Y"->{

                        }
                        "N"->{

                        }
                    }
                }

            }

            container.click {
                itemClick?.invoke(storeFollowBean.shop_id.toString())
            }
            picUser.loadNovelCover(storeFollowBean.shop_icon)
            shopname.text = storeFollowBean.shop_title
            follows.text = storeFollowBean.follow_count.toString()

            if(storeFollowBean.shop_pic.size==1){
                val pic1_path = storeFollowBean.shop_pic.get(0)
                pic1.loadNovelCover(pic1_path)
            }else if(storeFollowBean.shop_pic.size==2){
                val pic1_path = storeFollowBean.shop_pic.get(0)
                val pic2_path = storeFollowBean.shop_pic.get(1)
                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)
            }else if(storeFollowBean.shop_pic.size==3){
                val pic1_path = storeFollowBean.shop_pic.get(0)
                val pic2_path = storeFollowBean.shop_pic.get(1)
                val pic3_path = storeFollowBean.shop_pic.get(2)
                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)
                pic3.loadNovelCover(pic3_path)
            }

            ratings.text = storeFollowBean.rating.toString()
            ratingBar.setRating(storeFollowBean.rating.toFloat())

            btn_shopcare.click {
                if (userId!!.isNullOrEmpty()) {
                    val intent = Intent(itemView.context, OnBoardActivity::class.java)
                    itemView.context.startActivity(intent)
                }else{
                    if(followed_status.equals("Y")){
                        doStoreFollow(userId, storeFollowBean.shop_id, "N", storeFollowBean)
                    }else{
                        doStoreFollow(userId,  storeFollowBean.shop_id, "Y", storeFollowBean)
                    }
                }
            }
//            if(follow_inner.equals("Y")){
//                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
//            }else{
//                shopCare.setImageResource(R.mipmap.ic_addtakecare)
//            }
        }

        private fun doStoreFollow(userId: String, shop_id: String, follow: String, bean: StoreFollowBean) {
            Log.d("doStoreFollow", "userId: ${userId} \n " +
                    "shop_id: ${shop_id} \n " +
                    "follow: ${follow}")
            val url_follow = ApiConstants.API_HOST + "user/" + userId + "/followShop/" + shop_id + "/"
            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {
                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        val ret_val = json.get("ret_val")
                        val status = json.get("status")
                        Log.d("doStoreFollow", "返回資料 resStr：" + resStr)
                        Log.d("doStoreFollow", "返回資料 ret_val：" + ret_val)

                        if (status == 0) {
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()

                                when(bean.identity){
                                    "尊榮"->{
                                        when(bean.background_is_show){
                                            "Y"->{
                                                container.setBackgroundResource(R.drawable.sponsor_honorable_gradual_bg_8dp)
                                                if (follow.equals("Y")) {
                                                    btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                                    tv_add.visibility = View.GONE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                                                    followed_status = "Y"
                                                } else {
                                                    btn_shopcare.setBackgroundResource(R.drawable.sponsor_honorable_gradual_btn_bg_8dp)
                                                    tv_add.visibility = View.VISIBLE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))

                                                    followed_status = "N"
                                                }
                                            }
                                            "N"->{
                                                container.setBackgroundResource(R.drawable.customborder_addproduct)
                                            }
                                        }
                                    }
                                    "至尊"->{
                                        when(bean.background_is_show){
                                            "Y"->{
                                                container.setBackgroundResource(R.drawable.sponsor_supreme_gradual_bg_8dp)
                                                if (follow.equals("Y")) {
                                                    btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                                    tv_add.visibility = View.GONE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                                                    followed_status = "Y"
                                                } else {
                                                    btn_shopcare.setBackgroundResource(R.drawable.sponsor_supreme_gradual_btn_bg_8dp)
                                                    tv_add.visibility = View.VISIBLE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))

                                                    followed_status = "N"
                                                }
                                            }
                                            "N"->{
                                                container.setBackgroundResource(R.drawable.customborder_addproduct)
                                            }
                                        }
                                    }
                                    "榮耀"->{
                                        when(bean.background_is_show){
                                            "Y"->{
                                                container.setBackgroundResource(R.drawable.sponsor_glory_bg_8dp)
                                                if (follow.equals("Y")) {
                                                    btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                                    tv_add.visibility = View.GONE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                                                    followed_status = "Y"
                                                } else {
                                                    btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                                                    tv_add.visibility = View.VISIBLE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))

                                                    followed_status = "N"
                                                }
                                            }
                                            "N"->{
                                                container.setBackgroundResource(R.drawable.customborder_addproduct)
                                            }
                                        }
                                    }
                                    "卓越"->{
                                        when(bean.background_is_show){
                                            "Y"->{
                                                container.setBackgroundResource(R.drawable.sponsor_excellence_bg_8dp)
                                                if (follow.equals("Y")) {
                                                    btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                                                    tv_add.visibility = View.GONE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_followed))

                                                    followed_status = "Y"
                                                } else {
                                                    btn_shopcare.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                                                    tv_add.visibility = View.VISIBLE
                                                    tv_attention_status.setText(itemView.context.getText(R.string.shop_attention))

                                                    followed_status = "N"
                                                }
                                            }
                                            "N"->{
                                                container.setBackgroundResource(R.drawable.customborder_addproduct)
                                            }
                                        }
                                    }
                                }
                            }
                            RxBus.getInstance().post(EventRefreshUserInfo())
                            RxBus.getInstance().post(EventRefreshHomepage())
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        Log.d("doStoreFollow_errorMessage", "JSONException：" + e.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("doStoreFollow_errorMessage", "IOException：" + e.toString())
                    }
                }
                override fun onErrorResponse(ErrorResponse: IOException?) {
                    Log.d("doStoreFollow_errorMessage", "onErrorResponse：" + ErrorResponse.toString())
                }
            })
            web.Store_Follow(url_follow, follow)
        }
    }
}