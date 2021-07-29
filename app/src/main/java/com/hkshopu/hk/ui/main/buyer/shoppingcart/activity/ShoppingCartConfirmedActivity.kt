package com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity

import MyLinearLayoutManager
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.application.App
import com.HKSHOPU.hk.component.EventGenerateOeder
import com.HKSHOPU.hk.component.EventUpdateShoppingCartItemForConfirmed
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityShoppingCartConfirmedBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.activity.PaypalActivity
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.adapter.ShoppingCartShopsNestedAdapter
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.fragment.GenerateOrderCheckingDialogFragment
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.seller.product.fragment.SpecificationInfoDialogFragment
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.bumptech.glide.load.engine.Resource
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import java.math.BigDecimal
import java.util.*


class ShoppingCartConfirmedActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityShoppingCartConfirmedBinding

    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""

    var paymethod = "fps"
    var selected_payment_id = ""
    var address_less = false

//    var PAYPAL_REQUEST_CODE = 7171
//    private val config = PayPalConfiguration()
//        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
//        .clientId("AdBCLHocOrbf94O5WAIkLVi3OAjuwWseJfwNtX6uHSm96tV5gqB_e1g4uBvfvS6TlQeAs9mjT90b-Ok3")

    var mutableList_shoppingCartShopItems: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()
    var mAdapter_ShoppingCartItems = ShoppingCartShopsNestedAdapter(this)
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    var mutableList_userAddressBean: MutableList<UserAddressBean> = mutableListOf()

