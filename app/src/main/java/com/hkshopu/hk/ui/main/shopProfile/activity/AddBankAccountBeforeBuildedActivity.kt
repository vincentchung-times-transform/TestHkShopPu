package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BankCodeBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class AddBankAccountBeforeBuildedActivity : BaseActivity(){
    private lateinit var binding: ActivityAddbankaccountBinding

    private val VM = AuthVModel()

    var bankCode: String = ""
    var bankName: String = ""
    var accountName: String = ""
    var accountNumber: String = ""
    private lateinit var settings: SharedPreferences

    private var spBank: SmartMaterialSpinner<String>? = null
    private var BankCodeList_Descs: MutableList<String> = mutableListOf()

    var BankCodeBeanList:MutableList<BankCodeBean> = mutableListOf<BankCodeBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddbankaccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("shopdata", 0)

        GetBankCode()
        initView()
        initVM()
        initClick()

    }
    private fun initSpinner() {

        runOnUiThread {

            for( i in 0 until BankCodeBeanList.size){
                BankCodeList_Descs!!.add("${BankCodeBeanList.get(i).bank_code}${"\t"}${BankCodeBeanList.get(i).bank_name}")

            }

            spBank = findViewById(R.id.smartSpinner_bankAccount_for_addShop)

            spBank?.item = BankCodeList_Descs

            spBank?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                    Log.d("spBankSelectedItem", BankCodeBeanList!![position].bank_code)
                    bankCode = BankCodeBeanList!![position].bank_code
                    bankName = BankCodeBeanList!![position].bank_name
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }

        }

    }

    private fun initView() {

//        binding.etBankcode.doAfterTextChanged {
//            bankCode = binding.etBankcode.text.toString()
//        }
//        binding.etBankcode.setOnTouchListener (object : View.OnTouchListener {
//            override fun onTouch(v: View, m: MotionEvent): Boolean {
//                // Perform tasks here
//                binding.etBankcode.hasFocus()
//                binding.ivAddbankaccountCheck.visibility = View.INVISIBLE
//                KeyboardUtil.showKeyboard(v)
//                return true
//            }
//        })
//        binding.etBankcode.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                check_value()
//                KeyboardUtil.hideKeyboard(v)
//            }
//        })
//
//        binding.etBankname.doAfterTextChanged {
//            bankName = binding.etBankname.text.toString()
//        }
//        binding.etBankname.setOnTouchListener (object : View.OnTouchListener {
//            override fun onTouch(v: View, m: MotionEvent): Boolean {
//                // Perform tasks here
//                binding.etBankname.hasFocus()
//                binding.ivAddbankaccountCheck.visibility = View.INVISIBLE
//                KeyboardUtil.showKeyboard(v)
//                return true
//            }
//        })
//        binding.etBankname.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                check_value()
//                KeyboardUtil.hideKeyboard(v)
//            }
//        })

        binding.etBankaccountname.doAfterTextChanged {
            accountName = binding.etBankaccountname.text.toString()
        }
        binding.etBankaccountname.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.etBankaccountname.hasFocus()
                binding.ivAddbankaccountCheck.visibility = View.INVISIBLE
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.etBankaccountname.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
                KeyboardUtil.hideKeyboard(v)
            }
        })
        binding.etBankaccountname.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.etBankaccountname.clearFocus()

                true
            } else {
                false
            }
        }

        binding.etBankaccountnumber.doAfterTextChanged {
            accountNumber = binding.etBankaccountnumber.text.toString()
        }
        binding.etBankaccountnumber.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                binding.etBankaccountnumber.hasFocus()
                binding.ivAddbankaccountCheck.visibility = View.INVISIBLE
                KeyboardUtil.showKeyboard(v)
                return true
            }
        })
        binding.etBankaccountnumber.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                check_value()
                KeyboardUtil.hideKeyboard(v)
            }
        })
        binding.etBankaccountnumber.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.etBankaccountnumber.clearFocus()

                true
            } else {
                false
            }
        }


//        binding.layoutBankaccountEdit.setOnClickListener {
//            KeyboardUtil.hideKeyboard(it)
//        }


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

        binding.tvToaddshopaddress.setOnClickListener {
            checkFieldAndNext()
        }

    }
    private fun checkFieldAndNext() {

        var sErrorMsg = ""

        if (bankCode.isEmpty()) {
            sErrorMsg = """
            $sErrorMsg${getString(R.string.bankcode_input)}
            
            """.trimIndent()

        }

        if (bankName.isEmpty()) {
            sErrorMsg = """
            $sErrorMsg${getString(R.string.bankname_input)}
            
            """.trimIndent()
        }
        if (accountName.isEmpty()) {
            sErrorMsg = """
            $sErrorMsg${getString(R.string.bankaccoountname_input)}
            
            """.trimIndent()
        }
        if (accountNumber.isEmpty()) {
            sErrorMsg = """
            $sErrorMsg${getString(R.string.bankaccount_input)}
            
            """.trimIndent()
        }

//        if (sErrorMsg.isEmpty())
        if(bankCode.isNotEmpty() && bankName.isNotEmpty() && accountName.isNotEmpty() && accountNumber.isNotEmpty()){

            binding.ivAddbankaccountCheck.visibility = View.VISIBLE
            settings.edit()
                    .putString("bankcode", bankCode)
                    .putString("bankname", bankName)
                    .putString("accountname", accountName)
                    .putString("accountnumber", accountNumber)
                    .apply()

            val intent = Intent(this, AddShopAddressBeforeBuildActivity::class.java)
            startActivity(intent)
//            finish()

        } else {
            binding.ivAddbankaccountCheck.visibility = View.GONE

//            Toast.makeText(this, "尚有欄位未填寫", Toast.LENGTH_SHORT).show()
//            AlertDialog.Builder(this@AddBankAccountActivity)
//                .setTitle("")
//                .setMessage(sErrorMsg)
//                .setPositiveButton("確定"){
//                    // 此為 Lambda 寫法
//                        dialog, which ->dialog.cancel()
//                }
//                .show()

        }
    }

    fun check_value() {
        if(bankCode.isNotEmpty() && bankName.isNotEmpty() && accountName.isNotEmpty() && accountNumber.isNotEmpty()) {

            binding.tvToaddshopaddress.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            binding.tvToaddshopaddress.setTextColor(getColor(R.color.white))
            binding.ivAddbankaccountCheck.visibility = View.VISIBLE
            binding.ivAddbankaccountCheck.isClickable = true

        }else{
            binding.tvToaddshopaddress.setBackgroundResource(R.drawable.customborder_turquise)
            binding.tvToaddshopaddress.setTextColor(getColor(R.color.turquoise))
            binding.ivAddbankaccountCheck.visibility = View.GONE
            binding.ivAddbankaccountCheck.isClickable = false
        }
    }

    private fun GetBankCode() {

        val url = ApiConstants.API_HOST+"general/bankCode/"

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

                        for(i in 0..jsonArray.length()-1){
                            BankCodeBeanList.add(
                                Gson().fromJson(
                                jsonArray.getJSONObject(i).toString(),
                                BankCodeBean::class.java
                            ))
                        }


                        initSpinner()

                    }


//                    runOnUiThread {
//                        Toast.makeText(
//                            this@AddBankAccountActivity,
//                            ret_val.toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                    }


                } catch (e: JSONException) {

                    runOnUiThread {
                        Toast.makeText(
                            this@AddBankAccountBeforeBuildedActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        Toast.makeText(
                            this@AddBankAccountBeforeBuildedActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddBankAccountBeforeBuildedActivity,
                        ErrorResponse.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
        web.Get_Data(url)
    }

}