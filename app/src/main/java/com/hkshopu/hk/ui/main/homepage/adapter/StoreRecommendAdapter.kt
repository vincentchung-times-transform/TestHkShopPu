package com.HKSHOPU.hk.ui.main.homepage.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.data.bean.ShopRecommendBean
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

class StoreRecommendAdapter(var user_id: String) : RecyclerView.Adapter<StoreRecommendAdapter.ShopRecommendLinearHolder>(){
    private var mData: ArrayList<ShopRecommendBean> = ArrayList()
    private var newData: ArrayList<ShopRecommendBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var followClick : ((id: String, follow: String) -> Unit)? = null
    private var follow_inner = ""
    fun setData(list : ArrayList<ShopRecommendBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<ShopRecommendBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopRecommendLinearHolder {
        val v = parent.context.inflate(R.layout.item_store_ranking,parent,false)

        return ShopRecommendLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(follow: String){
        follow_inner =follow
        this.notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        val size = mData.size
        mData.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: ShopRecommendLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopRecommendLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.container)
        val iv_user_view= itemView.find<RelativeLayout>(R.id.iv_user_view)
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
        var followed_status = "N"
        var iv_forground_frame = itemView.find<ImageView>(R.id.iv_forground_frame)

        fun bindShop(shopRecommendBean : ShopRecommendBean){

            followed_status = shopRecommendBean.followed
            when(shopRecommendBean.identity){
                "尊榮"->{

                    when(shopRecommendBean.background_is_show){
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
                    }
                    when(shopRecommendBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_honor)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(shopRecommendBean.frame_is_show){
                        "Y"->{
                            iv_forground_frame.visibility = View.VISIBLE
                            iv_forground_frame.setImageResource(R.mipmap.frame_sponsor_honor)

                            setMarginFromDpToPixel(itemView,  iv_forground_frame, 0, 0, 0, 0)
                        }
                        "N"->{
                            iv_forground_frame.visibility = View.GONE
                        }
                    }

                }
                "至尊"->{
                    when(shopRecommendBean.background_is_show){
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
                    }
                    when(shopRecommendBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_supreme)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(shopRecommendBean.frame_is_show){
                        "Y"->{
                            iv_forground_frame.visibility = View.VISIBLE
                            iv_forground_frame.setImageResource(R.mipmap.frame_sponsor_supreme)

                            setMarginFromDpToPixel(itemView,  iv_forground_frame, 0, 0, 0, 0)
                        }
                        "N"->{
                            iv_forground_frame.visibility = View.GONE
                        }
                    }
                }
                "榮耀"->{
                    when(shopRecommendBean.background_is_show){
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
                    }
                    when(shopRecommendBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_glory)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(shopRecommendBean.frame_is_show){
                        "Y"->{
                            iv_forground_frame.visibility = View.VISIBLE
                            iv_forground_frame.setImageResource(R.mipmap.frame_sponsor_glory)

                            setMarginFromDpToPixel(itemView,  iv_forground_frame, 0, 0, 0, 0)
                        }
                        "N"->{
                            iv_forground_frame.visibility = View.GONE
                        }
                    }
                }
                "卓越"->{

                    when(shopRecommendBean.background_is_show){
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
                    }
                    when(shopRecommendBean.badge_is_show){
                        "Y"->{
                            iv_badge.visibility = View.VISIBLE
                            iv_badge.setImageResource(R.mipmap.badge_sponsor_excel)
                        }
                        "N"->{
                            iv_badge.visibility = View.GONE
                        }
                    }
                    when(shopRecommendBean.frame_is_show){
                        "Y"->{
                            iv_forground_frame.visibility = View.VISIBLE
                            iv_forground_frame.setImageResource(R.mipmap.frame_sponsor_excel)

                            setMarginFromDpToPixel(itemView,  iv_forground_frame, 0, 0, 0, 0)
                        }
                        "N"->{
                            iv_forground_frame.visibility = View.GONE
                        }
                    }
                }
                else->{
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
            }

            itemView.click {
                itemClick?.invoke(shopRecommendBean.shop_id.toString())
            }
            picUser.loadNovelCover(shopRecommendBean.shop_icon)
            shopname.text = shopRecommendBean.shop_name
            follows.text = shopRecommendBean.follower_count.toString()

                val pic1_path = shopRecommendBean.pic_path_1
                val pic2_path = shopRecommendBean.pic_path_2
                val pic3_path = shopRecommendBean.pic_path_3
                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)
                pic3.loadNovelCover(pic3_path)
            ratings.text = shopRecommendBean.rating.toString()
            ratingBar.setRating(shopRecommendBean.rating.toFloat())



            btn_shopcare.click {
                if (user_id!!.isEmpty()) {
                    val intent = Intent(itemView.context, OnBoardActivity::class.java)
                    itemView.context.startActivity(intent)
                }else{
                    if(followed_status.equals("Y")){
                        doStoreFollow(user_id, shopRecommendBean.shop_id, "N", shopRecommendBean)
                    }else{
                        doStoreFollow(user_id, shopRecommendBean.shop_id, "Y", shopRecommendBean)
                    }
                }
            }

//            if(follow_inner.equals("Y")){
//                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
//            }else{
//                shopCare.setImageResource(R.mipmap.ic_addtakecare)
//            }
        }
        fun setMarginFromDpToPixel(
            context: View, view: View, dp_left: Int,
            dp_top: Int, dp_right: Int, dp_bottom: Int,  ){
            val scale: Float =
                context.getResources().getDisplayMetrics().density
            // convert the DP into pixel
            val pixel_left = (dp_left * scale + 0.5f).toInt()
            val pixel_top = (dp_top * scale + 0.5f).toInt()
            val pixel_right = (dp_right * scale + 0.5f).toInt()
            val pixel_bottom = (dp_bottom * scale + 0.5f).toInt()

            val s = view.layoutParams as ViewGroup.MarginLayoutParams
            s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom)
            view.setLayoutParams(s)
        }


        private fun doStoreFollow(userId: String, shop_id: String, follow: String,  bean: ShopRecommendBean) {
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
                                        }
                                    }
                                    else->{
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
                                }
                            }
                            RxBus.getInstance().post(EventRefreshUserInfo())
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