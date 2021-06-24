package com.HKSHOPU.hk.ui.main.shoppingCart.activity

import MyLinearLayoutManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShoppingCartEditedBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.shoppingCart.adapter.ShoppingCartShopsNestedAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ShoppingCartedEditActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityShoppingCartEditedBinding

    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""

    var shipmentList: MutableList<ShoppingCartProductShipmentItem> = mutableListOf()
    var mAapter_ShoppingCartItems = ShoppingCartShopsNestedAdapter(shipmentList)
    var mutableList_shoppingCartShopItems: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingCartEditedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "25").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

        getShoppingCartItems(MMKV_user_id)
        getGetProductShipmentForBuyer(MMKV_product_id)


        var shop_number = 2
        var shop_products_number = 2



        initMMKV()
        initView()
    }

    fun initMMKV() {

    }

    fun initView() {
        initEvent()
        initClick()

    }


    fun initClick() {

        binding.btnShoppingCartCheckOut.setOnClickListener {

            val intent = Intent(this, ShoppingCartedConfirmedActivity::class.java)
            startActivity(intent)

        }

        binding.titleBackAddshop.setOnClickListener {


            val intent = Intent(this, ProductDetailedPageBuyerViewActivity::class.java)
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

        val intent = Intent(this, ProductDetailedPageBuyerViewActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getShoppingCartItems(user_id: String) {

        val url = ApiConstants.API_HOST+"shopping_cart/${user_id}/shopping_cart_item/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getShoppingCartItems", "返回資料 resStr：" + resStr)
                    Log.d("getShoppingCartItems", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getShoppingCartItems", "返回資料 jsonArray：" + jsonArray.toString())

                        if( jsonArray.length()>0 ){

                            for (i in 0 until jsonArray.length()) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                mutableList_shoppingCartShopItems.add(
                                    Gson().fromJson(
                                    jsonObject.toString(),
                                        ShoppingCartShopItemNestedLayer::class.java
                                ))
                            }
                        }

                        Log.d("getShoppingCartItems", "返回資料 mutableList_shoppingCartShopItems：" + mutableList_shoppingCartShopItems.toString())

                        runOnUiThread {

                            binding.rViewShoppingCartItems.setLayoutManager(MyLinearLayoutManager(this@ShoppingCartedEditActivity,false))
                            binding.rViewShoppingCartItems.adapter = mAapter_ShoppingCartItems
                            mAapter_ShoppingCartItems.set_edit_mode(true)
                            mAapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems)

                        }


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

    private fun doDeleteShoppingCartitems (shopping_cart_item_id: String) {

        val url = ApiConstants.API_HOST+"shopping_cart/delete/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doDeleteShoppingCartitems", "返回資料 resStr：" + resStr)
                    Log.d("doDeleteShoppingCartitems", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")

                    if (ret_val.equals("刪除成功")) {

                        runOnUiThread {
                            mAapter_ShoppingCartItems.notifyDataSetChanged()
                        }

                    }else{

                    }

                } catch (e: JSONException) {


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.doDeleteShoppingCartitems(url, shopping_cart_item_id)
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