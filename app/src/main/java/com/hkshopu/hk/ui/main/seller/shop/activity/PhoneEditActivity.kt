package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventChangeShopPhoneSuccess

import com.HKSHOPU.hk.databinding.ActivityPhoneeditBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener

import com.HKSHOPU.hk.ui.onboard.vm.AuthVModel
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class PhoneEditActivity : BaseActivity(){
    private lateinit var binding: ActivityPhoneeditBinding

    var phone_country: String = "852"
    var phone_number: String = ""
    var phone_pass: String = ""
    var isphoneShow: String = ""
    var address_id:String = ""
    var phone_old: String =""
    var phone_is_show: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneeditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        address_id = intent.getBundleExtra("bundle")!!.getString("address_id","")
        phone_old = intent.getBundleExtra("bundle")!!.getString("phone_old","")
        phone_is_show = intent.getBundleExtra("bundle")!!.getString("phone_is_show","")

        initView()
        initClick()
    }

    private fun initView() {
        binding.progressBarPhoneEdit.visibility = View.GONE
        binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.GONE

        binding.editShopphoneNumber.setText(phone_old)
        binding.editShopphoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.editShopphoneNumber.doAfterTextChanged {
            phone_number = binding.editShopphoneNumber.text.toString()
            phone_country = binding.tvShopphoneCountry.text.toString()
            phone_country = binding.tvShopphoneCountry.text.toString().replace("+", "", false)
            phone_pass = phone_country + phone_number
        }
        binding.layoutPhoneEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        if(phone_is_show.equals("Y")){
            binding.switchview.openSwitcher()
            isphoneShow ="Y"
        }else{
            binding.switchview.closeSwitcher()
            isphoneShow ="N"
        }
    }
    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvSave.setOnClickListener {
            binding.progressBarPhoneEdit.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.VISIBLE

            if(phone_number.isNullOrEmpty()){
                phone_number = phone_old
            }
            Log.d("phone_number_value_inspect",
                "phone_country: ${phone_country} ; "
                        + "phone_number: ${phone_number} ; "
                        + "isphoneShow: ${isphoneShow} ; ")
            doShopPhoneUpdate(phone_country,phone_number,isphoneShow)
        }
        binding.switchview.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){
                    isphoneShow ="Y"
                }else{
                    isphoneShow ="N"
                }
            }
        })
    }
    private fun doShopPhoneUpdate(countrycode: String,phone: String, is_phone_show:String) {
        val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
        var url = ApiConstants.API_PATH+"shop/"+shopId+"/update/"
        Log.d("PhoneEditActivity", "資料 countrycode：" + countrycode)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doShopPhoneUpdate", "返回資料 resStr：" + resStr)
                    Log.d("doShopPhoneUpdate", "返回資料 ret_val：" + ret_val)
                    if (status == 0) {
//                        if(isphoneShow.equals("Y")) {
                            RxBus.getInstance().post(EventChangeShopPhoneSuccess(phone_number))
//                        }
                        runOnUiThread {
                            Toast.makeText(this@PhoneEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarPhoneEdit.visibility = View.GONE
                            binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.GONE
                        }
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@PhoneEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarPhoneEdit.visibility = View.GONE
                            binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doShopPhoneUpdate_errorMessage", "JSONException：" + e.toString())
                    runOnUiThread {
                        binding.progressBarPhoneEdit.visibility = View.GONE
                        binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doShopPhoneUpdate_errorMessage", "IOException：" + e.toString())
                    runOnUiThread {
                        binding.progressBarPhoneEdit.visibility = View.GONE
                        binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doShopPhoneUpdate_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    binding.progressBarPhoneEdit.visibility = View.GONE
                    binding.imgViewLoadingBackgroundPhoneEdit.visibility = View.GONE
                }
            }
        })
        web.Do_ShopPhoneUpdate(url,address_id,countrycode,phone,is_phone_show)
    }
}