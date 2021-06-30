package com.HKSHOPU.hk.ui.main.shoppingCart.activity

import MyLinearLayoutManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventCheckedShoppingCartItem
import com.HKSHOPU.hk.component.EventRemoveShoppingCartItem
import com.HKSHOPU.hk.component.EventUpdateShoppingCartItem
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShoppingCartEditedBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.main.shoppingCart.adapter.ShoppingCartShopsNestedAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.GsonBuilder
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

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

        binding.containerGoToShopping.visibility = View.GONE
        binding.containerRViewShoppingCartItems.visibility = View.GONE

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "25").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

        getShoppingCartItems(MMKV_user_id)

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

        binding.cbCheckedAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){

                mutableList_shoppingCartShopItems =  mAdapter_ShoppingCartItems.getDatas()
                for (i in 0 until mutableList_shoppingCartShopItems.size){
                    mutableList_shoppingCartShopItems.get(i).shop_checked = true
                    for (j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                        mutableList_shoppingCartShopItems.get(i).productList.get(j).product_checked = true
                    }
                }
                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, true, false)

            }else{

                mutableList_shoppingCartShopItems =  mAdapter_ShoppingCartItems.getDatas()
                for (i in 0 until mutableList_shoppingCartShopItems.size){
                    mutableList_shoppingCartShopItems.get(i).shop_checked = false
                    for (j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                        mutableList_shoppingCartShopItems.get(i).productList.get(j).product_checked = false
                    }
                }
                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, true, false)

            }
        }

        binding.btnShoppingCartCheckOut.setOnClickListener {

            mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

            var mutableList_shoppingCartShopItems_checked: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()

            for (i in 0 until mutableList_shoppingCartShopItems.size){
                if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                    mutableList_shoppingCartShopItems_checked.add(mutableList_shoppingCartShopItems.get(i))
                }
            }

            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
            val jsonTutList: String = gson.toJson(mutableList_shoppingCartShopItems_checked)
            Log.d("ShoppingCartedEditActivity", jsonTutList.toString())
            val jsonTutListPretty: String = gsonPretty.toJson(mutableList_shoppingCartShopItems_checked)
            Log.d("ShoppingCartedEditActivity", jsonTutListPretty.toString())

            val intent = Intent(this, ShoppingCartConfirmedActivity::class.java)
            var bundle = Bundle()
            bundle.putString("mutableList_shoppingCartShopItems_checked_json",jsonTutList)
            intent.putExtra("bundle",bundle)
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

                            runOnUiThread {
                                binding.containerGoToShopping.visibility = View.GONE
                                binding.containerRViewShoppingCartItems.visibility = View.VISIBLE
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
                                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, false, false)

                            }

                        }else{
                            runOnUiThread {
                                binding.containerGoToShopping.visibility = View.VISIBLE
                                binding.containerRViewShoppingCartItems.visibility = View.GONE
                                binding.btnGoToShopping.setOnClickListener {
                                    val intent = Intent(this@ShoppingCartEditActivity, ShopmenuActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                        }

                    }else{


                    }

                } catch (e: JSONException) {
                    Log.d("getShoppingCartItems", "JSONException" + "${e.toString()}")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getShoppingCartItems", "IOException" + "${e.toString()}")
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getShoppingCartItems", "ErrorResponse" + "${ErrorResponse.toString()}")
            }
        })
        web.Get_Data(url)
    }


    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRemoveShoppingCartItem -> {

                        var id_list = it.id_list
                        var position = it.position

                        if(id_list.equals("")){
                            mAdapter_ShoppingCartItems.onItemDissmiss(position)
                        }else{
                            doDeleteShoppingCartitems(id_list, position)
                        }

                    }
                    is EventUpdateShoppingCartItem ->{

                        var product_checked = it.product_checked
                        var shopping_cart_item_id : String = it.shopping_cart_item_id
                        var new_quantity : String = it.new_quantity
                        var selected_shipment_id : String = it.selected_shipment_id
                        var selected_user_address_id: String = it.selected_user_address_id
                        var selected_payment_id : String = it.selected_payment_id

                        Log.d("checkEventUpdateShoppingCartItem",
                            "shopping_cart_item_id: ${shopping_cart_item_id.toString()} ; " +
                            "new_quantity: ${new_quantity.toString()} ; " +
                                "\n selected_shipment_id: ${selected_shipment_id.toString()} ; " +
                                "\n selected_user_address_id: ${selected_user_address_id.toString()} ; " +
                                "\n selected_payment_id: ${selected_payment_id.toString()} ; ")

                        doUpdateShoppingCartitems(product_checked, shopping_cart_item_id, new_quantity, selected_shipment_id, selected_user_address_id, selected_payment_id)

                    }
                    is EventCheckedShoppingCartItem -> {

                        final_total_price=0
                        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

                        for(i in 0 until mutableList_shoppingCartShopItems.size){
                            if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                                    final_total_price += mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.spec_quantity_sum_price.toInt()+ mutableList_shoppingCartShopItems.get(i).productList.get(j).shipmentSelected.shipment_price
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

    private fun doDeleteShoppingCartitems (shopping_cart_item_id: String, position: Int) {

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
                        }

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
        web.doDeleteShoppingCartitems(url, shopping_cart_item_id)

    }

    private fun doUpdateShoppingCartitems (product_checked : Boolean, shopping_cart_item_id : String, new_quantity : String, selected_shipment_id : String, selected_user_address_id: String, selected_payment_id : String) {

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
                        if(product_checked){
                            var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
                            for(i in 0 until mutableList_shoppingCartShopItems.size){
                                if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                    for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                                        final_total_price += mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.spec_quantity_sum_price.toInt()+ mutableList_shoppingCartShopItems.get(i).productList.get(j).shipmentSelected.shipment_price
                                            .toInt()
                                    }
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

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUpdateShoppingCartitems", "IOException：" + e.toString())

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUpdateShoppingCartitems", "ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.doUpdateShoppingCartitems(url, shopping_cart_item_id, new_quantity, selected_shipment_id, selected_user_address_id, selected_payment_id)
    }

}