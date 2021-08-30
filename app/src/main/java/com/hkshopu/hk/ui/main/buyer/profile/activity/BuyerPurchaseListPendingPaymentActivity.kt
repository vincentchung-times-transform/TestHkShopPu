package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerOrderDetail_Adapter
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity

import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.utils.extension.load
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class BuyerPurchaseListPendingPaymentActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerorderdetailPendingpaymentBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()

    private val adapter = BuyerOrderDetail_Adapter()
    var orderNumber =""
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    var selected_payment_id = ""
    var orderId = ""
    var mode = "shopping"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerorderdetailPendingpaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doGetPaymentMethodList()
        initView()
        initClick()
        orderId = intent.getBundleExtra("bundle")!!.getString("order_id").toString()

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
        binding.btnFps.setOnClickListener {
            getCheckFpsStatus(orderId, "inactive")
        }
    }
    private fun initRecyclerView(){
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager

        binding.recyclerview.adapter = adapter
    }

    fun getCheckFpsStatus(order_id:String, api_calling_mode: String) {
        Log.d("getCheckFpsStatus", "orderId: ${orderId}")

        val url = ApiConstants.API_HOST+"user_detail/${order_id}/fps_check/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getCheckFpsStatus", "返回資料 resStr：" + resStr)
                    Log.d("getCheckFpsStatus", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        when(api_calling_mode){
                            "active"->{
                                runOnUiThread {
                                    binding.btnFps.visibility = View.VISIBLE
                                    binding.tvStatus2.setText(com.HKSHOPU.hk.R.string.tobepaid)
                                }
                            }
                            "inactive"->{
                                val intent = Intent(this@BuyerPurchaseListPendingPaymentActivity, FpsPayActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString("jsonTutList", "[\"${orderId}\"]")
                                bundle.putString("orderNumber", "")
                                bundle.putString("mode", mode.toString())
                                intent.putExtra("bundle", bundle)
                                this@BuyerPurchaseListPendingPaymentActivity.startActivity(intent)
                            }
                        }

                    }else if(status == -1) {
                        when(api_calling_mode){
                            "active"->{
                                runOnUiThread {
                                    binding.btnFps.visibility = View.GONE
                                    binding.tvStatus2.setText(com.HKSHOPU.hk.R.string.add_value_reviewing_status)
                                }
                            }
                            "inactive"->{
                                val intent = Intent(this@BuyerPurchaseListPendingPaymentActivity, FpsPayAuditActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString("mode", mode.toString())
                                intent.putExtra("bundle", bundle)
                                this@BuyerPurchaseListPendingPaymentActivity.startActivity(intent)
                            }
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("getCheckFpsStatus", "JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getCheckFpsStatus", "IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getCheckFpsStatus", "ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.getCheckFpsStatus(url)
    }

    private fun doGetData(order_id: String) {
        Log.d("BuyerPurchaseList_pending_payment", "order_id: ${order_id.toString()}")
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
                    Log.d("BuyerPurchaseList_pending_payment", "返回資料 resStr：" + resStr)
                    Log.d("BuyerPurchaseList_pending_payment", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonObject = json.getJSONObject("data")
                        val jsonArray_product: JSONArray = jsonObject.getJSONArray("productList")
                        val state = jsonObject.getString("status")
                        if(state.equals("Pending Payment")) {
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
                                binding.tvAmount.setText("HKD$ ${myOrderBean.subtotal.toString()}")
                                binding.tvShippingFee.setText("HKD$ ${myOrderBean.shipment_price.toString()}")
                                val total_amount= myOrderBean.subtotal + myOrderBean.shipment_price
                                binding.tvTotal.setText("HKD$ ${total_amount.toString().toString()}")

                                binding.tvOrdernumber.text = myOrderBean.order_number
                                orderNumber = myOrderBean.order_number

                                initRecyclerView()
                            }
                            adapter.setData(list_product)

                        }
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }

//                        getCheckFpsStatus(orderId, "active")
                    }


                } catch (e: JSONException) {
                    Log.d("BuyerPurchaseList_pending_payment", "JSONException：" + e)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("BuyerPurchaseList_pending_payment", "IOException：" + e)
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("BuyerPurchaseList_pending_payment", "ErrorResponse：" + ErrorResponse)
            }
        })
        web.Get_Data(url)
    }
    private fun doGetPaymentMethodList() {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST + "payment/method"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doGetPaymentMethodList", "返回資料 resStr：" + resStr)
                    Log.d("doGetPaymentMethodList", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val paymentBean: PaymentBean =
                                Gson().fromJson(jsonObject.toString(), PaymentBean::class.java)

                            mutablelist_paymentBean.add(paymentBean)
                        }

                        val payment_list: MutableList<String> = ArrayList<String>()

                        for (i in 0 until mutablelist_paymentBean.size) {
                            payment_list.add(mutablelist_paymentBean.get(i).payment_desc.toString())
                        }

                        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                            FacebookSdk.getApplicationContext(),
                            R.layout.simple_spinner_dropdown_item,
                            payment_list
                        )
                        runOnUiThread {
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.containerPaymentSpinner.setAdapter(adapter)
                            binding.containerPaymentSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long,
                                ) {
                                    selected_payment_id = mutablelist_paymentBean.get(position).id
                                    Toast.makeText(this@BuyerPurchaseListPendingPaymentActivity, """已選取"${mutablelist_paymentBean.get(position).payment_desc}"作為付款方式""", Toast.LENGTH_SHORT).show()

//                                    var item_id_list = arrayListOf<String>()
//                                    for(i in 0 until mAdapter_ShoppingCartItems.getDatas().size){
//                                        for(j in 0 until mAdapter_ShoppingCartItems.getDatas().get(i).productList.size){
//                                            item_id_list.add(mAdapter_ShoppingCartItems.getDatas().get(i).productList.get(j).product_spec.shopping_cart_item_id.toString())
//                                        }
//                                    }
//                                    var gson = Gson()
//                                    var item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

//                                    doUpdateShoppingCartitems(item_id_list_json,"","","", payment_id.toString(), mutablelist_paymentBean.get(position).payment_desc.toString())
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }
                        }

                    }else{
                        runOnUiThread {
                            Toast.makeText(this@BuyerPurchaseListPendingPaymentActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetPaymentMethodList_errorMessage", "JSONException：" + e.toString())

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetPaymentMethodList_errorMessage", "IOException：" + e.toString())

                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetPaymentMethodList_errorMessage", "ErrorResponse：" + ErrorResponse.toString())

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
