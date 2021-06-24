package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshAddressList
import com.HKSHOPU.hk.databinding.*
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AddShopAddressAfterBuildedActivity : BaseActivity() {
    private lateinit var binding: ActivityShopaddressedit2Binding

    private val VM = AuthVModel()
    var companyName: String = ""
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

    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
    private lateinit var settings: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopaddressedit2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("shopdata", 0)
        initView()
        initVM()
        initClick()

    }

    private fun initView() {

        binding.editShopname.doAfterTextChanged {
            companyName = binding.editShopname.text.toString()

        }

        binding.editShopphoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.editShopphoneNumber.doAfterTextChanged {
            phone_number = binding.editShopphoneNumber.text.toString()
            phone_country = binding.tvShopphoneCountry.text.toString()
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

        binding.layoutShopaddressEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
//        binding.ivSave.isEnabled = false


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

    fun EditText.showSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    var file: File? = null
    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }


        binding.ivSave.setOnClickListener {
            var sErrorMsg = ""
            if (companyName.isEmpty()) {
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
                    companyName,
                    phone_country,
                    phone_number,
                    "N",
                    country,
                    admin,
                    thoroughfare,
                    feature,
                    subaddress,
                    floor,
                    room,

                    )
            }else{
                Toast.makeText(this, "尚有欄位未填寫", Toast.LENGTH_SHORT).show()

//                AlertDialog.Builder(this@AddShopAddressSellerInfoActivity)
//                    .setTitle("")
//                    .setMessage(sErrorMsg)
//                    .setPositiveButton("確定"){
//                        // 此為 Lambda 寫法
//                            dialog, which ->dialog.cancel()
//                    }
//                    .show()
            }

        }

    }

    fun processImage(): File? {
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + ".jpg")
        var mImageUri = settings.getString("image", null);
        val decodedString: ByteArray = Base64.decode(mImageUri, Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        val bos = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitmapdata: ByteArray = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file
    }


    private fun doAddShopAddress(

        address_name: String,
        address_country_code: String,
        address_phone: String,
        address_is_phone_show: String,
        address_area: String,
        address_district: String,
        address_road: String,
        address_number: String,
        address_other: String,
        address_floor: String,
        address_room: String,

        ) {
        val url = ApiConstants.API_HOST + "/shop/" + shopId + "/createShopAddress/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopAddress2Activity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopAddress2Activity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        RxBus.getInstance().post(EventRefreshAddressList())

                        val intent = Intent(
                            this@AddShopAddressAfterBuildedActivity,
                            ShopAddressListActivity::class.java
                        )

                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddShopAddressAfterBuildedActivity,
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
        web.Do_ShopAddAddress(
            url,
            address_name,
            address_country_code,
            address_phone,
            address_is_phone_show,
            address_area,
            address_district,
            address_road,
            address_number,
            address_other,
            address_floor,
            address_room,
            )
    }

}