package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshUserAddressList

import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.ActivityUseraddresslistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerAddressListAdapter

import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BuyerAddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityUseraddresslistBinding

    private val adapter = BuyerAddressListAdapter()
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    var url = ApiConstants.API_HOST + "shopping_cart/"+userId+"/buyer_address/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUseraddresslistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getUserAddressList(url)
    }

    private fun initView() {
        binding.progressBarBuyerAddressList.visibility = View.GONE
        binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
    }
    private fun initVM() {
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshUserAddressList ->{
                        getUserAddressList(url)
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun getUserAddressList(url: String) {
        binding.progressBarBuyerAddressList.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getUserAddressList", "返回資料 resStr：" + resStr)
                    Log.d("getUserAddressList", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val userAddressListBean: BuyerAddressListBean =
                                Gson().fromJson(jsonObject.toString(), BuyerAddressListBean::class.java)
                            list.add(userAddressListBean)
                        }
                        runOnUiThread {
                            adapter.setData(list)
                            if(list.size > 1){
                                binding.tvEdit.visibility = View.VISIBLE
                            }
                            binding.recyclerview.adapter = adapter

                            binding.progressBarBuyerAddressList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBarBuyerAddressList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getUserAddressList_errorMessage", "返回資料 ret_val：" + e.toString())
                    runOnUiThread {
                        binding.progressBarBuyerAddressList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    Log.d("getUserAddressList_errorMessage", "IOException：" + e.toString())
                    e.printStackTrace()
                    runOnUiThread {
                        binding.progressBarBuyerAddressList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getUserAddressList_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    binding.progressBarBuyerAddressList.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener{
            finish()
        }
        binding.tvEdit.setOnClickListener {
            if(binding.tvEdit.text.equals("編輯")){
                binding.tvEdit.text = "完成"
                binding.tvEdit.textColor = Color.parseColor("#1DBCCF")
                adapter.updateData(true)
            }else{
                binding.tvEdit.text = "編輯"
                binding.tvEdit.textColor = Color.parseColor("#8E8E93")
                adapter.updateData(false)
            }
        }
        binding.tvAdduseraddress.setOnClickListener {
            val intent = Intent(this, BuyerAddAddressActivity::class.java)
            startActivity(intent)
        }
        adapter.cancelClick = {
            binding.progressBarBuyerAddressList.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.VISIBLE
            doUserAddressDel(it.toString())
        }
        adapter.intentClick = {
        val intent = Intent(this@BuyerAddressListActivity, BuyerAddressPresetActivity::class.java)
            startActivity(intent)
        }
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }
    }


    private fun doUserAddressDel(address_id: String) {

        var url = ApiConstants.API_HOST +"shopping_cart/delete_user_address/"

        Log.d("doUserAddressDel", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doUserAddressDel", "返回資料 resStr：" + resStr)
                    Log.d("doUserAddressDel", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@BuyerAddressListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarBuyerAddressList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@BuyerAddressListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarBuyerAddressList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doUserAddressDel_errorMessage", "JSONException：" + e.toString())
                    runOnUiThread {
                        binding.progressBarBuyerAddressList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUserAddressDel_errorMessage", "IOException：" + e.toString())
                    runOnUiThread {
                        binding.progressBarBuyerAddressList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUserAddressDel_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    binding.progressBarBuyerAddressList.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerAddressList.visibility = View.GONE
                }
            }
        })
        web.Do_BuyerAddressDel(url, address_id)
    }
}