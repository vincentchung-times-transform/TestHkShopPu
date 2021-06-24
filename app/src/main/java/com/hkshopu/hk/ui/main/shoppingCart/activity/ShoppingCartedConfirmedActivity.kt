package com.HKSHOPU.hk.ui.main.shoppingCart.activity

import MyLinearLayoutManager
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import com.facebook.FacebookSdk
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShoppingCartConfirmedBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shoppingCart.adapter.ShoppingCartShopsNestedAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class ShoppingCartedConfirmedActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityShoppingCartConfirmedBinding

    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""

    var shipmentList: MutableList<ShoppingCartProductShipmentItem> = mutableListOf()
    var mAapter_ShoppingCartItems = ShoppingCartShopsNestedAdapter(shipmentList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingCartConfirmedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()



        var mutableList_shoppingCartShopItems: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()
        var shop_number = 2
        var shop_products_number = 2


        getGetProductShipmentForBuyer(MMKV_product_id)

//        for(i in 0..shop_number-1){
//            var mutableList_shoppingCartProductItems: MutableList<ShoppingCartProductItemNestedLayer> = mutableListOf()
//            for (j in 0..shop_products_number-1){
//                mutableList_shoppingCartProductItems.add(
//                    ShoppingCartProductItemNestedLayer(
//                        "product_icon_url",
//                        "產品${j}",
//                    "尺寸",
//                    "大",
//                    "顏色",
//                    "黑",
//                    0,
//                    0,
//                        temp_logistics,
//                        temp_logistics.get(0).shipment_desc,
//                        temp_logistics.get(0).price))
//            }
//            mutableList_shoppingCartShopItems.add(ShoppingCartShopItemNestedLayer(false, "no image url", "商店${i}",mutableList_shoppingCartProductItems))
//        }



        binding.rViewShoppingCartItems.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewShoppingCartItems.adapter = mAapter_ShoppingCartItems

        mAapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems)
        mAapter_ShoppingCartItems.set_edit_mode(false)


        binding.btnShoppingCartPaypal.setOnClickListener {

        }



        initMMKV()
        initView()
    }

    fun initMMKV() {


    }

    fun initView() {

        val payment_list: MutableList<String> = ArrayList<String>()

        for (i in 0..3) {
            payment_list.add("Payment_${i}")
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            FacebookSdk.getApplicationContext(),
            R.layout.simple_spinner_dropdown_item,
            payment_list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.containerPaymentSpinner.setAdapter(adapter)
        binding.containerPaymentSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


        initEvent()
        initClick()


    }


    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {


            val intent = Intent(this, ShoppingCartedEditActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun afterTextChanged(s: Editable?) {

    }


    override fun onBackPressed() {

        val intent = Intent(this, ShoppingCartedEditActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun getGetProductShipmentForBuyer (product_id: String) {

        val url = ApiConstants.API_HOST+"shopping_cart/${product_id}/product_shipment/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getGetProductShipmentForBuyer", "返回資料 resStr：" + resStr)
                    Log.d("getGetProductShipmentForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("運送方式取得成功!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getGetProductShipmentForBuyer", "返回資料 jsonArray：" + jsonArray.toString())

                        if( jsonArray.length()>0 ){

                            for (i in 0 until jsonArray.length()) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                shipmentList.add(
                                    Gson().fromJson(
                                        jsonObject.toString(),
                                        ShoppingCartProductShipmentItem::class.java
                                    ))
                            }
                        }


                        runOnUiThread {
                            mAapter_ShoppingCartItems.notifyDataSetChanged()
                        }

                        Log.d("getGetProductShipmentForBuyer", "返回資料 shipmentList：" + shipmentList.toString())



                    }



                } catch (e: JSONException) {


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        var boolean: Boolean

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {

                    //尚未設定

                }
            }, {
                it.printStackTrace()
            })

    }

}