package com.HKSHOPU.hk.ui.main.wallet.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TransactionDetailedInfoForAdActivity : BaseActivity() {
    private lateinit var binding: ActivityTransactionDetailedInfoForAdBinding
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "")

    private val adapter = BuyerOrderDetail_Adapter()
    var hkdDollarSign = ""
    var mutablelist_addValue : ArrayList<String> = arrayListOf()
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    var selected_payment_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailedInfoForAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hkdDollarSign = getString(com.HKSHOPU.hk.R.string.hkd_dollarSign)
        var order_number = intent.getBundleExtra("bundle")!!.getString("orderNumber").toString()

        if (userId != null) {
            getNotificationItemCount(userId)
        }
        getWalletHistoryDetailRead(order_number)

        initView()
        initClick()

    }

    private fun initView() {
//        initAddVlueSpinner()
    }
    private fun initClick() {
        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(this, ShopNotifyActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
//        binding.btnFps.setOnClickListener {
//            getCheckFpsStatus(orderId)
//        }
    }

    private fun initRecyclerView(){

    }

    fun getWalletHistoryDetailRead(
        order_number: String,
    )
    {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        Log.d("getWalletHistoryDetailRead", "order_number: ${order_number}")
        val url = ApiConstants.API_SWAGGER+"wallet/history/detail/${order_number}"


        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {

                var resStr: String? = ""
                var code: Int = 0
                try {
                    resStr = response.body()!!.string()
                    code = response.code()

                    val jsonObject = JSONObject(resStr)
                    Log.d("getWalletHistoryDetailRead", "返回資料 resStr：" + resStr)
                    Log.d("getWalletHistoryDetailRead", "返回資料 code：" + code.toString())

                    if (code == 200) {

                        val addValueHistoryBean: AddValueHistoryBean =
                            Gson().fromJson(jsonObject.toString(), AddValueHistoryBean::class.java)

                        runOnUiThread {
                            binding.tvDesc.setText(addValueHistoryBean.description.toString())
                            binding.ivProductIcon.setImageResource(com.HKSHOPU.hk.R.mipmap.transaction_product_keyword_ad)
                            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            val actual_finished_at: Date = format.parse(addValueHistoryBean.created_at.toString())
                            var actual_finished_at_result =  SimpleDateFormat("dd/MM/yyyy").format(actual_finished_at)
                            binding.tvDate.setText(actual_finished_at_result.toString())

                            binding.tvStoredValue.setText(addValueHistoryBean.change.toString().replace("-", ""))
                        }

                        runOnUiThread {
//                            binding.tvSubtotalValue.setText("${hkdDollarSign}${addValueHistoryBean.change.toString()}")
//                            binding.tvTotalValue.setText("${hkdDollarSign}${addValueHistoryBean.change.toString()}")
                            binding.tvAdvertisingId.setText(addValueHistoryBean.order_number.toString())
                        }

                        runOnUiThread {

                            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            val paid_at: Date = format.parse(addValueHistoryBean.created_at.toString())
                            var paid_at_result =  SimpleDateFormat("dd/MM/yyyy").format(paid_at)
                            val complete_at: Date = format.parse(addValueHistoryBean.updated_at.toString())
                            var complete_at_result =  SimpleDateFormat("dd/MM/yyyy").format(complete_at)
                            binding.tvCommercialTime.setText("${paid_at_result} - ${complete_at_result}")
                        }


                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@TransactionDetailedInfoForAdActivity, "Wallet Detail Get Failed", Toast.LENGTH_SHORT)
                                .show()

                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@TransactionDetailedInfoForAdActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletHistoryDetailRead", "JSONException: ${e.toString()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@TransactionDetailedInfoForAdActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletHistoryDetailRead", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@TransactionDetailedInfoForAdActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("getWalletHistoryDetailRead", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.Get_Data(
            url
        )
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