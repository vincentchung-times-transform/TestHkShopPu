package com.hkshopu.hk.data.repository


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.gson.reflect.TypeToken

import com.hkshopu.hk.data.service.AuthService
import com.hkshopu.hk.data.service.ShopmanageService
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.retrofit.RetrofitClient
import com.hkshopu.hk.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import okhttp3.*
import java.io.File
import java.io.IOException

class ShopmanageRepository : BaseRepository(){
    private val service = RetrofitClient.createService(ShopmanageService::class.java)


    fun shopnamecheck(lifecycleOwner: LifecycleOwner,shop_title : String) : Observable<Any>{
        return service.shopnamecheck(shop_title)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .map {
                if (it.status == 0) {

                }
                it
            }
            .compose(handleBean())
    }

    fun adddnewshop(lifecycleOwner: LifecycleOwner,shop_title : String) : Observable<Any>{
        return service.adddnewshop(shop_title)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
            .map {
                if (it.status == 0) {

                }
                it
            }
            .compose(handleBean())
    }


    fun add_product(lifecycleOwner: LifecycleOwner, shop_id : Int, product_category_id : Int, product_sub_category_id :Int, product_title : String, quantity : Int, product_description : String, product_price :Int, shipping_fee : Int, weight : Int, new_secondhand :String, product_pic_list : MutableList<File>, product_spec_list :String, user_id: Int, length : Int, width : Int, height : Int, shipment_method : String) : Observable<Any>{
        return service.add_product(shop_id, product_category_id, product_sub_category_id, product_title, quantity, product_description, product_price, shipping_fee, weight, new_secondhand, product_pic_list, product_spec_list, user_id,  length, width, height, shipment_method)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun syncShippingfare(lifecycleOwner: LifecycleOwner, id : Int ,shipment_settings: String) : Observable<Any>{
        return service.syncShippingfare(id, shipment_settings)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }



}