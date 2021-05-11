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
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecAdapter
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.singleLine

class InventoryAndPriceActivity : AppCompatActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding
    val mAdapters_InvenSpec = InventoryAndPriceSpecAdapter()

    var mutableList_InvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_InvenSize = mutableListOf<InventoryItemSize>()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var inven_price_range: String = ""
    var inven_quant_range: String = ""

    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()


    var specGroup_only:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryAndPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    fun initView() {

        initSpecDatas()

        if (mutableList_InvenSpec.isNotEmpty()){
            binding.btnInvenStore.isVisible = true
            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)

        }else{
//            binding.btnInvenStore.isEnabled = false
//            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)
            binding.btnInvenStore.isVisible = true
            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)
        }

        initClick()


    }

    fun save_Price_Quant_Datas() {

           //取得Bundle傳來的分類資料
        var datas_spec_size: Int =
            intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getInt("datas_spec_size")!!
        var datas_size_size: Int =
            intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getInt("datas_size_size")!!


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
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnInvenStore.setOnClickListener {

            val intent = Intent(this, AddNewProductActivity::class.java)

//            var datas_invenSpec: MutableList<InventoryItemSpec> = mAdapters_InvenSpec.getDatas_invenSpec()
//            var datas_invenSize: MutableList<InventoryItemSize> = mAdapters_InvenSpec.getDatas_invenSize()
//            var datas_invenSpec_size = datas_invenSpec.size
//            var datas_invenSize_size = datas_invenSize.size

//            var bundle = Bundle()
//
//            bundle.putInt("InvenDatas_size", mutableList_InvenDatas.size)
//            bundle.putInt("datas_invenSpec_size", datas_invenSpec_size)
//            bundle.putInt("datas_invenSize_size", datas_invenSize_size)

//            for(key in 0..datas_invenSpec.size-1) {
//                bundle.putParcelable("spec"+key.toString(), datas_invenSpec.get(key)!!)
//            }
//
//            for(key in 0..datas_invenSize.size-1) {
//                bundle.putParcelable("size"+key.toString(), datas_invenSize.get(key)!!)
//            }


            MMKV.mmkvWithID("addPro").putInt("inven_datas_size", mutableList_InvenDatas.size)

            save_Price_Quant_Datas()

//            for(key in 0..mutableList_InvenDatas.size-1){
//                bundle.putParcelable("InvenDatas"+key.toString(), mutableList_InvenDatas.get(key)!!)
//            }

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


//            intent.putExtra("InventoryAndPriceActivity", bundle)

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



        //從Bundle取得資料
        var datas_spec_title_first : String = intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getString("datas_spec_title_first")!!
        var datas_spec_title_second : String = intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getString("datas_spec_title_second")!!


        //(Discard)Rendering inventory items by RecyclerView
//        binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
//        binding.rViewSpecificationItemSpec.adapter = mAdapters_InvenSpec


        //取得Bundle傳來的分類資料
        var datas_spec_size: Int =
            intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getInt("datas_spec_size")!!
        var datas_size_size: Int =
            intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getInt("datas_size_size")!!


        if(datas_spec_size != null &&  datas_size_size != null) {

            specGroup_only = false

            for (i in 0..datas_spec_size-1){
                mutableList_spec.add( intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getParcelable<ItemSpecification>("spec"+i.toString())!!)
            }

            for (i in 0..datas_size_size-1) {
                mutableList_size.add(
                    intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getParcelable<ItemSpecification>("size" + i.toString())!!
                )
            }


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
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))

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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add( InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, 0, 0 ))

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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, 0, 0 ))

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
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, 0, 0 ))


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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, 0, 0 ))


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
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name, 0, 0 ))



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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, 0, 0 ))

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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, 0, 0 ))

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

                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[2].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[1].spec_name, mutableList_size[2].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[0].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[1].spec_name, 0, 0 ))
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[2].spec_name, mutableList_size[2].spec_name, 0, 0 ))


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

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, 0, 0 ))

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

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_size[0].spec_name, 0, 0 ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, 0, 0 ))


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

                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[0].spec_name, 0, 0 ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[1].spec_name, 0, 0 ))
                        mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, "", mutableList_spec[2].spec_name, 0, 0 ))

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


//            Thread(Runnable {
//
//                //產生規格資料Spec
//                for ( i in 0..mutableList_spec.size-1) {
//                    mutableList_InvenSpec.add(InventoryItemSpec(mutableList_spec[i].spec_name))
//                }
//                //產生規格資料Size
//                for ( i in 0..mutableList_size.size-1) {
//                    mutableList_InvenSize.add(InventoryItemSize(mutableList_size[i].spec_name, 0, 0))
//                }
//
//                runOnUiThread {
//
//                    mAdapters_InvenSpec.updateList(mutableList_InvenSpec, mutableList_InvenSize, specGroup_only, datas_spec_title_first, datas_spec_title_second)
//                    mAdapters_InvenSpec.notifyDataSetChanged()
//
//                }
//
//            }).start()
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

        val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
        startActivity(intent)
        finish()

    }


}