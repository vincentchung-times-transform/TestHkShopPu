package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.HKSHOPU.hk.widget.view.setTextColor
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerPhoneEditActivity : BaseActivity() {

    private lateinit var binding: ActivityUserphoneeditBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    val url = ApiConstants.API_HOST + "user_detail/update_detail/"
    var phone=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserphoneeditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.getBundleExtra("bundle")
        phone = bundle!!.getString("phone").toString()

        initView()
        initClick()

    }

    private fun initView() {
        binding.progressBarUserPhoneEdit.visibility = View.GONE
        binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.GONE

        binding.layoutPhoneEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }

        binding.etPhoneNumber.setText(phone)
        binding.etPhoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.etPhoneNumber.doAfterTextChanged {
            phone = binding.etPhoneNumber.text.toString()
        }
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvSave.setOnClickListener {
            binding.progressBarUserPhoneEdit.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.VISIBLE
            doUpdatePhone(url,phone)
        }
    }

    private fun doUpdatePhone(url: String,phone:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("UserPhoneEditActivity", "返回資料 resStr：" + resStr)
                    Log.d("UserPhoneEditActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        RxBus.getInstance().post(EventRefreshUserInfo())
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerPhoneEditActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            runOnUiThread {
                                binding.progressBarUserPhoneEdit.visibility = View.GONE
                                binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.GONE
                            }
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerPhoneEditActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBarUserPhoneEdit.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doUpdatePhone_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserPhoneEdit.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUpdatePhone_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserPhoneEdit.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUpdatePhone_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarUserPhoneEdit.visibility = View.GONE
                    binding.imgViewLoadingBackgroundUserPhoneEdit.visibility = View.GONE
                }
            }
        })
        web.Do_UserInfoUpdate(url,userId,"","","",phone,"","","")
    }

    public override fun onDestroy() {
        // Stop service when done
        super.onDestroy()
    }


}