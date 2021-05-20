package com.hkshopu.hk.ui.main.product.activity

import android.app.Activity
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
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecAdapter
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.singleLine

class AddInventoryAndPriceActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding


    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var mutableList_price = mutableListOf<Int>()
    var mutableList_quant = mutableListOf<Int>()
    var inven_price_range: String = ""
    var inven_quant_range: String = ""
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    var datas_spec_size: Int = 0
    var datas_size_size: Int = 0
    var datas_spec_title_first : String = ""
    var datas_spec_title_second : String = ""
    var datas_price_size: Int = 0
    var datas_quant_size: Int = 0



    var specGroup_only:Boolean = false

    //宣告頁面資料變數
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1
    var MMKV_inven_datas_size=0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryAndPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)

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

        datas_price_size = MMKV.mmkvWithID("addPro").getString(
            "datas_price_size",
            "0"
        ).toString().toInt()
        datas_quant_size = MMKV.mmkvWithID("addPro").getString(
            "datas_quant_size",
            "0"
        ).toString().toInt()

        if(datas_price_size.equals(0)||datas_quant_size.equals(0)){


            for (i in 0..datas_spec_size*datas_size_size - 1) {
                mutableList_price.add(0)
            }

            for (i in 0..datas_spec_size*datas_size_size - 1) {
                mutableList_quant.add(0)
            }


        }else{

            for (i in 0..datas_price_size - 1) {
                var price_item = MMKV.mmkvWithID("addPro").getString("spec_price${i}", "0").toString().toInt()
                mutableList_price.add(price_item)
            }

            for (i in 0..datas_quant_size - 1) {
                var quant_item = MMKV.mmkvWithID("addPro").getString("spec_quantity${i}", "0").toString().toInt()
                mutableList_quant.add(quant_item)
            }

        }
    }

    fun initView() {

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
//                            4->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                            }
//                            5->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                            }
//                            6->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//
//                            }
//                            7->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//
//                            }
//                            8->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//
//                            }
//                            9->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//
//                            }
//                            10->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//
//                            }
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
                                    binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString().toInt()

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
                                    binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString().toInt()

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
                                    binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice13.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant13.text.toString().toInt()


                            }
//                            4->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//
//
//                            }
//                            5->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//
//
//                            }
//                            6->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//
//                            }
//                            7->{
//
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//
//
//                            }
//                            8->{
//
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//
//                            }
//                            9->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//
//                            }
//                            10->{
//
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.price =
//                                    binding.secondLayerItemPrice20.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.quantity =
//                                    binding.secondLayerItemQuant20.text.toString().toInt()
//
//                            }

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
                                    binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice21.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant21.text.toString().toInt()

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
                                    binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice21.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant21.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice22.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant22.text.toString().toInt()


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
                                    binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice13.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant13.text.toString().toInt()

                                mutableList_InvenDatas[6]?.price =
                                    binding.secondLayerItemPrice21.text.toString().toInt()
                                mutableList_InvenDatas[6]?.quantity =
                                    binding.secondLayerItemQuant21.text.toString().toInt()
                                mutableList_InvenDatas[7]?.price =
                                    binding.secondLayerItemPrice22.text.toString().toInt()
                                mutableList_InvenDatas[7]?.quantity =
                                    binding.secondLayerItemQuant22.text.toString().toInt()
                                mutableList_InvenDatas[8]?.price =
                                    binding.secondLayerItemPrice23.text.toString().toInt()
                                mutableList_InvenDatas[8]?.quantity =
                                    binding.secondLayerItemQuant23.text.toString().toInt()

                            }
