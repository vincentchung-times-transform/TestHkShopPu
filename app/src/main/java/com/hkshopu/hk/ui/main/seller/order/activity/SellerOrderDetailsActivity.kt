package com.HKSHOPU.hk.ui.main.seller.order.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerOrderDetail_Adapter
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.UpComingDialogFragment
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


class SellerOrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitySellerOrderdetailBinding


    private val adapter = BuyerOrderDetail_Adapter()
    var orderId =""
    var OrderNumberValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerOrderdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarSellerOrderDetail.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.VISIBLE

        var bundle = intent.getBundleExtra("bundle")
        orderId = bundle!!.getString("order_id").toString()
        var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()

        initView()
        initClick()
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
        binding.buttomForPendingDelever.setOnClickListener {
            val intent = Intent(this, ShippingNotificationActivity::class.java)
            var bundle = Bundle()
            bundle.putString("order_id", orderId)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

        binding.buttomForOrderCanceled.setOnClickListener {
            UpComingDialogFragment().show(
                getSupportFragmentManager(),
                "MyCustomFragment"
            )
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun initRecyclerView(){
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
    }

    private fun doGetData(order_id: String) {

        Log.d("doGetSaleOrderDetail", "order_id: ${order_id.toString()}")
        var url = ApiConstants.API_HOST + "user/"+order_id +"/sale_order_detail/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list_product = ArrayList<OrderProductBean>()
                list_product.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doGetSaleOrderDetail", "返回資料 resStr：" + resStr)
                    Log.d("doGetSaleOrderDetail", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("doGetSaleOrderDetail", "返回資料 jsonObject：" + jsonObject.toString())

                        val state = jsonObject.getString("status")

                        val myOrderBean: MyOrderBean =
                            Gson().fromJson(jsonObject.toString(), MyOrderBean::class.java)
                        val jsonArray_product: JSONArray = jsonObject.getJSONArray("productList")
                        for (i in 0 until jsonArray_product.length()) {
                            val jsonObject_product: JSONObject = jsonArray_product.getJSONObject(i)
                            val orderProductBean: OrderProductBean =
                                Gson().fromJson(jsonObject_product.toString(), OrderProductBean::class.java)
                            list_product.add(orderProductBean)
                        }

                        //        待發貨 - Pending Delivery
                        //        待收貨 - Pending Good Receive
                        //        訂單已完成 - Completed
                        //        已取消 - Cancelled
                        //        退貨/退款 - Refunded
                        if(state.equals("Pending Delivery")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.tobedelivered))
                                binding.tvShipnumber.visibility = View.VISIBLE
                                binding.tvNumber.visibility = View.GONE

                                binding.buttomArea.visibility = View.VISIBLE
                                binding.buttomForPendingDelever.visibility = View.VISIBLE
                                binding.buttomForPendingReceive.visibility = View.GONE
                                binding.buttomForOrderCompleted.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE

                                binding.layoutOrderNumber.visibility = View.GONE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.GONE
                                binding.layoutExpectedArrivalTime.visibility = View.GONE
                                binding.layoutCompleteTime.visibility = View.GONE
                                binding.layoutCancelTime.visibility = View.GONE

                            }
                        }else if(state.equals("Pending Good Receive")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.tobereceived))

                                binding.tvShipnumber.visibility = View.VISIBLE
                                binding.tvNumber.visibility = View.VISIBLE

                                binding.buttomArea.visibility = View.GONE
                                binding.buttomForPendingDelever.visibility = View.GONE
                                binding.buttomForPendingReceive.visibility = View.VISIBLE
                                binding.buttomForOrderCompleted.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE


                                binding.layoutOrderNumber.visibility = View.VISIBLE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.VISIBLE
                                binding.layoutExpectedArrivalTime.visibility = View.VISIBLE
                                binding.layoutCompleteTime.visibility = View.GONE
                                binding.layoutCancelTime.visibility = View.GONE
                            }
                        }else if(state.equals("Completed")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.sales_completed))
                                binding.tvShipnumber.visibility = View.VISIBLE
                                binding.tvNumber.visibility = View.VISIBLE

                                binding.buttomArea.visibility = View.VISIBLE
                                binding.buttomForPendingDelever.visibility = View.GONE
                                binding.buttomForPendingReceive.visibility = View.GONE
                                binding.buttomForOrderCompleted.visibility = View.VISIBLE
                                binding.btnReviewsPublishColorful.visibility = View.VISIBLE
                                binding.btnReviewsViewing.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE

                                binding.layoutOrderNumber.visibility = View.VISIBLE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.VISIBLE
                                binding.layoutExpectedArrivalTime.visibility = View.VISIBLE
                                binding.layoutCompleteTime.visibility = View.VISIBLE
                                binding.layoutCancelTime.visibility = View.GONE
                            }
                        }else if(state.equals("Cancelled")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.sales_tab4))
                                binding.buttomArea.visibility = View.VISIBLE
                                binding.buttomForPendingDelever.visibility = View.GONE
                                binding.buttomForPendingReceive.visibility = View.GONE
                                binding.buttomForOrderCompleted.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE

                                binding.layoutOrderNumber.visibility = View.VISIBLE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.GONE
                                binding.layoutExpectedArrivalTime.visibility = View.GONE
                                binding.layoutCompleteTime.visibility = View.GONE
                                binding.layoutCancelTime.visibility = View.VISIBLE
                            }
                        }

                        adapter.setData(list_product)
                        runOnUiThread {
                            binding.tvStatus.setText(myOrderBean.shop_message_title)
                            binding.tvReceive.setText(myOrderBean.shop_message_content)

                            binding.tvLogistic.text = myOrderBean.shipment_info
                            binding.tvNumber.setText(myOrderBean.waybill_number)

                            binding.tvBuyername.text = myOrderBean.name_in_address
                            binding.tvBuyerphone.text = myOrderBean.phone
                            binding.tvBuyeraddress.text = myOrderBean.full_address

                            binding.ivStore.load(myOrderBean.shop_icon)
                            binding.tvStoreName.text = myOrderBean.shop_title
                            binding.tvSubtotal.setText("HKD$ ${myOrderBean.subtotal.toString()}")
                            binding.tvShippingFee.setText("HKD$ ${myOrderBean.shipment_price.toString()}")
                            val total_amount= myOrderBean.subtotal + myOrderBean.shipment_price
                            binding.tvTotal.setText("HKD$ ${total_amount.toString().toString()}")

                            binding.tvOrderNumberValue.text = myOrderBean.order_number
                            OrderNumberValue = myOrderBean.order_number



                            try {


                                if(myOrderBean.payment_at.isNotEmpty()){
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    val payment_at: Date = format.parse(myOrderBean.payment_at)
                                    var payment_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(payment_at)
                                    binding.tvPaytime.text = payment_at_result.toString()
                                }

                                if(myOrderBean.estimated_deliver_at.isNotEmpty()){
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    val estimated_deliver_at: Date = format.parse(myOrderBean.estimated_deliver_at)
                                    var estimated_deliver_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(estimated_deliver_at)
                                    binding.tvExpectedArrivalTimeValue.setText(estimated_deliver_at_result.toString())
                                }
                                if(myOrderBean.actual_post_at.isNotEmpty()){
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    val estimated_deliver_at: Date = format.parse(myOrderBean.actual_post_at)
                                    var estimated_deliver_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(estimated_deliver_at)
                                    binding.tvDeliveryTimeValue.setText(estimated_deliver_at_result.toString())
                                }
//
                                if(myOrderBean.actual_finished_at.isNotEmpty()){
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    val actual_finished_at: Date = format.parse(myOrderBean.actual_finished_at)
                                    var actual_finished_at_result =  SimpleDateFormat("dd/MM/yyyy HH:mm").format(actual_finished_at)
                                    binding.tvCompleteTimeValue.setText(actual_finished_at_result.toString())
                                }


                            } catch (e: ParseException) {
                                // TODO Auto-generated catch block
                                e.printStackTrace()
                            }


                            binding.progressBarSellerOrderDetail.visibility = View.GONE
                            binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE


                            initRecyclerView()
                        }


                    }else{
                        runOnUiThread {
                            binding.progressBarSellerOrderDetail.visibility = View.GONE
                            binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetSaleOrderDetail_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarSellerOrderDetail.visibility = View.GONE
                        binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetSaleOrderDetail_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarSellerOrderDetail.visibility = View.GONE
                        binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetSaleOrderDetail_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarSellerOrderDetail.visibility = View.GONE
                    binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                }
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
