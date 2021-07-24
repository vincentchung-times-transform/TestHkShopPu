package com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity

import MyLinearLayoutManager
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventCheckedShoppingCartItem
import com.HKSHOPU.hk.component.EventRefreshShoppingCartItemCount
import com.HKSHOPU.hk.component.EventRemoveShoppingCartItem
import com.HKSHOPU.hk.component.EventUpdateShoppingCartItem
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShoppingCartEditedBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.product.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.adapter.ShoppingCartShopsNestedAdapter
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.facebook.FacebookSdk
import com.google.gson.GsonBuilder
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class ShoppingCartEditActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityShoppingCartEditedBinding

    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""

    var shipmentList: MutableList<ShoppingCartProductShipmentItem> = mutableListOf()
    var mAdapter_ShoppingCartItems = ShoppingCartShopsNestedAdapter(this)
    var mutableList_shoppingCartShopItems: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()

    var final_total_price = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingCartEditedBinding.inflate(layoutInflater)
        setContentView(binding.root)


        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

        getShoppingCartItems(MMKV_user_id)

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

        binding.cbCheckedAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){

                mutableList_shoppingCartShopItems =  mAdapter_ShoppingCartItems.getDatas()
                for (i in 0 until mutableList_shoppingCartShopItems.size){
                    mutableList_shoppingCartShopItems.get(i).shop_checked = true
                    for (j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                        mutableList_shoppingCartShopItems.get(i).productList.get(j).product_checked = true
                    }
                }
                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, false)

            }else{

                mutableList_shoppingCartShopItems =  mAdapter_ShoppingCartItems.getDatas()
                for (i in 0 until mutableList_shoppingCartShopItems.size){
                    mutableList_shoppingCartShopItems.get(i).shop_checked = false
                    for (j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                        mutableList_shoppingCartShopItems.get(i).productList.get(j).product_checked = false
                    }
                }
                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, false)

            }
        }

        binding.btnShoppingCartCheckOut.setOnClickListener {

            var checked_count = 0

            var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

            for (i in 0 until mutableList_shoppingCartShopItems.size){
                if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                    checked_count +=1
                }
            }

            if(checked_count>0){

                var forward_mode = "checkOut"

                var mutableList_cartItemQuantity: MutableList<CartItemQuantityBean> = mutableListOf()

                for (i in 0 until mutableList_shoppingCartShopItems.size){
                    for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
                        var cartItemQuantityBean: CartItemQuantityBean = CartItemQuantityBean()
                        cartItemQuantityBean.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id
                        cartItemQuantityBean.new_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity
                        mutableList_cartItemQuantity.add(cartItemQuantityBean)
                    }
                }

                val gson = Gson()
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                val jsonTutList_CartQuantity: String = gson.toJson(mutableList_cartItemQuantity)
                Log.d("ShoppingCartedEditActivity", jsonTutList_CartQuantity.toString())
                val jsonTutListPretty_CartQuantity: String = gsonPretty.toJson(mutableList_cartItemQuantity)
                Log.d("ShoppingCartedEditActivity", jsonTutListPretty_CartQuantity.toString())

                doShoppingCartQuantityDoubleChecking(MMKV_user_id, jsonTutListPretty_CartQuantity, forward_mode)
            }else{
                Toast.makeText(this, "請至少勾選一個項目", Toast.LENGTH_SHORT).show()
            }


        }

        binding.titleBackAddshop.setOnClickListener {
            finish()
//            var forward_mode = "finish"
//
//            var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
//            var mutableList_cartItemQuantity: MutableList<CartItemQuantityBean> = mutableListOf()
//
//            for (i in 0 until mutableList_shoppingCartShopItems.size){
//                for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
//                    var cartItemQuantityBean: CartItemQuantityBean = CartItemQuantityBean()
//                    cartItemQuantityBean.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id
//                    cartItemQuantityBean.new_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity
//                    mutableList_cartItemQuantity.add(cartItemQuantityBean)
//                }
//            }
//
//            val gson = Gson()
//            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
//
//            val jsonTutList_CartQuantity: String = gson.toJson(mutableList_cartItemQuantity)
//            Log.d("ShoppingCartedEditActivity", jsonTutList_CartQuantity.toString())
//            val jsonTutListPretty_CartQuantity: String = gsonPretty.toJson(mutableList_cartItemQuantity)
//            Log.d("ShoppingCartedEditActivity", jsonTutListPretty_CartQuantity.toString())
//
//            doShoppingCartQuantityDoubleChecking(MMKV_user_id, jsonTutListPretty_CartQuantity, forward_mode)

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
        finish()
//        var forward_mode = "finish"
//
//        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
//        var mutableList_cartItemQuantity: MutableList<CartItemQuantityBean> = mutableListOf()
//
//        for (i in 0 until mutableList_shoppingCartShopItems.size){
//            for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
//                var cartItemQuantityBean: CartItemQuantityBean = CartItemQuantityBean()
//                cartItemQuantityBean.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id
//                cartItemQuantityBean.new_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity
//                mutableList_cartItemQuantity.add(cartItemQuantityBean)
//            }
//        }
//
//        val gson = Gson()
//        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
//
//        val jsonTutList_CartQuantity: String = gson.toJson(mutableList_cartItemQuantity)
//        Log.d("ShoppingCartedEditActivity", jsonTutList_CartQuantity.toString())
//        val jsonTutListPretty_CartQuantity: String = gsonPretty.toJson(mutableList_cartItemQuantity)
//        Log.d("ShoppingCartedEditActivity", jsonTutListPretty_CartQuantity.toString())
//
//        doShoppingCartQuantityDoubleChecking(MMKV_user_id, jsonTutListPretty_CartQuantity, forward_mode)
    }

    private fun getShoppingCartItems(user_id: String) {

        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        Log.d("getShoppingCartItems", "user_id: ${user_id}")
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

                            runOnUiThread {

                                binding.containerGoToShopping.visibility = View.GONE
                                binding.containerRViewShoppingCartItems.visibility = View.VISIBLE
                                binding.bottomStatusSelecting.visibility = View.VISIBLE

                            }

                            for (i in 0 until jsonArray.length()) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                mutableList_shoppingCartShopItems.add(
                                    Gson().fromJson(
                                    jsonObject.toString(),
                                        ShoppingCartShopItemNestedLayer::class.java
                                ))
                                
                            }

                            Log.d("getShoppingCartItems", "返回資料 mutableList_shoppingCartShopItems：" + mutableList_shoppingCartShopItems.toString())

                            runOnUiThread {

                                binding.rViewShoppingCartItems.setLayoutManager(MyLinearLayoutManager(this@ShoppingCartEditActivity,false))
                                binding.rViewShoppingCartItems.adapter = mAdapter_ShoppingCartItems
                                mAdapter_ShoppingCartItems.set_edit_mode(true)
                                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, false)

                            }


                        }else{
                            runOnUiThread {
                                binding.containerGoToShopping.visibility = View.VISIBLE
                                binding.containerRViewShoppingCartItems.visibility = View.GONE
                                binding.bottomStatusSelecting.visibility = View.GONE
                                binding.btnGoToShopping.setOnClickListener {
                                    val intent = Intent(this@ShoppingCartEditActivity, ShopmenuActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getShoppingCartItems: JSONException" + "${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getShoppingCartItems: IOException" + "${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getShoppingCartItems: ErrorResponse" + "${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.Get_Data(url)
    }

//    private fun getProductShipmentForBuyer (product_id: String): MutableList<ShoppingCartProductShipmentItem> {
//
//        val url = ApiConstants.API_HOST+"shopping_cart/${product_id}/product_shipment/"
//        var item_shipment_List : MutableList<ShoppingCartProductShipmentItem> = mutableListOf()
//        val web = Web(object : WebListener {
//            override fun onResponse(response: Response) {
//                var resStr: String? = ""
//                try {
//
//                    resStr = response.body()!!.string()
//                    val json = JSONObject(resStr)
//                    Log.d("getProductShipmentForBuyer", "返回資料 resStr：" + resStr)
//                    Log.d("getProductShipmentForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
//                    val ret_val = json.get("ret_val")
//                    if (ret_val.equals("運送方式取得成功!")) {
//
//                        val jsonArray: JSONArray = json.getJSONArray("data")
//                        Log.d("getProductShipmentForBuyer", "返回資料 jsonArray：" + jsonArray.toString())
//
//                        if( jsonArray.length()>0 ){
//
//
//                            for (i in 0 until jsonArray.length()) {
//
//                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
//                                item_shipment_List.add(
//                                    Gson().fromJson(
//                                        jsonObject.toString(),
//                                        ShoppingCartProductShipmentItem::class.java
//                                    ))
//                            }
//
//
//                        }else{
//
//                        }
//
//                    }else{
//
//                    }
//
//                } catch (e: JSONException) {
//                    Log.d("errormessage", "getProductShipmentForBuyer: JSONException" + "${e.toString()}")
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    Log.d("errormessage", "getProductShipmentForBuyer: IOException" + "${e.toString()}")
//                }
//            }
//
//            override fun onErrorResponse(ErrorResponse: IOException?) {
//                Log.d("errormessage", "getProductShipmentForBuyer: ErrorResponse" + "${ErrorResponse.toString()}")
//            }
//        })
//        web.Get_Data(url)
//
//        return item_shipment_List
//    }
    
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRemoveShoppingCartItem -> {

                        var shop_id = it.shop_id
                        var id_list = it.item_id_list_json
                        var position = it.position

                        if(shop_id.equals("") && id_list.equals("")){
                            mAdapter_ShoppingCartItems.onItemDissmiss(position)
                        }else{
                            doDeleteShoppingCartitems(MMKV_user_id, shop_id, id_list, position)
                        }

                    }
                    is EventUpdateShoppingCartItem ->{

                        var shopping_cart_item_id : String = it.shopping_cart_item_id
                        var new_quantity : String = it.new_quantity
                        var selected_shipment_id : String = it.selected_shipment_id
                        var selected_user_address_id: String = it.selected_user_address_id
                        var selected_payment_id : String = it.selected_payment_id

                        Log.d("checkEventUpdateShoppingCartItem", "shopping_cart_item_id: ${shopping_cart_item_id.toString()} ; " +
                            "new_quantity: ${new_quantity.toString()} ; " +
                                "\n selected_shipment_id: ${selected_shipment_id.toString()} ; " +
                                "\n selected_user_address_id: ${selected_user_address_id.toString()} ; " +
                                "\n selected_payment_id: ${selected_payment_id.toString()} ; ")

                        doUpdateShoppingCartitems(shopping_cart_item_id, new_quantity, selected_shipment_id, selected_user_address_id, selected_payment_id)

                    }
                    is EventCheckedShoppingCartItem -> {

                        final_total_price=0
                        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

                        for(i in 0 until mutableList_shoppingCartShopItems.size){
                            if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                                    final_total_price += mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.spec_quantity_sum_price.toInt()+ mutableList_shoppingCartShopItems.get(i).productList.get(j).selected_shipment.shipment_price
                                }
                            }
                        }

//                        var shipment_selected_list: MutableList<ShoppingCartProductShipmentItem> = mutableListOf()
//                        for(i in 0 until mutableList_shoppingCartShopItems.size){
//                            if(mutableList_shoppingCartShopItems.get(i).shop_checked){
//                                for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
//                                    var shoppingCartProductShipmentItem = ShoppingCartProductShipmentItem()
//                                    shoppingCartProductShipmentItem.shipment_id =  mutableList_shoppingCartShopItems.get(i).productList.get(j).shipmentSelected.shipment_id
//                                    shoppingCartProductShipmentItem.shipment_desc =  mutableList_shoppingCartShopItems.get(i).productList.get(j).shipmentSelected.shipment_desc
//                                    shoppingCartProductShipmentItem.shipment_price =  mutableList_shoppingCartShopItems.get(i).productList.get(j).shipmentSelected.shipment_price
//                                    shipment_selected_list.add(shoppingCartProductShipmentItem)
//                                }
//                            }
//                        }


                        runOnUiThread {
                            binding.tvFinalTotalPrice.setText(final_total_price.toString())
                        }


                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun doDeleteShoppingCartitems (user_id: String, shop_id: String, shopping_cart_item_id: String, position: Int) {

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
                            Toast.makeText(this@ShoppingCartEditActivity , ret_val.toString(), Toast.LENGTH_SHORT).show()
                            mAdapter_ShoppingCartItems.onItemDissmiss(position)

                            if(mAdapter_ShoppingCartItems.getDatas().size == 0 ){
                                binding.containerGoToShopping.visibility = View.VISIBLE
                                binding.containerRViewShoppingCartItems.visibility = View.GONE
                                binding.bottomStatusSelecting.visibility = View.GONE
                            }else{
                                binding.containerGoToShopping.visibility = View.GONE
                                binding.containerRViewShoppingCartItems.visibility = View.VISIBLE
                                binding.bottomStatusSelecting.visibility = View.VISIBLE
                            }
                        }

                        RxBus.getInstance().post(EventRefreshShoppingCartItemCount())

                    }else{

                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                        }

                    }

                } catch (e: JSONException) {
                    Log.d("doDeleteShoppingCartitems", "JSONException：" + e.toString())

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doDeleteShoppingCartitems", "IOException：" + e.toString())

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doDeleteShoppingCartitems", "ErrorResponse：" + ErrorResponse.toString())

            }
        })
        web.doDeleteShoppingCartitems(url, user_id, shop_id, shopping_cart_item_id)

    }

    private fun doUpdateShoppingCartitems (shopping_cart_item_id : String, new_quantity : String, selected_shipment_id : String, selected_user_address_id: String, selected_payment_id : String) {

        val url = ApiConstants.API_HOST+"shopping_cart/update/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doUpdateShoppingCartitems", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateShoppingCartitems", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")

                    if (ret_val.equals("購物車更新成功!")) {

//                        runOnUiThread {
//                            Toast.makeText(this@ShoppingCartedEditActivity , ret_val.toString(), Toast.LENGTH_SHORT).show()
//                        }


                        final_total_price=0
                        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
                        for(i in 0 until mutableList_shoppingCartShopItems.size){
                            if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                                    final_total_price += mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.spec_quantity_sum_price.toInt()+ mutableList_shoppingCartShopItems.get(i).productList.get(j).selected_shipment.shipment_price
                                        .toInt()
                                }
                            }
                        }

                        runOnUiThread {
                            binding.tvFinalTotalPrice.setText(final_total_price.toString())
                        }

                    }else{

                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                        }

                    }

                } catch (e: JSONException) {
                    Log.d("doUpdateShoppingCartitems", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUpdateShoppingCartitems", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUpdateShoppingCartitems", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.doUpdateShoppingCartitems(url, shopping_cart_item_id, new_quantity, selected_shipment_id, selected_user_address_id, selected_payment_id)
    }

    private fun doShoppingCartQuantityDoubleChecking (user_id : String, new_quantity : String, forward_mode: String) {
        Log.d("doShoppingCartQuantityDoubleChecking", "user_id: ${user_id} \n " +
                "new_quantity: ${new_quantity} \n " +
                "forward_mode: ${forward_mode} \n ")
        val url = ApiConstants.API_HOST+"shopping_cart/checkout/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doShoppingCartQuantityDoubleChecking", "返回資料 resStr：" + resStr)
                    val ret_val = json.get("ret_val")

                    if (ret_val.equals("更新購物車數量成功!")) {
                        Log.d("doShoppingCartQuantityDoubleChecking", "返回資料 ret_val：" + "數量二次送出成功")

//                        runOnUiThread {
//                            Toast.makeText(this@ShoppingCartedEditActivity , ret_val.toString(), Toast.LENGTH_SHORT).show()
//                        }
                        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
                        var mutableList_shoppingCartShopItems_checked: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()

                        for (i in 0 until mutableList_shoppingCartShopItems.size){
                            if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                mutableList_shoppingCartShopItems_checked.add(mutableList_shoppingCartShopItems.get(i))
                            }
                        }

                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                        val jsonTutList_shoppingCart: String = gson.toJson(mutableList_shoppingCartShopItems_checked)
                        Log.d("ShoppingCartedEditActivity", jsonTutList_shoppingCart.toString())
                        val jsonTutListPretty_shoppingCart: String = gsonPretty.toJson(mutableList_shoppingCartShopItems_checked)
                        Log.d("ShoppingCartedEditActivity", jsonTutListPretty_shoppingCart.toString())

                        if(forward_mode == "checkOut"){
                            val intent = Intent(this@ShoppingCartEditActivity, ShoppingCartConfirmedActivity::class.java)
                            var bundle = Bundle()
                            bundle.putString("mutableList_shoppingCartShopItems_checked_json",jsonTutList_shoppingCart)
                            intent.putExtra("bundle",bundle)
                            startActivity(intent)
                        }else{
                            finish()
                        }

                    }else if(ret_val.equals("購買數量超過剩餘庫存!")){
                        Log.d("doShoppingCartQuantityDoubleChecking", "返回資料 ret_val：" + "購買數量超過剩餘庫存!")
                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartEditActivity , "購買數量超過剩餘庫存!", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Log.d("doShoppingCartQuantityDoubleChecking", "返回資料 ret_val：" + "數量二次送出失敗")
                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "doShoppingCartQuantityDoubleChecking: JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "doShoppingCartQuantityDoubleChecking: IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartEditActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "doShoppingCartQuantityDoubleChecking: ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.doShoppingCartQuantityDoubleChecking(url, user_id, new_quantity)
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
//            return false
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onPause() {
        super.onPause()

        if(mAdapter_ShoppingCartItems.getDatas().size == 0 ){
         //dont do double checking
        }else{
            var forward_mode = "finish"

            var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
            var mutableList_cartItemQuantity: MutableList<CartItemQuantityBean> = mutableListOf()

            for (i in 0 until mutableList_shoppingCartShopItems.size){
                for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
                    var cartItemQuantityBean: CartItemQuantityBean = CartItemQuantityBean()
                    cartItemQuantityBean.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id
                    cartItemQuantityBean.new_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity
                    mutableList_cartItemQuantity.add(cartItemQuantityBean)
                }
            }

            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonTutList_CartQuantity: String = gson.toJson(mutableList_cartItemQuantity)
            Log.d("ShoppingCartedEditActivity", jsonTutList_CartQuantity.toString())
            val jsonTutListPretty_CartQuantity: String = gsonPretty.toJson(mutableList_cartItemQuantity)
            Log.d("ShoppingCartedEditActivity", jsonTutListPretty_CartQuantity.toString())

            doShoppingCartQuantityDoubleChecking(MMKV_user_id, jsonTutListPretty_CartQuantity, forward_mode)
        }
    }
    override fun onStop() {
        super.onStop()
//        var forward_mode = "finish"
//
//        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
//        var mutableList_cartItemQuantity: MutableList<CartItemQuantityBean> = mutableListOf()
//
//        for (i in 0 until mutableList_shoppingCartShopItems.size){
//            for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
//                var cartItemQuantityBean: CartItemQuantityBean = CartItemQuantityBean()
//                cartItemQuantityBean.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id
//                cartItemQuantityBean.new_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity
//                mutableList_cartItemQuantity.add(cartItemQuantityBean)
//            }
//        }
//
//        val gson = Gson()
//        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
//
//        val jsonTutList_CartQuantity: String = gson.toJson(mutableList_cartItemQuantity)
//        Log.d("ShoppingCartedEditActivity", jsonTutList_CartQuantity.toString())
//        val jsonTutListPretty_CartQuantity: String = gsonPretty.toJson(mutableList_cartItemQuantity)
//        Log.d("ShoppingCartedEditActivity", jsonTutListPretty_CartQuantity.toString())
//
//        doShoppingCartQuantityDoubleChecking(MMKV_user_id, jsonTutListPretty_CartQuantity, forward_mode)

    }

    override fun onDestroy() {
        super.onDestroy()
//        var forward_mode = "finish"
//
//        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
//        var mutableList_cartItemQuantity: MutableList<CartItemQuantityBean> = mutableListOf()
//
//        for (i in 0 until mutableList_shoppingCartShopItems.size){
//            for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
//                var cartItemQuantityBean: CartItemQuantityBean = CartItemQuantityBean()
//                cartItemQuantityBean.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id
//                cartItemQuantityBean.new_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity
//                mutableList_cartItemQuantity.add(cartItemQuantityBean)
//            }
//        }
//
//        val gson = Gson()
//        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
//
//        val jsonTutList_CartQuantity: String = gson.toJson(mutableList_cartItemQuantity)
//        Log.d("ShoppingCartedEditActivity", jsonTutList_CartQuantity.toString())
//        val jsonTutListPretty_CartQuantity: String = gsonPretty.toJson(mutableList_cartItemQuantity)
//        Log.d("ShoppingCartedEditActivity", jsonTutListPretty_CartQuantity.toString())
//
//        doShoppingCartQuantityDoubleChecking(MMKV_user_id, jsonTutListPretty_CartQuantity, forward_mode)

    }

    private fun doInsertAuditLog(user_id: String, action: String, parameter_in: String, parameter_out: String) {

        var url = ApiConstants.API_PATH+"user/${user_id}/auditLog/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("doInsertAuditLog", "返回資料 resStr：" + resStr)
//                    Log.d("doInsertAuditLog", "返回資料 ret_val：" + json.get("ret_val"))

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        if (ret_val.equals("新增成功")){
                            Log.d("doInsertAuditLog", "訊息狀態：訊息已送出!!")
                        }else{
                            Log.d("doInsertAuditLog", "訊息狀態：訊息尚未送出~")
                        }

                    }

                } catch (e: JSONException) {
                    Log.d("doInsertAuditLog_errorMessage", "JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doInsertAuditLog_errorMessage", "IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doInsertAuditLog_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.InsertAuditLog(url, action,parameter_in,parameter_out)
    }

}