//    override fun onDestroy() {
//        stopService(Intent(this, PayPalService::class.java))
//        super.onDestroy()
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PAYPAL_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
//                if (confirm != null) {
//                    try {
//
//                        Log.i("Paypal", confirm.toJSONObject().toString(4))
//                        Log.i("Paypal", confirm.payment.toJSONObject().toString(4))
//                        /**
//                         * TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
//                         * or consent completion.
//                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
//                         * for more details.
//                         * For sample mobile backend interactions, see
//                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
//                         */
//                        displayResultText("PaymentConfirmation info received from PayPal")
//
//
//                    } catch (e: JSONException) {
//                        Log.e("Paypal_error", "an extremely unlikely failure occurred: ", e)
//                    }
//
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("Paypal_error", "The user canceled.")
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.i(
//                    "Paypal_error",
//                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
//            }
//        }
//    }

    protected fun displayResultText(result: String) {
//        var resultView: TextView = findViewById(R.id.txtResult)
//        resultView.text = "Result : " + result
        Toast.makeText(
            applicationContext,
            result, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Start Paypal Service
//        var  intent = Intent(this, PayPalService::class.java)
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
//        startService(intent)
        binding = ActivityShoppingCartConfirmedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
        Log.d("debug_MMKV_user_id", "MMKV_user_id: ${MMKV_user_id}")

        doGetPaymentMethodList()


        initView()
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

//        binding.btnShoppingCartPaypalTest.setOnClickListener {
//            processPayment()
//        }

//        binding.btnShoppingCartPaypal.setup(
//
//            createOrder = CreateOrder { createOrderActions ->
//                val order = Order(
//                    intent = OrderIntent.CAPTURE,
//                    appContext = AppContext(
//                        userAction = UserAction.PAY_NOW
//                    ),
//                    purchaseUnitList = listOf(
//                        PurchaseUnit(
//                            amount = Amount(
//                                currencyCode = CurrencyCode.HKD,
//                                value = "1.00"
////                                value = binding.textViewSumPrice.text.toString()
//                            )
//                        )
//                    )
//                )
//
//                createOrderActions.create(order)
//
//            },
//            onApprove = OnApprove { approval ->
//                approval.orderActions.capture { captureOrderResult ->
//                    Log.d("Papal_CaptureOrder", "CaptureOrderResult: $captureOrderResult")
//                }
//            },
//            onCancel = OnCancel {
//                Log.d("Papal_OnCancel", "Buyer canceled the PayPal experience.")
//            },
//            onError = OnError { errorInfo ->
//                Log.d("Papal_OnError", "Error: $errorInfo")
//            }
//
//        )

//        binding.btnShoppingCartPaypalTest.setOnClickListener {
//            processPayment()
//        }

        binding.btnShoppingCartGenerateOder.setOnClickListener {
            GenerateOrderCheckingDialogFragment().show(supportFragmentManager, "MyCustomFragment")
//            if(paymethod.equals("fps")){
//                GenerateOrderCheckingDialogFragment().show(supportFragmentManager, "MyCustomFragment")
//            }else{
//                Toast.makeText(this, "Comming Soon", Toast.LENGTH_SHORT).show()
//            }
        }
    }

//    private fun processPayment() {
//        var amount = "1"
//        var paypalPayment = PayPalPayment(BigDecimal(amount), "HKD", "Sandbox Test", PayPalPayment.PAYMENT_INTENT_SALE)
//
//        var intent = Intent(this, PaymentActivity::class.java)
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, paypalPayment)
//        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
//    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("Not yet implemented")
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("Not yet implemented")
    }
    override fun afterTextChanged(s: Editable?){
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
                        var address_updateMode = it.address_updateMode

                        mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()

                        if(address_updateMode.equals("single")){
                            for(i in 0 until mutableList_shoppingCartShopItems.size){
                                if(mutableList_shoppingCartShopItems.get(i).shop_id == shoppingCartShopId.toString()){
                                    mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_id = id.toString()
                                    mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_name = buyerName.toString()
                                    mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_phone = buyerPhone.toString()
                                    mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_address = buyerAddress.toString()
                                }
                            }
                        }else{
                            for(i in 0 until mutableList_shoppingCartShopItems.size){
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_id = id.toString()
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_name = buyerName.toString()
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_phone = buyerPhone.toString()
                                mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_user_address = buyerAddress.toString()
                            }
                        }


                        address_less = false
                        mAdapter_ShoppingCartItems.setDatas(mutableList_shoppingCartShopItems, address_less)

                        Log.d("check_upadte_buyer_address", "specId_json: ${specId_json.toString()}\n" +
                                "id:　${id.toString()}")
//                        doUpdateShoppingCartitems(specId_json, "", "", id.toString(), "", "")
                    }
                    is EventGenerateOeder ->{
                        var mutableList_shoppingCartShopItems = mAdapter_ShoppingCartItems.getDatas()
                        var mutableList_shoppingCartShopItems_covertedToOrder: MutableList<CovertedToOrderBean> = mutableListOf()

                        for (i in 0 until mutableList_shoppingCartShopItems.size){
                            var covertedToOrderBean = CovertedToOrderBean()
                            covertedToOrderBean.shop_id = mutableList_shoppingCartShopItems.get(i).shop_id.toString()
                            covertedToOrderBean.user_address_id = mutableList_shoppingCartShopItems.get(i).selected_addresss.selected_addresss_id.toString()
                            covertedToOrderBean.payment_id = selected_payment_id
                            for (j in 0 until mutableList_shoppingCartShopItems.get(i).productList.size){
                                var shoppingCartItemConvertedToOrder = ShoppingCartItemConvertedToOrder()
                                shoppingCartItemConvertedToOrder.product_shipment_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).selected_shipment.product_shipment_id.toString()
                                shoppingCartItemConvertedToOrder.shopping_cart_item_id = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_item_id.toString()
                                shoppingCartItemConvertedToOrder.shopping_cart_quantity = mutableList_shoppingCartShopItems.get(i).productList.get(j).product_spec.shopping_cart_quantity.toInt()
                                covertedToOrderBean.productList.add(shoppingCartItemConvertedToOrder)
                            }
                            mutableList_shoppingCartShopItems_covertedToOrder.add(covertedToOrderBean)
                        }

                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                        val jsonTutList_shoppingCart_covertedToOrder: String = gson.toJson(mutableList_shoppingCartShopItems_covertedToOrder)
                        Log.d("ShoppingCartConfirmedActivity", jsonTutList_shoppingCart_covertedToOrder.toString())
                        val jsonTutListPretty_shoppingCart_covertedToOrder: String = gsonPretty.toJson(mutableList_shoppingCartShopItems_covertedToOrder)
                        Log.d("ShoppingCartConfirmedActivity", jsonTutListPretty_shoppingCart_covertedToOrder.toString())

