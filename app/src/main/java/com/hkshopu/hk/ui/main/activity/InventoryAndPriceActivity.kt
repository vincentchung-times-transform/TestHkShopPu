package com.hkshopu.hk.ui.main.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.databinding.InventoryandpriceSpecListItemBinding
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSizeAdapter
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ItemTouchHelperCallback
import com.hkshopu.hk.ui.main.adapter.SpecificationSpecAdapter

class InventoryAndPriceActivity : AppCompatActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding
    val mAdapters_InvenSpec = InventoryAndPriceSpecAdapter()

    var mutableList_InvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_InvenSize = mutableListOf<InventoryItemSize>()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()


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

        generateInventoryItems()

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
            var datas_invenSpec_spec = datas_invenSpec.size
            var datas_invenSize_size = datas_invenSize.size

            var bundle = Bundle()

            bundle.putInt("datas_invenSpec_spec", datas_invenSpec_spec)
            bundle.putInt("datas_invenSize_size", datas_invenSize_size)

            for(key in 0..datas_invenSpec.size-1) {
                bundle.putParcelable("spec"+key.toString(), datas_invenSpec.get(key)!!)
            }

            for(key in 0..datas_invenSize.size-1) {
                bundle.putParcelable("size"+key.toString(), datas_invenSize.get(key)!!)
            }

            intent.putExtra("InventoryAndPriceActivity", bundle)

            startActivity(intent)
        }

    }
    fun initSpecDatas() {

        //取得Bundle傳來的分類資料
        var datas_spec_size: Int =
            intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getInt("datas_spec_size")!!
        var datas_size_size: Int =
            intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getInt("datas_size_size")!!


        if(datas_spec_size > 0 && datas_size_size > 0){

            specGroup_only = false

            for (i in 0..datas_spec_size-1){
                mutableList_spec.add( intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getParcelable<ItemSpecification>("spec"+i.toString())!!)
            }

            for (i in 0..datas_size_size-1) {
                mutableList_size.add(
                    intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getParcelable<ItemSpecification>("size" + i.toString())!!
                )
            }
        }else if( datas_spec_size>0 && datas_size_size==0){

            specGroup_only = true

            mutableList_spec.add(ItemSpecification("",R.drawable.custom_unit_transparent))
            for (i in 0..datas_spec_size-1){
                mutableList_size.add(
                    intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getParcelable<ItemSpecification>("spec" + i.toString())!!
                )
            }
        }

    }

    fun generateInventoryItems() {

        //從Bundle取得資料
        var datas_spec_title_first : String = intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getString("datas_spec_title_first")!!
        var datas_spec_title_second : String = intent.getBundleExtra("bundle_AddProductSpecificationMainActivity")?.getString("datas_spec_title_second")!!


        binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.rViewSpecificationItemSpec.adapter = mAdapters_InvenSpec

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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun afterTextChanged(s: Editable?) {

    }

}