package com.HKSHOPU.hk.ui.main.seller.order.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.SalerSaleListBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseList_deliverActivity
import com.HKSHOPU.hk.ui.main.seller.order.adapter.CompletedOrderAdapter
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopPreviewActivity
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class OderCompletedForSalesFragment : Fragment() {

    companion object {
        fun newInstance(): OderCompletedForSalesFragment {
            val args = Bundle()
            val fragment = OderCompletedForSalesFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = CompletedOrderAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_seller_sales, container, false)

        allProduct = v.find<RecyclerView>(R.id.recyclerview)
        progressBar = v.find<ProgressBar>(R.id.progressBar)
        progressBar.isVisible = true

        val shopId= MMKV.mmkvWithID("http").getString("ShopId", "")
        val status = "Completed"
        //        待付款 - Pending Payment
//        待發貨 - Pending Delivery
//        待收貨 - Pending Good Receive
//        訂單已完成 - Completed
//        已取消 - Cancelled
//        退貨/退款 - Refunded
//        getSellerSaleList(shopId!!, status)
        getSellerSaleList(shopId!!, status)

        initView()

        return v
    }

    private fun initView(){

    }


    private fun initRecyclerView(){

        val layoutManager = LinearLayoutManager(requireActivity())
        allProduct.layoutManager = layoutManager

        allProduct.adapter = adapter
//        adapter.itemClick = {
//        }

    }

    private fun getSellerSaleList (shop_id: String, order_status: String) {
        Log.d("getSellerSaleList", "shop_id: ${shop_id} \n " +
                "order_status: ${order_status}")
        val url = ApiConstants.API_HOST+"user/sale_list/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<SalerSaleListBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getSellerSaleList", "返回資料 resStr：" + resStr)
                    Log.d("getSellerSaleList", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("訂單資訊取得成功")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getSellerSaleList", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val salerSaleListBean: SalerSaleListBean =
                                Gson().fromJson(jsonObject.toString(), SalerSaleListBean::class.java)
                            list.add(salerSaleListBean)
                        }

                        adapter.setData(list)
                    }

                    Log.d("getSellerSaleList", "返回資料 list：" + list.toString())

                    if(list.size > 0){
                        adapter.setData(list)
                        activity!!.runOnUiThread {
                            initRecyclerView()
                            progressBar.visibility = View.GONE
                        }
                    }else{
                        activity!!.runOnUiThread {
                            progressBar.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getSellerSaleList: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getSellerSaleList: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getSellerSaleList: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        })
        web.Do_getSellerSaleList(url, shop_id, order_status)
    }

}