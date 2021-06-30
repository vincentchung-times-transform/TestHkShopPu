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
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventAddShopSuccess
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


class AddShopAddressBeforeBuildActivity : BaseActivity() {
    private lateinit var binding: ActivityShopaddresseditBinding

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

    val userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    private lateinit var settings: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopaddresseditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("shopdata", 0)

        initView()
        initVM()
        initClick()

    }

    private fun initView() {

        binding.editShopname.requestFocus()
        binding.editShopname.doAfterTextChanged {
            companyName = binding.editShopname.text.toString()
        }
        binding.editShopname.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editShopname.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editShopname.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editShopname.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editShopname.clearFocus()
                    binding.editShopphoneNumber.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editShopname.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editShopname.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editShopphoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.editShopphoneNumber.doAfterTextChanged {
            phone_number = binding.editShopphoneNumber.text.toString()
            phone_country = binding.tvShopphoneCountry.text.toString()
            phone = phone_country + phone_number
        }
        binding.editShopphoneNumber.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editShopphoneNumber.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editShopphoneNumber.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()

            }
        })
        binding.editShopphoneNumber.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editShopphoneNumber.clearFocus()
                    binding.editCountry.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editShopphoneNumber.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editShopphoneNumber.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editCountry.doAfterTextChanged {
            country = binding.editCountry.text.toString()
        }
        binding.editCountry.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editCountry.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editCountry.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editCountry.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editCountry.clearFocus()
                    binding.editAdmin.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editCountry.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editCountry.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editAdmin.doAfterTextChanged {
            admin = binding.editAdmin.text.toString()
        }
        binding.editAdmin.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editAdmin.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editAdmin.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editAdmin.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editAdmin.clearFocus()
                    binding.editthoroughfare.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editAdmin.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editAdmin.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editthoroughfare.doAfterTextChanged {
            thoroughfare = binding.editthoroughfare.text.toString()
        }
        binding.editthoroughfare.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editthoroughfare.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editthoroughfare.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
       binding.editthoroughfare.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editthoroughfare.clearFocus()
                    binding.editfeature.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editthoroughfare.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editthoroughfare.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editfeature.doAfterTextChanged {
            feature = binding.editfeature.text.toString()
        }
        binding.editfeature.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editfeature.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editfeature.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editfeature.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editfeature.clearFocus()
                    binding.editsubaddress.requestFocus()
                    true
                }
                else -> false
            }
        }
         binding.editfeature.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editfeature.clearFocus()
                    binding.editsubaddress.requestFocus()
                    true
                }
                else -> false
            }
        }

        binding.editfeature.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editfeature.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editsubaddress.doAfterTextChanged {
            subaddress = binding.editsubaddress.text.toString()
        }

        binding.editsubaddress.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editsubaddress.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editsubaddress.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editsubaddress.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editsubaddress.clearFocus()
                    binding.editfloor.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editsubaddress.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editsubaddress.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editfloor.doAfterTextChanged {
            floor = binding.editfloor.text.toString()
        }
        binding.editfloor.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editfloor.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editfloor.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()

            }
        })
        binding.editfloor.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editfloor.clearFocus()
                    binding.editroom.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editfloor.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editfloor.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editroom.doAfterTextChanged {
            room = binding.editroom.text.toString()
        }
        binding.editroom.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editroom.requestFocus()
                binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
                binding.ivAddbankaddressCheck.visibility = View.GONE
                binding.tvCreateshop.isClickable = false
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editroom.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editroom.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editroom.clearFocus()
                    binding.layoutShopaddressEdit.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editroom.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editroom.clearFocus()

                true
            } else {
                false
            }
        }

        binding.layoutShopaddressEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
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

    fun EditText.showSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    var file: File? = null
    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.tvCreateshop.setOnClickListener {
           
            if (companyName.isNotEmpty() && phone_number.isNotEmpty() && country.isNotEmpty()
                && admin.isNotEmpty() && thoroughfare.isNotEmpty()) {
                file = processImage()
                var shopName = settings.getString("shopname", "")
                var shop_category_id1 = settings.getString("shop_category_id1", "").toString()
                var shop_category_id2 = settings.getString("shop_category_id2", "").toString()
                var shop_category_id3 = settings.getString("shop_category_id3", "").toString()
                var bankCode = settings.getString("bankcode", "")
                var bankName = settings.getString("bankname", "")
                var accountName = settings.getString("accountname", "")
                var accountNumber = settings.getString("accountnumber", "")


                if(!shop_category_id1.isNullOrEmpty()){
                    CommonVariable.shopCategorySelectedListForAdd.add(shop_category_id1.toString())
                    if (!shop_category_id2.isNullOrEmpty()){
                        CommonVariable.shopCategorySelectedListForAdd.add(shop_category_id2.toString())
                        if (!shop_category_id3.isNullOrEmpty()){
                            CommonVariable.shopCategorySelectedListForAdd.add(shop_category_id3.toString())
                        }
                    }
                }


                doAddShop(
                    shopName!!,
                    userId.toString(),
                    CommonVariable.shopCategorySelectedListForAdd,
                    bankCode!!,
                    bankName!!,
                    accountName!!,
                    accountNumber!!,
                    companyName,
                    phone_country,
                    phone_number,
                    "",
                    country,
                    admin,
                    thoroughfare,
                    feature,
                    subaddress,
                    floor,
                    room,
                    file!!
                )
            } else {

                Toast.makeText(this, "尚有欄位未填寫", Toast.LENGTH_SHORT).show()

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


    private fun doAddShop(
        shop_title: String,
        user_id: String,
        shop_category_id: List<String>,
        bank_code: String,
        bank_name: String,
        bank_account_name: String,
        bank_account: String,
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
        postImg: File
    ) {
        val url = ApiConstants.API_HOST + "/shop/save/"
        val editor = settings.edit()
        editor.clear()
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopAddressActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopAddressActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        RxBus.getInstance().post(EventAddShopSuccess())

                        val intent = Intent(
                            this@AddShopAddressBeforeBuildActivity,
                            ShopmenuActivity::class.java
                        )
                        startActivity(intent)
                        finish()
//                        runOnUiThread {
//
//                            Toast.makeText(
//                                this@AddShopAddressActivity,
//                                ret_val.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddShopAddressBeforeBuildActivity,
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
        web.Do_ShopAdd(
            url,
            shop_title,
            user_id,
            shop_category_id,
            bank_code,
            bank_name,
            bank_account_name,
            bank_account,
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
            postImg
        )
    }

    fun check_value() {
        if(companyName.isNotEmpty() && phone_country.isNotEmpty() && phone_number.isNotEmpty() && phone.isNotEmpty()
            && country.isNotEmpty() && admin.isNotEmpty() && thoroughfare.isNotEmpty()) {

            binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            binding.tvCreateshop.setTextColor(getColor(R.color.white))
            binding.ivAddbankaddressCheck.visibility = View.VISIBLE
            binding.tvCreateshop.isClickable = true

        }else{
            binding.tvCreateshop.setBackgroundResource(R.drawable.customborder_turquise)
            binding.tvCreateshop.setTextColor(getColor(R.color.turquoise))
            binding.ivAddbankaddressCheck.visibility = View.GONE
            binding.tvCreateshop.isClickable = false
        }
    }


}