//                        status
//                        待付款 - Pending Payment
//                        待發貨 - Pending Delivery
//                        待收貨 - Pending Good Receive
//                        訂單已完成 - Completed
//                        已取消 - Cancelled
//                        退貨/退款 - Refunded
                        doConvertShoppingCartItemsToOrder(MMKV_user_id, jsonTutList_shoppingCart_covertedToOrder, "Pending Payment")

                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun doGetPaymentMethodList() {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST + "payment/method"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doGetPaymentMethodList", "返回資料 resStr：" + resStr)
                    Log.d("doGetPaymentMethodList", "返回資料 ret_val：" + ret_val)

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
                                    selected_payment_id = mutablelist_paymentBean.get(position).id
                                    Toast.makeText(this@ShoppingCartConfirmedActivity, """已選取"${mutablelist_paymentBean.get(position).payment_desc}"作為付款方式""", Toast.LENGTH_SHORT).show()

//                                    var item_id_list = arrayListOf<String>()
//                                    for(i in 0 until mAdapter_ShoppingCartItems.getDatas().size){
//                                        for(j in 0 until mAdapter_ShoppingCartItems.getDatas().get(i).productList.size){
//                                            item_id_list.add(mAdapter_ShoppingCartItems.getDatas().get(i).productList.get(j).product_spec.shopping_cart_item_id.toString())
//                                        }
//                                    }
//                                    var gson = Gson()
//                                    var item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

//                                    doUpdateShoppingCartitems(item_id_list_json,"","","", payment_id.toString(), mutablelist_paymentBean.get(position).payment_desc.toString())
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }
                        }

                        doGetUserAddressList(MMKV_user_id)
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetPaymentMethodList_errorMessage", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetPaymentMethodList_errorMessage", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetPaymentMethodList_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                }
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
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doGetUserAddressList", "返回資料 resStr：" + resStr)
                    Log.d("doGetUserAddressList", "返回資料 ret_val：" + ret_val)


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
                        }

                        runOnUiThread {
                            generate_checkoutShoppingCart()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetUserAddressList_errorMessage", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetUserAddressList_errorMessage", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetUserAddressList_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.Get_Data(url)
    }
    fun generate_checkoutShoppingCart(){

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

        binding.progressBar.visibility = View.GONE
        binding.imgViewLoadingBackground.visibility = View.GONE
    }

    private fun doUpdateShoppingCartitems (shopping_cart_item_id : String, new_quantity : String, selected_shipment_id : String, selected_user_address_id: String, selected_payment_id : String, selected_payment_name: String) {

        val url = ApiConstants.API_HOST+"shopping_cart/update/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doUpdateShoppingCartitems", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateShoppingCartitems", "返回資料 ret_val：" + ret_val)

                    if (ret_val.equals("購物車更新成功!")) {

//                        if(selected_payment_name.equals("PayPal")){
//                            runOnUiThread {
//                                binding.btnShoppingCartGenerateOder.setImageResource(com.HKSHOPU.hk.R.mipmap.btn_shopping_cart_paypal)
//                            }
//                        }else if(selected_payment_name.equals("轉數快")){
//                            runOnUiThread {
//                                binding.btnShoppingCartGenerateOder.setImageResource(com.HKSHOPU.hk.R.mipmap.btn_fps)
//                            }
//                        }

                    }else{
//                        runOnUiThread {
//                            Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
//                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doUpdateShoppingCartitems", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUpdateShoppingCartitems", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUpdateShoppingCartitems", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.doUpdateShoppingCartitems(url, shopping_cart_item_id, new_quantity, selected_shipment_id, selected_user_address_id, selected_payment_id)
    }
    private fun doConvertShoppingCartItemsToOrder (user_id : String, shopping_cart  : String, order_status:String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        Log.d("doConvertShoppingCartItemsToOrder", "user_id: ${user_id} ; shopping_cart: ${shopping_cart}")
        val url = ApiConstants.API_HOST+"shopping_cart/covert_shopping_cart/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<String>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doConvertShoppingCartItemsToOrder", "返回資料 resStr：" + resStr)
                    Log.d("doConvertShoppingCartItemsToOrder", "返回資料 ret_val：" + json.get("ret_val"))
                    if (ret_val.equals("訂單新增成功")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("doConvertShoppingCartItemsToOrder", "返回資料 jsonArray：" + jsonArray.toString())

                        if(jsonArray.length() > 0){
                            for (i in 0 until jsonArray.length()) {
                                val order_id: String = jsonArray.getString(i)
                                list.add(order_id)
                            }
                        }

                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                        val jsonTutList: String = gson.toJson(list)
                        Log.d("doConvertShoppingCartItemsToOrder", jsonTutList.toString())
                        val jsonTutListPretty: String = gsonPretty.toJson(list)
                        Log.d("doConvertShoppingCartItemsToOrder", jsonTutListPretty.toString())

                        val intent = Intent(this@ShoppingCartConfirmedActivity, FpsPayActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("jsonTutList", jsonTutList)
                        intent.putExtra("bundle", bundle)
                        startActivity(intent)

                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartConfirmedActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }

                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doConvertShoppingCartItemsToOrder_errorMessage", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doConvertShoppingCartItemsToOrder_errorMessage", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doConvertShoppingCartItemsToOrder_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(this@ShoppingCartConfirmedActivity , "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.doConvertShoppingCartItemsToOrder(url, user_id, shopping_cart, order_status)
    }


}