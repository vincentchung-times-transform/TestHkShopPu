package com.HKSHOPU.hk.ui.main.productSeller.activity

import android.content.Context
import android.content.Intent
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
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityInventoryAndPriceOldBinding
import com.HKSHOPU.hk.widget.view.disable
import com.HKSHOPU.hk.widget.view.enable
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.singleLine

class EditInventoryAndPriceOldActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceOldBinding


    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var mutableList_price = mutableListOf<String>()
    var mutableList_quant = mutableListOf<String>()
    var inven_price_range: String = ""
    var inven_quant_range: String = ""
    var mutableList_InvenDatas = mutableListOf<ItemInventory>()

    var datas_spec_size: Int = 0
    var datas_size_size: Int = 0
    var datas_spec_title_first : String = ""
    var datas_spec_title_second : String = ""
    var datas_price_size: Int = 0
    var datas_quant_size: Int = 0



    var specGroup_only:Boolean = false
    var rebuild_datas = false

    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""
    var MMKV_inven_datas_size=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryAndPriceOldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()
        rebuild_datas = MMKV.mmkvWithID("addPro").getBoolean("rebuild_datas", false)

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
            mutableList_spec.add(ItemSpecification(item_name.toString()))
        }


        for(i in 0..datas_size_size-1){
            var item_name = MMKV.mmkvWithID("addPro").getString("datas_size_item${i}", "")
            mutableList_size.add(ItemSpecification(item_name.toString()))
        }

        datas_price_size = MMKV.mmkvWithID("addPro").getString(
            "datas_price_size",
            "0"
        ).toString().toInt()
        datas_quant_size = MMKV.mmkvWithID("addPro").getString(
            "datas_quant_size",
            "0"
        ).toString().toInt()

        Log.d("rebuild_datasrebuild_datas", rebuild_datas.toString())

        if(datas_price_size.equals(0)||datas_quant_size.equals(0)
            ||datas_spec_size*datas_size_size != datas_price_size
            ||datas_spec_size*datas_size_size != datas_quant_size||rebuild_datas.equals(true)){

            binding.btnInvenStore.disable()
            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)

            for (i in 0..datas_spec_size*datas_size_size - 1) {
                mutableList_price.add("")
            }

            for (i in 0..datas_spec_size*datas_size_size - 1) {
                mutableList_quant.add("")
            }


        }else{

            binding.btnInvenStore.enable()
            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)

            for (i in 0..datas_price_size - 1) {
                var price_item = MMKV.mmkvWithID("addPro").getString("spec_price${i}", "0").toString().toInt()
                mutableList_price.add(price_item.toString())
            }

            for (i in 0..datas_quant_size - 1) {
                var quant_item = MMKV.mmkvWithID("addPro").getString("spec_quantity${i}", "0").toString().toInt()
                mutableList_quant.add(quant_item.toString())
            }

        }

    }

    fun initView() {
        binding.titleInven.setText(R.string.title_editInventoryAndPrice)

        initSpecDatas()
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
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                            }
                            2 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString()
                            }
                            3 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice03.text.toString()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant03.text.toString()
                            }

                        }

                    }
                    2 -> {

                        when (datas_size_size) {

                            1 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString()

                            }
                            2 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString()

                            }
                            3 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice03.text.toString()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant03.text.toString()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice13.text.toString()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant13.text.toString()


                            }

                        }

                    }
                    3 -> {

                        when (datas_size_size) {

                            1 -> {

                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice21.text.toString()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant21.text.toString()

                            }
                            2 -> {

                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice21.text.toString()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant21.text.toString()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice22.text.toString()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant22.text.toString()


                            }
                            3 -> {
                                mutableList_InvenDatas[0]?.price =
                                    binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity =
                                    binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price =
                                    binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity =
                                    binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price =
                                    binding.secondLayerItemPrice03.text.toString()
                                mutableList_InvenDatas[2]?.quantity =
                                    binding.secondLayerItemQuant03.text.toString()
                                mutableList_InvenDatas[3]?.price =
                                    binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[3]?.quantity =
                                    binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[4]?.price =
                                    binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[4]?.quantity =
                                    binding.secondLayerItemQuant12.text.toString()
                                mutableList_InvenDatas[5]?.price =
                                    binding.secondLayerItemPrice13.text.toString()
                                mutableList_InvenDatas[5]?.quantity =
                                    binding.secondLayerItemQuant13.text.toString()
                                mutableList_InvenDatas[6]?.price =
                                    binding.secondLayerItemPrice21.text.toString()
                                mutableList_InvenDatas[6]?.quantity =
                                    binding.secondLayerItemQuant21.text.toString()
                                mutableList_InvenDatas[7]?.price =
                                    binding.secondLayerItemPrice22.text.toString()
                                mutableList_InvenDatas[7]?.quantity =
                                    binding.secondLayerItemQuant22.text.toString()
                                mutableList_InvenDatas[8]?.price =
                                    binding.secondLayerItemPrice23.text.toString()
                                mutableList_InvenDatas[8]?.quantity =
                                    binding.secondLayerItemQuant23.text.toString()

                            }

                        }

                    }
                }


            } else if (datas_spec_size > 0 && datas_size_size == 0) {

                when (datas_spec_size) {
                    1 -> {
                        mutableList_InvenDatas[0]?.price =
                            binding.secondLayerItemPrice01.text.toString()
                        mutableList_InvenDatas[0]?.quantity =
                            binding.secondLayerItemQuant01.text.toString()
                    }
                    2 -> {
                        mutableList_InvenDatas[0]?.price =
                            binding.secondLayerItemPrice01.text.toString()
                        mutableList_InvenDatas[0]?.quantity =
                            binding.secondLayerItemQuant01.text.toString()
                        mutableList_InvenDatas[1]?.price =
                            binding.secondLayerItemPrice02.text.toString()
                        mutableList_InvenDatas[1]?.quantity =
                            binding.secondLayerItemQuant02.text.toString()

                    }
                    3 -> {

                        mutableList_InvenDatas[0]?.price =
                            binding.secondLayerItemPrice01.text.toString()
                        mutableList_InvenDatas[0]?.quantity =
                            binding.secondLayerItemQuant01.text.toString()
                        mutableList_InvenDatas[1]?.price =
                            binding.secondLayerItemPrice02.text.toString()
                        mutableList_InvenDatas[1]?.quantity =
                            binding.secondLayerItemQuant02.text.toString()
                        mutableList_InvenDatas[2]?.price =
                            binding.secondLayerItemPrice03.text.toString()
                        mutableList_InvenDatas[2]?.quantity =
                            binding.secondLayerItemQuant03.text.toString()

                    }
                }
            }

        }
    }
    fun initClick() {
        binding.titleBackAddshop.setOnClickListener {

            MMKV.mmkvWithID("editPro").putBoolean("reset_spec_datas", false)

            val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
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

            MMKV.mmkvWithID("editPro").putBoolean("reset_spec_datas", false)

            val intent = Intent(this, EditProductActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun initSpecDatas() {

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
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)


//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()

                            }
                            2->{
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant02, 1)


//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add( InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                            }
                            3->{
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
                                setTextWatcher_price(binding.textViewHKdolors03, binding.secondLayerItemPrice03, 2)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
                                setTextWatcher_quant(binding.secondLayerItemQuant03, 2)

//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))

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


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString()
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
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors11, binding.secondLayerItemPrice11, 1)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant11, 1)




//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(1), mutableList_quant.get(1) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = false
                                binding.secondLayerItemContainer13.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())
                                binding.secondLayerItemName11.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice11.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant11.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant11.text.toString()

                            }
                            2->{
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
                                setTextWatcher_price(binding.textViewHKdolors11, binding.secondLayerItemPrice11, 2)
                                setTextWatcher_price(binding.textViewHKdolors12, binding.secondLayerItemPrice12, 3)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
                                setTextWatcher_quant(binding.secondLayerItemQuant11, 2)
                                setTextWatcher_quant(binding.secondLayerItemQuant12, 3)





//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(1), mutableList_quant.get(1)))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(2), mutableList_quant.get(2) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(3), mutableList_quant.get(3) ))


                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = false
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = true
                                binding.secondLayerItemContainer13.isVisible = false

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

                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant12.text.toString()

                            }
                            3->{
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
                                setTextWatcher_price(binding.textViewHKdolors03, binding.secondLayerItemPrice03, 2)
                                setTextWatcher_price(binding.textViewHKdolors11, binding.secondLayerItemPrice11, 3)
                                setTextWatcher_price(binding.textViewHKdolors12, binding.secondLayerItemPrice12, 4)
                                setTextWatcher_price(binding.textViewHKdolors13, binding.secondLayerItemPrice13, 5)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
                                setTextWatcher_quant(binding.secondLayerItemQuant03, 2)
                                setTextWatcher_quant(binding.secondLayerItemQuant11, 3)
                                setTextWatcher_quant(binding.secondLayerItemQuant12, 4)
                                setTextWatcher_quant(binding.secondLayerItemQuant13, 5)

//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(1), mutableList_quant.get(1) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name,  mutableList_price.get(2), mutableList_quant.get(2) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name,  mutableList_price.get(3), mutableList_quant.get(3) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name,  mutableList_price.get(4), mutableList_quant.get(4) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name,  mutableList_price.get(5), mutableList_quant.get(5) ))



                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = true
                                binding.secondLayerItemContainer03.isVisible = true
                                binding.secondLayerItemContainer11.isVisible = true
                                binding.secondLayerItemContainer12.isVisible = true
                                binding.secondLayerItemContainer13.isVisible = true

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


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant12.text.toString()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice13.text.toString()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant13.text.toString()


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
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors11, binding.secondLayerItemPrice11, 1)
                                setTextWatcher_price(binding.textViewHKdolors21, binding.secondLayerItemPrice21, 2)


                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant11, 1)
                                setTextWatcher_quant(binding.secondLayerItemQuant21, 2)



