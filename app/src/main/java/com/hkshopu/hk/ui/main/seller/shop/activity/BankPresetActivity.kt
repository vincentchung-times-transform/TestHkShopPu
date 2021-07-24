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
import com.HKSHOPU.hk.component.EventSyncBank
import com.HKSHOPU.hk.data.bean.ShopBankAccountBean
import com.HKSHOPU.hk.databinding.ActivityBankaccountpresetBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.adapter.BankPresetAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BankPresetActivity : BaseActivity() {
    private lateinit var binding: ActivityBankaccountpresetBinding

    private val adapter = BankPresetAdapter()
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
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
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopBankAccountBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getShopBackListBankPresetActivity", "返回資料 resStr：" + resStr)
                    Log.d("getShopBackListBankPresetActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopBankAccountBean: ShopBankAccountBean =
                                Gson().fromJson(jsonObject.toString(), ShopBankAccountBean::class.java)
                            list.add(shopBankAccountBean)
                        }
                        runOnUiThread {
                            adapter.setData(list)
                            binding.recyclerview.adapter = adapter
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }else {
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }


                } catch (e: JSONException) {
                    Log.d("getShopBackListBankPresetActivity_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getShopBackListBankPresetActivity_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getShopBackListBankPresetActivity_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun do_ShopBankPreset(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("do_ShopBankPreset", "返回資料 resStr：" + resStr)
                    Log.d("do_ShopBankPreset", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        runOnUiThread {
                            val intent = Intent(this@BankPresetActivity, BankListActivity::class.java)
                            RxBus.getInstance().post(EventSyncBank())
                            Toast.makeText(this@BankPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE

                            startActivity(intent)
                            finish()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@BankPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("do_ShopBankPreset_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("do_ShopBankPreset_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("do_ShopBankPreset_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Patch_Data(url)
    }
    private fun initClick() {
        binding.tvBankaccountpreset.setOnClickListener {
            AlertDialog.Builder(this@BankPresetActivity, R.style.AlertDialogTheme)
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