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
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.data.bean.StoreFollowBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerFollowListActivity
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

class StoreFollowAdapter(var userId: String) : RecyclerView.Adapter<StoreFollowAdapter.ShopFollowLinearHolder>(){
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
        val shopCare = itemView.find<ImageView>(R.id.iv_shopcare)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val ratings = itemView.find<TextView>(R.id.tv_rating)
        val ratingBar = itemView.find<NiceRatingBar>(R.id.ratingBar)
        val follows = itemView.find<TextView>(R.id.tv_attentionnumber)
        var followed_status = "Y"
        fun bindShop(storeFollowBean : StoreFollowBean){
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

            if(followed_status.equals("Y")){
                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
            }else{
                shopCare.setImageResource(R.mipmap.ic_addtakecare)
            }

            shopCare.click {
                if (userId!!.isNullOrEmpty()) {
                    val intent = Intent(itemView.context, OnBoardActivity::class.java)
                    itemView.context.startActivity(intent)
                }else{
                    if(followed_status.equals("Y")){
                        doStoreFollow(userId, storeFollowBean.shop_id, "N")
                    }else{
                        doStoreFollow(userId,  storeFollowBean.shop_id, "Y")
                    }
                }
            }
//            if(follow_inner.equals("Y")){
//                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
//            }else{
//                shopCare.setImageResource(R.mipmap.ic_addtakecare)
//            }
        }

        private fun doStoreFollow(userId: String, shop_id: String, follow: String) {
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
                                if (follow.equals("Y")) {
                                    shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
                                    followed_status = "Y"
                                } else {
                                    shopCare.setImageResource(R.mipmap.ic_addtakecare)
                                    followed_status = "N"
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