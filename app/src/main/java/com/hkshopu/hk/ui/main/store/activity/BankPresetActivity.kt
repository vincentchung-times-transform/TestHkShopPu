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
import com.hkshopu.hk.component.EventSyncBank

import com.hkshopu.hk.data.bean.ShopBankAccountBean

import com.hkshopu.hk.databinding.ActivityBankaccountpresetBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.adapter.BankListAdapter
import com.hkshopu.hk.ui.main.store.adapter.BankPresetAdapter

import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BankPresetActivity : BaseActivity() {
    private lateinit var binding: ActivityBankaccountpresetBinding

    private val adapter = BankPresetAdapter()
    val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/bankAccount/"
    var preseturl:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankaccountpresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getShopBackList(url)

    }

    private fun initView() {
        adapter.presetClick = {
            val presetId = it
            preseturl = ApiConstants.API_HOST +"/shop/bankAccount/"+presetId+"/"
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

    private fun getShopBackList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopBankAccountBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BankPresetActivity", "返回資料 resStr：" + resStr)
                    Log.d("BankPresetActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopBankAccountBean: ShopBankAccountBean =
                                Gson().fromJson(jsonObject.toString(), ShopBankAccountBean::class.java)
                            list.add(shopBankAccountBean)
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

    private fun do_ShopBankPreset(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BankPresetActivity", "返回資料 resStr：" + resStr)
                    Log.d("BankPresetActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        runOnUiThread {
                            val intent = Intent(this@BankPresetActivity, BankListActivity::class.java)
                            RxBus.getInstance().post(EventSyncBank())
                            startActivity(intent)
                            finish()
                            Toast.makeText(this@BankPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }


                    }else{
                        runOnUiThread {
                            Toast.makeText(this@BankPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Patch_Data(url)
    }
    private fun initClick() {
        binding.tvBankaccountpreset.setOnClickListener {
            AlertDialog.Builder(this@BankPresetActivity)
                .setTitle("")
                .setMessage("確定將此帳號改為預設銀行帳號嗎?")
                .setPositiveButton("確定"){
                    // 此為 Lambda 寫法
                        dialog, which ->do_ShopBankPreset(preseturl)
                }
                .setNegativeButton("取消"){ dialog, which -> dialog.cancel()

                }
                .show()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
//        iv_Google.setOnClickListener {
//
//            GoogleSignIn()
//        }
//
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