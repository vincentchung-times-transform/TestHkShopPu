package com.hkshopu.hk.ui.main.product.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventCheckInvenSpecEnableBtnOrNot
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable
import com.tencent.mmkv.MMKV

class AddInventoryAndPriceActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding


    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var mutableList_price = mutableListOf<Int>()
    var mutableList_quant = mutableListOf<Int>()
    var inven_price_range: String = ""
    var inven_quant_range: String = ""
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    val mAdapter = InventoryAndPriceSpecAdapter()
    var mutableList_Inventory = mutableListOf<ItemInventory>()

    var datas_spec_size: Int = 0
    var datas_size_size: Int = 0
    var datas_spec_title_first : String = ""
    var datas_spec_title_second : String = ""
    var datas_price_size: Int = 0
    var datas_quant_size: Int = 0



    var specGroup_only:Boolean = false
    var rebuild_datas = false

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

        datas_spec_title_first = MMKV.mmkvWithID("addPro_temp").getString("value_editTextProductSpecFirst", "").toString()
        datas_spec_title_second = MMKV.mmkvWithID("addPro_temp").getString("value_editTextProductSpecSecond", "").toString()
        datas_spec_size = MMKV.mmkvWithID("addPro_temp").getString("datas_spec_size", "0").toString().toInt()
        datas_size_size = MMKV.mmkvWithID("addPro_temp").getString("datas_size_size", "0").toString().toInt()

        for(i in 0..datas_spec_size-1){
            var item_name = MMKV.mmkvWithID("addPro_temp").getString("datas_spec_item${i}", "")
            mutableList_spec.add(ItemSpecification(item_name.toString(), R.drawable.custom_unit_transparent))
        }


        for(i in 0..datas_size_size-1){
            var item_name = MMKV.mmkvWithID("addPro_temp").getString("datas_size_item${i}", "")
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



        for (i in 0..datas_price_size - 1) {
            var price_item = MMKV.mmkvWithID("addPro").getString("spec_price${i}", "0").toString().toInt()
            mutableList_price.add(price_item)
        }

        for (i in 0..datas_quant_size - 1) {
            var quant_item = MMKV.mmkvWithID("addPro").getString("spec_quantity${i}", "0").toString().toInt()
            mutableList_quant.add(quant_item)
        }

        rebuild_datas = MMKV.mmkvWithID("addPro_temp").getBoolean("rebuild_datas", false)

        if(!datas_spec_title_first.equals("") && datas_spec_title_second.equals("") ){
            specGroup_only = true

            if(mutableList_spec.size == datas_price_size &&  !rebuild_datas){

                for(i in 0..datas_spec_size-1){
                    mutableList_Inventory.add(ItemInventory(datas_spec_title_first, "", mutableList_spec.get(i).spec_name, "","", ""))

                }


                for(i in 0..datas_spec_size-1){
                    mutableList_Inventory.get(i).price = mutableList_price.get(i).toString()
                    mutableList_Inventory.get(i).quantity = mutableList_quant.get(i).toString()
                }


            }else{

                for(i in 0..datas_spec_size-1){
                    mutableList_Inventory.add(ItemInventory(datas_spec_title_first, "", mutableList_spec.get(i).spec_name,"","", ""))

                }
            }


        }else{

            specGroup_only = false

            if(mutableList_spec.size*mutableList_size.size   == datas_price_size && mutableList_spec.size*mutableList_size.size  == datas_quant_size &&  rebuild_datas.equals(false)){


                for(i in 0..datas_spec_size-1){

                    for(j in 0..datas_size_size-1){

                        mutableList_Inventory.add(ItemInventory(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i).spec_name, mutableList_size.get(j).spec_name,"", ""))

                    }
                }

                for(i in 0..datas_spec_size*datas_size_size-1){
                    mutableList_Inventory.get(i).price = mutableList_price.get(i).toString()
                    mutableList_Inventory.get(i).quantity = mutableList_quant.get(i).toString()
                }


            }else{

                for(i in 0..datas_spec_size-1){

                    for(j in 0..datas_size_size-1){

                        mutableList_Inventory.add(ItemInventory(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i).spec_name, mutableList_size.get(j).spec_name,"", ""))

                    }
                }
            }

        }



        binding.rViewInventory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rViewInventory.adapter = mAdapter

        mAdapter.updateList(mutableList_Inventory, specGroup_only, datas_size_size)


    }

    fun initView() {

//        initSpecDatas()


        initEvent()
        initClick()


    }


    fun initClick() {
        binding.titleBackAddshop.setOnClickListener {

            MMKV.mmkvWithID("addPro_temp").putBoolean("get_temp", true)
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnInvenStore.setOnClickListener {


            MMKV.mmkvWithID("addPro").putString("datas_spec_size", mutableList_spec.size.toString())
            MMKV.mmkvWithID("addPro").putString("datas_size_size", mutableList_size.size.toString())
            MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecFirst", datas_spec_title_first)
            MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecSecond", datas_spec_title_second)


            for (i in 0..datas_spec_size - 1) {

                MMKV.mmkvWithID("addPro")
                    .putString("datas_spec_item${i}", mutableList_spec.get(i).spec_name.toString())
            }

            for (i in 0..datas_size_size - 1) {

                MMKV.mmkvWithID("addPro")
                    .putString("datas_size_item${i}", mutableList_size.get(i).spec_name.toString())

            }


            mutableList_Inventory = mAdapter.getDatas_invenSpec()

            for (i in 0..mutableList_Inventory.size-1){

                mutableList_InvenDatas.add(
                    InventoryItemDatas(
                        mutableList_Inventory.get(i).spec_desc_1,
                        mutableList_Inventory.get(i).spec_desc_2,
                        mutableList_Inventory.get(i).spec_dec_1_items,
                        mutableList_Inventory.get(i).spec_dec_2_items,
                        mutableList_Inventory.get(i).price.toInt(),
                        mutableList_Inventory.get(i).quantity.toInt())
                )

            }
            MMKV.mmkvWithID("addPro").putInt("inven_datas_size", mutableList_InvenDatas.size)


//            save_Price_Quant_Datas()

            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonTutList_inven: String = gson.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutList_inven.toString())
            val jsonTutListPretty_inven: String = gsonPretty.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutListPretty_inven.toString())

            MMKV.mmkvWithID("addPro").putString("jsonTutList_inven", jsonTutList_inven)
            Log.d("result_check",jsonTutList_inven )
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


            MMKV.mmkvWithID("addPro_temp").clear()

            val intent = Intent(this, AddNewProductActivity::class.java)
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

        MMKV.mmkvWithID("addPro_temp").putBoolean("get_temp", true)
        val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        var boolean: Boolean

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventCheckInvenSpecEnableBtnOrNot -> {

                        boolean = it.boolean

                        if(!boolean){
                            binding.btnInvenStore.disable()
                            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)

                        }else{
                            var empty_count = 0

                            for(i in 0..mutableList_Inventory.size -1){

                                if(mutableList_Inventory.get(i).price.equals("")){
                                    empty_count+=1

                                }
                                if(mutableList_Inventory.get(i).quantity.equals("")){
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
                        }

                    }
                }
            }, {
                it.printStackTrace()
            })

    }

}