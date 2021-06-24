package com.HKSHOPU.hk.data.repository


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import com.HKSHOPU.hk.data.service.ShopmanageService
import com.HKSHOPU.hk.net.retrofit.RetrofitClient
import com.HKSHOPU.hk.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import java.io.File

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


    fun add_product(lifecycleOwner: LifecycleOwner, shop_id : String, product_category_id : String, product_sub_category_id :String, product_title : String, quantity : Int, product_description : String, product_price :Int, shipping_fee : Int, weight : Int, new_secondhand :String, product_pic_list : MutableList<File>, product_spec_list :String, user_id: String, length : Int, width : Int, height : Int, shipment_method : String) : Observable<Any>{
        return service.add_product(shop_id, product_category_id, product_sub_category_id, product_title, quantity, product_description, product_price, shipping_fee, weight, new_secondhand, product_pic_list, product_spec_list, user_id,  length, width, height, shipment_method)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun syncShippingfare(lifecycleOwner: LifecycleOwner, id : String ,shipment_settings: String) : Observable<Any>{
        return service.syncShippingfare(id, shipment_settings)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }

    fun updateProductStatus(lifecycleOwner: LifecycleOwner, id : String ,status: String) : Observable<Any>{
        return service.updateProductStatus(id, status)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
            .compose(handleBean())
    }




}