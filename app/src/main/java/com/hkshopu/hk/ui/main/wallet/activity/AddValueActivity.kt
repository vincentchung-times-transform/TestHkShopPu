package com.HKSHOPU.hk.ui.main.wallet.activity

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventGenerateAddValueOeder
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerOrderDetail_Adapter
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.fragment.GenerateOrderCheckingDialogFragment
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class  AddValueActivity : BaseActivity() {
    private lateinit var binding: ActivityAddValueBinding


    private val adapter = BuyerOrderDetail_Adapter()
    var hkdDollarSign = ""
    var mutablelist_addValue : ArrayList<String> = arrayListOf()
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    var selected_payment_id = ""
    var add_value = ""
    var shop_id = ""
    var wallet_id = ""
    var action = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddValueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hkdDollarSign = getString(com.HKSHOPU.hk.R.string.hkd_dollarSign).toString()
        var bundle  = intent.getBundleExtra("bundle")
        add_value = bundle!!.getString("addValue").toString()
        shop_id = bundle!!.getString("shopId").toString()
        wallet_id = bundle!!.getString("walletId").toString()


        doGetPaymentMethodList()
        initView()
        initEvent()


    }

    private fun initView() {
        initAddVlueSpinner()
        initClick()
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
            GenerateOrderCheckingDialogFragment("addValue").show(supportFragmentManager, "MyCustomFragment")
        }
    }

    fun initAddVlueSpinner(){
        mutablelist_addValue.add("159")
        mutablelist_addValue.add("398")
        mutablelist_addValue.add("788")

        val addValue_list_DollarSignFree: ArrayList<String> = arrayListOf()

        for (i in 0 until mutablelist_addValue.size) {
            addValue_list_DollarSignFree.add("${hkdDollarSign}${mutablelist_addValue.get(i).toString()}")
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            FacebookSdk.getApplicationContext(),
            R.layout.simple_spinner_dropdown_item,
            addValue_list_DollarSignFree
        )
        runOnUiThread {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerAddValue.setAdapter(adapter)

            when(add_value){
                "158"->{
                    binding.spinnerAddValue.setSelection(0)
                }
                "398"->{
                    binding.spinnerAddValue.setSelection(1)
                }
                "788"->{
                    binding.spinnerAddValue.setSelection(2)
                }
            }

            binding.spinnerAddValue.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
//                    selected_payment_id = mutablelist_paymentBean.get(position).id
                    add_value = mutablelist_addValue.get(position).toString()
                    binding.tvStatus.setText(addValue_list_DollarSignFree.get(position).toString())

                    when(position){
                        0->{
                            binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_green_87dfd6)
                            binding.bannerPic.setImageResource(com.HKSHOPU.hk.R.mipmap.big_coin)
                        }
                        1->{
                            binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_blue_7dace4)
                            binding.bannerPic.setImageResource(com.HKSHOPU.hk.R.mipmap.big_coins)
                        }
                        2->{
                            binding.layoutStatus.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_16dp_purple_7b61ff)
                            binding.bannerPic.setImageResource(com.HKSHOPU.hk.R.mipmap.big_podium)
                        }
                    }

                    binding.tvSubtotalValue.setText(mutablelist_addValue.get(position).toString())
                    binding.tvTotalValue.setText(mutablelist_addValue.get(position).toString())

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }


        }

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
                                    Toast.makeText(this@AddValueActivity, """已選取"${mutablelist_paymentBean.get(position).payment_desc}"作為付款方式""", Toast.LENGTH_SHORT).show()
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
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

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                     is EventGenerateAddValueOeder ->{

                         Log.d("doConvertAddValueToOrder", "wallet_id: ${wallet_id}, shop_id: ${shop_id}, change: ${add_value}")

                         //SR:贊助 SA:儲值 AD:廣告
                         action = "SA"
                         doConvertAddValueToOrder(wallet_id, shop_id, add_value.toInt(), action)
                    }
                }
            }, {
                it.printStackTrace()
            })
    }


    private fun doConvertAddValueToOrder (wallet_id: String, shop_id: String, change: Int, action: String) {
        Log.d("doConvertAddValueToOrder", "wallet_id: ${wallet_id}, shop_id: ${shop_id}, change: ${change}")
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val url = ApiConstants.API_SWAGGER+"wallet/history/add/"

        // create your json here
        val jsonObject = JSONObject()
        try {
            jsonObject.put("wallet_id", wallet_id)
            jsonObject.put("shop_id", shop_id)
            jsonObject.put("change", change)
            jsonObject.put("action", action)//    SR:贊助 SA:儲值 AD:廣告
            Log.d("doConvertAddValueToOrder", "jsonObject: ${jsonObject.toString()}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String = ""
                var code = 0

                try {
                    resStr = response.body()!!.string()
                    code = response.code()
                    Log.d("doConvertAddValueToOrder", "返回資料 resStr：" + resStr)
                    val json = JSONObject(resStr)
//                    {
//                        "id": 22,
//                        "order_id": "3bf06a19-ea51-4b40-88b3-883aca757a96",
//                        "wallet_id": "ff04efcc-ddad-4995-88a2-ca21145151ea",
//                        "change": 398,
//                        "action": "錢包儲值",
//                        "status": 0,
//                        "order_number": "HKSHOPU2021081822",
//                        "description": ""
//                    }
                    if (code.equals(201)) {
                        runOnUiThread {
                            Toast.makeText(this@AddValueActivity, "儲值訂單生成成功", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }

                        var id = json.getString("id")
                        var order_id = json.getString("order_id")
                        var wallet_id = json.getString("wallet_id")
                        var change = json.getString("change")
                        var action = json.getString("action")
                        var status = json.getString("status")
                        var order_number = json.getString("order_number")
                        var description = json.getString("description")

                        var id_list = arrayListOf<String>()
                        id_list.add(order_id)
                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                        val jsonTutList: String = gson.toJson(id_list)
                        Log.d("jsonTutList_id_list", jsonTutList.toString())
                        val jsonTutListPretty: String = gsonPretty.toJson(id_list)
                        Log.d("jsonTutListPretty_id_list", jsonTutListPretty.toString())

                        val intent = Intent(this@AddValueActivity, FpsPayActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("jsonTutList", jsonTutList.toString())
                        bundle.putString("orderNumber", order_number.toString())
                        bundle.putString("mode", "addValue")
                        intent.putExtra("bundle", bundle)
                        startActivity(intent)



                    }else{
                        runOnUiThread {
                            Toast.makeText(this@AddValueActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doConvertAddValueToOrder", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@AddValueActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doConvertAddValueToOrder", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@AddValueActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doConvertAddValueToOrder", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(this@AddValueActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.do_walletHistoryAddCreate(url, jsonObject)
    }

}