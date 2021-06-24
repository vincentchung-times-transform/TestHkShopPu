package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventChangeShopTitleSuccess

import com.HKSHOPU.hk.databinding.ActivityShopnameeditBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener

import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopNameEditActivity : BaseActivity(){
    private lateinit var binding: ActivityShopnameeditBinding

    private val VM = AuthVModel()
    var shopName: String = ""
    var shopName_old: String = ""
    var address_id:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopnameeditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        address_id = intent.getBundleExtra("bundle")!!.getString("address_id","").toString()
        shopName_old = intent.getBundleExtra("bundle")!!.getString("shop_name","")
        initView()
        initVM()
        initClick()

    }



    private fun initView() {
        binding.etShopnameedit.setText(shopName_old)
        binding.layoutShopnameEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        binding.etShopnameedit.doAfterTextChanged {
            shopName = binding.etShopnameedit.text.toString()
        }

    }
    private fun initVM() {
//        VM.socialloginLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    if (url.isNotEmpty()) {
//                        toast("登录成功")
//
//                    }
//
//                    finish()
//                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
//            }
//        })
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.tvSave.setOnClickListener {
//            Log.d("hfdjdshfd", "shopName_old : ${shopName_old}" + " ; shopName : ${shopName}")
            if(shopName.isEmpty()){
                doShopTitleUpdate(shopName_old)
            }else{
                doShopTitleUpdate(shopName)
            }

        }

    }
    private fun doShopTitleUpdate(shopTitle: String) {
        val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
        var url = ApiConstants.API_PATH+"shop/"+shopId+"/update/"
        Log.d("ShopNameEditActivity", "送資料 URL：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopNameEditActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopNameEditActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        RxBus.getInstance().post(EventChangeShopTitleSuccess(shopTitle))
                        finish()
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@ShopNameEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopTitleUpdate(url,address_id,shopTitle)
    }


}