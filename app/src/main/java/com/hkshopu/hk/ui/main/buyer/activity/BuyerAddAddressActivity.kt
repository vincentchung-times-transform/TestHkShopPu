package com.HKSHOPU.hk.ui.main.buyer.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshAddressList
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

        initView()
        initClick()
    }

    fun initView() {
        binding.etUsername.doAfterTextChanged {
            userName = binding.etUsername.text.toString()

        }

        binding.editUserphoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.editUserphoneNumber.doAfterTextChanged {
            phone_number = binding.editUserphoneNumber.text.toString()
            phone_country = binding.tvUserphoneCountry.text.toString()
            phone = phone_country + phone_number

        }


        binding.editCountry.doAfterTextChanged {

            country = binding.editCountry.text.toString()

        }


        binding.editAdmin.doAfterTextChanged {
            admin = binding.editAdmin.text.toString()

        }


        binding.editthoroughfare.doAfterTextChanged {
            thoroughfare = binding.editthoroughfare.text.toString()

        }


        binding.editfeature.doAfterTextChanged {
            feature = binding.editfeature.text.toString()

        }

        binding.editsubaddress.doAfterTextChanged {
            subaddress = binding.editsubaddress.text.toString()

        }

        binding.editfloor.doAfterTextChanged {
            floor = binding.editfloor.text.toString()

        }

        binding.editroom.doAfterTextChanged {
            room = binding.editroom.text.toString()

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
            var sErrorMsg = ""
            if (userName.isEmpty()) {
                sErrorMsg = """
            $sErrorMsg${getString(R.string.shopname_input)}
            
            """.trimMargin()
            }
            if (phone_number.isEmpty()) {
                sErrorMsg = """
            $sErrorMsg${getString(R.string.shopphone_input)}
            
            """.trimIndent()
            }
            if (country.isEmpty()) {
                sErrorMsg = """
            $sErrorMsg${getString(R.string.region_input)}
            
            """.trimIndent()
            }
            if (admin.isEmpty()) {
                sErrorMsg = """
            $sErrorMsg${getString(R.string.admin_input)}
            
            """.trimIndent()
            }
            if (thoroughfare.isEmpty()) {
                sErrorMsg = """
            $sErrorMsg${getString(R.string.thoroughfare_input)}
            
            """.trimMargin()
            }
            if (sErrorMsg.isEmpty()) {
                binding.ivSave.setImageResource(R.mipmap.ic_save_en)
                binding.ivSave.isEnabled = true
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
            }else{
                AlertDialog.Builder(this@BuyerAddAddressActivity)
                    .setTitle("")
                    .setMessage(sErrorMsg)
                    .setPositiveButton("確定"){
                        // 此為 Lambda 寫法
                            dialog, which ->dialog.cancel()
                    }
                    .show()
            }

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
        val url = ApiConstants.API_HOST + "/shop/shopping_cart/add_buyer_address/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        RxBus.getInstance().post(EventRefreshUserAddressList())

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
}
