package com.HKSHOPU.hk.ui.main.advertisement.adapter


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
import com.HKSHOPU.hk.data.bean.AdvertisementBean
import com.HKSHOPU.hk.data.bean.KeywordAdBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.advertisement.activity.*
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

class AdvertisementAdapter (var type: String): RecyclerView.Adapter<AdvertisementAdapter.TopProductLinearHolder>(){

    var shop_id = ""

    private var mData: ArrayList<KeywordAdBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()

    private var like_inner = ""

    var order_status = "Pending Delivery"

    fun setShopId(id:String){
        this.shop_id = id
    }

    fun setData(list : ArrayList<KeywordAdBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<KeywordAdBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_advertisement,parent,false)

        return TopProductLinearHolder(v)
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


    fun clear() {
        val size = mData.size
        mData.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: TopProductLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class TopProductLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_ad_icon = itemView.find<ImageView>(R.id.iv_ad_icon)
        val tv_ad_name = itemView.find<TextView>(R.id.tv_ad_name)
        val tv_after_days_over_value = itemView.find<TextView>(R.id.tv_after_days_over_value)
        val iv_keyword = itemView.find<ImageView>(R.id.iv_keyword)
        val tv_keyword = itemView.find<TextView>(R.id.tv_keyword)
        val tv_keyword_value = itemView.find<TextView>(R.id.tv_keyword_value)
        val tv_expenditure_value = itemView.find<TextView>(R.id.tv_expenditure_value)
        val layout_ad_statusBtn = itemView.find<RelativeLayout>(R.id.layout_ad_statusBtn)
        val iv_ad_status = itemView.find<ImageView>(R.id.iv_ad_status)
        val tv_ad_status = itemView.find<TextView>(R.id.tv_ad_status)
        val iv_ad_edit = itemView.find<ImageView>(R.id.iv_ad_edit)

        fun bindShop(advertisementBean : KeywordAdBean){

            renderAdType(type, advertisementBean, advertisementBean.status)

            tv_after_days_over_value.setText(advertisementBean.count_down)
            tv_keyword_value.setText(advertisementBean.keyword_count)
            tv_expenditure_value.setText(advertisementBean.budget_amount)

            //current_status : 現在的狀態 (running、reviewing、editable)

            layout_ad_statusBtn.setOnClickListener {

                when(advertisementBean.status){
                    "running"->{
                        doUpdateAdStatus(advertisementBean, advertisementBean.ad_header_id, advertisementBean.status, type, adapterPosition)
                    }
                    "reviewing"->{
                        when(type){
                            "product_keyword"->{
                                val intent = Intent(itemView.context, AddEditProductKeywordAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "product_recommended"->{
                                val intent = Intent(itemView.context, AddEditProductRecommendedAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "product_market"->{
                                val intent = Intent(itemView.context, AddEditProductMarketAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "store_keyword"->{
                                val intent = Intent(itemView.context, AddEditStoreKeywordAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "store_recommended"->{
                                val intent = Intent(itemView.context, AddEditStoreRecommendedAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "store_market"->{
                                val intent = Intent(itemView.context, AddEditStoreMarketAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                        }

                    }
                    "editable"->{
                        when(type){
                            "product_keyword"->{
                                val intent = Intent(itemView.context, AddEditProductKeywordAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "product_recommended"->{
                                val intent = Intent(itemView.context, AddEditProductRecommendedAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "product_market"->{
                                val intent = Intent(itemView.context, AddEditProductMarketAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "store_keyword"->{
                                val intent = Intent(itemView.context, AddEditStoreKeywordAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "store_recommended"->{
                                val intent = Intent(itemView.context, AddEditStoreRecommendedAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                            "store_market"->{
                                val intent = Intent(itemView.context, AddEditStoreMarketAdvertisementActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("shopId", shop_id)
                                bundle.putString("mode", "edit")
                                bundle.putString("adId", advertisementBean.ad_header_id)
                                intent.putExtra("bundle", bundle)
                                itemView.context.startActivity(intent)
                            }
                        }

                    }
                }

            }

        }

        fun renderAdType(type:String, bean: KeywordAdBean,status: String){
            when(type){
                "product_keyword" ->{
                    iv_ad_icon.loadNovelCover(bean.product_pic)
                    tv_ad_name.setText(bean.product_title)

                    iv_keyword.visibility = View.VISIBLE
                    tv_keyword.visibility = View.VISIBLE
                    tv_keyword_value.visibility = View.VISIBLE
                    when(status){
                        "running"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_purple_7b61ff)
                            iv_ad_status.visibility = View.VISIBLE
                            tv_ad_status.setText(itemView.context.getText(R.string.pause))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "reviewing"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "editable"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.edit))
                            iv_ad_edit.visibility = View.VISIBLE
                        }
                    }
                }
                "store_keyword" ->{
                    iv_ad_icon.loadNovelCover(bean.shop_icon)
                    tv_ad_name.setText(bean.shop_title)

                    iv_keyword.visibility = View.VISIBLE
                    tv_keyword.visibility = View.VISIBLE
                    tv_keyword_value.visibility = View.VISIBLE
                    when(status){
                        "running"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                            iv_ad_status.visibility = View.VISIBLE
                            tv_ad_status.setText(itemView.context.getText(R.string.pause))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "reviewing"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "editable"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.edit))
                            iv_ad_edit.visibility = View.VISIBLE
                        }
                    }
                }
                "product_recommended" ->{
                    iv_ad_icon.loadNovelCover(bean.product_pic)
                    tv_ad_name.setText(bean.product_title)

                    iv_keyword.visibility = View.INVISIBLE
                    tv_keyword.visibility = View.INVISIBLE
                    tv_keyword_value.visibility = View.INVISIBLE
                    when(status){
                        "running"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_purple_7b61ff)
                            iv_ad_status.visibility = View.VISIBLE
                            tv_ad_status.setText(itemView.context.getText(R.string.pause))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "reviewing"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "editable"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.edit))
                            iv_ad_edit.visibility = View.VISIBLE
                        }
                    }
                }
                "store_recommended" ->{
                    iv_ad_icon.loadNovelCover(bean.shop_icon)
                    tv_ad_name.setText(bean.shop_title)

                    iv_keyword.visibility = View.INVISIBLE
                    tv_keyword.visibility = View.INVISIBLE
                    tv_keyword_value.visibility = View.INVISIBLE
                    when(status){
                        "running"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                            iv_ad_status.visibility = View.VISIBLE
                            tv_ad_status.setText(itemView.context.getText(R.string.pause))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "reviewing"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "editable"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.edit))
                            iv_ad_edit.visibility = View.VISIBLE
                        }
                    }
                }
                "product_market" ->{
                    iv_ad_icon.loadNovelCover(bean.product_pic)
                    tv_ad_name.setText(bean.product_title)

                    iv_keyword.visibility = View.INVISIBLE
                    tv_keyword.visibility = View.INVISIBLE
                    tv_keyword_value.visibility = View.INVISIBLE
                    when(status){
                        "running"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_purple_7b61ff)
                            iv_ad_status.visibility = View.VISIBLE
                            tv_ad_status.setText(itemView.context.getText(R.string.pause))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "reviewing"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "editable"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.edit))
                            iv_ad_edit.visibility = View.VISIBLE
                        }
                    }
                }
                "store_market" ->{
                    iv_ad_icon.loadNovelCover(bean.shop_icon)
                    tv_ad_name.setText(bean.shop_title)

                    iv_keyword.visibility = View.INVISIBLE
                    tv_keyword.visibility = View.INVISIBLE
                    tv_keyword_value.visibility = View.INVISIBLE
                    when(status){
                        "running"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                            iv_ad_status.visibility = View.VISIBLE
                            tv_ad_status.setText(itemView.context.getText(R.string.pause))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "reviewing"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                            iv_ad_edit.visibility = View.GONE
                        }
                        "editable"->{
                            layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                            iv_ad_status.visibility = View.GONE
                            tv_ad_status.setText(itemView.context.getText(R.string.edit))
                            iv_ad_edit.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }


        fun doUpdateAdStatus(
            bean: KeywordAdBean,
            ad_header_id: String,
            current_status: String,
            type:String,
            adapter_position: Int)
        {
            Log.d("doUpdateAdStatus", "ad_header_id: ${ad_header_id}, current_status: ${current_status}")
            //current_status : 現在的狀態 (running、reviewing、editable)
            val url = ApiConstants.API_HOST+"user/updateAdStatus/"

            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {
                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        val ret_val = json.get("ret_val")
                        Log.d("doUpdateAdStatus", "返回資料 resStr：" + resStr)
                        Log.d("doUpdateAdStatus", "返回資料 ret_val：" + json.get("ret_val"))

                        if (ret_val.equals("廣告狀態更新成功")) {

                            bean.status = "reviewing"
                            runOnUiThread {
                                when(type){
                                    "product_keyword"->{
                                        layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                        iv_ad_status.visibility = View.GONE
                                        tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                                        iv_ad_edit.visibility = View.GONE
                                        notifyItemChanged(adapter_position)
                                    }
                                    "store_keyword"->{
                                        layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                        iv_ad_status.visibility = View.GONE
                                        tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                                        iv_ad_edit.visibility = View.GONE
                                        notifyItemChanged(adapter_position)
                                    }
                                    "product_recommended"->{
                                        layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                        iv_ad_status.visibility = View.GONE
                                        tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                                        iv_ad_edit.visibility = View.GONE
                                        notifyItemChanged(adapter_position)
                                    }
                                    "store_recommended"->{
                                        layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                        iv_ad_status.visibility = View.GONE
                                        tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                                        iv_ad_edit.visibility = View.GONE
                                        notifyItemChanged(adapter_position)
                                    }
                                    "product_market"->{
                                        layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                        iv_ad_status.visibility = View.GONE
                                        tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                                        iv_ad_edit.visibility = View.GONE
                                        notifyItemChanged(adapter_position)
                                    }
                                    "store_market"->{
                                        layout_ad_statusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                        iv_ad_status.visibility = View.GONE
                                        tv_ad_status.setText(itemView.context.getText(R.string.reviewing))
                                        iv_ad_edit.visibility = View.GONE
                                        notifyItemChanged(adapter_position)
                                    }
                                }


                                Toast.makeText(itemView.context, ret_val.toString(), Toast.LENGTH_SHORT).show()

                            }
                            Log.d("doUpdateAdStatus", "ret_val: ${ret_val.toString()}")
                        } else {
                            runOnUiThread {
                                Toast.makeText(itemView.context, ret_val.toString(), Toast.LENGTH_SHORT).show()

                            }
                            Log.d("doUpdateAdStatus", "ret_val: ${ret_val.toString()}")
                        }

                    } catch (e: JSONException) {
                        runOnUiThread {
                            Toast.makeText(itemView.context, "網路異常", Toast.LENGTH_SHORT).show()
                            Log.d("doUpdateAdStatus", "JSONException: ${e.toString()}")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(itemView.context, "網路異常", Toast.LENGTH_SHORT).show()
                            Log.d("doUpdateAdStatus", "IOException: ${e.toString()}")
                        }
                    }
                }

                override fun onErrorResponse(ErrorResponse: IOException?) {
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(itemView.context, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateAdStatus", "ErrorResponse: ${ErrorResponse.toString()}")
                    }
                }
            })
            web.doUpdateAdStatus(
                url,
                ad_header_id,
                current_status,
            )
        }
    }
}

