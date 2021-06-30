package com.HKSHOPU.hk.ui.main.shoppingCart.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
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
import com.HKSHOPU.hk.component.EventUpdateShoppingCartItemForConfirmed
import com.HKSHOPU.hk.data.bean.ShopAddressListBean
import com.HKSHOPU.hk.data.bean.UserAddressBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class AddBuyerAddressForShoppingCartActivity : BaseActivity() {
    private lateinit var binding: ActivityShopaddressedit2Binding

    var addMode=""
    var shoppingCartShopId=""
    var specId_json=""
    var mutableList_userAddressBean: MutableList<UserAddressBean> = mutableListOf()

    var user_id: String = ""
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
        binding = ActivityShopaddressedit2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("shopdata", 0)
        user_id = MMKV.mmkvWithID("http").getString("UserId", "25").toString()

        var bundle_addMode = intent.getBundleExtra("bundle_addMode")
        addMode = bundle_addMode?.getString("addMode").toString()
        shoppingCartShopId = bundle_addMode?.getString("shoppingCartShopId").toString()
        specId_json = bundle_addMode?.getString("specId_json").toString()

        check_value()
        initView()
        initClick()

    }

    private fun initView() {

        binding.editName.requestFocus()
        binding.editName.doAfterTextChanged {
            companyName = binding.editName.text.toString()
        }
        binding.editName.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editName.requestFocus()
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editName.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editName.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editName.clearFocus()
                    binding.editPhoneNumber.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editName.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editName.clearFocus()

                true
            } else {
                false
            }
        }

        binding.editPhoneNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.editPhoneNumber.doAfterTextChanged {
            phone_number = binding.editPhoneNumber.text.toString()
            phone_country = binding.tvShopphoneCountry.text.toString()
            phone = phone_country + phone_number
        }
        binding.editPhoneNumber.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.editPhoneNumber.requestFocus()
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.editPhoneNumber.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
            }
        })
        binding.editPhoneNumber.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.editPhoneNumber.clearFocus()
                    binding.editCountry.requestFocus()
                    true
                }
                else -> false
            }
        }
        binding.editPhoneNumber.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.editPhoneNumber.clearFocus()

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

        binding.ivSave.setOnClickListener {
           
//            if (companyName.isNotEmpty() && phone_number.isNotEmpty() && country.isNotEmpty()
//                && admin.isNotEmpty() && thoroughfare.isNotEmpty()) {
//
//            }
//
//            Log.d("checkAddUserAddress" , "companyName: ${companyName.toString()}\n" +
//                    "phone_number: ${phone_number.toString()} \n" +
//                    "country: ${country.toString()}\n" +
//                    "admin: ${phone_number.toString()}\n" +
//                    "thoroughfare: ${thoroughfare.toString()}")


            doAddUserAddress(user_id,
                companyName,
                phone_country,
                phone_number,
                country,
                admin,
                thoroughfare,
                feature,
                subaddress,
                floor,
                room,)


        }

    }

    private fun doAddUserAddress(
        user_id: String,
        address_name: String,
        address_country_code: String,
        address_phone: String,
        address_area: String,
        address_district: String,
        address_road: String,
        address_number: String,
        address_other: String,
        address_floor: String,
        address_room: String,
    ) {

        val url = ApiConstants.API_HOST + "shopping_cart/add_buyer_address/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doAddUserAddress", "返回資料 resStr：" + resStr)
                    Log.d("doAddUserAddress", "返回資料 ret_val：" + json.get("ret_val"))
                    var ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        runOnUiThread {
                            Toast.makeText(
                                this@AddBuyerAddressForShoppingCartActivity ,ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }

                        if(addMode.equals("first_address")){

                            doGetUserAddressList(user_id)

                        }else{
                            finish()
                        }

                    } else {

                        runOnUiThread {
                            Toast.makeText(
                                this@AddBuyerAddressForShoppingCartActivity ,ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }


                } catch (e: JSONException) {
                    Log.d("doAddUserAddress", "JSONException： ${e.toString()}" )
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doAddUserAddress", "IOException： ${e.toString()}" )
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doAddUserAddress", "ErrorResponse： ${ErrorResponse.toString()}" )
            }
        })
        web.Do_BuyerAddAddress(
            url,
            user_id,
            address_name,
            address_country_code,
            address_phone,
            address_area,
            address_district,
            address_road,
            address_number,
            address_other,
            address_floor,
            address_room,
        )
    }
    private fun doGetUserAddressList( user_id: String) {

        var url = ApiConstants.API_HOST + "shopping_cart/" + user_id + "/buyer_address/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doGetUserAddressList", "返回資料 resStr：" + resStr)
                    Log.d("doGetUserAddressList", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val userAddressBean: UserAddressBean =
                                Gson().fromJson(jsonObject.toString(), UserAddressBean::class.java)

                            mutableList_userAddressBean.add(userAddressBean)
                        }

                        RxBus.getInstance().post(
                            EventUpdateShoppingCartItemForConfirmed(
                                mutableList_userAddressBean.get(0).id.toString(),
                                mutableList_userAddressBean.get(0).name.toString(),
                                mutableList_userAddressBean.get(0).phone.toString(),
                                mutableList_userAddressBean.get(0).address.toString(),
                                shoppingCartShopId.toString(),
                                specId_json)
                        )
                        finish()

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
        web.Get_Data(url)
    }


    fun check_value() {
        if(companyName.isNotEmpty() && phone_country.isNotEmpty() && phone_number.isNotEmpty() && phone.isNotEmpty()
            && country.isNotEmpty() && admin.isNotEmpty() && thoroughfare.isNotEmpty()) {

            binding.ivSave.setImageResource(R.mipmap.ic_save_en)
            binding.ivSave.isClickable = true

        }else{
            binding.ivSave.setImageResource(R.mipmap.btn_shippingfarestore_disable)
            binding.ivSave.isClickable = false
        }
    }
}