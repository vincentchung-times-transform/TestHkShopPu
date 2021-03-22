package com.hkshopu.hk.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.databinding.ActivityAddProductDescriptionMainBinding
import com.hkshopu.hk.ui.main.adapter.ItemTouchHelperCallback
import com.hkshopu.hk.ui.main.adapter.SpecificationSizeAdapter
import com.hkshopu.hk.ui.main.adapter.SpecificationSpecAdapter

class AddProductSpecificationMainActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductDescriptionMainBinding

    val mAdapters_spec = SpecificationSpecAdapter()
    val mAdapter_size = SpecificationSizeAdapter()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var EDIT_MODE_SPEC = "0"
    var EDIT_MODE_SIZE = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductDescriptionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    fun initView() {

//        binding.btnNextStep.isEnabled = true

//        if(mutableList_spec.isNotEmpty()) {
//
//            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
//            binding.btnNextStep.isEnabled = true
//
//        }else{
//            binding.btnNextStep.setImageResource(R.mipmap.bnt_nextstepdisable)
//            binding.btnNextStep.isEnabled = false
//
//        }

        //first specification "spec" item generate
        Thread(Runnable {
            runOnUiThread {
                //產生規格假資料
                for ( i in 0..2) {
                    mutableList_spec.add(ItemSpecification("SPEC0${i}", R.drawable.customborder_specification))
                }


                binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                binding.rViewSpecificationItemSpec.adapter = mAdapters_spec

                mAdapters_spec.updateList(mutableList_spec)

                val callback = ItemTouchHelperCallback(mAdapters_spec)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(binding.rViewSpecificationItemSpec)

//                    var listview: ListView = binding.listview
//                    var adapter: CustomAdapter = CustomAdapter(this, bitmaps)
//                    listview.adapter = adapter as ListAdapter?
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

//                for (b in bitmaps) {
//                    runOnUiThread { imageView.setImageBitmap(b) }
//                    try {
//                        Thread.sleep(3000)
//                    } catch (e: InterruptedException) {
//                        e.printStackTrace()
//                    }
//                }
        }).start()

        //second specification "size" item generate
        Thread(Runnable {
            runOnUiThread {


                for ( i in 0..2) {
                    mutableList_size.add(ItemSpecification("SIZE0${i}", R.drawable.custom_unit_transparent))
                }

                binding.rviewSpecificationitemSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                binding.rviewSpecificationitemSize.adapter = mAdapter_size

                mAdapter_size.updateList(mutableList_size)

                val callback = ItemTouchHelperCallback(mAdapter_size)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(binding.rviewSpecificationitemSize)

//                    var listview: ListView = binding.listview
//                    var adapter: CustomAdapter = CustomAdapter(this, bitmaps)
//                    listview.adapter = adapter as ListAdapter?
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

//                for (b in bitmaps) {
//                    runOnUiThread { imageView.setImageBitmap(b) }
//                    try {
//                        Thread.sleep(3000)
//                    } catch (e: InterruptedException) {
//                        e.printStackTrace()
//                    }
//                }
        }).start()


        initClick()

    }

    fun initClick(){

        binding.btnNextStep.setOnClickListener {
            val intent = Intent(this, InventoryAndPriceActivity::class.java)
            startActivity(intent)
        }

        //first specification "spec" item add
        binding.btnAddspecificationSpec.setOnClickListener {

            if (mutableList_spec.size <3) {

                if(EDIT_MODE_SPEC=="0"){
                    Thread(Runnable {
                        runOnUiThread {

                            mutableList_spec.add(ItemSpecification("SPEC0${mutableList_spec.size+1}", R.drawable.custom_unit_transparent))

                            binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                            binding.rViewSpecificationItemSpec.adapter = mAdapters_spec

                            mAdapters_spec.updateList(mutableList_spec)

                            try {
                                Thread.sleep(500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                        }

                    }).start()

                }else{

                    Thread(Runnable {
                        runOnUiThread {

                            mutableList_spec.add(ItemSpecification("SPEC0${mutableList_spec.size+1}", R.mipmap.btn_cancel))

                            binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                            binding.rViewSpecificationItemSpec.adapter = mAdapters_spec

                            mAdapters_spec.updateList(mutableList_spec)

                            try {
                                Thread.sleep(500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                        }

                    }).start()

                }

            }else {

                Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()

            }



        }
        //second specification "spec" item setting cancel
        binding.btnEditspecificationDisableSpec.setOnClickListener {

            EDIT_MODE_SPEC = "0"

            binding.btnEditspecificationDisableSpec.isEnabled = false
            binding.btnEditspecificationDisableSpec.isVisible = false
            binding.btnEditspecificationEnableSpec.isEnabled = true
            binding.btnEditspecificationEnableSpec.isVisible = true

            mutableList_spec.clear()

            for ( i in 0..2) {
                mutableList_spec.add(ItemSpecification("SPEC0${i}", R.drawable.customborder_specification))
            }
            binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
            binding.rViewSpecificationItemSpec.adapter = mAdapters_spec

            mAdapters_spec.updateList(mutableList_spec)
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }



        }
        //second specification "spec" setting enable
        binding.btnEditspecificationEnableSpec.setOnClickListener {

            EDIT_MODE_SPEC = "1"

            binding.btnEditspecificationEnableSpec.isEnabled = false
            binding.btnEditspecificationEnableSpec.isVisible = false
            binding.btnEditspecificationDisableSpec.isEnabled = true
            binding.btnEditspecificationDisableSpec.isVisible = true


            mutableList_spec.clear()

            for ( i in 0..2) {
                mutableList_spec.add(ItemSpecification("SPEC0${i}", R.mipmap.btn_cancel))
            }

            binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
            binding.rViewSpecificationItemSpec.adapter = mAdapters_spec

            mAdapters_spec.updateList(mutableList_spec)

            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }

        //second specification "size" item add
        binding.btnAddspecificationSize.setOnClickListener {

            if (mutableList_size.size<3){

                if(EDIT_MODE_SIZE == "0") {

                    mutableList_size.add(ItemSpecification("SIZE0${mutableList_size.size+1}", R.drawable.custom_unit_transparent))

                    binding.rviewSpecificationitemSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                    binding.rviewSpecificationitemSize.adapter = mAdapter_size

                    mAdapter_size.updateList(mutableList_size)

                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }else{
                    mutableList_size.add(ItemSpecification("SIZE0${mutableList_size.size+1}", R.mipmap.btn_cancel))

                    binding.rviewSpecificationitemSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                    binding.rviewSpecificationitemSize.adapter = mAdapter_size

                    mAdapter_size.updateList(mutableList_size)

                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }



            }else {
                Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()
            }




        }

        //second specification "size" item settings cancel
        binding.btnEditspecificationDisableSize.setOnClickListener {

            EDIT_MODE_SIZE = "0"

            binding.btnEditspecificationDisableSize.isEnabled = false
            binding.btnEditspecificationDisableSize.isVisible = false
            binding.btnEditspecificationEnableSize.isEnabled = true
            binding.btnEditspecificationEnableSize.isVisible = true

            mutableList_size.clear()

            for ( i in 0..2) {
                mutableList_size.add(ItemSpecification("SIZE0${i}", R.drawable.customborder_specification))
            }
            binding.rviewSpecificationitemSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
            binding.rviewSpecificationitemSize.adapter = mAdapter_size

            mAdapter_size.updateList(mutableList_size)
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }



        }

        //second specification "size" item settings enable
        binding.btnEditspecificationEnableSize.setOnClickListener {

            EDIT_MODE_SIZE = "1"

            binding.btnEditspecificationEnableSize.isEnabled = false
            binding.btnEditspecificationEnableSize.isVisible = false
            binding.btnEditspecificationDisableSize.isEnabled = true
            binding.btnEditspecificationDisableSize.isVisible = true


            mutableList_size.clear()

            for ( i in 0..2) {
                mutableList_size.add(ItemSpecification("SIZE0${i}", R.mipmap.btn_cancel))
            }

            binding.rviewSpecificationitemSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
            binding.rviewSpecificationitemSize.adapter = mAdapter_size

            mAdapter_size.updateList(mutableList_size)

            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }
}