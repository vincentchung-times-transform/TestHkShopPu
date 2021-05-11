package com.hkshopu.hk.ui.main.product.activity

import MyLinearLayoutManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding
import com.hkshopu.hk.databinding.ActivityShippingFareBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareAdapter
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Type

class EditShippingFareActivity : AppCompatActivity(){

    private lateinit var binding : ActivityShippingFareBinding
    private val VM = ShopVModel()

    val mAdapters_shippingFare = ShippingFareAdapter(this)
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_certained = mutableListOf<ItemShippingFare_Certained>()

    var value_txtViewFareRange :String = ""



    var weight_check = false
    var length_check = false
    var width_check = false
    var height_check = false


    var product_edit_spec_session = false
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
        product_edit_spec_session =  MMKV.mmkvWithID("http").getBoolean("product_edit_spec_session", false)

//        if(product_edit_spec_session.equals(false)){
//
//            product_edit_spec_session = true
//            getProductInfo(MMKV_product_id)
//        }
        getProductInfo(MMKV_product_id)


        try{
            Thread.sleep(800)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }


        initVM()
        initView()
    }

    fun initView() {

        MMKV_weight = MMKV.mmkvWithID("addPro").getString("datas_packagesWeights", "").toString()
        MMKV_length = MMKV.mmkvWithID("addPro").getString("datas_length", "").toString()
        MMKV_width = MMKV.mmkvWithID("addPro").getString("datas_width", "").toString()
        MMKV_height = MMKV.mmkvWithID("addPro").getString("datas_height", "").toString()
        binding.editPackageWeight.setText(MMKV_weight)
        binding.editPackageLength.setText(MMKV_length)
        binding.editPackageWidth.setText(MMKV_width)
        binding.editPackageHeight.setText(MMKV_height)


        var fare_datas_size = MMKV.mmkvWithID("addPro").getString("fare_datas_size","0")

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

        if(MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
            binding.btnShippingFareStore.isEnabled = true
            binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
        }else{
            binding.btnShippingFareStore.isEnabled = false
            binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
        }

        setMonitor(binding.editPackageWeight, width_check)
        setMonitor(binding.editPackageLength, length_check)
        setMonitor(binding.editPackageWidth, width_check)
        setMonitor(binding.editPackageHeight, height_check)

        generateCustomFare_uneditable()

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

            generateCustomFare_uneditable()

        }


        binding.btnEditFareOn.setOnClickListener {

            //開啟編輯按鍵disable隱藏
            binding.btnEditFareOn.isVisible = false
            binding.btnEditFareOn.isEnabled = false

            //關閉編輯按鍵enable出現
            binding.btnEditFareOff.isVisible = true
            binding.btnEditFareOff.isEnabled = true

            generateCustomFare_editable()

        }

        binding.btnShippingFareStore.setOnClickListener {

            val intent = Intent(this, AddNewProductActivity::class.java)
            var datas_ship_method_and_fare : MutableList<ItemShippingFare> = mAdapters_shippingFare.get_shipping_method_datas()

            MMKV_weight = binding.editPackageWeight.text.toString()
            MMKV_length = binding.editPackageLength.text.toString()
            MMKV_width = binding.editPackageWidth.text.toString()
            MMKV_height = binding.editPackageHeight.text.toString()

            MMKV.mmkvWithID("addPro").putString("datas_packagesWeights", MMKV_weight.toString())
            MMKV.mmkvWithID("addPro").putString("datas_length", MMKV_length)
            MMKV.mmkvWithID("addPro").putString("datas_width", MMKV_width)
            MMKV.mmkvWithID("addPro").putString("datas_height", MMKV_height)

            if(datas_ship_method_and_fare.size.toString() != ""){
                MMKV.mmkvWithID("addPro").putString("fare_datas_size", datas_ship_method_and_fare.size.toString())
            }else{
                MMKV.mmkvWithID("addPro").putString("fare_datas_size", "0")
            }
            Log.d("checkVariable", datas_ship_method_and_fare.size.toString())

            for (i in 0..datas_ship_method_and_fare.size-1!!) {
                val jsonTutList_mutableList_itemShipingFare: String = GsonProvider.gson.toJson(datas_ship_method_and_fare[i])
                MMKV.mmkvWithID("addPro").putString("value_fare_item${i}", jsonTutList_mutableList_itemShipingFare)
            }

            //篩選所有已勾選的運費方式
            for (i in 0..datas_ship_method_and_fare.size-1!!) {
                if(datas_ship_method_and_fare[i].onoff == "on" ){
                    mutableList_itemShipingFare_filtered.add(
                        datas_ship_method_and_fare[i]
                    )
                }
            }

            //MMKV放入已經確定勾選的Fare Item Size
            if(mutableList_itemShipingFare_filtered.size.toString() != ""){
                MMKV.mmkvWithID("addPro").putString("fare_datas_filtered_size", mutableList_itemShipingFare_filtered.size.toString())
            }else{
                MMKV.mmkvWithID("addPro").putString("fare_datas_filtered_size", "0")

            }
            Log.d("check_content","fare_datas_filtered_size : ${mutableList_itemShipingFare_filtered.size.toString()}")

            for (i in 0..mutableList_itemShipingFare_filtered.size-1!!) {
            val jsonTutList_mutableList_itemShipingFare_filtered: String = GsonProvider.gson.toJson(mutableList_itemShipingFare_filtered[i])
            MMKV.mmkvWithID("addPro").putString("value_fare_item_filtered${i}", jsonTutList_mutableList_itemShipingFare_filtered)

            }

            value_txtViewFareRange = fare_pick_max_and_min_num(mutableList_itemShipingFare_filtered.size)
            MMKV.mmkvWithID("addPro").putString("value_txtViewFareRange", value_txtViewFareRange)

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

            MMKV.mmkvWithID("addPro").putString("jsonTutList_fare", jsonTutList_fare)


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

                    v.hideKeyboard()
                    binding.editPackageWeight.clearFocus()

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

                    v.hideKeyboard()
                    binding.editPackageWeight.clearFocus()

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
                    v.hideKeyboard()
                    binding.editPackageHeight.clearFocus()
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
                mutableList_itemShipingFare[i] = ItemShippingFare(mutableList_itemShipingFare[i].shipment_desc, mutableList_itemShipingFare[i].price, R.drawable.custom_unit_transparent, mutableList_itemShipingFare[i].onoff,  mutableList_itemShipingFare[i].shop_id)
            }

            mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
            mAdapters_shippingFare.notifyDataSetChanged()
        }



    }

    //自訂費用項目(可編輯部分)
    fun generateCustomFare_editable() {

        //進入"可編輯模式"新增資料或重新新增資料
        mutableList_itemShipingFare = mAdapters_shippingFare.get_shipping_method_datas()

        var mutableList_size = mAdapters_shippingFare.get_shipping_method_datas().size

        if(mutableList_size>=2){
            for(i in 0..mutableList_size-2){
                mutableList_itemShipingFare[i] = ItemShippingFare(mutableList_itemShipingFare[i].shipment_desc, mutableList_itemShipingFare[i].price, R.mipmap.btn_delete_fare,  mutableList_itemShipingFare[i].onoff,  mutableList_itemShipingFare[i].shop_id)
            }

            mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
            mAdapters_shippingFare.notifyDataSetChanged()

        }

    }


    fun setMonitor(editText : EditText, var_check : Boolean) {

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isNotEmpty()||s.toString()!=""){

                    when (editText) {
                        binding.editPackageWeight ->{
                            weight_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                        binding.editPackageLength ->{
                            length_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                        binding.editPackageWidth ->{
                            width_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                        binding.editPackageHeight ->{
                            height_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                    }

                    if(weight_check==true && length_check==true && width_check==true && height_check==true ){
                        binding.btnShippingFareStore.isEnabled = true
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
                    }else{
                        binding.btnShippingFareStore.isEnabled = false
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
                    }



                }else{

                    when (editText) {
                        binding.editPackageWeight ->{
                            weight_check=false
                        }
                        binding.editPackageLength ->{
                            length_check=false
                        }
                        binding.editPackageLength ->{
                            width_check=false
                        }
                        binding.editPackageLength ->{
                            height_check=false
                        }
                    }

                    if(weight_check==true && length_check==true && width_check==true && height_check==true ){
                        binding.btnShippingFareStore.isEnabled = true
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
                    }else{
                        binding.btnShippingFareStore.isEnabled = false
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
                    }

                }
            }
        }
        editText.addTextChangedListener(textWatcher)


        editText.singleLine = true
        editText.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())

                    editText.clearFocus()

                    true
                }

                else -> false
            }
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


    //計算費用最大最小範圍
    fun fare_pick_max_and_min_num(size: Int): String {
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



    private fun getProductInfo(product_id: Int) {

        val url = ApiConstants.API_HOST+"product/${product_id}/product_info_forAndroid/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductInfoBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getProductInfo", "返回資料 resStr：" + resStr)
                    Log.d("getProductInfo", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品資訊!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getProductInfo", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            productInfoList = Gson().fromJson(
                                jsonObject.toString(),
                                ProductInfoBean::class.java
                            )

                        }
                        Log.d("getProductInfo", "返回資料 productInfoList：" + productInfoList.toString())

                        MMKV.mmkvWithID("addPro").putString("datas_packagesWeights", productInfoList.weight.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_length", productInfoList.length.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_width",  productInfoList.width.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_height", productInfoList.height.toString())

                        MMKV.mmkvWithID("addPro").putString("fare_datas_size",productInfoList.product_shipment_list.size.toString())
                        if(productInfoList.product_shipment_list.size>0){
                            for (i in 0..productInfoList.product_shipment_list.size - 1) {

                                var json_shippingItem = GsonProvider.gson.toJson(ItemShippingFare(productInfoList.product_shipment_list.get(i).shipment_desc, productInfoList.product_shipment_list.get(i).price, R.drawable.custom_unit_transparent, productInfoList.product_shipment_list.get(i).onoff, MMKV_shop_id))
                                MMKV.mmkvWithID("addPro").putString("value_fare_item${i}",json_shippingItem)

                            }
                        }else{
                            var json_shippingItem = GsonProvider.gson.toJson(ItemShippingFare("", 0, R.drawable.custom_unit_transparent, "off", MMKV_shop_id))
                            MMKV.mmkvWithID("addPro").putString("value_fare_item${0}",json_shippingItem)
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
        web.Get_Data(url)
    }

}