//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))

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
                                binding.secondLayerItemPrice12.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant13.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName21.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice22.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant23.setText(mutableList_InvenDatas[2]?.quantity.toString())


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant12.text.toString()

                            }
                            2->{
                                setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                                setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
                                setTextWatcher_price(binding.textViewHKdolors11, binding.secondLayerItemPrice11, 2)
                                setTextWatcher_price(binding.textViewHKdolors12, binding.secondLayerItemPrice12, 3)
                                setTextWatcher_price(binding.textViewHKdolors21, binding.secondLayerItemPrice21, 4)
                                setTextWatcher_price(binding.textViewHKdolors22, binding.secondLayerItemPrice22, 5)

                                setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                                setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
                                setTextWatcher_quant(binding.secondLayerItemQuant11, 2)
                                setTextWatcher_quant(binding.secondLayerItemQuant12, 3)
                                setTextWatcher_quant(binding.secondLayerItemQuant21, 4)
                                setTextWatcher_quant(binding.secondLayerItemQuant22, 5)


//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, mutableList_price.get(3), mutableList_quant.get(3) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, mutableList_price.get(4), mutableList_quant.get(4) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, mutableList_price.get(5), mutableList_quant.get(5) ))

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


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant12.text.toString()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice21.text.toString()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant21.text.toString()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice22.text.toString()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant22.text.toString()


                            }
                            3->{
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


//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, mutableList_price.get(3), mutableList_quant.get(3) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, mutableList_price.get(4), mutableList_quant.get(4) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name, mutableList_price.get(5), mutableList_quant.get(5) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, mutableList_price.get(6), mutableList_quant.get(6) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, mutableList_price.get(7), mutableList_quant.get(7) ))
//                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[2].spec_name, mutableList_price.get(8), mutableList_quant.get(8) ))


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


                                mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                                mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                                mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                                mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                                mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString()
                                mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString()
                                mutableList_InvenDatas[3]?.price = binding.secondLayerItemPrice11.text.toString()
                                mutableList_InvenDatas[3]?.quantity = binding.secondLayerItemQuant11.text.toString()
                                mutableList_InvenDatas[4]?.price = binding.secondLayerItemPrice12.text.toString()
                                mutableList_InvenDatas[4]?.quantity = binding.secondLayerItemQuant12.text.toString()
                                mutableList_InvenDatas[5]?.price = binding.secondLayerItemPrice13.text.toString()
                                mutableList_InvenDatas[5]?.quantity = binding.secondLayerItemQuant13.text.toString()
                                mutableList_InvenDatas[6]?.price = binding.secondLayerItemPrice21.text.toString()
                                mutableList_InvenDatas[6]?.quantity = binding.secondLayerItemQuant21.text.toString()
                                mutableList_InvenDatas[7]?.price = binding.secondLayerItemPrice22.text.toString()
                                mutableList_InvenDatas[7]?.quantity = binding.secondLayerItemQuant22.text.toString()
                                mutableList_InvenDatas[8]?.price = binding.secondLayerItemPrice23.text.toString()
                                mutableList_InvenDatas[8]?.quantity = binding.secondLayerItemQuant23.text.toString()

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
                        ItemSpecification( mutableList_spec.get(i).spec_name)
                    )
                }

                mutableList_price.clear()
                mutableList_quant.clear()
                if(datas_price_size.equals(0)||datas_quant_size.equals(0)){


                    for (i in 0..datas_spec_size - 1) {
                        mutableList_price.add("")
                    }

                    for (i in 0..datas_spec_size - 1) {
                        mutableList_quant.add("")
                    }


                }else{

                    for (i in 0..datas_price_size - 1) {
                        var price_item = MMKV.mmkvWithID("addPro").getString("spec_price${i}", "0").toString().toInt()
                        mutableList_price.add(price_item.toString())
                    }

                    for (i in 0..datas_quant_size - 1) {
                        var quant_item = MMKV.mmkvWithID("addPro").getString("spec_quantity${i}", "0").toString().toInt()
                        mutableList_quant.add(quant_item.toString())
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
                        setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                        setTextWatcher_quant(binding.secondLayerItemQuant01, 0)


//                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))

                        binding.secondLayerItemContainer01.isVisible = true
                        binding.secondLayerItemContainer02.isVisible = false
                        binding.secondLayerItemContainer03.isVisible = false

                        binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                        binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                        binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())

                        mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                        mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()


                    }
                    2->{
                        setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                        setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)

                        setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                        setTextWatcher_quant(binding.secondLayerItemQuant02, 1)

