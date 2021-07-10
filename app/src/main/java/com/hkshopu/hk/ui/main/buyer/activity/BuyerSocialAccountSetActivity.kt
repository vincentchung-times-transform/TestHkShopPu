package com.HKSHOPU.hk.ui.main.buyer.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.HKSHOPU.hk.Base.BaseActivity

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


class BuyerSocialAccountSetActivity : BaseActivity() {
    private lateinit var binding: ActivityUsersocialacntsetBinding
    var facebook_on:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersocialacntsetBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    AlertDialog.Builder(this@BuyerSocialAccountSetActivity)
                        .setTitle("「HKSHOPU」想要使用「facebook.com」登入")
                        .setMessage("這會讓App和網路分享關於您的資訊。")
                        .setPositiveButton("繼續"){
                            // 此為 Lambda 寫法
                                dialog, which ->doUserFaceBookOnOff(facebook_on)
                        }
                        .setNegativeButton("取消"){
                                dialog, which -> dialog.cancel()
                            binding.switchviewFb.closeSwitcher()
                        }
                        .show()
                }else{
                    val facebook_on = "N"
                    doUserFaceBookOnOff(facebook_on)
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

    private fun doUserFaceBookOnOff(facebook_on: String) {
        var userId = MMKV.mmkvWithID("http").getString("UserId", "");
        var url = ApiConstants.API_HOST + "user_detail/" + userId + "/show/"

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

                            Toast.makeText(this@BuyerSocialAccountSetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@BuyerSocialAccountSetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_UserInfoUpdate(url, userId,"","","","",facebook_on,"","")
    }

}