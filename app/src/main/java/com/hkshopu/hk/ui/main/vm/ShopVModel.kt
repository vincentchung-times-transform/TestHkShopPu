package com.hkshopu.hk.ui.user.vm


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.Base.BaseViewModel
import com.hkshopu.hk.Base.response.StatusResourceObserver
import com.hkshopu.hk.Base.response.UIDataBean
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.data.repository.AuthRepository
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import okhttp3.*
import java.io.IOException

class ShopVModel : BaseViewModel() {


    private val repository = AuthRepository()
    val shopinfoLiveData = MediatorLiveData<List<ShopInfoBean>>()
    fun getShopInfo(successCall: () -> Unit, failed: () -> Unit) {
        //测试环境 使用测试域名
        if (true) {
            ApiConstants.API_HOST = "https://hkshopu-20700.df.r.appspot.com/user/[id]/shop/"
            successCall.invoke()
            return
        }
        //在正式环境下，先获取API域名
        val request = Request.Builder()
            .url(ApiConstants.API_HOST)
            .get()
            .build()
        OkHttpClient()
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    failed.invoke()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val type = object : TypeToken<List<ShopInfoBean>>() {}.type

                        val shopList =
                            GsonProvider.gson.fromJson<List<ShopInfoBean>>(response.body()!!.charStream(), type)
                        if (shopList.isEmpty()) {
                            failed.invoke()
                            return
                        }

                        if (ApiConstants.API_HOST.isNotEmpty() || ApiConstants.API_HOST.isNotBlank()) {

                            successCall.invoke()
                        } else {
                            failed.invoke()
                        }
                        return
                    }
                    failed.invoke()
                }
            })
    }

//    fun login(lifecycleOwner: LifecycleOwner, phone: String, password: String) {
//        repository.login(lifecycleOwner, phone, password)
//            .subscribe(StatusResourceObserver(loginLiveData, silent = false))
//    }



}