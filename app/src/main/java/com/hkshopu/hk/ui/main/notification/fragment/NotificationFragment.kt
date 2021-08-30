package com.HKSHOPU.hk.ui.main.notification.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventOrderCompelete
import com.HKSHOPU.hk.data.bean.NotificationMessageBean
import com.HKSHOPU.hk.data.bean.SalerSaleListBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseListCompeleteActivity
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseListDeliverActivity
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseListPendingPaymentActivity
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseListRecieveActivity
import com.HKSHOPU.hk.ui.main.notification.adapter.NotificationMessageAdapter
import com.HKSHOPU.hk.ui.main.seller.order.activity.SellerOrderDetailsActivity
import com.HKSHOPU.hk.ui.main.seller.order.adapter.CompletedOrderAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.paypal.pyplcheckout.sca.runOnUiThread
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
    private var _myTag: String? = null
    var message_for_buyer_or_seller = ""
    val userId= MMKV.mmkvWithID("http").getString("UserId", "")

    fun setMyTag(value: String) {
        if ("" == value) return
        _myTag = value
    }
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

        if(_myTag.equals(userId)) {
            message_for_buyer_or_seller = "user"
            getNotificationMessages(userId!!)
        }else{
            message_for_buyer_or_seller = "buyer"
            getNotificationMessages_shop(_myTag!!)
        }

        initView()

        return v
    }

    private fun initView(){

    }

    private fun initRecyclerView(){

        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = adapter

        adapter.itemClick = { id: String, id_notification: String, order_status: String ->
            if(_myTag.equals(userId)) {
//                待付款 - Pending Payment
//                待發貨 - Pending Delivery
//                待收貨 - Pending Good Receive
//                訂單已完成 - Completed
//                已取消 - Cancelled
//                退貨/退款 - Refunded
                when(order_status){
                    "Pending Payment"->{
                        val intent = Intent(requireActivity(), BuyerPurchaseListPendingPaymentActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("order_id",id)
                        intent.putExtra("bundle", bundle)
                        requireActivity().startActivity(intent)
                    }
                    "Pending Delivery"->{
                        val intent = Intent(requireActivity(), BuyerPurchaseListDeliverActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("order_id",id)
                        intent.putExtra("bundle", bundle)
                        requireActivity().startActivity(intent)
                    }
                    "Pending Good Receive"->{
                        val intent = Intent(requireActivity(), BuyerPurchaseListRecieveActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("order_id",id)
                        intent.putExtra("bundle", bundle)
                        requireActivity().startActivity(intent)
                    }
                    "Completed"->{
                        val intent = Intent(requireActivity(), BuyerPurchaseListCompeleteActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("order_id",id)
                        intent.putExtra("bundle", bundle)
                        requireActivity().startActivity(intent)
                    }
                    "Cancelled"->{

                    }
                    "Refunded"->{

                    }
                }


                var url = ApiConstants.API_HOST + "user_detail/click_notification_buyer/"
                Do_NotificationClicked(url, id_notification)
            }else{
                val intent = Intent(requireActivity(), SellerOrderDetailsActivity::class.java)
                val bundle = Bundle()
                bundle.putString("order_id",id)
                intent.putExtra("bundle", bundle)
                requireActivity().startActivity(intent)

                var url = ApiConstants.API_HOST + "user_detail/click_notification_shop/"
                Do_NotificationClicked(url, id_notification)
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        fragmentManager!!.beginTransaction().remove((this as Fragment?)!!)
            .commitAllowingStateLoss()

    }
    private fun getNotificationMessages(user_id: String) {
        Log.d("getNotificationMessages", "buyer_id: ${user_id}")
        var url = ApiConstants.API_HOST + "user_detail/"+user_id+"/notification_buyer/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<NotificationMessageBean>()
                list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getNotificationMessages", "返回資料 resStr：" + resStr)
                    Log.d("getNotificationMessages", "返回資料 ret_val：" + json.get("ret_val"))
                    val status = json.get("status")
                    if (status == 0) {

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
        web.Get_Data(url)
    }

    private fun getNotificationMessages_shop(shop_id: String) {

        var url = ApiConstants.API_HOST + "user_detail/"+shop_id+"/notification_shop/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<NotificationMessageBean>()
                list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getNotificationMessages_shop", "返回資料 resStr：" + resStr)
                    Log.d("getNotificationMessages_shop", "返回資料 ret_val：" + json.get("ret_val"))
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getNotificationMessages_shop", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val notificationMessageBean: NotificationMessageBean =
                                Gson().fromJson(jsonObject.toString(), NotificationMessageBean::class.java)
                            list.add(notificationMessageBean)
                        }

                    }

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
                    Log.d("getNotificationMessages_shop", "getSellerSaleList: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getNotificationMessages_shop", "getSellerSaleList: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getNotificationMessages_shop", "getSellerSaleList: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }
    private fun Do_NotificationClicked(url:String,notification_id: String) {
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_NotificationClicked", "返回資料 resStr：" + resStr)
                    Log.d("Do_NotificationClicked", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                    } else {
                        runOnUiThread {
                            Toast.makeText(requireActivity(), ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_NotificationClicked", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_NotificationClicked", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_NotificationClicked", "onErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_NotificationClicked(
            url,notification_id
        )
    }
}