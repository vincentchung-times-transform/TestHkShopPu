package com.hkshopu.hk.ui.main.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import java.io.File


class AddBankAccountActivity : BaseActivity(), TextWatcher {
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(p0: Editable?) {
        bankCode = binding.etBankcode.text.toString()
        bankName = binding.etBankname.text.toString()
        accountName = binding.etBankaccountname.text.toString()
        accountNumber = binding.etBankaccountnumber.text.toString()
    }

    private fun initView() {
        binding.etBankcode.addTextChangedListener(this)
        binding.etBankname.addTextChangedListener(this)
        binding.etBankaccountname.addTextChangedListener(this)
        binding.etBankaccountnumber.addTextChangedListener(this)

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
            finish()
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