//                            4->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice21.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant21.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice22.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant22.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice23.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant23.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice24.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant24.text.toString().toInt()
//
//                            }
//                            5->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice31.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant31.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice32.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant32.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice33.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant33.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice34.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant34.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice35.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant35.text.toString().toInt()
//
//                            }
//                            6->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.price =
//                                    binding.secondLayerItemPrice20.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.quantity =
//                                    binding.secondLayerItemQuant20.text.toString().toInt()
//                            }
//                            7->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.price =
//                                    binding.secondLayerItemPrice20.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.quantity =
//                                    binding.secondLayerItemQuant20.text.toString().toInt()
//                            }
//                            8->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.price =
//                                    binding.secondLayerItemPrice20.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.quantity =
//                                    binding.secondLayerItemQuant20.text.toString().toInt()
//                            }
//                            9->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.price =
//                                    binding.secondLayerItemPrice20.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.quantity =
//                                    binding.secondLayerItemQuant20.text.toString().toInt()
//                            }
//                            10->{
//                                mutableList_InvenDatas[0]?.price =
//                                    binding.secondLayerItemPrice01.text.toString().toInt()
//                                mutableList_InvenDatas[0]?.quantity =
//                                    binding.secondLayerItemQuant01.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.price =
//                                    binding.secondLayerItemPrice02.text.toString().toInt()
//                                mutableList_InvenDatas[1]?.quantity =
//                                    binding.secondLayerItemQuant02.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.price =
//                                    binding.secondLayerItemPrice03.text.toString().toInt()
//                                mutableList_InvenDatas[2]?.quantity =
//                                    binding.secondLayerItemQuant03.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.price =
//                                    binding.secondLayerItemPrice04.text.toString().toInt()
//                                mutableList_InvenDatas[3]?.quantity =
//                                    binding.secondLayerItemQuant04.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.price =
//                                    binding.secondLayerItemPrice05.text.toString().toInt()
//                                mutableList_InvenDatas[4]?.quantity =
//                                    binding.secondLayerItemQuant05.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.price =
//                                    binding.secondLayerItemPrice06.text.toString().toInt()
//                                mutableList_InvenDatas[5]?.quantity =
//                                    binding.secondLayerItemQuant06.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.price =
//                                    binding.secondLayerItemPrice07.text.toString().toInt()
//                                mutableList_InvenDatas[6]?.quantity =
//                                    binding.secondLayerItemQuant07.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.price =
//                                    binding.secondLayerItemPrice08.text.toString().toInt()
//                                mutableList_InvenDatas[7]?.quantity =
//                                    binding.secondLayerItemQuant08.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.price =
//                                    binding.secondLayerItemPrice09.text.toString().toInt()
//                                mutableList_InvenDatas[8]?.quantity =
//                                    binding.secondLayerItemQuant09.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.price =
//                                    binding.secondLayerItemPrice10.text.toString().toInt()
//                                mutableList_InvenDatas[9]?.quantity =
//                                    binding.secondLayerItemQuant10.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.price =
//                                    binding.secondLayerItemPrice11.text.toString().toInt()
//                                mutableList_InvenDatas[10]?.quantity =
//                                    binding.secondLayerItemQuant11.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.price =
//                                    binding.secondLayerItemPrice12.text.toString().toInt()
//                                mutableList_InvenDatas[11]?.quantity =
//                                    binding.secondLayerItemQuant12.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.price =
//                                    binding.secondLayerItemPrice13.text.toString().toInt()
//                                mutableList_InvenDatas[12]?.quantity =
//                                    binding.secondLayerItemQuant13.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.price =
//                                    binding.secondLayerItemPrice14.text.toString().toInt()
//                                mutableList_InvenDatas[13]?.quantity =
//                                    binding.secondLayerItemQuant14.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.price =
//                                    binding.secondLayerItemPrice15.text.toString().toInt()
//                                mutableList_InvenDatas[14]?.quantity =
//                                    binding.secondLayerItemQuant15.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.price =
//                                    binding.secondLayerItemPrice16.text.toString().toInt()
//                                mutableList_InvenDatas[15]?.quantity =
//                                    binding.secondLayerItemQuant16.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.price =
//                                    binding.secondLayerItemPrice17.text.toString().toInt()
//                                mutableList_InvenDatas[16]?.quantity =
//                                    binding.secondLayerItemQuant17.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.price =
//                                    binding.secondLayerItemPrice18.text.toString().toInt()
//                                mutableList_InvenDatas[17]?.quantity =
//                                    binding.secondLayerItemQuant18.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.price =
//                                    binding.secondLayerItemPrice19.text.toString().toInt()
//                                mutableList_InvenDatas[18]?.quantity =
//                                    binding.secondLayerItemQuant19.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.price =
//                                    binding.secondLayerItemPrice20.text.toString().toInt()
//                                mutableList_InvenDatas[19]?.quantity =
//                                    binding.secondLayerItemQuant20.text.toString().toInt()
//                            }

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
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnInvenStore.setOnClickListener {

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

            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
    fun initSpecDatas() {

        setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
        setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
        setTextWatcher_price(binding.textViewHKdolors03, binding.secondLayerItemPrice03, 2)
        setTextWatcher_price(binding.textViewHKdolors11, binding.secondLayerItemPrice11, 3)
        setTextWatcher_price(binding.textViewHKdolors12, binding.secondLayerItemPrice12, 4)
        setTextWatcher_price(binding.textViewHKdolors13, binding.secondLayerItemPrice13, 5)
        setTextWatcher_price(binding.textViewHKdolors21, binding.secondLayerItemPrice21, 6)
        setTextWatcher_price(binding.textViewHKdolors22, binding.secondLayerItemPrice22, 7)
        setTextWatcher_price(binding.textViewHKdolors23, binding.secondLayerItemPrice23, 8)

        setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
        setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
        setTextWatcher_quant(binding.secondLayerItemQuant03, 2)
        setTextWatcher_quant(binding.secondLayerItemQuant11, 3)
        setTextWatcher_quant(binding.secondLayerItemQuant12, 4)
        setTextWatcher_quant(binding.secondLayerItemQuant13, 5)
        setTextWatcher_quant(binding.secondLayerItemQuant21, 6)
        setTextWatcher_quant(binding.secondLayerItemQuant22, 7)
        setTextWatcher_quant(binding.secondLayerItemQuant23, 8)


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
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))

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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add( InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))

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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))

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
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(1), mutableList_quant.get(1) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = false
                                binding.secondLayerItemContainer13.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[1].price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[1].quantity.toString())
                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant11.text.toString().toInt()

                            }
                            2->{
                                Log.d("datas_spec_title_first",
                                    "datas_spec_title_first : ${datas_spec_title_first} ;" +
                                            " datas_spec_title_second : ${datas_spec_title_second} ;" +
                                            " mutableList_spec : ${mutableList_spec}} ;" +
                                            " mutableList_size : ${mutableList_size} ;" +
                                            " mutableList_price : ${mutableList_price} ;" +
                                            " mutableList_quant : ${mutableList_quant} ; " +
                                            " datas_price_size : ${datas_price_size} ; " +
                                            " datas_quant_size : ${datas_quant_size}")

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(1), mutableList_quant.get(1)))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(2), mutableList_quant.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(3), mutableList_quant.get(3) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1].price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1].quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[2].price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[2].quantity.toString())
                                binding.secondLayerItemName12.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice12.setText(mutableList_InvenDatas[3].price.toString())
                                binding.secondLayerItemQuant12.setText(mutableList_InvenDatas[3].quantity.toString())

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant12.text.toString().toInt()

                            }
                            3->{
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(1), mutableList_quant.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name,  mutableList_price.get(2), mutableList_quant.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(3), mutableList_quant.get(3) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(4), mutableList_quant.get(4) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name,  mutableList_price.get(5), mutableList_quant.get(5) ))



                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = true

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName03.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice03.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant03.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[3]?.quantity.toString())
                                binding.secondLayerItemName12.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice12.setText(mutableList_InvenDatas[4]?.price.toString())
                                binding.secondLayerItemQuant12.setText(mutableList_InvenDatas[4]?.quantity.toString())
                                binding.secondLayerItemName13.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice13.setText(mutableList_InvenDatas[5]?.price.toString())
                                binding.secondLayerItemQuant13.setText(mutableList_InvenDatas[5]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant12.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice13.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant13.text.toString().toInt()


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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = false
                                binding.secondLayerItemContainer13.isVisible = false
                                binding.secondLayerItemContainer21.isVisible = true
                                binding.secondLayerItemContainer22.isVisible = false
                                binding.secondLayerItemContainer23.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName21.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice21.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant21.setText(mutableList_InvenDatas[2]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice21.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant21.text.toString().toInt()

                            }
                            2->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, mutableList_price.get(3), mutableList_quant.get(3) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, mutableList_price.get(4), mutableList_quant.get(4) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, mutableList_price.get(5), mutableList_quant.get(5) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = true
                                binding.secondLayerItemContainer13.isVisible = false
                                binding.secondLayerItemContainer21.isVisible = true
                                binding.secondLayerItemContainer22.isVisible = true
                                binding.secondLayerItemContainer23.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName12.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice12.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant12.setText(mutableList_InvenDatas[3]?.quantity.toString())
                                binding.secondLayerItemName21.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice21.setText(mutableList_InvenDatas[4]?.price.toString())
                                binding.secondLayerItemQuant21.setText(mutableList_InvenDatas[4]?.quantity.toString())
                                binding.secondLayerItemName22.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice22.setText(mutableList_InvenDatas[5]?.price.toString())
                                binding.secondLayerItemQuant22.setText(mutableList_InvenDatas[5]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant12.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice21.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant21.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice22.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant22.text.toString().toInt()


                            }
                            3->{

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, mutableList_price.get(3), mutableList_quant.get(3) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, mutableList_price.get(4), mutableList_quant.get(4) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name, mutableList_price.get(5), mutableList_quant.get(5) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, mutableList_price.get(6), mutableList_quant.get(6) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, mutableList_price.get(7), mutableList_quant.get(7) ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[2].spec_name, mutableList_price.get(8), mutableList_quant.get(8) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = true
                                binding.secondLayerItemContainer13.isVisible = true
                                binding.secondLayerItemContainer21.isVisible = true
                                binding.secondLayerItemContainer22.isVisible = true
                                binding.secondLayerItemContainer23.isVisible = true

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName03.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice03.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant03.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[3]?.quantity.toString())
                                binding.secondLayerItemName12.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice12.setText(mutableList_InvenDatas[4]?.price.toString())
                                binding.secondLayerItemQuant12.setText(mutableList_InvenDatas[4]?.quantity.toString())
                                binding.secondLayerItemName13.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice13.setText(mutableList_InvenDatas[5]?.price.toString())
                                binding.secondLayerItemQuant13.setText(mutableList_InvenDatas[5]?.quantity.toString())
                                binding.secondLayerItemName21.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice21.setText(mutableList_InvenDatas[6]?.price.toString())
                                binding.secondLayerItemQuant21.setText(mutableList_InvenDatas[6]?.quantity.toString())
                                binding.secondLayerItemName22.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice22.setText(mutableList_InvenDatas[7]?.price.toString())
                                binding.secondLayerItemQuant22.setText(mutableList_InvenDatas[7]?.quantity.toString())
                                binding.secondLayerItemName23.text =  mutableList_size[2].spec_name
                                binding.secondLayerItemPrice23.setText(mutableList_InvenDatas[8]?.price.toString())
                                binding.secondLayerItemQuant23.setText(mutableList_InvenDatas[8]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString().toInt()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString().toInt()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString().toInt()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString().toInt()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString().toInt()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString().toInt()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice11.text.toString().toInt()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant11.text.toString().toInt()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice12.text.toString().toInt()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant12.text.toString().toInt()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice13.text.toString().toInt()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant13.text.toString().toInt()
                                mutableList_InvenDatas[6]?.price = binding.secondLayerItemPrice21.text.toString().toInt()
                                mutableList_InvenDatas[6]?.quantity = binding.secondLayerItemQuant21.text.toString().toInt()
                                mutableList_InvenDatas[7]?.price = binding.secondLayerItemPrice22.text.toString().toInt()
                                mutableList_InvenDatas[7]?.quantity = binding.secondLayerItemQuant22.text.toString().toInt()
                                mutableList_InvenDatas[8]?.price = binding.secondLayerItemPrice23.text.toString().toInt()
                                mutableList_InvenDatas[8]?.quantity = binding.secondLayerItemQuant23.text.toString().toInt()

                            }

                        }

                    }
                }


            }else if( datas_spec_size>0 && datas_size_size==0){

                specGroup_only = true

                binding.firstLayerColumn01.text = datas_spec_title_first



                mutableList_size.clear()
                for (i in 0..datas_spec_size-1){
                    mutableList_size.add(
                        ItemSpecification( mutableList_spec.get(i).spec_name, R.drawable.custom_unit_transparent)
                    )
                }

                mutableList_price.clear()
                mutableList_quant.clear()
                if(datas_price_size.equals(0)||datas_quant_size.equals(0)){


                    for (i in 0..datas_spec_size - 1) {
                        mutableList_price.add(0)
                    }

                    for (i in 0..datas_spec_size - 1) {
                        mutableList_quant.add(0)
                    }


                }else{

                    for (i in 0..datas_price_size - 1) {
                        var price_item = MMKV.mmkvWithID("addPro").getString("spec_price${i}", "0").toString().toInt()
                        mutableList_price.add(price_item)
                    }

                    for (i in 0..datas_quant_size - 1) {
                        var quant_item = MMKV.mmkvWithID("addPro").getString("spec_quantity${i}", "0").toString().toInt()
                        mutableList_quant.add(quant_item)
                    }

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

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))

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

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))


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

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[2].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))

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

                if(editText.text.toString() == "" ){

                    editText.setText("0")
                    editText.setTextColor(resources.getColor(R.color.gray_txt))
                    textView.setTextColor(resources.getColor(R.color.gray_txt))
                }else{
                    editText.setTextColor(resources.getColor(R.color.black))
                    textView.setTextColor(resources.getColor(R.color.black))
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
                        editText.setTextColor(resources.getColor(R.color.gray_txt))
                        textView.setTextColor(resources.getColor(R.color.gray_txt))

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

                if(editText.text.toString() == "" ){

                    editText.setText("0")
                    editText.setTextColor(resources.getColor(R.color.gray_txt))
                }else{
                    editText.setTextColor(resources.getColor(R.color.black))
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
                        editText.setTextColor(resources.getColor(R.color.gray_txt))
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

}