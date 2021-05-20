package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil


class AddBankAccountActivity : BaseActivity(){
    private lateinit var binding: ActivityAddbankaccountBinding

    private val VM = AuthVModel()

    var bankCode: String = ""
    var bankName: String = ""
    var accountName: String = ""
    var accountNumber: String = ""
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddbankaccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("shopdata", 0)
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
        if (sErrorMsg.isEmpty()) {
            binding.ivAddbankaccountCheck.visibility = View.VISIBLE
            settings.edit()
                    .putString("bankcode", bankCode)
                    .putString("bankname", bankName)
                    .putString("accountname", accountName)
                    .putString("accountnumber", accountNumber)
                    .apply()
            val intent = Intent(this, AddShopAddressActivity::class.java)
            startActivity(intent)
//            finish()
        } else {
            AlertDialog.Builder(this@AddBankAccountActivity)
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