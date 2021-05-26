package com.hkshopu.hk.ui.main.store.activity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventCheckShipmentEnableBtnOrNot
import com.hkshopu.hk.component.EventSyncBank
import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class AddBankAccount2Activity : BaseActivity(){
    private lateinit var binding: ActivityAddbankaccount2Binding

    private val VM = AuthVModel()

    var bankCode: String = ""
    var bankName: String = ""
    var accountName: String = ""
    var accountNumber: String = ""
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddbankaccount2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initClick()

    }

    private fun initView() {
        binding.etBankcode.doAfterTextChanged {
            bankCode = binding.etBankcode.text.toString()
        }
        binding.etBankname.doAfterTextChanged {
            bankName = binding.etBankname.text.toString()
        }
        binding.etBankaccountname.doAfterTextChanged {
            accountName = binding.etBankaccountname.text.toString()
        }
        binding.etBankaccountnumber.doAfterTextChanged {
            accountNumber = binding.etBankaccountnumber.text.toString()
        }

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
        if (sErrorMsg.isEmpty()) {
            doShopBankAccountUpdate(bankCode, bankName, accountName, accountNumber)
        } else {
            AlertDialog.Builder(this@AddBankAccount2Activity)
                .setTitle("")
                .setMessage(sErrorMsg)
                .setPositiveButton("確定"){
                    // 此為 Lambda 寫法
                        dialog, which ->dialog.cancel()
                }
                .show()

        }
    }

    private fun doShopBankAccountUpdate(
        code: String,
        name: String,
        account: String,
        account_name: String
    ) {

        val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
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
                                this@AddBankAccount2Activity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@AddBankAccount2Activity,
                                BankListActivity::class.java
                            )

                            RxBus.getInstance().post(EventSyncBank())


                            startActivity(intent)
                            finish()

                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(
                                this@AddBankAccount2Activity,
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
        web.Do_ShopBankAccountUpdate(url, code, name, account, account_name)
    }


}