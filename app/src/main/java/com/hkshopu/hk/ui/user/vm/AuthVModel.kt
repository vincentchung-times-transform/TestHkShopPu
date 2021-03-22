package com.hkshopu.hk.ui.user.vm


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.hkshopu.hk.Base.BaseViewModel
import com.hkshopu.hk.Base.response.StatusResourceObserver
import com.hkshopu.hk.Base.response.UIDataBean
import com.hkshopu.hk.data.repository.AuthRepository

class AuthVModel : BaseViewModel() {


    private val repository = AuthRepository()
    val registerLiveData = MediatorLiveData<UIDataBean<Any>>()
    val socialloginLiveData = MediatorLiveData<UIDataBean<Any>>()
    val loginLiveData = MediatorLiveData<UIDataBean<Any>>()
    val verifycodeLiveData = MediatorLiveData<UIDataBean<Any>>()
    val emailverifyLiveData = MediatorLiveData<UIDataBean<Any>>()
    val emailcheckLiveData = MediatorLiveData<UIDataBean<Any>>()
    val resetLiveData = MediatorLiveData<UIDataBean<Any>>()
    val generateAndSendVerificationCodeData = MediatorLiveData<UIDataBean<Any>>()
    val authenticationCodeData = MediatorLiveData<UIDataBean<Any>>()
    val resetPasswordLiveData = MediatorLiveData<UIDataBean<Any>>()

    fun sociallogin(lifecycleOwner: LifecycleOwner,email: String, facebook_account: String, google_account: String,apple_account: String) {
        repository.sociallogin(lifecycleOwner, email,facebook_account, google_account,apple_account)
                .subscribe(StatusResourceObserver(socialloginLiveData, silent = false))
    }

    fun register(lifecycleOwner: LifecycleOwner, account_name : String,email : String,password : String,confirm_password : String,first_name : String,last_name : String,gender : String,birthday : String,phone : String, address: String,region: String,district: String,street_name: String,street_no: String,floor: String,room: String) {
        repository.register(lifecycleOwner,account_name,email,password,confirm_password,first_name,last_name,gender,birthday,phone,address,region, district, street_name, street_no, floor, room)
            .subscribe(StatusResourceObserver(registerLiveData))
    }

    fun login(lifecycleOwner: LifecycleOwner, phone: String, password: String) {
        repository.login(lifecycleOwner, phone, password)
                .subscribe(StatusResourceObserver(loginLiveData, silent = false))
    }

    fun verifycode(lifecycleOwner: LifecycleOwner) {
        repository.verifycode(lifecycleOwner)
            .subscribe(StatusResourceObserver(verifycodeLiveData, silent = false))
    }

    fun emailverify(lifecycleOwner: LifecycleOwner,email : String,validation_code: String) {
        repository.emailverify(lifecycleOwner,email,validation_code)
            .subscribe(StatusResourceObserver(emailverifyLiveData, silent = false))
    }
    fun emailCheck(lifecycleOwner: LifecycleOwner,email : String) {
        repository.emailcheck(lifecycleOwner,email)
            .subscribe(StatusResourceObserver(emailcheckLiveData, silent = false))
    }

    fun reset(lifecycleOwner: LifecycleOwner, phone: String, password_orig: String, password: String) {
        repository.reset(lifecycleOwner, phone, password_orig, password)
                .subscribe(StatusResourceObserver(resetLiveData, silent = false))
    }

    fun generate_and_send_verification_code(lifecycleOwner: LifecycleOwner) {
        repository.generate_and_send_verification_code(lifecycleOwner)
                .subscribe(StatusResourceObserver(generateAndSendVerificationCodeData, silent = false))
    }

    fun authenticate_email(lifecycleOwner: LifecycleOwner, email: String, validation_code: String) {
        repository.authenticate_email(lifecycleOwner, email, validation_code)
                .subscribe(StatusResourceObserver(authenticationCodeData, silent = false))
    }

    fun reset_password(lifecycleOwner: LifecycleOwner, email: String, password: String, confirm_password : String) {
        repository.reset_password(lifecycleOwner, email, password, confirm_password)
                .subscribe(StatusResourceObserver(resetPasswordLiveData, silent = false))
    }

}