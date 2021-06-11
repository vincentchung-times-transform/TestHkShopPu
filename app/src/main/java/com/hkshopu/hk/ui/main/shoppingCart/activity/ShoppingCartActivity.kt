package com.hkshopu.hk.ui.main.shoppingCart.activity

import MyLinearLayoutManager
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import com.facebook.FacebookSdk
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityShoppingCartBinding
import com.hkshopu.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.hkshopu.hk.ui.main.shoppingCart.adapter.ShoppingCartShopsNestedAdapter
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import java.util.ArrayList

class ShoppingCartActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityShoppingCartBinding

    //宣告頁面資料變數
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)



        var mutableList_shoppingCartShopItems: MutableList<ShoppingCartShopItemNestedLayer> = mutableListOf()
        var shop_number = 2
        var shop_products_number = 2

        //暫時假資料
        var temp_logistics:MutableList<ItemShippingFare_Filtered> = mutableListOf()
        temp_logistics.add(ItemShippingFare_Filtered("郵局", 9, "on", 0))
        temp_logistics.add(ItemShippingFare_Filtered("順豐", 19, "on", 0))
        temp_logistics.add(ItemShippingFare_Filtered("黑貓", 29, "on", 0))

        for(i in 0..shop_number-1){
            var mutableList_shoppingCartProductItems: MutableList<ShoppingCartProductItemNestedLayer> = mutableListOf()
            for (j in 0..shop_products_number-1){
                mutableList_shoppingCartProductItems.add(
                    ShoppingCartProductItemNestedLayer(
                        "product_icon_url",
                        "產品${j}",
                    "尺寸",
                    "大",
                    "顏色",
                    "黑",
                    0,
                    0,
                        temp_logistics,
                        temp_logistics.get(0).shipment_desc,
                        temp_logistics.get(0).price))
            }
            mutableList_shoppingCartShopItems.add(ShoppingCartShopItemNestedLayer(false, "no image url", "商店${i}",mutableList_shoppingCartProductItems))
        }

        var mAapter_Shopping = ShoppingCartShopsNestedAdapter()

        binding.rViewShoppingCartItems.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewShoppingCartItems.adapter = mAapter_Shopping

        mAapter_Shopping.setDatas(mutableList_shoppingCartShopItems)

        binding.bottomStatusSelecting.visibility = View.VISIBLE
        binding.bottomStatusSelectingConfirmed.visibility = View.GONE

        binding.btnShoppingCartCheckOut.setOnClickListener {

            mAapter_Shopping.set_edit_mode(false)
            binding.bottomStatusSelecting.visibility = View.GONE
            binding.bottomStatusSelectingConfirmed.visibility = View.VISIBLE

        }

        binding.btnShoppingCartPaypal.setOnClickListener {

            mAapter_Shopping.set_edit_mode(true)
            binding.bottomStatusSelecting.visibility = View.VISIBLE
            binding.bottomStatusSelectingConfirmed.visibility = View.GONE

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