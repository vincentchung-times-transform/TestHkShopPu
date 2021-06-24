package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.tencent.mmkv.MMKV

import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class SocialAccountSetActivity : BaseActivity() {
    private lateinit var binding: ActivitySocialacntsetupBinding
    var address_id:String = ""
    var facebook_on:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialacntsetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        address_id = intent.getBundleExtra("bundle")!!.getString("address_id","").toString()
        facebook_on= intent.getBundleExtra("bundle")!!.getString("facebook_on","")
        initVM()
        initClick()

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
        if(facebook_on.equals("Y")){
            binding.switchviewFb.openSwitcher()
        }

        binding.switchviewFb.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){
                    val facebook_on = "Y"
                    AlertDialog.Builder(this@SocialAccountSetActivity, R.style.AlertDialogTheme)
                        .setTitle("「HKSHOPU」想要使用「facebook.com」登入")
                        .setMessage("這會讓App和網路分享關於您的資訊。")
                        .setPositiveButton("繼續"){
                            // 此為 Lambda 寫法
                                dialog, which ->doShopFaceBookOnOff(facebook_on)
                        }
                        .setNegativeButton("取消"){
                                dialog, which -> dialog.cancel()
                            binding.switchviewFb.closeSwitcher()
                        }
                        .show()
                }else{
                    val facebook_on = "N"
                    doShopFaceBookOnOff(facebook_on)
                }
            }
        })

        binding.switchviewIg.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){

//                    AlertDialog.Builder(this@SocialAccountSetActivity)
//                        .setTitle("「HKSHOPU」想要使用「facebook.com」登入")
//                        .setMessage("這會讓App和網路分享關於您的資訊。")
//                        .setPositiveButton("繼續"){
//                            // 此為 Lambda 寫法
//                                dialog, which ->
//                        }
//                        .setNegativeButton("取消"){
//                                dialog, which -> dialog.cancel()
//                            binding.switchviewFb.closeSwitcher()
//                        }
//                        .show()
                }
            }
        })

    }

    private fun doShopFaceBookOnOff(facebook_on: String) {
        val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
        var url = ApiConstants.API_PATH+"shop/"+shopId+"/update/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("SocialAccountSetActivity", "返回資料 resStr：" + resStr)
                    Log.d("SocialAccountSetActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {

                            Toast.makeText(this@SocialAccountSetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@SocialAccountSetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopFaceBookOnOff(url, address_id,facebook_on)
    }

}