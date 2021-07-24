package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshAddressList
import com.HKSHOPU.hk.data.bean.ShopAddressListBean
import com.HKSHOPU.hk.databinding.ActivityShopaddresspresetBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.adapter.ShopAddressPresetAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
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
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
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
        binding.progressBar.visibility = View.VISIBLE
        binding.ivLoadingBackground.visibility = View.VISIBLE

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
                    Log.d("getShopAddressList", "返回資料 resStr：" + resStr)
                    Log.d("getShopAddressList", "返回資料 ret_val：" + ret_val)

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
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getShopAddressList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getShopAddressList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getShopAddressList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.ivLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun do_ShopAddressPreset(pressId: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.ivLoadingBackground.visibility = View.VISIBLE

         val url = ApiConstants.API_HOST+"shop/updateShopAddress_isDefault/"
        Log.d("do_ShopAddressPreset", "返回資料 shopid：" + shopId)
        Log.d("do_ShopAddressPreset", "返回資料 url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("do_ShopAddressPreset", "返回資料 resStr：" + resStr)
                    Log.d("do_ShopAddressPreset", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            val intent = Intent(this@ShopAddressPresetActivity, ShopAddressListActivity::class.java)
                            RxBus.getInstance().post(EventRefreshAddressList())
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                            Toast.makeText(this@ShopAddressPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()

                            startActivity(intent)
                            finish()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShopAddressPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("do_ShopAddressPreset_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("do_ShopAddressPreset_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("do_ShopAddressPreset_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.ivLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Do_ShopAddressPreset(url,shopId,pressId)
    }
    private fun initClick() {
        binding.tvShopaddresspreset.setOnClickListener {
            AlertDialog.Builder(this , R.style.AlertDialogTheme)
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