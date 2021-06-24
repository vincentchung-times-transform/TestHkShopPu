package com.HKSHOPU.hk.ui.main.shopProfile.activity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventSyncBank
import com.HKSHOPU.hk.data.bean.BankCodeBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
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


class AddBankAccountAfterBuiledctivity : BaseActivity(){
    private lateinit var binding: ActivityAddBankAccountAfterBuildedBinding

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
        binding = ActivityAddBankAccountAfterBuildedBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    Toast.makeText(this@AddBankAccountAfterBuiledctivity, BankCodeBeanList!![position].bank_code, Toast.LENGTH_SHORT).show()
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
//        binding.etBankcode.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
//        binding.etBankname.doAfterTextChanged {
//            bankName = binding.etBankname.text.toString()
//        }
//        binding.etBankname.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))

        binding.etBankaccountname.doAfterTextChanged {
            accountName = binding.etBankaccountname.text.toString()
        }
        binding.etBankaccountname.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))

        binding.etBankaccountnumber.doAfterTextChanged {
            accountNumber = binding.etBankaccountnumber.text.toString()
        }
        binding.etBankaccountnumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))

        binding.layoutBankaccountEdit.setOnClickListener {
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

    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.tvNext.setOnClickListener {
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
//        if (sErrorMsg.isEmpty()) {
        if(bankCode.isNotEmpty() && bankName.isNotEmpty() && accountName.isNotEmpty() && accountNumber.isNotEmpty()){
            doShopBankAccountUpdate(bankCode, bankName, accountName, accountNumber)
        } else {

            Toast.makeText(this, "尚有欄位未填寫", Toast.LENGTH_SHORT).show()

//            AlertDialog.Builder(this@AddBankAccountSellerInfoActivity)
//                .setTitle("")
//                .setMessage(sErrorMsg)
//                .setPositiveButton("確定"){
//                    // 此為 Lambda 寫法
//                        dialog, which ->dialog.cancel()
//                }
//                .show()

        }
    }

    private fun doShopBankAccountUpdate(
        code: String,
        name: String,
        account_name: String,
        account: String
    ) {

        val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        var url = ApiConstants.API_PATH +"shop/"+ shopId + "/bankAccount/"

        Log.d("AddBankAccount2Activity", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddBankAccount2Activity", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    Log.d("AddBankAccount2Activity", "返回資料 ret_val：" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddBankAccountAfterBuiledctivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@AddBankAccountAfterBuiledctivity,
                                BankListActivity::class.java
                            )

                            RxBus.getInstance().post(EventSyncBank())


                            startActivity(intent)
                            finish()

                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(
                                this@AddBankAccountAfterBuiledctivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ShopBankAccountUpdate(url, code, name, account_name, account)
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
                            this@AddBankAccountAfterBuiledctivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        Toast.makeText(
                            this@AddBankAccountAfterBuiledctivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddBankAccountAfterBuiledctivity,
                        ErrorResponse.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
        web.Get_Data(url)
    }

}