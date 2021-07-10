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
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventUpdateShoppingCartItemForConfirmed
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShoppingCartConfirmedBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shoppingCart.adapter.ShoppingCartShopsNestedAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class ShoppingCartConfirmedActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityShoppingCartConfirmedBinding


    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""

    var address_less = false


    var mutableList_shoppingCartShopItems: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()
    var mAdapter_ShoppingCartItems = ShoppingCartShopsNestedAdapter(this)
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    var mutableList_userAddressBean: MutableList<UserAddressBean> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingCartConfirmedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()

        doGetPaymentMethodList(MMKV_user_id)
        doGetUserAddressList(MMKV_user_id)

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

        binding.titleBackAddshop.setOnClickListener {

            val intent = Intent(this, ShoppingCartEditActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnShoppingCartPaypal.setup(

            createOrder = CreateOrder { createOrderActions ->
                val order = Order(
                    intent = OrderIntent.CAPTURE,
                    appContext = AppContext(
                        userAction = UserAction.PAY_NOW
                    ),
                    purchaseUnitList = listOf(
                        PurchaseUnit(
                            amount = Amount(
                                currencyCode = CurrencyCode.HKD,
                                value = "10.00"
//                                value = binding.textViewSumPrice.text.toString()
                            )
                        )
                    )
                )

                createOrderActions.create(order)

            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.d("Papal_CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.d("Papal_OnCancel", "Buyer canceled the PayPal experience.")
            },
            onError = OnError { errorInfo ->
                Log.d("Papal_OnError", "Error: $errorInfo")
            }

        )

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

        val intent = Intent(this, ShoppingCartEditActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventUpdateShoppingCartItemForConfirmed -> {

                        var id = it.id
                        var buyerName = it.buyerName
                        var buyerPhone = it.buyerPhone
                        var buyerAddress = it.buyerAddress
                        var shoppingCartShopId = it.shoppingCartShopId
                        var specId_json = it.specId_json

                        mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

                        for(i in 0 until mutableList_shoppingCartShopItems.size){
                            if(mutableList_shoppingCartShopItems.get(i).shop_id == shoppingCartShopId.toString()){
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_id = id.toString()
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_name = buyerName.toString()
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_phone = buyerPhone.toString()
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_address = buyerAddress.toString()
                            }
                        }

                        mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, address_less)

                        Log.d("check_upadte_buyer_address", "specId_json: ${specId_json.toString()}\n" +
                                "id:　${id.toString()}")
                        doUpdateShoppingCartitems(specId_json, "", "", id.toString(), "")

                    }

                }
            }, {
                it.printStackTrace()
            })

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

                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartConfirmedActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }

                    }else{

                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
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
    private fun doGetPaymentMethodList( user_id: String) {

        var url = ApiConstants.API_HOST + "payment/method"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doGetUserAddressList", "返回資料 resStr：" + resStr)
                    Log.d("doGetUserAddressList", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val paymentBean: PaymentBean =
                                Gson().fromJson(jsonObject.toString(), PaymentBean::class.java)

                            mutablelist_paymentBean.add(paymentBean)

                        }


                        val payment_list: MutableList<String> = ArrayList<String>()

                        for (i in 0 until mutablelist_paymentBean.size) {
                            payment_list.add(mutablelist_paymentBean.get(i).payment_desc.toString())
                        }

                        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                            FacebookSdk.getApplicationContext(),
                            R.layout.simple_spinner_dropdown_item,
                            payment_list
                        )
                        runOnUiThread {

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.containerPaymentSpinner.setAdapter(adapter)
                            binding.containerPaymentSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long,
                                ) {
                                    var payment_id = mutablelist_paymentBean.get(position).id
                                    Toast.makeText(this@ShoppingCartConfirmedActivity, """已選取"${mutablelist_paymentBean.get(position).payment_desc}"作為付款方式""", Toast.LENGTH_SHORT).show()


                                    var item_id_list = arrayListOf<String>()
                                    for(i in 0 until mAdapter_ShoppingCartItems.getDatas().size){
                                        for(j in 0 until mAdapter_ShoppingCartItems.getDatas().get(i).productList.size){
                                            item_id_list.add(mAdapter_ShoppingCartItems.getDatas().get(i).productList.get(j).product_spec.shopping_cart_item_id.toString())
                                        }
                                    }
                                    var gson = Gson()
                                    var item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

                                    doUpdateShoppingCartitems(item_id_list_json,"","","", payment_id.toString())

                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }

                        }

                    }
