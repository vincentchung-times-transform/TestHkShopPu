package com.hkshopu.hk.ui.main.store.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle

import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.data.bean.ShopAddressListBean

import com.hkshopu.hk.data.bean.ShopBankAccountBean

import com.hkshopu.hk.databinding.ActivityBankaccountpresetBinding
import com.hkshopu.hk.databinding.ActivityShopaddresslistBinding
import com.hkshopu.hk.databinding.ActivityShopaddresspresetBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.adapter.BankListAdapter
import com.hkshopu.hk.ui.main.store.adapter.BankPresetAdapter
import com.hkshopu.hk.ui.main.store.adapter.ShopAddressListAdapter
import com.hkshopu.hk.ui.main.store.adapter.ShopAddressPresetAdapter

import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ShopAddressPresetActivity : BaseActivity() {
    private lateinit var binding: ActivityShopaddresspresetBinding

    private val adapter = ShopAddressPresetAdapter()
    val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/get_shop_address/"
    var presetid:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopaddresspresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getShopAddressList(url)

    }

    private fun initView() {
        adapter.presetClick = {
            val presetId = it
            presetid = presetId
        }

    }

    private fun initVM() {

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

    private fun getShopAddressList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopAddressPreset", "返回資料 resStr：" + resStr)
                    Log.d("ShopAddressPreset", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopAddressListBean: ShopAddressListBean =
                                Gson().fromJson(jsonObject.toString(), ShopAddressListBean::class.java)
                            if(shopAddressListBean.is_default.equals("Y")){
                                list.add(0,shopAddressListBean)
                            }else{
                                list.add(shopAddressListBean)
                            }

                        }
                        adapter.setData(list)
                        runOnUiThread {
                            binding.recyclerview.adapter = adapter

                        }


                    }
//                        initRecyclerView()

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }

    private fun do_ShopAddressPreset(pressId: String) {
         val url = ApiConstants.API_HOST+"shop/updateShopAddress_isDefault/"
        Log.d("ShopAddressPreset", "返回資料 shopid：" + shopId)
        Log.d("ShopAddressPreset", "返回資料 url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopAddressPreset", "返回資料 resStr：" + resStr)
                    Log.d("ShopAddressPreset", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        runOnUiThread {
                            val intent = Intent(this@ShopAddressPresetActivity, ShopAddressListActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this@ShopAddressPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }


                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShopAddressPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ShopAddressPreset(url,shopId,pressId)
    }
    private fun initClick() {
        binding.tvShopaddresspreset.setOnClickListener {
            AlertDialog.Builder(this@ShopAddressPresetActivity)
                .setTitle("")
                .setMessage("確定將此地址改為預設地址嗎?")
                .setPositiveButton("確定"){
                    // 此為 Lambda 寫法
                        dialog, which ->do_ShopAddressPreset(presetid)
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