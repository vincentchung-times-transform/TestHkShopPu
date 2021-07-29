package com.HKSHOPU.hk.ui.main.seller.notification.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.NotificationMessageBean
import com.HKSHOPU.hk.data.bean.SalerSaleListBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.notification.adapter.NotificationMessageAdapter
import com.HKSHOPU.hk.ui.main.seller.order.adapter.CompletedOrderAdapter
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class NotificationFragment : Fragment() {

    companion object {
        fun newInstance(): NotificationFragment {
            val args = Bundle()
            val fragment = NotificationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var progressBar: ProgressBar
    lateinit var imgView_loading_background: ImageView
    lateinit var recyclerview :RecyclerView
    private val adapter = NotificationMessageAdapter()
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerview = v.find<RecyclerView>(R.id.recyclerview)
        progressBar = v.find<ProgressBar>(R.id.progressBar)
        imgView_loading_background = v.find<ImageView>(R.id.imgView_loading_background)
        progressBar.visibility = View.GONE
        imgView_loading_background.visibility = View.GONE

        val shopId= MMKV.mmkvWithID("http").getString("ShopId", "")

        getNotificationMessages(shopId!!)

        initView()

        return v
    }

    private fun initView(){

    }

    private fun initRecyclerView(){

        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = adapter

    }

    private fun getNotificationMessages(shop_id: String) {
        Log.d("getNotificationMessages", "shop_id: ${shop_id}")
        var url = ApiConstants.API_HOST + "user/sale_list/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<NotificationMessageBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getNotificationMessages", "返回資料 resStr：" + resStr)
                    Log.d("getNotificationMessages", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("訂單資訊取得成功")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getNotificationMessages", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val notificationMessageBean: NotificationMessageBean =
                                Gson().fromJson(jsonObject.toString(), NotificationMessageBean::class.java)
                            list.add(notificationMessageBean)
                        }

                    }

                    Log.d("getNotificationMessages", "返回資料 list：" + list.toString())

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
                    Log.d("getNotificationMessages", "getSellerSaleList: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getNotificationMessages", "getSellerSaleList: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getNotificationMessages", "getSellerSaleList: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        })
        web.Do_getNotificationMessages(url, shop_id)
    }

}