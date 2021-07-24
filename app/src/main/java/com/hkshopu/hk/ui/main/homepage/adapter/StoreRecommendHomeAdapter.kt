package com.HKSHOPU.hk.ui.main.homepage.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserInfo

import com.HKSHOPU.hk.data.bean.ShopRecommendHomeBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.click
import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response


import org.jetbrains.anko.find
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class StoreRecommendHomeAdapter(var user_id: String) : RecyclerView.Adapter<StoreRecommendHomeAdapter.ShopRecommendLinearHolder>(){
    private var mData: ArrayList<ShopRecommendHomeBean> = ArrayList()
    var itemClick : ((id: String) -> Unit)? = null
    var followClick : ((id: String, follow: String) -> Unit)? = null
    private var follow_inner = ""
    fun setData(list : ArrayList<ShopRecommendHomeBean>){
        list?:return
        this.mData = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopRecommendLinearHolder {
        val v = parent.context.inflate(R.layout.item_store_recommend,parent,false)

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

    override fun onBindViewHolder(holder: ShopRecommendLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ShopRecommendLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val container = itemView.find<RelativeLayout>(R.id.layout_shopbg)
        val pic1 = itemView.find<ImageView>(R.id.iv_shop_pic1)
        val pic2 = itemView.find<ImageView>(R.id.iv_shop_pic2)
        val pic3 = itemView.find<ImageView>(R.id.iv_shop_pic3)
        val picUser = itemView.find<ImageView>(R.id.iv_user_pic)
        val shopCare = itemView.find<ImageView>(R.id.iv_shopcare)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val evaluate = itemView.find<TextView>(R.id.tv_eveluatenumber)
        var followed_status = "N"

        fun bindShop(homeBean : ShopRecommendHomeBean){
            itemView.click {
                itemClick?.invoke(homeBean.shop_id)

            }
            picUser.loadNovelCover(homeBean.shop_icon)
            shopname.text = homeBean.shop_title
            evaluate.text = homeBean.shop_average_ratings.toString()
            if(homeBean.product_pics.size == 1) {

                val pic1_path = homeBean.product_pics[0]

                pic1.loadNovelCover(pic1_path)

            }else if(homeBean.product_pics.size == 2){
                val pic1_path = homeBean.product_pics[0]
                val pic2_path = homeBean.product_pics[1]

                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)

            }else if(homeBean.product_pics.size == 3){
                val pic1_path = homeBean.product_pics[0]
                val pic2_path = homeBean.product_pics[1]
                val pic3_path = homeBean.product_pics[2]
                pic1.loadNovelCover(pic1_path)
                pic2.loadNovelCover(pic2_path)
                pic3.loadNovelCover(pic3_path)
            }
                Log.d("StoreRecommendAdapter", "資料 shop_followed：" + homeBean.shop_followed)

            followed_status = homeBean.shop_followed
            if(followed_status.equals("Y")){
                shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
//                shopCare.click {
//                    followClick?.invoke(homeBean.shop_id,"N")
//                    shopCare.setImageResource(R.mipmap.ic_addtakecare)
//                }
            }else{
                shopCare.setImageResource(R.mipmap.ic_addtakecare)
//                shopCare.click {
//                    followClick?.invoke(homeBean.shop_id,"Y")
//                    shopCare.setImageResource(R.mipmap.ic_addtakecare_en)
//                }
            }

            shopCare.click {
                if (user_id!!.isEmpty()) {
                    val intent = Intent(itemView.context, OnBoardActivity::class.java)
                    itemView.context.startActivity(intent)
                }else{
                    if(followed_status.equals("Y")){
                        doStoreFollow(user_id, homeBean.shop_id, "N")
                    }else{
                        doStoreFollow(user_id, homeBean.shop_id, "Y")
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