package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.MyOrderBean
import com.HKSHOPU.hk.data.bean.OrderProductBean
import com.HKSHOPU.hk.databinding.ActivityBuyerorderdetailDeliverBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerOrderDetail_Adapter
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.utils.extension.load
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BuyerPurchaseListDeliverActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerorderdetailDeliverBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()

    private val adapter = BuyerOrderDetail_Adapter()
    var orderNumber =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerorderdetailDeliverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.isVisible = true
        initView()
        initClick()
        val orderId = intent.getBundleExtra("bundle")!!.getString("order_id")
        doGetData(orderId!!)
        getNotificationItemCount(userId)
    }

    private fun initView() {

    }


    private fun initClick() {
        binding!!.layoutNotify.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivContact.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("https://www.hkshopu.com/")
            startActivity(intent)
        }
        binding.ivCancelorder.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("https://www.hkshopu.com/")
            startActivity(intent)
        }
    }
    private fun initRecyclerView(){
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
    }

    private fun doGetData(order_id: String) {
        Log.d("BuyerPurchaseList_deliverActivity", "order_id: ${order_id}")
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST + "user_detail/"+order_id +"/order_detail/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var myOrderBean: MyOrderBean = MyOrderBean()
                val list_product = ArrayList<OrderProductBean>()
                list_product.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerPurchaseList_deliverActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerPurchaseList_deliverActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonObject = json.getJSONObject("data")
                        val jsonArray_product: JSONArray = jsonObject.getJSONArray("productList")
                        val state = jsonObject.getString("status")
                        if(state.equals("Pending Delivery")) {
                            myOrderBean =
                                Gson().fromJson(jsonObject.toString(), MyOrderBean::class.java)

                            for (i in 0 until jsonArray_product.length()) {
                                val jsonObject_product: JSONObject = jsonArray_product.getJSONObject(i)
                                val orderProductBean: OrderProductBean =
                                    Gson().fromJson(jsonObject_product.toString(), OrderProductBean::class.java)
                                list_product.add(orderProductBean)
                            }

                            runOnUiThread {
                                binding.tvStatus.setText(myOrderBean.buyer_message_title)
                                binding.tvReceive.setText(myOrderBean.buyer_message_content)

                                binding.tvLogistic.text = myOrderBean.shipment_info

                                binding.tvBuyername.text = myOrderBean.name_in_address
                                binding.tvBuyerphone.text = myOrderBean.phone
                                binding.tvBuyeraddress.text = myOrderBean.full_address

                                binding.ivStore.load(myOrderBean.shop_icon)
                                binding.tvStoreName.text = myOrderBean.shop_title
                                binding.tvSubtotal.setText("HKD$ ${myOrderBean.subtotal.toString()}")
                                binding.tvShippingFee.setText("HKD$ ${myOrderBean.shipment_price.toString()}")
                                val total_amount= myOrderBean.subtotal + myOrderBean.shipment_price
                                binding.tvTotal.setText("HKD$ ${total_amount.toString().toString()}")

                                binding.tvOrdernumber.text = myOrderBean.order_number
                                orderNumber = myOrderBean.order_number


                                try {
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                                    if(myOrderBean.payment_at.isNotEmpty()){
                                        val payment_at: Date = format.parse(myOrderBean.payment_at)
                                        var payment_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(payment_at)
                                        binding.tvPaytime.text = payment_at_result.toString()
                                    }

                                } catch (e: ParseException) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace()
                                }

                                initRecyclerView()
                            }
                            adapter.setData(list_product)

                        }
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }


                } catch (e: JSONException) {
                    Log.d("BuyerPurchaseList_deliverActivity", "JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("BuyerPurchaseList_deliverActivity", "IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("BuyerPurchaseList_deliverActivity", "ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Get_Data(url)
    }

    private fun  getNotificationItemCount (user_id: String) {
        val url = ApiConstants.API_HOST+"user_detail/${user_id}/notification_count/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var notificationItemCount : String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getNotificationItemCount", "返回資料 resStr：" + resStr)
                    Log.d("getNotificationItemCount", "返回資料 ret_val：" + ret_val)
                    if (status == 0) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            notificationItemCount = jsonArray.get(i).toString()
                        }
                        Log.d(
                            "getNotificationItemCount",
                            "返回資料 jsonArray：" + notificationItemCount
                        )

                        runOnUiThread {
//                            binding!!.tvNotifycount.text = notificationItemCount
                            if(notificationItemCount!!.equals("0")){
                                binding!!.tvNotifycount.visibility = View.GONE
                            }else{
                                binding!!.tvNotifycount.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        runOnUiThread {
//                            binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: JSONException: ${e.toString()}")
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: IOException: ${e.toString()}")
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
//                    binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

}