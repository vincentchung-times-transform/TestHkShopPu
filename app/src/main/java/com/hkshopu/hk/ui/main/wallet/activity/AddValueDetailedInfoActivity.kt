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
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity

import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddValueDetailedInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityAddValueDetailBinding
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "")

    var hk_dollarsign = ""
    var order_id=""
    var order_number = ""
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    var selected_payment_id = ""
    var mode = "addValue"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddValueDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hk_dollarsign = getString(com.HKSHOPU.hk.R.string.hkd_dollarSign).toString()
        order_number = intent.getBundleExtra("bundle")!!.getString("orderNumber").toString()

        if (userId != null) {
            getNotificationItemCount(userId)
        }
        doGetPaymentMethodList()

        getWalletHistoryDetailRead(order_number)
        initView()
        initClick()

    }

    private fun initView() {
//        initAddVlueSpinner()
    }
    private fun initClick() {
        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(this, ShopNotifyActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnFps.setOnClickListener {
            getCheckFpsStatus(order_id)
        }
    }

    fun getCheckFpsStatus(order_id:String) {
//        Log.d("getCheckFpsStatus", "orderId: ${orderId}")

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
                        val intent = Intent(this@AddValueDetailedInfoActivity, FpsPayActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("jsonTutList", "[\"${order_id}\"]")
                        bundle.putString("orderNumber", order_number.toString())
                        bundle.putString("mode", mode.toString())
                        intent.putExtra("bundle", bundle)
                        this@AddValueDetailedInfoActivity.startActivity(intent)
                    }else if(status == -1) {
                        val intent = Intent(this@AddValueDetailedInfoActivity, FpsPayAuditActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("mode", mode.toString())
                        intent.putExtra("bundle", bundle)
                        this@AddValueDetailedInfoActivity.startActivity(intent)
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

                        order_id = addValueHistoryBean.order_id

                        when(addValueHistoryBean.change.toString()){
                            "788"->{
                                runOnUiThread {
                                    binding.ivProductIcon.setImageResource(com.HKSHOPU.hk.R.mipmap.petit_podium)
                                }
                            }
                            "398"->{
                                runOnUiThread {
                                    binding.ivProductIcon.setImageResource(com.HKSHOPU.hk.R.mipmap.petit_coins)
                                }
                            }
                            "158"->{
                                runOnUiThread {
                                    binding.ivProductIcon.setImageResource(com.HKSHOPU.hk.R.mipmap.petit_coin)
                                }
                            }
                        }

                        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        val date: Date = format.parse(addValueHistoryBean.created_at.toString())
                        var date_result =  SimpleDateFormat("dd/MM/yyyy").format(date)
                        runOnUiThread {

                            binding.tvDate.setText(date_result.toString())
                            binding.tvStoredValue.setText(addValueHistoryBean.change.toString())

                            binding.tvSubtotalValue.setText("${hk_dollarsign}${addValueHistoryBean.change.toString()}")
                            binding.tvTotalValue.setText("${hk_dollarsign}${addValueHistoryBean.change.toString()}")

                            binding.tvOrdernumber.setText(addValueHistoryBean.order_number.toString())
                        }
                        when(addValueHistoryBean.status.toString()){
                            "0"->{
                                runOnUiThread {
                                    binding.tvStatus.setText(getText(com.HKSHOPU.hk.R.string.tobepaid))
                                    binding.tvReceive.setText(getText(com.HKSHOPU.hk.R.string.add_value_pending_payment_description_description))
                                    binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_hkcolor)

                                    binding.layoutSpinner.visibility = View.VISIBLE
                                    binding.layoutPaidTime.visibility = View.GONE
                                    binding.layoutFinishTime.visibility = View.GONE
                                    binding.btnFps.visibility = View.VISIBLE
                                }
                            }
                            "1"->{
                                runOnUiThread {
                                    binding.tvStatus.setText(getText(com.HKSHOPU.hk.R.string.add_value_reviewing_status))
                                    binding.tvReceive.setText(getText(com.HKSHOPU.hk.R.string.add_value_reviewing_status_description))
                                    binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_hkcolor)

                                    binding.layoutSpinner.visibility = View.GONE
                                    binding.layoutPaidTime.visibility = View.GONE
                                    binding.layoutFinishTime.visibility = View.GONE
                                    binding.btnFps.visibility = View.GONE
                                }
                            }
                            "2"->{
                                runOnUiThread {
                                    binding.tvStatus.setText(getText(com.HKSHOPU.hk.R.string.add_value_fail_status))
                                    binding.tvReceive.setText(getText(com.HKSHOPU.hk.R.string.add_value_fail_status_description))
                                    binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_red_dc4d3f)

                                    binding.layoutSpinner.visibility = View.GONE
                                    binding.layoutPaidTime.visibility = View.GONE
                                    binding.layoutFinishTime.visibility = View.GONE
                                    binding.btnFps.visibility = View.GONE
                                }
                            }
                            "3"->{
                                runOnUiThread {
                                    binding.tvStatus.setText(getText(com.HKSHOPU.hk.R.string.add_value_complete_status))
                                    binding.tvReceive.setText(getText(com.HKSHOPU.hk.R.string.add_value_complete_status_description))
                                    binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_hkcolor)

                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    val paid_at: Date = format.parse(addValueHistoryBean.created_at.toString())
                                    var paid_at_result =  SimpleDateFormat("dd/MM/yyyy HH:mm").format(paid_at)
                                    val complete_at: Date = format.parse(addValueHistoryBean.updated_at.toString())
                                    var complete_at_result =  SimpleDateFormat("dd/MM/yyyy HH:mm").format(complete_at)

                                    binding.layoutSpinner.visibility = View.GONE
                                    binding.layoutPaidTime.visibility = View.VISIBLE
                                    binding.tvPaidTime.setText(paid_at_result)
                                    binding.layoutFinishTime.visibility = View.VISIBLE
                                    binding.tvFinishTime.setText(complete_at_result)
                                    binding.btnFps.visibility = View.GONE
                                }
                            }
                        }
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddValueDetailedInfoActivity, "Wallet Detail Get Failed", Toast.LENGTH_SHORT)
                                .show()

                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@AddValueDetailedInfoActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletHistoryDetailRead", "JSONException: ${e.toString()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@AddValueDetailedInfoActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletHistoryDetailRead", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@AddValueDetailedInfoActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("getWalletHistoryDetailRead", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.Get_Data(
            url
        )
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
                                    Toast.makeText(this@AddValueDetailedInfoActivity, """已選取"${mutablelist_paymentBean.get(position).payment_desc}"作為付款方式""", Toast.LENGTH_SHORT).show()

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
                            Toast.makeText(this@AddValueDetailedInfoActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
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