//                        initRecyclerView()

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

    private fun doGetUserAddressList( user_id: String) {

        var url = ApiConstants.API_HOST + "shopping_cart/" + user_id + "/buyer_address/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doGetUserAddressList", "返回資料 resStr：" + resStr)
                    Log.d("doGetUserAddressList", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        if(translations.length() == 0){
                            address_less = true
                        }else{
                            address_less = false
                            for (i in 0 until translations.length()) {
                                val jsonObject: JSONObject = translations.getJSONObject(i)
                                val userAddressBean: UserAddressBean =
                                    Gson().fromJson(jsonObject.toString(), UserAddressBean::class.java)

                                mutableList_userAddressBean.add(userAddressBean)

                            }

                            runOnUiThread {

                                var bundle_mutablelist_json = intent.getBundleExtra("bundle")!!.getString("mutableList_shoppingCartShopItems_checked_json","").toString()

                                val parser = JsonParser()
                                val jsonElement: JsonElement = parser.parse(bundle_mutablelist_json)
                                val jsonArray = jsonElement.asJsonArray


                                if( jsonArray.size()>0 ){

                                    for (i in 0 until jsonArray.size()) {

                                        val jsonObject = jsonArray.get(i)
                                        mutableList_shoppingCartShopItems.add(
                                            Gson().fromJson(
                                                jsonObject.toString(),
                                                ShoppingCartShopItemNestedLayer::class.java
                                            ))
                                    }
                                }

                                MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
                                MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
                                MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

                                for(i in 0 until mutableList_shoppingCartShopItems.size){
                                    for(j in 0 until mutableList_userAddressBean.size){
                                        if(mutableList_userAddressBean.get(j).is_default == "Y"){
                                            mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_id = mutableList_userAddressBean.get(j).id
                                            mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_name = mutableList_userAddressBean.get(j).name
                                            mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_phone = mutableList_userAddressBean.get(j).phone
                                            mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_address = mutableList_userAddressBean.get(j).address
                                        }
                                    }
                                }


                                binding.rViewShoppingCartItems.setLayoutManager(MyLinearLayoutManager(this@ShoppingCartConfirmedActivity,false))
                                binding.rViewShoppingCartItems.adapter = mAdapter_ShoppingCartItems

                                mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems , address_less)
                                mAdapter_ShoppingCartItems.set_edit_mode(false)

                                var final_total_prodcut_price = 0
                                var final_total_shipent_price = 0
                                var final_total_price = 0
                                var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

                                for(i in 0 until mutableList_shoppingCartShopItems.size){
                                    if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                        for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                                            final_total_prodcut_price += mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.spec_quantity_sum_price.toInt()
                                        }
                                    }
                                }
                                for(i in 0 until mutableList_shoppingCartShopItems.size){
                                    if(mutableList_shoppingCartShopItems.get(i).shop_checked){
                                        for(j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size ){
                                            final_total_shipent_price += mutableList_shoppingCartShopItems.get(i).productList.get(j).selected_shipment.shipment_price.toInt()
                                        }
                                    }
                                }
                                final_total_price = final_total_prodcut_price + final_total_shipent_price

                                binding.textViewCommdityTotal.setText(final_total_prodcut_price.toString())
                                binding.textViewFreightTotal.setText(final_total_shipent_price.toString())
                                binding.textViewSumPrice.setText(final_total_price.toString())

                            }

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


}