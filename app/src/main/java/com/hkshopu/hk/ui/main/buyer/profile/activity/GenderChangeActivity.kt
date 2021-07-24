package com.HKSHOPU.hk.ui.main.buyer.profile.activity


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus

import com.paypal.android.sdk.payments.*
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


//import kotlinx.android.synthetic.main.activity_main.*

class GenderChangeActivity : BaseActivity() {

    private lateinit var binding: ActivityGenderchangeBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    val url = ApiConstants.API_HOST + "user_detail/update_detail/"
    var gender: String = "O"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle = intent.getBundleExtra("bundle")
        gender = bundle!!.getString("gender").toString()

        initView()
        initClick()
    }

    private fun initView() {
        binding.progressBarGenderchange.visibility = View.GONE
        binding.imgViewLoadingBackgroundGenderchange.visibility = View.GONE

        //預設性別為其他
        if(gender.equals("M")){
            binding.tvMale.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            gender="M"
        }else if(gender.equals("F")){
            binding.tvFemale.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            gender="F"
        }else{
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            gender="O"
        }

    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvMale.setOnClickListener {
            binding.tvMale.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            gender="M"
        }
        binding.tvFemale.setOnClickListener {
            binding.tvFemale.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            gender="F"
        }
        binding.tvRainbow.setOnClickListener {
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            gender="O"
        }
        binding.tvSave.setOnClickListener {
            binding.progressBarGenderchange.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundGenderchange.visibility = View.VISIBLE
            doUpdateGender(url,gender)
        }

    }
    private fun doUpdateGender(url: String,gender:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doUpdateGender", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateGender", "返回資料 ret_val：" + json.get("ret_val"))

                    if (status == 0) {
                        RxBus.getInstance().post(EventRefreshUserInfo())
                        runOnUiThread {
                            Toast.makeText(
                                this@GenderChangeActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBarGenderchange.visibility = View.GONE
                            binding.imgViewLoadingBackgroundGenderchange.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBarGenderchange.visibility = View.GONE
                            binding.imgViewLoadingBackgroundGenderchange.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doUpdateGender_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarGenderchange.visibility = View.GONE
                        binding.imgViewLoadingBackgroundGenderchange.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUpdateGender_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarGenderchange.visibility = View.GONE
                        binding.imgViewLoadingBackgroundGenderchange.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUpdateGender_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarGenderchange.visibility = View.GONE
                    binding.imgViewLoadingBackgroundGenderchange.visibility = View.GONE
                }
            }
        })
        web.Do_UserInfoUpdate(url,userId,"",gender,"","","","","")
    }
    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}