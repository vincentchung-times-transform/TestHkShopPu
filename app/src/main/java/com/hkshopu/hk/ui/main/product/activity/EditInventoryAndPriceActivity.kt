package com.hkshopu.hk.ui.main.product.activity

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
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecAdapter
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Type

class EditInventoryAndPriceActivity : AppCompatActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding


    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var inven_price_range: String = ""
    var inven_quant_range: String = ""
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    var datas_spec_size: Int = 0
    var datas_size_size: Int = 0
    var datas_spec_title_first : String = ""
    var datas_spec_title_second : String = ""

    var specGroup_only:Boolean = false

    //宣告頁面資料變數
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1
    var MMKV_inven_datas_size=0
    lateinit var productInfoList :  ProductInfoBean


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryAndPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)

        getProductInfo(MMKV_product_id)
        try{
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        initMMKV()
        initView()
    }

    fun initMMKV() {


        datas_spec_title_first = MMKV.mmkvWithID("addPro").getString("value_editTextProductSpecFirst", "").toString()
        datas_spec_title_second = MMKV.mmkvWithID("addPro").getString("value_editTextProductSpecSecond", "").toString()
        datas_spec_size = MMKV.mmkvWithID("addPro").getString("datas_spec_size", "0").toString().toInt()
        datas_size_size = MMKV.mmkvWithID("addPro").getString("datas_size_size", "0").toString().toInt()

        for(i in 0..datas_spec_size-1){
            var item_name = MMKV.mmkvWithID("addPro").getString("datas_spec_item${i}", "")
            mutableList_spec.add(ItemSpecification(item_name.toString(), R.drawable.custom_unit_transparent))
        }

        for(i in 0..datas_size_size-1){
            var item_name = MMKV.mmkvWithID("addPro").getString("datas_size_item${i}", "")
            mutableList_size.add(ItemSpecification(item_name.toString(), R.drawable.custom_unit_transparent))
        }

    }

    fun initView() {
        binding.titleInven.setText(R.string.title_editInventoryAndPrice)
        initSpecDatas()

        if (mutableList_InvenDatas.isNotEmpty()){

            binding.btnInvenStore.isVisible = true
            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)

        }else{

            binding.btnInvenStore.isVisible = true
            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)
        }

        initClick()


    }

    fun save_Price_Quant_Datas() {

        if(datas_spec_size != null &&  datas_size_size != null) {


            if (datas_spec_size > 0 && datas_size_size > 0) {

                when (datas_spec_size) {
                    1 -> {

                        when (datas_size_size) {

                            1 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                            }
                            2 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString().toInt()
                            }
                            3 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant03.text.toString().toInt()
                            }

                        }

                    }
                    2 -> {

                        when (datas_size_size) {

                            1 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant04.text.toString().toInt()

                            }
                            2 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant05.text.toString().toInt()

                            }
                            3 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant03.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant05.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice06.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant06.text.toString().toInt()


                            }

                        }

                    }
                    3 -> {

                        when (datas_size_size) {

                            1 -> {

                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice07.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant07.text.toString().toInt()

                            }
                            2 -> {

                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant05.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice07.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant07.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice08.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant08.text.toString().toInt()


                            }
                            3 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant03.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant05.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice06.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant06.text.toString().toInt()
                                mutableList_InvenDatas[6]?.price =
                                    binding.secondLayerItemPrice07.text.toString().toInt()
                                mutableList_InvenDatas[6]?.quantity =
                                    binding.secondLayerItemQuant07.text.toString().toInt()
                                mutableList_InvenDatas[7]?.price =
                                    binding.secondLayerItemPrice08.text.toString().toInt()
                                mutableList_InvenDatas[7]?.quantity =
                                    binding.secondLayerItemQuant08.text.toString().toInt()
                                mutableList_InvenDatas[8]?.price =
                                    binding.secondLayerItemPrice09.text.toString().toInt()
                                mutableList_InvenDatas[8]?.quantity =
                                    binding.secondLayerItemQuant09.text.toString().toInt()

                            }

                        }

                    }
                }


            } else if (datas_spec_size > 0 && datas_size_size == 0) {

                when (datas_spec_size) {
                    1 -> {
                        mutableList_InvenDatas[0]?.price =
                            binding.secondLayerItemPrice01.text.toString().toInt()
                        mutableList_InvenDatas[0]?.quantity =
                            binding.secondLayerItemQuant01.text.toString().toInt()
                    }
                    2 -> {
                        mutableList_InvenDatas[0]?.price =
                            binding.secondLayerItemPrice01.text.toString().toInt()
                        mutableList_InvenDatas[0]?.quantity =
                            binding.secondLayerItemQuant01.text.toString().toInt()
                        mutableList_InvenDatas[1]?.price =
                            binding.secondLayerItemPrice02.text.toString().toInt()
                        mutableList_InvenDatas[1]?.quantity =
                            binding.secondLayerItemQuant02.text.toString().toInt()

                    }
                    3 -> {

                        mutableList_InvenDatas[0]?.price =
                            binding.secondLayerItemPrice01.text.toString().toInt()
                        mutableList_InvenDatas[0]?.quantity =
                            binding.secondLayerItemQuant01.text.toString().toInt()
                        mutableList_InvenDatas[1]?.price =
                            binding.secondLayerItemPrice02.text.toString().toInt()
                        mutableList_InvenDatas[1]?.quantity =
                            binding.secondLayerItemQuant02.text.toString().toInt()
                        mutableList_InvenDatas[2]?.price =
                            binding.secondLayerItemPrice03.text.toString().toInt()
                        mutableList_InvenDatas[2]?.quantity =
                            binding.secondLayerItemQuant03.text.toString().toInt()

                    }
                }
            }

        }
    }
    fun initClick() {
        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnInvenStore.setOnClickListener {

            val intent = Intent(this, EditProductActivity::class.java)

            MMKV.mmkvWithID("addPro").putInt("inven_datas_size", mutableList_InvenDatas.size)

            save_Price_Quant_Datas()

            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonTutList_inven: String = gson.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutList_inven.toString())
            val jsonTutListPretty_inven: String = gsonPretty.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutListPretty_inven.toString())

            MMKV.mmkvWithID("addPro").putString("jsonTutList_inven", jsonTutList_inven)

            //MMKV放入mutableList_InvenDatas
            for(i in 0..mutableList_InvenDatas.size!!-1){

                val gson = Gson()
                val jsonTutList: String = gson.toJson(mutableList_InvenDatas.indexOf(i))

                MMKV.mmkvWithID("addPro").putString("value_inven${i}", jsonTutList)

            }

            //挑選最大與最小金額，回傳價格區間
            inven_price_range = inven_price_pick_max_and_min_num(mutableList_InvenDatas.size!!)
            inven_quant_range = inven_quant_pick_max_and_min_num(mutableList_InvenDatas.size!!)
            MMKV.mmkvWithID("addPro").putString("inven_price_range", inven_price_range)
            MMKV.mmkvWithID("addPro").putString("inven_quant_range", inven_quant_range)

            startActivity(intent)

            finish()
        }

    }
    fun initSpecDatas() {

        setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
        setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
        setTextWatcher_price(binding.textViewHKdolors03, binding.secondLayerItemPrice03, 2)
        setTextWatcher_price(binding.textViewHKdolors04, binding.secondLayerItemPrice04, 3)
        setTextWatcher_price(binding.textViewHKdolors05, binding.secondLayerItemPrice05, 4)
        setTextWatcher_price(binding.textViewHKdolors06, binding.secondLayerItemPrice06, 5)
        setTextWatcher_price(binding.textViewHKdolors07, binding.secondLayerItemPrice07, 6)
        setTextWatcher_price(binding.textViewHKdolors08, binding.secondLayerItemPrice08, 7)
        setTextWatcher_price(binding.textViewHKdolors09, binding.secondLayerItemPrice09, 8)

        setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
        setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
        setTextWatcher_quant(binding.secondLayerItemQuant03, 2)
        setTextWatcher_quant(binding.secondLayerItemQuant04, 3)
        setTextWatcher_quant(binding.secondLayerItemQuant05, 4)
        setTextWatcher_quant(binding.secondLayerItemQuant06, 5)
        setTextWatcher_quant(binding.secondLayerItemQuant07, 6)
        setTextWatcher_quant(binding.secondLayerItemQuant08, 7)
        setTextWatcher_quant(binding.secondLayerItemQuant09, 8)


        if(datas_spec_size != null &&  datas_size_size != null) {

            specGroup_only = false

            if(datas_spec_size > 0 && datas_size_size > 0){

                when(datas_spec_size){
                    1->{
                        binding.containerInvenItem01.isVisible = true
                        binding.containerInvenItem02.isVisible = false
                        binding.containerInvenItem03.isVisible = false

                        binding.firstLayerSpec01.text = datas_spec_title_first
                        binding.firstLayerTitle01.text = mutableList_spec[0].spec_name
                        binding.firstLayerColumn01.text  = datas_spec_title_second

                        when(datas_size_size){

                            1->{
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()

                            }
                            2->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add( InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                            }
                            3->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = true

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName03.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice03.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant03.setText(mutableList_InvenDatas[2]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString().toInt()
                            }

                        }

                    }
                    2->{

                        binding.containerInvenItem01.isVisible = true
                        binding.containerInvenItem02.isVisible = true
                        binding.containerInvenItem03.isVisible = false

                        binding.firstLayerSpec01.text = datas_spec_title_first
                        binding.firstLayerTitle01.text = mutableList_spec[0].spec_name
                        binding.firstLayerSpec02.text = datas_spec_title_first
                        binding.firstLayerTitle02.text = mutableList_spec[1].spec_name
                        binding.firstLayerColumn01.text  = datas_spec_title_second
                        binding.firstLayerColumn02.text  = datas_spec_title_second


                        when(datas_size_size){

                            1->{
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer04.isVisible = true
                                binding.secondLayerItemContainer05.isVisible = false
                                binding.secondLayerItemContainer06.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant04.text.toString().toInt()

                            }
                            2->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name,  productInfoList.price.get(1), productInfoList.spec_quantity.get(1)))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name,  productInfoList.price.get(3), productInfoList.spec_quantity.get(3) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer04.isVisible = true
                                binding.secondLayerItemContainer05.isVisible = true
                                binding.secondLayerItemContainer06.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName05.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice05.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant05.setText(mutableList_InvenDatas[3]?.quantity.toString())

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant05.text.toString().toInt()

                            }
                            3->{
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name,  productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name,  productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  productInfoList.price.get(3), productInfoList.spec_quantity.get(3) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name,  productInfoList.price.get(4), productInfoList.spec_quantity.get(4) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name,  productInfoList.price.get(5), productInfoList.spec_quantity.get(5) ))



                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = true
                                binding.secondLayerItemContainer04.isVisible = true
                                binding.secondLayerItemContainer05.isVisible = true
                                binding.secondLayerItemContainer06.isVisible = true

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName03.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice03.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant03.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[3]?.quantity.toString())
                                binding.secondLayerItemName05.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice05.setText(mutableList_InvenDatas[4]?.price.toString())
                                binding.secondLayerItemQuant05.setText(mutableList_InvenDatas[4]?.quantity.toString())
                                binding.secondLayerItemName06.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice06.setText(mutableList_InvenDatas[5]?.price.toString())
                                binding.secondLayerItemQuant06.setText(mutableList_InvenDatas[5]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant05.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice06.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant06.text.toString().toInt()


                            }

                        }

                    }
                    3->{


                        binding.firstLayerSpec01.text = datas_spec_title_first
                        binding.firstLayerTitle01.text = mutableList_spec[0].spec_name
                        binding.firstLayerSpec02.text = datas_spec_title_first
                        binding.firstLayerTitle02.text = mutableList_spec[1].spec_name
                        binding.firstLayerSpec03.text = datas_spec_title_first
                        binding.firstLayerTitle03.text = mutableList_spec[2].spec_name
                        binding.firstLayerColumn01.text  = datas_spec_title_second
                        binding.firstLayerColumn02.text  = datas_spec_title_second
                        binding.firstLayerColumn03.text  = datas_spec_title_second

                        binding.containerInvenItem01.isVisible = true
                        binding.containerInvenItem02.isVisible = true
                        binding.containerInvenItem03.isVisible = true

                        when(datas_size_size){

                            1->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer04.isVisible = true
                                binding.secondLayerItemContainer05.isVisible = false
                                binding.secondLayerItemContainer06.isVisible = false
                                binding.secondLayerItemContainer07.isVisible = true
                                binding.secondLayerItemContainer08.isVisible = false
                                binding.secondLayerItemContainer09.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName07.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice07.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant07.setText(mutableList_InvenDatas[2]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice07.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant07.text.toString().toInt()

                            }
                            2->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(3), productInfoList.spec_quantity.get(3) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(4), productInfoList.spec_quantity.get(4) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(5), productInfoList.spec_quantity.get(5) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer04.isVisible = true
                                binding.secondLayerItemContainer05.isVisible = true
                                binding.secondLayerItemContainer06.isVisible = false
                                binding.secondLayerItemContainer07.isVisible = true
                                binding.secondLayerItemContainer08.isVisible = true
                                binding.secondLayerItemContainer09.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName05.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice05.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant05.setText(mutableList_InvenDatas[3]?.quantity.toString())
                                binding.secondLayerItemName07.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice07.setText(mutableList_InvenDatas[4]?.price.toString())
                                binding.secondLayerItemQuant07.setText(mutableList_InvenDatas[4]?.quantity.toString())
                                binding.secondLayerItemName08.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice08.setText(mutableList_InvenDatas[5]?.price.toString())
                                binding.secondLayerItemQuant08.setText(mutableList_InvenDatas[5]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant05.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice07.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant07.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice08.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant08.text.toString().toInt()


                            }
                            3->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(3), productInfoList.spec_quantity.get(3) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(4), productInfoList.spec_quantity.get(4) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name, productInfoList.price.get(5), productInfoList.spec_quantity.get(5) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, productInfoList.price.get(6), productInfoList.spec_quantity.get(6) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, productInfoList.price.get(7), productInfoList.spec_quantity.get(7) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[2].spec_name, productInfoList.price.get(8), productInfoList.spec_quantity.get(8) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = true
                                binding.secondLayerItemContainer04.isVisible = true
                                binding.secondLayerItemContainer05.isVisible = true
                                binding.secondLayerItemContainer06.isVisible = true
                                binding.secondLayerItemContainer07.isVisible = true
                                binding.secondLayerItemContainer08.isVisible = true
                                binding.secondLayerItemContainer09.isVisible = true

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName03.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice03.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant03.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[3]?.quantity.toString())
                                binding.secondLayerItemName05.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice05.setText(mutableList_InvenDatas[4]?.price.toString())
                                binding.secondLayerItemQuant05.setText(mutableList_InvenDatas[4]?.quantity.toString())
                                binding.secondLayerItemName06.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice06.setText(mutableList_InvenDatas[5]?.price.toString())
                                binding.secondLayerItemQuant06.setText(mutableList_InvenDatas[5]?.quantity.toString())
                                binding.secondLayerItemName07.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice07.setText(mutableList_InvenDatas[6]?.price.toString())
                                binding.secondLayerItemQuant07.setText(mutableList_InvenDatas[6]?.quantity.toString())
                                binding.secondLayerItemName08.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice08.setText(mutableList_InvenDatas[7]?.price.toString())
                                binding.secondLayerItemQuant08.setText(mutableList_InvenDatas[7]?.quantity.toString())
                                binding.secondLayerItemName09.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice09.setText(mutableList_InvenDatas[8]?.price.toString())
                                binding.secondLayerItemQuant09.setText(mutableList_InvenDatas[8]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice04.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant04.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice05.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant05.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice06.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant06.text.toString().toInt()
                                mutableList_InvenDatas[6]?.price = binding.secondLayerItemPrice07.text.toString().toInt()
                                mutableList_InvenDatas[6]?.quantity = binding.secondLayerItemQuant07.text.toString().toInt()
                                mutableList_InvenDatas[7]?.price = binding.secondLayerItemPrice08.text.toString().toInt()
                                mutableList_InvenDatas[7]?.quantity = binding.secondLayerItemQuant08.text.toString().toInt()
                                mutableList_InvenDatas[8]?.price = binding.secondLayerItemPrice09.text.toString().toInt()
                                mutableList_InvenDatas[8]?.quantity = binding.secondLayerItemQuant09.text.toString().toInt()

                            }

                        }

                    }
                }


            }else if( datas_spec_size>0 && datas_size_size==0){

                specGroup_only = true

                binding.firstLayerColumn01.text = datas_spec_title_first

                mutableList_spec.add(ItemSpecification("",R.drawable.custom_unit_transparent))

                for (i in 0..datas_spec_size-1){
                    mutableList_size.add(
                        intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getParcelable<ItemSpecification>("spec" + i.toString())!!
                    )
                }

                binding.containerInvenItem01.isVisible = true
                binding.containerInvenItem02.isVisible = false
                binding.containerInvenItem03.isVisible = false

                if(specGroup_only==true){
                    binding.firstLayerSpec01.isVisible = false
                    binding.containerFistLayerItemTitle.isVisible = false

                }else{
                    binding.firstLayerSpec01.isVisible = true
                    binding.containerFistLayerItemTitle.isVisible = true
                }

                when(datas_spec_size){
                    1->{

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))

                        binding.secondLayerItemContainer01.isVisible = true
                        binding.secondLayerItemContainer02.isVisible = false
                        binding.secondLayerItemContainer03.isVisible = false

                        binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                        binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                        binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())

                        mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                        mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()


                    }
                    2->{

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_size[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))


                        binding.secondLayerItemContainer01.isVisible = true
                        binding.secondLayerItemContainer02.isVisible = true
                        binding.secondLayerItemContainer03.isVisible = false

                        binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                        binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                        binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())
                        binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                        binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1].price.toString())
                        binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1].quantity.toString())

                        mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                        mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                        mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                        mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()

                    }
                    3->{

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, productInfoList.price.get(0), productInfoList.spec_quantity.get(0) ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, productInfoList.price.get(1), productInfoList.spec_quantity.get(1) ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[2].spec_name, productInfoList.price.get(2), productInfoList.spec_quantity.get(2) ))

                        binding.secondLayerItemContainer01.isVisible = true
                        binding.secondLayerItemContainer02.isVisible = true
                        binding.secondLayerItemContainer03.isVisible = true

                        binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                        binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                        binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())
                        binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                        binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1].price.toString())
                        binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1].quantity.toString())
                        binding.secondLayerItemName03.text =  mutableList_size[2].spec_name
                        binding.secondLayerItemPrice03.setText(mutableList_InvenDatas[2].price.toString())
                        binding.secondLayerItemQuant03.setText(mutableList_InvenDatas[2].quantity.toString())


                        mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                        mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                        mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                        mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                        mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString().toInt()
                        mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString().toInt()


                    }
                }
            }



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


    fun setTextWatcher_price(textView: TextView, editText : EditText, postion : Int) {

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(editText.text.toString().length >= 2 && editText.text.toString().startsWith("0")){
                    editText.setText(editText.text.toString().replace("0", "", false))
                    editText.setSelection(editText.text.toString().length)
                }

            }
        }
        editText.addTextChangedListener(textWatcher)

        editText.singleLine = true
        editText.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    if(editText.text.toString() == "" ){

                        editText.setText("0")

                    }else{
                        editText.setTextColor(resources.getColor(R.color.black))
                        textView.setTextColor(resources.getColor(R.color.black))
                    }

                    editText.clearFocus()
                    editText.hideKeyboard()

                    true
                }

                else -> false
            }
        }

    }

    fun setTextWatcher_quant(editText : EditText, postion: Int) {


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(editText.text.toString().length >= 2 && editText.text.toString().startsWith("0")){
                    editText.setText(editText.text.toString().replace("0", "", false))
                    editText.setSelection(editText.text.toString().length)
                }

            }
        }
        editText.addTextChangedListener(textWatcher)


        editText.singleLine = true
        editText.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    if(editText.text.toString() == "" ){
                        editText.setText("0")
                    }else{
                        editText.setTextColor(resources.getColor(R.color.black))
                    }


                    editText.clearFocus()
                    editText.hideKeyboard()

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


    //計算庫存"費用"最大最小範圍
    fun inven_price_pick_max_and_min_num(size: Int): String {

        var min: Int = 0
        var max: Int = 0

        //挑出最大與最小的數字
        if(!(mutableList_InvenDatas.size==0||mutableList_InvenDatas.size == null)){

            min = mutableList_InvenDatas[0]!!.price.toInt()
            max = mutableList_InvenDatas[0]!!.price.toInt()

        }

        for (f in 1..size-1) {

            if(mutableList_InvenDatas[f]!!.price.toInt() >= min ){
                max = mutableList_InvenDatas[f]!!.price.toInt()
            }else{
                min = mutableList_InvenDatas[f]!!.price.toInt()
            }

        }

        return "HKD$${min}-HKD$${max}"

    }

    //計算庫存"數量"最大最小範圍
    fun inven_quant_pick_max_and_min_num(size: Int): String {
        //挑出最大與最小的數字


        var min: Int = 0
        var max: Int = 0

        //挑出最大與最小的數字
        if(!(mutableList_InvenDatas.size==0||mutableList_InvenDatas.size == null)){
            min = mutableList_InvenDatas[0]!!.quantity.toInt()
            max = mutableList_InvenDatas[0]!!.quantity.toInt()
        }


        for (f in 1..size-1) {
            if(mutableList_InvenDatas[f]!!.quantity.toInt() >= min ){
                max = mutableList_InvenDatas[f]!!.quantity.toInt()
            }else{
                min = mutableList_InvenDatas[f]!!.quantity.toInt()
            }
        }

        return "${min}-${max}"

    }

    override fun onBackPressed() {

        val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
        startActivity(intent)
        finish()

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

//                        MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecFirst", productInfoList.spec_desc_1.get(0)).toString()
//                        MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecSecond", productInfoList.spec_desc_2.get(0)).toString()
//                        MMKV.mmkvWithID("addPro").putString("datas_spec_size", productInfoList.spec_dec_1_items.size.toString()).toString().toInt()
//                        MMKV.mmkvWithID("addPro").putString("datas_size_size", productInfoList.spec_dec_2_items.size.toString()).toString().toInt()


                        var mutableSet_spec_dec_1_items : MutableSet<String> = productInfoList.spec_dec_1_items.toMutableSet()
                        var mutableSet_spec_dec_2_items : MutableSet<String> = productInfoList.spec_dec_2_items.toMutableSet()
                        var mutableList_spec_dec_1_items : MutableList<String> = mutableSet_spec_dec_1_items.toMutableList()
                        var mutableList_spec_dec_2_items : MutableList<String> = mutableSet_spec_dec_2_items.toMutableList()

//                        MMKV.mmkvWithID("addPro").putString("datas_spec_size",  mutableSet_spec_dec_1_items.size.toString())
//                        MMKV.mmkvWithID("addPro").putString("datas_size_size",  mutableSet_spec_dec_2_items.size.toString())

                        Thread(Runnable {

                            for(i in 0..mutableSet_spec_dec_1_items.size -1){
                                MMKV.mmkvWithID("addPro").putString("datas_spec_item${i}", mutableList_spec_dec_1_items.get(i))
                            }

                        }).start()


                        Thread(Runnable {

                            for(i in 0..mutableSet_spec_dec_2_items.size-1){
                                MMKV.mmkvWithID("addPro").putString("datas_size_item${i}", mutableList_spec_dec_2_items.get(i))
                            }

                            runOnUiThread {

                            }

                        }).start()



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