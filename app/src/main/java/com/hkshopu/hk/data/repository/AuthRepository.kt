package com.hkshopu.hk.data.repository


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.hkshopu.hk.component.EventLoginSuccess
import com.hkshopu.hk.data.service.AuthService
import com.hkshopu.hk.net.retrofit.RetrofitClient
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.utils.rxjava.SchedulersUtil

import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable

class AuthRepository : BaseRepository(){
    private val service = RetrofitClient.createService(AuthService::class.java)

    fun sociallogin(lifecycleOwner: LifecycleOwner,email: String,facebook_account: String, google_account: String,apple_account: String) : Observable<Any>{
        return service.sociallogin(email,facebook_account,google_account,apple_account)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .map {
                if (it.status == 0) {
                    RxBus.getInstance().post(EventLoginSuccess())
//                        it.ret_val = 0

                }
                it
            }
            .compose(handleBean())
    }

    fun register(lifecycleOwner: LifecycleOwner, account_name : String,email : String,password : String,confirm_password : String,first_name : String,last_name : String,gender : String,birthday : String,phone : String,address: String,region: String,district: String,street_name: String,street_no: String,floor: String,room: String) : Observable<Any> {
        return service.register(account_name,email,password,confirm_password,first_name,last_name,gender,birthday,phone,address,region, district, street_name, street_no, floor, room)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun login(lifecycleOwner: LifecycleOwner,phone : String,password: String) : Observable<Any>{
        return service.login(phone,password)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .map {
                if (it.status == 0) {
                    RxBus.getInstance().post(EventLoginSuccess())
//                    it.ret_val = 0
                }
                it
            }
            .compose(handleBean())
    }

    fun verifycode(lifecycleOwner: LifecycleOwner, email : String) : Observable<Any>{
        return service.verifycode(email)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun emailverify(lifecycleOwner: LifecycleOwner,email : String,validation_code: String) : Observable<Any>{
        return service.emailverify(email,validation_code)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun emailcheck(lifecycleOwner: LifecycleOwner,email : String) : Observable<Any>{
        return service.emailcheck(email)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }


    fun reset_password(lifecycleOwner: LifecycleOwner,email : String, password : String, confirm_password :String) : Observable<Any>{
        return service.reset_password(email,password,confirm_password)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }




}