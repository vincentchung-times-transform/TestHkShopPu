package com.hkshopu.hk.ui.user.vm


import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.Base.BaseViewModel
import com.hkshopu.hk.Base.response.StatusResourceObserver
import com.hkshopu.hk.Base.response.UIDataBean
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.data.repository.AuthRepository
import com.hkshopu.hk.data.repository.ShopmanageRepository
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import okhttp3.*
import java.io.IOException

class ShopVModel : BaseViewModel() {


    private val repository = ShopmanageRepository()
    val shopnameLiveData = MediatorLiveData<UIDataBean<Any>>()
    val addnewshopLiveData = MediatorLiveData<UIDataBean<Any>>()
    fun shopnamecheck(lifecycleOwner: LifecycleOwner, shop_title: String) {
        repository.shopnamecheck(lifecycleOwner, shop_title)
            .subscribe(StatusResourceObserver(shopnameLiveData, silent = false))
    }

    fun adddnewshop(lifecycleOwner: LifecycleOwner, shop_title: String) {
        repository.adddnewshop(lifecycleOwner, shop_title)
            .subscribe(StatusResourceObserver(addnewshopLiveData, silent = false))
    }

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
    fun getShopCategory(){
        //测试环境 使用测试域名
        if (true) {
            ApiConstants.API_HOST = "https://hkshopu-20700.df.r.appspot.com/shop_category/index/"

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
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("ShopVMmodel", "ShopCategory Response"+ response.body().toString())
                    }
                }
            })
    }
//    fun login(lifecycleOwner: LifecycleOwner, phone: String, password: String) {
//        repository.login(lifecycleOwner, phone, password)
//            .subscribe(StatusResourceObserver(loginLiveData, silent = false))
//    }



}
