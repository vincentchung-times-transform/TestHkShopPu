package com.hkshopu.hk.ui.main.store.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.component.CommonVariable
import com.hkshopu.hk.component.EventGetBankAccountSuccess
import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.data.bean.ProductChildCategoryBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.databinding.ActivityAccountsetupBinding
import com.hkshopu.hk.databinding.ActivityBankaccountlistBinding
import com.hkshopu.hk.ui.main.store.adapter.BankListAdapter
import com.hkshopu.hk.ui.main.store.adapter.CategoryMultiAdapter
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus


class BankListActivity : BaseActivity() {
    private lateinit var binding: ActivityBankaccountlistBinding

    private val adapter = BankListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankaccountlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()

    }
    private fun initView() {
        adapter.setData(CommonVariable.bankaccountlist)
        binding.recyclerview.adapter = adapter

    }

    private fun initVM() {

    }
    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {


                }
            }, {
                it.printStackTrace()
            })

    }

    private fun initClick() {
//        iv_Fb.setOnClickListener {
//
//        }
//
//        iv_Google.setOnClickListener {
//
//            GoogleSignIn()
//        }
//
//        btn_Signup.setOnClickListener {
//
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }

}