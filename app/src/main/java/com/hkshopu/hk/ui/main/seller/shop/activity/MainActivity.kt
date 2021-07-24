package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.onboard.vm.AuthVModel
import com.HKSHOPU.hk.ui.login.vm.ShopVModel

//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}