//                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_size[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))


                        binding.secondLayerItemContainer01.isVisible = true
                        binding.secondLayerItemContainer02.isVisible = true
                        binding.secondLayerItemContainer03.isVisible = false

                        binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                        binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0].price.toString())
                        binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0].quantity.toString())
                        binding.secondLayerItemName02.text =  mutableList_size[1].spec_name
                        binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1].price.toString())
                        binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1].quantity.toString())

                        mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                        mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                        mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                        mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()

                    }
                    3->{
                        setTextWatcher_price(binding.textViewHKdolors01, binding.secondLayerItemPrice01, 0)
                        setTextWatcher_price(binding.textViewHKdolors02, binding.secondLayerItemPrice02, 1)
                        setTextWatcher_price(binding.textViewHKdolors03, binding.secondLayerItemPrice03, 2)


                        setTextWatcher_quant(binding.secondLayerItemQuant01, 0)
                        setTextWatcher_quant(binding.secondLayerItemQuant02, 1)
                        setTextWatcher_quant(binding.secondLayerItemQuant03, 2)



//                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, mutableList_price.get(0), mutableList_quant.get(0) ))
//                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, mutableList_price.get(1), mutableList_quant.get(1) ))
//                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[2].spec_name, mutableList_price.get(2), mutableList_quant.get(2) ))

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


                        mutableList_InvenDatas[0]?.price = binding.secondLayerItemPrice01.text.toString()
                        mutableList_InvenDatas[0]?.quantity = binding.secondLayerItemQuant01.text.toString()
                        mutableList_InvenDatas[1]?.price = binding.secondLayerItemPrice02.text.toString()
                        mutableList_InvenDatas[1]?.quantity = binding.secondLayerItemQuant02.text.toString()
                        mutableList_InvenDatas[2]?.price = binding.secondLayerItemPrice03.text.toString()
                        mutableList_InvenDatas[2]?.quantity = binding.secondLayerItemQuant03.text.toString()


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


    fun setTextWatcher_price(textView: TextView, editText : EditText, position : Int) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus ){
                binding.btnInvenStore.disable()
                binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)
            }
        }
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
//                    editText.setText("0")
                    editText.setTextColor(resources.getColor(R.color.bright_gray))
                    textView.setTextColor(resources.getColor(R.color.bright_gray))

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

                    mutableList_InvenDatas.get(position).price = editText.text.toString()

                    if(editText.text.toString() == "" ){

//                        editText.setText("0")
                        editText.setTextColor(resources.getColor(R.color.bright_gray))
                        textView.setTextColor(resources.getColor(R.color.bright_gray))

                    }else{
                        editText.setTextColor(resources.getColor(R.color.black))
                        textView.setTextColor(resources.getColor(R.color.black))
                    }


                    var empty_count = 0
                    for(i in 0..mutableList_InvenDatas.size-1){
                        if(mutableList_InvenDatas.get(i).price.isNullOrEmpty()){
                            empty_count+=1
                        }
                        if(mutableList_InvenDatas.get(i).quantity.isNullOrEmpty()){
                            empty_count+=1
                        }
                    }

                    if(empty_count>0){
                        binding.btnInvenStore.disable()
                        binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)

                    }else{
                        binding.btnInvenStore.enable()
                        binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)
                    }


                    editText.clearFocus()
                    editText.hideKeyboard()

                    true
                }

                else -> false
            }
        }

    }

    fun setTextWatcher_quant(editText : EditText, position: Int) {

        editText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus ){
                binding.btnInvenStore.disable()
                binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)
            }
        }
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

//                    editText.setText("0")
                    editText.setTextColor(resources.getColor(R.color.bright_gray))

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

                    mutableList_InvenDatas.get(position).quantity = editText.text.toString()

                    if(editText.text.toString() == "" ){
//                        editText.setText("0")
                        editText.setTextColor(resources.getColor(R.color.bright_gray))
                    }else{
                        editText.setTextColor(resources.getColor(R.color.black))
                    }


                    var empty_count = 0
                    for(i in 0..mutableList_InvenDatas.size-1){
                        if(mutableList_InvenDatas.get(i).price.isNullOrEmpty()){
                            empty_count+=1
                        }
                        if(mutableList_InvenDatas.get(i).quantity.isNullOrEmpty()){
                            empty_count+=1
                        }
                    }

                    if(empty_count>0){
                        binding.btnInvenStore.disable()
                        binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)

                    }else{
                        binding.btnInvenStore.enable()
                        binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)
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

        MMKV.mmkvWithID("editPro").putBoolean("reset_spec_datas", false)

        val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
        startActivity(intent)
        finish()

    }


}