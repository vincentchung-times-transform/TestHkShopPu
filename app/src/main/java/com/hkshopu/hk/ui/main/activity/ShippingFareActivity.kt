package com.hkshopu.hk.ui.main.activity

import MyLinearLayoutManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ItemShippingFare_Certained
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding
import com.hkshopu.hk.databinding.ActivityShippingFareBinding
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareAdapter
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.singleLine

class ShippingFareActivity : AppCompatActivity(){

    private lateinit var binding : ActivityShippingFareBinding

    val mAdapters_shippingFare = ShippingFareAdapter(this)
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_certained = mutableListOf<ItemShippingFare_Certained>()

    var value_txtViewFareRange :String = ""

    var weight_check = false
    var length_check = false
    var width_check = false
    var height_check = false

    //資料變數宣告
    var datas_packagesWeights : Int = 0
    var datas_lenght : String = ""
    var datas_width : String = ""
    var datas_height : String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingFareBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()
    }

    fun initView() {

        initRecyclerView_ShippingFareItem()

        binding.btnEditFareOn.isVisible = true
        binding.btnEditFareOn.isEnabled = true
        binding.btnEditFareOff.isVisible = false
        binding.btnEditFareOff.isEnabled = false
        binding.btnShippingFareStore.isEnabled = false
        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)



        setMonitor(binding.editPackageWeight, width_check)
        setMonitor(binding.editPackageLength, length_check)
        setMonitor(binding.editPackageWidth, width_check)
        setMonitor(binding.editPackageHeight, height_check)


        generateCustomFare_uneditable()


        initClick()
        initEdit()
    }

    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, AddNewProductActivity::class.java)
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

            MMKV.mmkvWithID("addPro").putString("datas_packagesWeights", datas_packagesWeights.toString())
            MMKV.mmkvWithID("addPro").putString("datas_lenght", datas_lenght)
            MMKV.mmkvWithID("addPro").putString("datas_width", datas_width)
            MMKV.mmkvWithID("addPro").putString("datas_height", datas_height)

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
                if(datas_ship_method_and_fare[i].onoff ==true ){
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


            startActivity(intent)
            finish()

        }

    }

    fun initEdit() {


        binding.editPackageWeight.singleLine = true
        binding.editPackageWeight.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_packagesWeights = binding.editPackageWeight.text.toString().toInt()

                    v.hideKeyboard()
                    binding.editPackageWeight.clearFocus()

                    true
                }
                else -> false
            }
        }

        binding.editPackageLength.singleLine = true
        binding.editPackageLength.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_lenght = binding.editPackageLength.text.toString()

                    v.hideKeyboard()
                    binding.editPackageLength.clearFocus()

                    true
                }
                else -> false
            }
        }


        binding.editPackageWidth.singleLine = true
        binding.editPackageWidth.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_width = binding.editPackageWidth.text.toString()

                    v.hideKeyboard()
                    binding.editPackageWeight.clearFocus()

                    true
                }
                else -> false
            }
        }


        binding.editPackageHeight.singleLine = true
        binding.editPackageHeight.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_height = binding.editPackageHeight.text.toString()

                    v.hideKeyboard()
                    binding.editPackageHeight.clearFocus()

                    true
                }
                else -> false
            }
        }

    }

    fun initRecyclerView_ShippingFareItem() {

        initFareDatas()

        //自訂layoutManager
        binding.rViewFareItemSpec.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewFareItemSpec.adapter = mAdapters_shippingFare

        mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
        mAdapters_shippingFare.notifyDataSetChanged()
    }

    fun initFareDatas() {

        mutableList_itemShipingFare.add(ItemShippingFare("郵局", 0, R.drawable.custom_unit_transparent, false, 0))
        mutableList_itemShipingFare.add(ItemShippingFare("順豐速運", 0, R.drawable.custom_unit_transparent, false, 0))
        mutableList_itemShipingFare.add(ItemShippingFare("", 0, R.drawable.custom_unit_transparent, false, 0))

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

        val intent = Intent(this, AddNewProductActivity::class.java)
        startActivity(intent)
        finish()
    }

}