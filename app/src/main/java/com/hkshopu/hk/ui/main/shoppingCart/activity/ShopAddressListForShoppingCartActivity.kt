package com.HKSHOPU.hk.ui.main.shoppingCart.activity

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

import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShopAddressListForShoppingCartBinding

import com.HKSHOPU.hk.databinding.ActivityShopaddresslistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.activity.AddShopAddressAfterBuildedActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.BankPresetActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopAddressPresetActivity

import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ShopAddressListAdapter
import com.HKSHOPU.hk.ui.main.shoppingCart.adapter.UserAddressItemAdapter

import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopAddressListForShoppingCartActivity : BaseActivity() {
    private lateinit var binding: ActivityShopAddressListForShoppingCartBinding

    private var adapter = UserAddressItemAdapter(this, "", "")
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()

    var mutableList_userAddressBean: MutableList<UserAddressBean> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopAddressListForShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.getBundleExtra("bundle_shoppingCart")
        var shoppingCartId = bundle?.getString("shoppingCartId").toString()
        var specId_json = bundle?.getString("specId_json").toString()
        adapter = UserAddressItemAdapter(this, shoppingCartId.toString(), specId_json)

        doGetUserAddressList( "25")

        initView()
        initEvent()
        initView()
        initVM()
        initClick()

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


                }
            }, {
                it.printStackTrace()
            })

    }

    private fun doGetUserAddressList( user_id: String) {

        var url = ApiConstants.API_HOST + "shopping_cart/" + user_id + "/buyer_address/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doGetUserAddressList", "返回資料 resStr：" + resStr)
                    Log.d("doGetUserAddressList", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val userAddressBean: UserAddressBean =
                                Gson().fromJson(jsonObject.toString(), UserAddressBean::class.java)

                            mutableList_userAddressBean.add(userAddressBean)

                        }

                        runOnUiThread {
                            binding.recyclerview.adapter = adapter
                            adapter.setData(mutableList_userAddressBean)
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

        binding.tvAddshopaddress.setOnClickListener {

            val intent = Intent(this, AddBuyerAddressForShoppingCartActivity::class.java)
            var bundle = Bundle()
            bundle.putString("addMode", "not_first_address")
            intent.putExtra("bundle_addMode", bundle)

            startActivity(intent)

        }

        adapter.intentClick = {
        val intent = Intent(this@ShopAddressListForShoppingCartActivity, ShopAddressPresetActivity::class.java)
            startActivity(intent)
        }

    }


}