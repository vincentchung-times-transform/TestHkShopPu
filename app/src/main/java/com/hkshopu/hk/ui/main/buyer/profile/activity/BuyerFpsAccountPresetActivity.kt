package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshFpsAccountList
import com.HKSHOPU.hk.data.bean.BuyerPaymentBean
import com.HKSHOPU.hk.databinding.ActivityBuyerfpsaccountpresetBinding

import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerPaymentPresetAdapter


import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class BuyerFpsAccountPresetActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerfpsaccountpresetBinding

    private val adapter = BuyerPaymentPresetAdapter()
    val userId = MMKV.mmkvWithID("http").getString("UserId", "");
    var url = ApiConstants.API_HOST + "user/"+userId+"/paymentAccount/"
    var presetid:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerfpsaccountpresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initClick()
        getBuyerFpsaccountList(url)

    }

    private fun initView() {
        binding.progressBarBuyerFpsAccountPreset.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.VISIBLE

        adapter.presetClick = {
            val presetId = it
            presetid = presetId
            Log.d("FpsAccountPreset", "返回資料 presetId：" + presetid)
        }
    }
    private fun initRecyclerView() {

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.recyclerview.layoutManager = layoutManager
        binding!!.recyclerview.adapter = adapter

    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {


                }
            }, {
                it.printStackTrace()
            })

    }

    private fun getBuyerFpsaccountList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerPaymentBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("FpsAccountPreset", "返回資料 resStr：" + resStr)
                    Log.d("FpsAccountPreset", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val buyerPaymentBean: BuyerPaymentBean =
                                Gson().fromJson(jsonObject.toString(), BuyerPaymentBean::class.java)
                            list.add(buyerPaymentBean)
                        }
                        runOnUiThread {
                            adapter.setData(list)
                            initRecyclerView()
                            binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getBuyerFpsaccountList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getBuyerFpsaccountList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getBuyerFpsaccountList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun do_BuyerFpsAccountPreset(presetid: String) {

        binding.progressBarBuyerFpsAccountPreset.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.VISIBLE

        val url = ApiConstants.API_HOST+"user/paymentAccount/"+presetid+"/"
        Log.d("FpsAccountPreset", "資料 url：" + url)
        val is_default = "Y"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            RxBus.getInstance().post(EventRefreshFpsAccountList())
                            finish()
                            Toast.makeText(this@BuyerFpsAccountPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@BuyerFpsAccountPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("do_BuyerFpsAccountPreset_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("do_BuyerFpsAccountPreset_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("do_BuyerFpsAccountPreset_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerFpsAccountPreset.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerFpsAccountPreset.visibility = View.GONE
                }
            }
        })
        web.Do_BuyerPaymentPreset(url,presetid,is_default)
    }
    private fun initClick() {
        binding.tvBuyerfpsaccountpreset.setOnClickListener {
            AlertDialog.Builder(this@BuyerFpsAccountPresetActivity)
                .setTitle("")
                .setMessage("確定將此帳號改為預設轉數快帳號嗎?")
                .setPositiveButton("確定"){
                    // 此為 Lambda 寫法
                        dialog, which ->do_BuyerFpsAccountPreset(presetid)
                }
                .setNegativeButton("取消"){ dialog, which -> dialog.cancel()

                }
                .show()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

//        btn_Signup.setOnClickListener {
//
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }

}