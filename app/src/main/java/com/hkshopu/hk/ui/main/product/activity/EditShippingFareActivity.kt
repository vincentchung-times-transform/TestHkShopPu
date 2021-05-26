package com.hkshopu.hk.ui.main.product.activity

import MyLinearLayoutManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventCheckShipmentEnableBtnOrNot
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityShippingFareBinding
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.ui.main.adapter.ShippingFareAdapter
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVHandler
import org.jetbrains.anko.singleLine

class EditShippingFareActivity : AppCompatActivity(){

    private lateinit var binding : ActivityShippingFareBinding
    private val VM = ShopVModel()

    val mAdapters_shippingFare = ShippingFareAdapter(this)
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare_Filtered>()
    var mutableList_itemShipingFare_certained = mutableListOf<ItemShippingFare_Certained>()

    var value_txtViewFareRange :String = ""

    //資料變數宣告
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1 //待合併
    var MMKV_weight: String = ""
    var MMKV_length:String = ""
    var MMKV_width: String = ""
    var MMKV_height: String = ""
    var sync_to_shop = false
    lateinit var productInfoList :  ProductInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingFareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)


        initVM()
        initView()
    }

    fun initView() {

        MMKV_weight = MMKV.mmkvWithID("editPro").getString("datas_packagesWeights", "").toString()
        MMKV_length = MMKV.mmkvWithID("editPro").getString("datas_length", "").toString()
        MMKV_width = MMKV.mmkvWithID("editPro").getString("datas_width", "").toString()
        MMKV_height = MMKV.mmkvWithID("editPro").getString("datas_height", "").toString()
        binding.editPackageWeight.setText(MMKV_weight)
        binding.editPackageLength.setText(MMKV_length)
        binding.editPackageWidth.setText(MMKV_width)
        binding.editPackageHeight.setText(MMKV_height)

        if( binding.editPackageWeight.text.isNotEmpty() && binding.editPackageLength.text.isNotEmpty() &&  binding.editPackageWidth.text.isNotEmpty() && binding.editPackageHeight.text.isNotEmpty() ){

            var check_onOff= 0
            var empty_count= 0
            for (i in 0..mAdapters_shippingFare.get_shipping_method_datas().size-1){
                if(  mAdapters_shippingFare.get_shipping_method_datas().get(i).onoff.equals("on")){
                    check_onOff+=1
                }
                if(  mAdapters_shippingFare.get_shipping_method_datas().get(i).price.equals("")){
                    empty_count+=1
                }
            }
            if(check_onOff>1 && empty_count.equals(0)){
                binding.btnShippingFareStore.isEnabled = true
                binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
            }else{
                binding.btnShippingFareStore.isEnabled = false
                binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
            }


        }else{
            binding.btnShippingFareStore.isEnabled = false
            binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
        }

        var fare_datas_size = MMKV.mmkvWithID("editPro").getString("fare_datas_size","0")

        if (fare_datas_size != null && fare_datas_size.toInt() >=1 ) {

            for (i in 0..fare_datas_size.toInt()-1!!) {
                mutableList_itemShipingFare.add(GsonProvider.gson.fromJson( MMKV.mmkvWithID("addPro").getString("value_fare_item${i}",""), ItemShippingFare::class.java))
            }

        }



        //商品運費項目
        initRecyclerView_ShippingFareItem()

        binding.btnEditFareOn.isVisible = true
        binding.btnEditFareOn.isEnabled = true
        binding.btnEditFareOff.isVisible = false
        binding.btnEditFareOff.isEnabled = false

        generateCustomFare_uneditable()

        initVM()
        initEvent()
        initClick()
        initEdit()
    }

    fun initClick() {

        binding.checkBoxAsyncFareSetting.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                sync_to_shop = binding.checkBoxAsyncFareSetting.isChecked
            }else{
                sync_to_shop = binding.checkBoxAsyncFareSetting.isChecked
            }
        }

        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnEditFareOff.setOnClickListener {

            //關閉編輯按鍵disable隱藏
            binding.btnEditFareOff.isVisible = false
            binding.btnEditFareOff.isEnabled = false
            //開啟編輯按鍵enable出現
            binding.btnEditFareOn.isVisible = true
            binding.btnEditFareOn.isEnabled = true

            mAdapters_shippingFare.onOff_editStatus(false)

//            generateCustomFare_uneditable()

        }


        binding.btnEditFareOn.setOnClickListener {

            //開啟編輯按鍵disable隱藏
            binding.btnEditFareOn.isVisible = false
            binding.btnEditFareOn.isEnabled = false

            //關閉編輯按鍵enable出現
            binding.btnEditFareOff.isVisible = true
            binding.btnEditFareOff.isEnabled = true

            Thread(Runnable {

                runOnUiThread {
                    binding.progressBar6.visibility = View.VISIBLE
                    mAdapters_shippingFare.onOff_editStatus(true)
                }

                try{
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(false))

                runOnUiThread {
                    binding.progressBar6.visibility = View.GONE
                }

            }).start()


        }

        binding.btnShippingFareStore.setOnClickListener {

            val intent = Intent(this, EditProductActivity::class.java)
            var datas_ship_method_and_fare : MutableList<ItemShippingFare> = mAdapters_shippingFare.get_shipping_method_datas()

            MMKV_weight = binding.editPackageWeight.text.toString()
            MMKV_length = binding.editPackageLength.text.toString()
            MMKV_width = binding.editPackageWidth.text.toString()
            MMKV_height = binding.editPackageHeight.text.toString()

            MMKV.mmkvWithID("editPro").putString("datas_packagesWeights", MMKV_weight.toString())
            MMKV.mmkvWithID("editPro").putString("datas_length", MMKV_length)
            MMKV.mmkvWithID("editPro").putString("datas_width", MMKV_width)
            MMKV.mmkvWithID("editPro").putString("datas_height", MMKV_height)

            if(datas_ship_method_and_fare.size.toString() != ""){
                MMKV.mmkvWithID("editPro").putString("fare_datas_size", datas_ship_method_and_fare.size.toString())
            }else{
                MMKV.mmkvWithID("editPro").putString("fare_datas_size", "0")
            }
            Log.d("checkVariable", datas_ship_method_and_fare.size.toString())

            for (i in 0..datas_ship_method_and_fare.size-1!!) {
                val jsonTutList_mutableList_itemShipingFare: String = GsonProvider.gson.toJson(datas_ship_method_and_fare[i])
                MMKV.mmkvWithID("editPro").putString("value_fare_item${i}", jsonTutList_mutableList_itemShipingFare)
            }

            //篩選所有已勾選的運費方式
            for (i in 0..datas_ship_method_and_fare.size-1!!) {
                if(datas_ship_method_and_fare[i].onoff == "on" ){
                    mutableList_itemShipingFare_filtered.add(
                        ItemShippingFare_Filtered(datas_ship_method_and_fare[i].shipment_desc, datas_ship_method_and_fare[i].price.toInt(), datas_ship_method_and_fare[i].onoff, datas_ship_method_and_fare[i].shop_id)
                    )
                }
            }

            //MMKV放入已經確定勾選的Fare Item Size
            if(mutableList_itemShipingFare_filtered.size.toString() != ""){
                MMKV.mmkvWithID("editPro").putString("fare_datas_filtered_size", mutableList_itemShipingFare_filtered.size.toString())
            }else{
                MMKV.mmkvWithID("editPro").putString("fare_datas_filtered_size", "0")

            }
            Log.d("check_content","fare_datas_filtered_size : ${mutableList_itemShipingFare_filtered.size.toString()}")

            for (i in 0..mutableList_itemShipingFare_filtered.size-1!!) {
                val jsonTutList_mutableList_itemShipingFare_filtered: String = GsonProvider.gson.toJson(mutableList_itemShipingFare_filtered[i])
                MMKV.mmkvWithID("editPro").putString("value_fare_item_filtered${i}", jsonTutList_mutableList_itemShipingFare_filtered)

            }

            value_txtViewFareRange = fare_pick_max_and_min_num(mutableList_itemShipingFare_filtered.size)
            MMKV.mmkvWithID("editPro").putString("value_txtViewFareRange", value_txtViewFareRange)

            //取出所有Fare Item(拿掉btn_delete參數)
            for(i in 0..datas_ship_method_and_fare.size!!-1){
                //去除btn_delete參數重新創造List(資料庫存取用)
                if(datas_ship_method_and_fare[i].shipment_desc != ""){
                    mutableList_itemShipingFare_certained.add(ItemShippingFare_Certained(datas_ship_method_and_fare[i].shipment_desc, datas_ship_method_and_fare[i].price, datas_ship_method_and_fare[i].onoff, datas_ship_method_and_fare[i].shop_id)) //傳輸API需要
                }
            }

            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonTutList_fare: String = gson.toJson(mutableList_itemShipingFare_certained)
            Log.d("AddNewProductActivity", mutableList_itemShipingFare_certained.toString())
            val jsonTutListPretty_fare: String = gsonPretty.toJson(mutableList_itemShipingFare_certained)
            Log.d("AddNewProductActivity", mutableList_itemShipingFare_certained.toString())

            MMKV.mmkvWithID("editPro").putString("jsonTutList_fare", jsonTutList_fare)


            //sync prodcut fare settings to Shop fare setting
            MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
            Log.d("MMKV_shop_id", MMKV_shop_id.toString())
            if(sync_to_shop == true){

                VM.syncShippingfare(this, MMKV_shop_id, jsonTutList_fare)
            }


            startActivity(intent)
            finish()

        }

    }

    fun initEdit() {

        binding.editPackageWeight.singleLine = true
        binding.editPackageWeight.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_weight = binding.editPackageWeight.text.toString()

                    binding.editPackageWeight.clearFocus()
                    v.hideKeyboard()

                    true
                }
                else -> false
            }
        }
        val textWatcher_datas_packagesWeights = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {

                RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))

                if(binding.editPackageWeight.text.toString().length >= 2 && binding.editPackageWeight.text.toString().startsWith("0")){
                    binding.editPackageWeight.setText(binding.editPackageWeight.text.toString().replace("0", "", false))
                    binding.editPackageWeight.setSelection(binding.editPackageWeight.text.toString().length)
                }

                MMKV_weight = binding.editPackageWeight.text.toString()
            }
        }
        binding.editPackageWeight.addTextChangedListener(textWatcher_datas_packagesWeights)



        binding.editPackageLength.singleLine = true
        binding.editPackageLength.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    MMKV_length = binding.editPackageLength.text.toString()
                    v.hideKeyboard()
                    binding.editPackageLength.clearFocus()
                    true
                }
                else -> false
            }
        }
        val textWatcher_editPackageLength = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {

                RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))

                if(binding.editPackageLength.text.toString().length >= 2 && binding.editPackageLength.text.toString().startsWith("0")){
                    binding.editPackageLength.setText(binding.editPackageLength.text.toString().replace("0", "", false))
                    binding.editPackageLength.setSelection(binding.editPackageLength.text.toString().length)
                }

                MMKV_length = binding.editPackageLength.text.toString()
            }
        }
        binding.editPackageLength.addTextChangedListener(textWatcher_editPackageLength)




        binding.editPackageWidth.singleLine = true
        binding.editPackageWidth.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_width = binding.editPackageWidth.text.toString()

                    binding.editPackageWidth.clearFocus()
                    v.hideKeyboard()


                    true
                }
                else -> false
            }
        }
        val textWatcher_editPackageWidth = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {

                RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))

                if(binding.editPackageWidth.text.toString().length >= 2 && binding.editPackageWidth.text.toString().startsWith("0")){
                    binding.editPackageWidth.setText(binding.editPackageWidth.text.toString().replace("0", "", false))
                    binding.editPackageWidth.setSelection(binding.editPackageWidth.text.toString().length)
                }

                MMKV_width = binding.editPackageWidth.text.toString()
            }
        }
        binding.editPackageWidth.addTextChangedListener(textWatcher_editPackageWidth)


        binding.editPackageHeight.singleLine = true
        binding.editPackageHeight.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    MMKV_height = binding.editPackageHeight.text.toString()



                    binding.editPackageHeight.clearFocus()
                    v.hideKeyboard()
                    true
                }
                else -> false
            }
        }
        val textWatcher_editPackageHeight = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {

                RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))

                if(binding.editPackageHeight.text.toString().length >= 2 && binding.editPackageHeight.text.toString().startsWith("0")){
                    binding.editPackageHeight.setText(binding.editPackageHeight.text.toString().replace("0", "", false))
                    binding.editPackageHeight.setSelection(binding.editPackageHeight.text.toString().length)
                }
                MMKV_height = binding.editPackageHeight.text.toString()
            }
        }
        binding.editPackageHeight.addTextChangedListener(textWatcher_editPackageHeight)

    }

    fun initRecyclerView_ShippingFareItem() {

        //自訂layoutManager
        binding.rViewFareItemSpec.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewFareItemSpec.adapter = mAdapters_shippingFare

        mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
        mAdapters_shippingFare.notifyDataSetChanged()

    }

    //自訂費用項目(不可編輯狀態)
    fun generateCustomFare_uneditable() {

        //進入"不可編輯模式"新增資料或重新新增資料
        mutableList_itemShipingFare = mAdapters_shippingFare.get_shipping_method_datas()

        var mutableList_size = mAdapters_shippingFare.get_shipping_method_datas().size

        if(mutableList_size>=2){
            for(i in 0..mutableList_size-2){
                mutableList_itemShipingFare[i] = ItemShippingFare(mutableList_itemShipingFare[i].shipment_desc, mutableList_itemShipingFare[i].price, mutableList_itemShipingFare[i].onoff,  mutableList_itemShipingFare[i].shop_id)
            }

            mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
            mAdapters_shippingFare.notifyDataSetChanged()
        }



    }


    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


    //計算費用最大最小範圍
    fun fare_pick_max_and_min_num(size: Int): String {

        if(mutableList_itemShipingFare_filtered.size>0){
            //挑出最大與最小的數字
            var min: Int =mutableList_itemShipingFare_filtered[0].price.toInt()
            var max: Int =mutableList_itemShipingFare_filtered[0].price.toInt()

            for (f in 1..size-1) {
                if(mutableList_itemShipingFare_filtered[f].price.toInt() >= min ){
                    max = mutableList_itemShipingFare_filtered[f].price.toInt()
                }else{
                    min = mutableList_itemShipingFare_filtered[f].price.toInt()
                }
            }

            return "HKD$${min}-HKD$${max}"
        }else{
            return ""
        }


    }

    override fun onBackPressed() {

        val intent = Intent(this, EditProductActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initVM() {

        VM.syncShippingfareData.observe(
            this,
            Observer {
                when (it?.status) {
                    Status.Success -> {
                        if (it.ret_val.toString().equals("運輸設定更新成功!")) {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()
                            Log.d("shippingFare", it.ret_val.toString())

                        } else {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()
                            Log.d("shippingFare", it.ret_val.toString())

                        }

                    }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
                }
            }
        )

    }


    @SuppressLint("CheckResult")
    fun initEvent() {
        var boolean: Boolean

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventCheckShipmentEnableBtnOrNot -> {

                        boolean = it.boolean


                        if(boolean){

                            var check_onOff= 0
                            var empty_count= 0
                            for (i in 0..mAdapters_shippingFare.get_shipping_method_datas().size-1){
                                if(  mAdapters_shippingFare.get_shipping_method_datas().get(i).onoff.equals("on")){
                                    check_onOff=check_onOff+1
                                    if(mAdapters_shippingFare.get_shipping_method_datas().get(i).price.isNullOrEmpty()){
                                        empty_count+=1
                                    }
                                }

                            }

                            if(check_onOff>0 && empty_count.equals(0) && MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
                                binding.btnShippingFareStore.isEnabled = true
                                binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
                            }else{
                                binding.btnShippingFareStore.isEnabled = false
                                binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
                            }
                        }else{
                            binding.btnShippingFareStore.isEnabled = false
                            binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)

                        }
                    }
                }
            }, {
                it.printStackTrace()
            })

    }

}