package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserAddressList
import com.HKSHOPU.hk.databinding.ActivityBuyeraddaddressBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class BuyerAddAddressActivity : BaseActivity() {
    lateinit var binding: ActivityBuyeraddaddressBinding
    var userName: String = ""
    var phone_country: String = ""
    var phone_number: String = ""
    var phone: String = ""
    var country: String = ""
    var admin: String = ""
    var thoroughfare: String = ""
    var feature: String = ""
    var subaddress: String = ""
    var floor: String = ""
    var room: String = ""
    val userId = MMKV.mmkvWithID("http").getString("UserId", "");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyeraddaddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarBuyerAddAddress.visibility = View.GONE
        binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.GONE

        initView()
        initClick()
    }

    fun initView() {
        binding.etUsername.doAfterTextChanged {
            userName = binding.etUsername.text.toString()
            inspect_value()
        }
        binding.editUserphoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.editUserphoneNumber.doAfterTextChanged {
            phone_number = binding.editUserphoneNumber.text.toString()
            phone_country = binding.tvUserphoneCountry.text.toString()
            phone = phone_country + phone_number
            inspect_value()
        }
        binding.editCountry.doAfterTextChanged {
            country = binding.editCountry.text.toString()
            inspect_value()
        }
        binding.editAdmin.doAfterTextChanged {
            admin = binding.editAdmin.text.toString()
            inspect_value()
        }
        binding.editthoroughfare.doAfterTextChanged {
            thoroughfare = binding.editthoroughfare.text.toString()
            inspect_value()
        }
        binding.editfeature.doAfterTextChanged {
            feature = binding.editfeature.text.toString()
            inspect_value()
        }
        binding.editsubaddress.doAfterTextChanged {
            subaddress = binding.editsubaddress.text.toString()
            inspect_value()
        }
        binding.editfloor.doAfterTextChanged {
            floor = binding.editfloor.text.toString()
            inspect_value()
        }
        binding.editroom.doAfterTextChanged {
            room = binding.editroom.text.toString()
            inspect_value()
        }
        binding.layoutUseraddressEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
    }

    fun initClick() {
        binding.titleBack.setOnClickListener {
            finish()
        }
        binding.ivSave.setOnClickListener {
            binding.progressBarBuyerAddAddress.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.VISIBLE

            doAddShopAddress(
                userId!!,
                userName,
                phone_country,
                phone_number,
                country,
                admin,
                thoroughfare,
                feature,
                subaddress,
                floor,
                room,

                )
        }
    }

    private fun doAddShopAddress(
        user_id: String,
        name: String,
        country_code: String,
        phone: String,
        area: String,
        district: String,
        road: String,
        number: String,
        other: String,
        floor: String,
        room: String,
        ) {
        Log.d("doAddShopAddress", "user_id: ${user_id} ; name: ${name} ; country_code: ${country_code} ; phone: ${phone} ; area: ${area} ; district: ${district} ; road: ${road}")
        val url = ApiConstants.API_HOST + "shopping_cart/add_buyer_address/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doAddShopAddress", "返回資料 resStr：" + resStr)
                    Log.d("doAddShopAddress", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        RxBus.getInstance().post(EventRefreshUserAddressList())
                        runOnUiThread {
                            binding.progressBarBuyerAddAddress.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.GONE
                        }

                        val intent = Intent(
                            this@BuyerAddAddressActivity,
                            BuyerAddressListActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerAddAddressActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBarBuyerAddAddress.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doAddShopAddress_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerAddAddress.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    Log.d("doAddShopAddress_errorMessage", "IOException: ${e.toString()}")
                    e.printStackTrace()
                    runOnUiThread {
                        binding.progressBarBuyerAddAddress.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doAddShopAddress_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerAddAddress.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerAddAddress.visibility = View.GONE
                }
            }
        })
        web.Do_UserAddAddress(
            url,
            user_id,
            name,
            country_code,
            phone,
            area,
            district,
            road,
            number,
            other,
            floor,
            room,
            )
    }

    fun inspect_value(){
//        var sErrorMsg = ""
//        if (userName.isEmpty()) {
//            sErrorMsg = """
//            $sErrorMsg${getString(R.string.shopname_input)}
//
//            """.trimMargin()
//        }
//        if (phone_number.isEmpty()) {
//            sErrorMsg = """
//            $sErrorMsg${getString(R.string.shopphone_input)}
//
//            """.trimIndent()
//        }
//        if (country.isEmpty()) {
//            sErrorMsg = """
//            $sErrorMsg${getString(R.string.region_input)}
//
//            """.trimIndent()
//        }
//        if (admin.isEmpty()) {
//            sErrorMsg = """
//            $sErrorMsg${getString(R.string.admin_input)}
//
//            """.trimIndent()
//        }
//        if (thoroughfare.isEmpty()) {
//            sErrorMsg = """
//            $sErrorMsg${getString(R.string.thoroughfare_input)}
//
//            """.trimMargin()
//        }
        if (userName.isNullOrEmpty() || phone_number.isNullOrEmpty() || country.isNullOrEmpty() || admin.isNullOrEmpty() || thoroughfare.isNullOrEmpty()) {
            binding.ivSave.setImageResource(R.mipmap.ic_save_dis)
            binding.ivSave.isEnabled = false
        }else{
            binding.ivSave.setImageResource(R.mipmap.ic_save_en)
            binding.ivSave.isEnabled = true
//                AlertDialog.Builder(this@BuyerAddAddressActivity)
//                    .setTitle("")
//                    .setMessage(sErrorMsg)
//                    .setPositiveButton("確定"){
//                        // 此為 Lambda 寫法
//                            dialog, which ->dialog.cancel()
//                    }
//                    .show()
//            Toast.makeText(this, "尚有欄位未填寫", Toast.LENGTH_SHORT).show()
        }
    }

}
