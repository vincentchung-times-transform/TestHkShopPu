package com.HKSHOPU.hk.ui.main.productBuyer.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventBuyerDetailedProductNewProDetailedFragment
import com.HKSHOPU.hk.component.EventChangeShopCategory
import com.HKSHOPU.hk.data.bean.ProductDetailedPageForBuyer_RecommendedProductsBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity

import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.click
import com.squareup.picasso.Picasso
import com.tencent.mmkv.MMKV
import okhttp3.Response


import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LikeProductAdapter(var product_type: String, var activity: Activity) : RecyclerView.Adapter<LikeProductAdapter.ShopInfoLinearHolder>(){

    var itemClick : ((id: String) -> Unit)? = null

    var MMKV_product_id: String = "1"
    var mutablelist_buyerProductsBean: MutableList<ProductDetailedPageForBuyer_RecommendedProductsBean> = mutableListOf()

    fun setData(list : MutableList<ProductDetailedPageForBuyer_RecommendedProductsBean>){
        list?:return
        this.mutablelist_buyerProductsBean = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_like_product,parent,false)

        return ShopInfoLinearHolder(v)

    }

    override fun getItemCount(): Int {
        return mutablelist_buyerProductsBean.size
    }
    fun removeAt(position: Int) {
        mutablelist_buyerProductsBean.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mutablelist_buyerProductsBean.get(position)
        holder.bindShop(item)

        holder.itemView.setOnClickListener{

            MMKV_product_id = mutablelist_buyerProductsBean.get(holder.adapterPosition).product_id
            MMKV.mmkvWithID("http").putString("ProductId", MMKV_product_id)

            itemClick?.invoke(MMKV_product_id)

            RxBus.getInstance().post(EventBuyerDetailedProductNewProDetailedFragment(MMKV_product_id))

//            val intent = Intent(holder.itemView.context, ProductDetailedPageBuyerViewActivity::class.java)
//            activity.finish()
//
//            holder.itemView.context?.startActivity(intent)

        }

    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.layout_like_product)
        val img_product = itemView.find<ImageView>(R.id.img_product)
        val tv_product_name = itemView.find<TextView>(R.id.tv_product_name)
        val tv_shop_name = itemView.find<TextView>(R.id.tv_shop_name)
        val tv_unit = itemView.find<TextView>(R.id.tv_unit)
        val price = itemView.find<TextView>(R.id.tv_price)
        val img_like = itemView.find<ImageView>(R.id.img_like)
        var like = "N"
        val cardvView_detailed_product = itemView.find<CardView>(R.id.cardvView_detailed_product)


        fun bindShop(bean : ProductDetailedPageForBuyer_RecommendedProductsBean){

            Picasso.with(itemView.context).load(bean.pic_path).into(img_product)
            tv_product_name.setText(bean.product_title)
            tv_shop_name.setText(bean.shop_title)

            if(product_type.equals("recommended")){
                tv_shop_name.visibility = View.VISIBLE
            }else{
                tv_shop_name.visibility = View.GONE
            }

            if(bean.product_price.toString().equals("-1")){
                price.setText("${bean.min_price}~${bean.max_price}")
            }else{
                price.setText(bean.product_price.toString())
            }

            val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
            val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

            if(width.equals(1080)){

                val params_container: ViewGroup.LayoutParams = container.getLayoutParams()
                var width_scaling =  (width*96)/368

                params_container.width = width_scaling
                container.setLayoutParams(params_container)

                val params_layout_product: ViewGroup.LayoutParams = cardvView_detailed_product.getLayoutParams()
                params_layout_product.width = width_scaling
                params_layout_product.height = (width_scaling*84)/96
                cardvView_detailed_product.setLayoutParams(params_layout_product)
            }

            img_like.click {

                if(like.equals("Y")){
                    img_like.setImageResource(R.mipmap.ic_heart_colorless)
                    like = "N"
                }else{
                    img_like.setImageResource(R.mipmap.ic_heart_colorful)
                    like = "Y"
                }

                var MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "")

                doLikeProductForBuyer("25", bean.product_id, like)


            }

        }

        private fun doLikeProductForBuyer (user_id: String , product_id: String, like: String) {

            val url = ApiConstants.API_HOST+"product/like_product/"
            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {


                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        Log.d("doLikeProductForBuyer", "返回資料 resStr：" + resStr)
                        Log.d("doLikeProductForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
                        val ret_val = json.get("ret_val")
                        if (ret_val.equals("商品收藏成功!")) {

                            val jsonArray: JSONArray = json.getJSONArray("data")
                            Log.d("doLikeProductForBuyer", "返回資料 jsonArray：" + jsonArray.toString())


                            itemView.context.runOnUiThread {
                                Toast.makeText(
                                    itemView.context,
                                    ret_val.toString(),
                                    Toast.LENGTH_SHORT).show()

                            }


                        }else{


                            itemView.context.runOnUiThread {
                                Toast.makeText(
                                    itemView.context,
                                    ret_val.toString(),
                                    Toast.LENGTH_SHORT).show()

                            }

                        }



                    } catch (e: JSONException) {


                    } catch (e: IOException) {
                        e.printStackTrace()

                    }
                }

                override fun onErrorResponse(ErrorResponse: IOException?) {

                }
            })
            web.doLikeProductForBuyer(url, user_id, product_id, like)
        }
    }




}