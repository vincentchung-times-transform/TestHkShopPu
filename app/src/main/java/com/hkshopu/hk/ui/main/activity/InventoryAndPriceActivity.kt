package com.hkshopu.hk.ui.main.activity

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.databinding.InventoryandpriceSpecListItemBinding
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSizeAdapter
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ItemTouchHelperCallback
import com.hkshopu.hk.ui.main.adapter.SpecificationSpecAdapter
import org.jetbrains.anko.singleLine

class InventoryAndPriceActivity : AppCompatActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding
    val mAdapters_InvenSpec = InventoryAndPriceSpecAdapter()

    var mutableList_InvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_InvenSize = mutableListOf<InventoryItemSize>()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()

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

    fun initClick() {
        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)
        }

        binding.btnInvenStore.setOnClickListener {

            val intent = Intent(this, AddNewProductActivity::class.java)

            var datas_invenSpec: MutableList<InventoryItemSpec> = mAdapters_InvenSpec.getDatas_invenSpec()
            var datas_invenSize: MutableList<InventoryItemSize> = mAdapters_InvenSpec.getDatas_invenSize()
            var datas_invenSpec_size = datas_invenSpec.size
            var datas_invenSize_size = datas_invenSize.size

            var bundle = Bundle()

            bundle.putInt("datas_invenSpec_spec", datas_invenSpec_size)
            bundle.putInt("datas_invenSize_size", datas_invenSize_size)
            bundle.putInt("InvenDatas_size", mutableList_InvenDatas.size)

            for(key in 0..datas_invenSpec.size-1) {
                bundle.putParcelable("spec"+key.toString(), datas_invenSpec.get(key)!!)
            }

            for(key in 0..datas_invenSize.size-1) {
                bundle.putParcelable("size"+key.toString(), datas_invenSize.get(key)!!)
            }

            for(key in 0..mutableList_InvenDatas.size-1){
                bundle.putParcelable("InvenDatas"+key.toString(), mutableList_InvenDatas.get(key)!!)

            }

            intent.putExtra("InventoryAndPriceActivity", bundle)

            startActivity(intent)
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

                        when(datas_size_size){

                            1->{
                                mutableList_InvenDatas.add(InventoryItemDatas(datas_spec_title_first, datas_spec_title_second, mutableList_spec[0].spec_name, mutableList_size[0].spec_name, 0, 0 ))

                                binding.secondLayerItemContainer01.isVisible = true
                                binding.secondLayerItemContainer02.isVisible = false
                                binding.secondLayerItemContainer03.isVisible = false

                                binding.secondLayerItemName01.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice01.setText(mutableList_InvenDatas[0]?.price.toString())
                                binding.secondLayerItemQuant01.setText(mutableList_InvenDatas[0]?.quantity.toString())

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
                                binding.secondLayerItemName02.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice02.setText(mutableList_InvenDatas[1]?.price.toString())
                                binding.secondLayerItemQuant02.setText(mutableList_InvenDatas[1]?.quantity.toString())
                                binding.secondLayerItemName04.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice04.setText(mutableList_InvenDatas[2]?.price.toString())
                                binding.secondLayerItemQuant04.setText(mutableList_InvenDatas[2]?.quantity.toString())
                                binding.secondLayerItemName05.text =  mutableList_size[0].spec_name
                                binding.secondLayerItemPrice05.setText(mutableList_InvenDatas[3]?.price.toString())
                                binding.secondLayerItemQuant05.setText(mutableList_InvenDatas[3]?.quantity.toString())

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

                            }

                        }

                    }
                }


            }else if( datas_spec_size>0 && datas_size_size==0){

                specGroup_only = true


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

                    }
                }
            }


            Thread(Runnable {

                //產生規格資料Spec
                for ( i in 0..mutableList_spec.size-1) {
                    mutableList_InvenSpec.add(InventoryItemSpec(mutableList_spec[i].spec_name))
                }
                //產生規格資料Size
                for ( i in 0..mutableList_size.size-1) {
                    mutableList_InvenSize.add(InventoryItemSize(mutableList_size[i].spec_name, 0, 0))
                }

                runOnUiThread {

                    mAdapters_InvenSpec.updateList(mutableList_InvenSpec, mutableList_InvenSize, specGroup_only, datas_spec_title_first, datas_spec_title_second)
                    mAdapters_InvenSpec.notifyDataSetChanged()

                }

            }).start()
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


}