package com.hkshopu.hk.data.repository


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.data.service.AuthService
import com.hkshopu.hk.data.service.ShopmanageService
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.retrofit.RetrofitClient
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import okhttp3.*
import java.io.IOException

class ShopmanageRepository : BaseRepository(){
    private val service = RetrofitClient.createService(ShopmanageService::class.java)



//
//    fun login(lifecycleOwner: LifecycleOwner,phone : String,password: String) : Observable<Any>{
//        return service.login(phone,password)
//            .compose(SchedulersUtil.applySchedulers())
//            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
//            .map {
//                if (it.status == 0) {
//                    RxBus.getInstance().post(EventLoginSuccess())
//                    it.ret_val = 0
//                }
//                it
//            }
//            .compose(handleBean())
//    }

}