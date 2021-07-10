package com.HKSHOPU.hk.ui.main.buyer.activity

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
import com.HKSHOPU.hk.component.EventRefreshAddressList
import com.HKSHOPU.hk.component.EventRefreshUserAddressList

import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.ActivityUseraddresslistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.adapter.BuyerAddressListAdapter

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
    var addressIds: ArrayList<String> = arrayListOf()
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

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BuyerAddressListActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerAddressListActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
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
                doUserAddressDel(addressIds)
            }

        }

        binding.tvAdduseraddress.setOnClickListener {
            val intent = Intent(this, BuyerAddAddressActivity::class.java)
            startActivity(intent)

        }
        adapter.cancelClick = {

            addressIds.add(it)

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


    private fun doUserAddressDel(list: ArrayList<String>) {

        var url = ApiConstants.API_HOST +"shop/delete_shop_address_forAndroid/"

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

                            Toast.makeText(this@BuyerAddressListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@BuyerAddressListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopAddressDel(url, list)
    }
}