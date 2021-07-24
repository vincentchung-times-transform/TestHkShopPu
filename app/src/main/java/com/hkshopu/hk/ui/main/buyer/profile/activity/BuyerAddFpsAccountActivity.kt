package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshFpsAccountList
import com.HKSHOPU.hk.data.bean.BankCodeBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerAddFpsAccountActivity : BaseActivity() {
    private lateinit var binding: ActivityAddfpsaccountBinding
    var bankCode: String = ""
    var bankName: String = ""
    var bankAccountName: String = ""
    var phone: String = ""
    var phonetoserver: String = ""
    var email: String = ""

    //phone or email
    var personalInfo_selected = "phone"

    private var spBank: SmartMaterialSpinner<String>? = null
    private var BankCodeList_Descs: MutableList<String> = mutableListOf()
    var BankCodeBeanList: MutableList<BankCodeBean> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddfpsaccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GetBankCode()
        initView()
        initClick()
    }

    private fun initView() {
        binding.etBankaccountname.doAfterTextChanged {
            bankAccountName = binding.etBankaccountname.text.toString()
        }
        binding.etPhone.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
        binding.etPhone.doAfterTextChanged {
            phone = binding.etPhone.text.toString()
        }
        binding.etEmail.doAfterTextChanged {
            email = binding.etEmail.text.toString()
        }
    }

    private fun initClick() {
        binding.layoutBankaccountEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
            checkBtnEnable()
        }
        binding.etBankaccountname.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    checkBtnEnable()

                    binding.etBankaccountname.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.etBankaccountname)

                    true
                }
                else -> false
            }
        }
        binding.tvPhone.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    checkBtnEnable()

                    binding.tvPhone.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.tvPhone)

                    true
                }
                else -> false
            }
        }
        binding.tvEmail.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    checkBtnEnable()

                    binding.tvEmail.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.tvEmail)

                    true
                }
                else -> false
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvPhone.setOnClickListener {
            binding.tvEmail.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvEmail.setTextColor(getColor(R.color.dark_gray))
            binding.tvPhone.setBackgroundResource(R.drawable.customborder_onboard_8dp)
            binding.tvPhone.setTextColor(getColor(R.color.turquoise))
            binding.tvPhoneCountry.visibility = View.VISIBLE
            binding.etPhone.visibility = View.VISIBLE
            binding.etEmail.visibility = View.INVISIBLE

            personalInfo_selected = "phone"
        }
        binding.tvEmail.setOnClickListener {
            binding.tvEmail.setBackgroundResource(R.drawable.customborder_onboard_8dp)
            binding.tvEmail.setTextColor(getColor(R.color.turquoise))
            binding.tvPhone.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvPhone.setTextColor(getColor(R.color.dark_gray))
            binding.tvPhoneCountry.visibility = View.INVISIBLE
            binding.etPhone.visibility = View.INVISIBLE
            binding.etEmail.visibility = View.VISIBLE

            personalInfo_selected="email"
        }
        binding.tvNext.setOnClickListener {
            binding.progressBarBuyerAddFpsAccount.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.VISIBLE
            var userId = MMKV.mmkvWithID("http").getString("UserId", "")
            if (binding.etPhone.visibility == View.VISIBLE) {
                val phoneCountryCode = binding.tvPhoneCountry.text.toString()
                doAddFpsAccount(userId!!, "FPS", bankCode, bankName, bankAccountName, "phone", phoneCountryCode, phone, "")
            } else {
                doAddFpsAccount(userId!!, "FPS", bankCode, bankName, bankAccountName, "", "", "", email)
            }
        }
    }

    private fun initSpinner() {
        runOnUiThread {
            for (i in 0 until BankCodeBeanList.size) {
                BankCodeList_Descs!!.add("${BankCodeBeanList.get(i).bank_code}${"\t"}${BankCodeBeanList.get(i).bank_name}")
            }
            spBank = findViewById(R.id.smartSpinner_bankAccount)
            spBank?.item = BankCodeList_Descs
            spBank?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                    Log.d("spBankSelectedItem", BankCodeBeanList!![position].bank_code)
                    bankCode = BankCodeBeanList!![position].bank_code
                    bankName = BankCodeBeanList!![position].bank_name

                    checkBtnEnable()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
            binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
            binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
        }
    }

    private fun GetBankCode() {
        binding.progressBarBuyerAddFpsAccount.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.VISIBLE

        val url = ApiConstants.API_HOST + "general/bankCode/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("GetBankCode", "返回資料 resStr：" + resStr)
                    Log.d("GetBankCode", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("")) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d(
                            "GetBankCode",
                            "返回資料 jsonArray：" + jsonArray.toString()
                        )
                        for (i in 0..jsonArray.length() - 1) {
                            BankCodeBeanList.add(
                                Gson().fromJson(
                                    jsonArray.getJSONObject(i).toString(),
                                    BankCodeBean::class.java
                                )
                            )
                        }
                        initSpinner()
                    }else{
                        runOnUiThread {
                            binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("GetBankCode_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(
                            this@BuyerAddFpsAccountActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("GetBankCode_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(
                            this@BuyerAddFpsAccountActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("GetBankCode_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(
                        this@BuyerAddFpsAccountActivity,
                        ErrorResponse.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun doAddFpsAccount(
        user_id: String,
        payment_type: String,
        bank_code: String,
        bank_name: String,
        bank_account_name: String,
        contact_type: String,
        phone_country_code: String,
        phone_number: String,
        contact_email: String
    ) {
        val url = ApiConstants.API_HOST + "user/" + user_id + "/paymentAccount/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BirthDayChangeActivity", "返回資料 resStr：" + resStr)
                    Log.d("BirthDayChangeActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        RxBus.getInstance().post(EventRefreshFpsAccountList())
                        runOnUiThread {
                            binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                        }
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerAddFpsAccountActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doAddFpsAccount_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doAddFpsAccount_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doAddFpsAccount_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerAddFpsAccount.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerAddFpsAccount.visibility = View.GONE
                }
            }
        })
        web.Do_addBuyerPayment(
            url,
            user_id,
            payment_type,
            bank_code,
            bank_name,
            bank_account_name,
            contact_type,
            phone_country_code,
            phone_number,
            contact_email

        )
    }

    fun checkBtnEnable() {
        if (personalInfo_selected == "phone") {
            if (bankCode.isNotEmpty() && bankName.isNotEmpty() && bankAccountName.isNotEmpty() && phone.isNotEmpty()) {
                binding.tvNext.isClickable = true
                binding.tvNext.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else {
                binding.tvNext.isClickable = false
                binding.tvNext.setBackgroundResource(R.drawable.customborder_onboard_darkgray)
            }
        } else if (personalInfo_selected == "email") {
            if (bankCode.isNotEmpty() && bankName.isNotEmpty() && bankAccountName.isNotEmpty() && email.isNotEmpty()) {
                binding.tvNext.isClickable = true
                binding.tvNext.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else {
                binding.tvNext.isClickable = false
                binding.tvNext.setBackgroundResource(R.drawable.customborder_onboard_darkgray)
            }
        }
    }
}