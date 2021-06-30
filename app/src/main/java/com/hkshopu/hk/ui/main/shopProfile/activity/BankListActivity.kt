package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopBankAccountBean
import com.HKSHOPU.hk.databinding.ActivityBankaccountlistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.BankListAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BankListActivity : BaseActivity() {
    private lateinit var binding: ActivityBankaccountlistBinding

    private val adapter = BankListAdapter()
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/bankAccount/"
    var cancelurl:String = ""
    lateinit var list : ArrayList<ShopBankAccountBean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankaccountlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getShopBackList(url)

    }

    private fun initView() {
        adapter.cancelClick = {
            cancelurl =  ApiConstants.API_HOST +"shop/bankAccount/"+it+"/"
            if(cancelurl.isNotEmpty()){
                doShopBankDel(cancelurl)
            }
        }
        adapter.toPresetClick = {

            val intent = Intent(this, BankPresetActivity::class.java)
            startActivity(intent)

        }
    }

    private fun initVM() {

    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventSyncBank -> {
                        getShopBackList(url)
                    }
                }
            }, {
                it.printStackTrace()
            })

    }

    private fun getShopBackList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                list = ArrayList<ShopBankAccountBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BankListActivity", "返回資料 resStr：" + resStr)
                    Log.d("BankListActivity", "返回資料 ret_val：" + json.get("ret_val"))
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
                        runOnUiThread {
                            adapter.setData(list)
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

    private fun initClick() {

        binding.tvAddbankaccount2.setOnClickListener {
            val intent = Intent(this, AddBankAccountAfterBuiledctivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvEdit.setOnClickListener {


            if(binding.tvEdit.text.equals("編輯")){
                binding.tvEdit.text = "完成"
                binding.tvEdit.textColor = Color.parseColor("#1DBCCF")

                if(list.size>1) {
                    adapter.updateData(true)
                }
            }else{
                binding.tvEdit.text = "編輯"
                binding.tvEdit.textColor = Color.parseColor("#8E8E93")
                adapter.updateData(false)


            }


        }
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
    private fun doShopBankDel(url: String) {

        Log.d("ShopAddressListActivity", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopAddressListActivity", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    Log.d("ShopAddressListActivity", "返回資料 ret_val：" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {

                            Toast.makeText(this@BankListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@BankListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Delete_Data(url)
    }

}