package com.HKSHOPU.hk.ui.main.buyer.profile.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshHomepage
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.data.bean.ProductLikedBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener

import com.HKSHOPU.hk.ui.main.buyer.product.activity.ProductDetailedPageBuyerViewActivity
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

class BuyerProfile_BrowsedAdapter (var currency: Currency, var user_id: String): RecyclerView.Adapter<BuyerProfile_BrowsedAdapter.ProductLikedLinearHolder>(){
    private var mData: ArrayList<ProductLikedBean> = ArrayList()
//    var itemClick : ((id: String) -> Unit)? = null
//    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""
    fun setData(list : ArrayList<ProductLikedBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductLikedLinearHolder {
        val v = parent.context.inflate(R.layout.item_top_products,parent,false)

        return ProductLikedLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(like: String){
        like_inner =like
        this.notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ProductLikedLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class ProductLikedLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.container)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val like = itemView.find<ImageView>(R.id.iv_product_like)
//        val like_click = itemView.find<ImageView>(R.id.iv_product_like_click)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val price = itemView.find<TextView>(R.id.tv_price)
        var liked_status = "N"

        fun bindShop(productLikedBean : ProductLikedBean){

            container.click {
//                itemClick?.invoke(productLikedBean.product_id)

                val intent = Intent(itemView.context, ProductDetailedPageBuyerViewActivity::class.java)
                var bundle = Bundle()
                bundle.putString("product_id", productLikedBean.product_id)
                intent.putExtra("bundle_product_id", bundle)
                itemView.context.startActivity(intent)

            }
            image.loadNovelCover(productLikedBean.pic_path)
            title .text = productLikedBean.product_title
            shopname.text = productLikedBean.shop_title
            price.text = currency.toString()+ productLikedBean.product_price.toString()

            liked_status = productLikedBean.liked

            if(liked_status.equals("Y")){
                like.setImageResource(R.mipmap.ic_heart_red)
            }else{
                like.setImageResource(R.mipmap.ic_heart_white)
            }

            like.click {
                if (user_id!!.isNullOrEmpty()) {
                    val intent = Intent(itemView.context, OnBoardActivity::class.java)
                    itemView.context.startActivity(intent)
                }else{
                    if(liked_status.equals("Y")){
                        doProductLike(user_id, productLikedBean.product_id, "N")
    //                    likeClick?.invoke(productLikedBean.product_id,"N")

                    }else{
                        doProductLike(user_id, productLikedBean.product_id, "Y")
//                        likeClick?.invoke(productLikedBean.product_id,"Y")
                    }
                }
            }
        }


        private fun doProductLike(userId: String, productId: String, like_status: String) {
            val url = ApiConstants.API_HOST + "product/like_product/"
            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {
                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        val ret_val = json.get("ret_val")
                        val status = json.get("status")
                        Log.d("doProductLike", "返回資料 resStr：" + resStr)
                        Log.d("doProductLike", "返回資料 ret_val：" + ret_val)

                        if (ret_val.equals("商品收藏成功!")) {
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                                like.setImageResource(R.mipmap.ic_heart_red)
                                liked_status = "Y"
                            }
                            RxBus.getInstance().post(EventRefreshUserInfo())
                            RxBus.getInstance().post(EventRefreshHomepage())
                        } else if(ret_val.equals("取消收藏成功")){
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                                like.setImageResource(R.mipmap.ic_heart_white)
                                liked_status = "N"
                            }
                            RxBus.getInstance().post(EventRefreshUserInfo())
                            RxBus.getInstance().post(EventRefreshHomepage())
                        }else{
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        Log.d("doProductLike_errorMessage", "JSONException：" + e.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("doProductLike_errorMessage", "IOException：" + e.toString())
                    }
                }
                override fun onErrorResponse(ErrorResponse: IOException?) {
                    Log.d("doProductLike_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                }
            })
            web.Product_Like(url, productId, userId, like_status)
        }
    }
}