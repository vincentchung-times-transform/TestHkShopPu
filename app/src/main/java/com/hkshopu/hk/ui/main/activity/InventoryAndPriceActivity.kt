package com.hkshopu.hk.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.databinding.ActivityInventoryAndPriceBinding
import com.hkshopu.hk.databinding.InventoryandpriceSpecListItemBinding
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ItemTouchHelperCallback
import com.hkshopu.hk.ui.main.adapter.SpecificationSpecAdapter

class InventoryAndPriceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityInventoryAndPriceBinding
    val mAdapters_InvenSpec = InventoryAndPriceSpecAdapter()
    var mutableList_InvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_InvenSize = mutableListOf<InventoryItemSize>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryAndPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    fun initView() {

        Thread(Runnable {
            runOnUiThread {
                //產生規格假資料Spec
                for ( i in 0..2) {
                    mutableList_InvenSpec.add(InventoryItemSpec("SPEC0${i}"))
                }
                //產生規格假資料Size
                for ( i in 0..2) {
                    mutableList_InvenSize.add(InventoryItemSize("1", "1", "1"))
                }


                binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                binding.rViewSpecificationItemSpec.adapter = mAdapters_InvenSpec

                mAdapters_InvenSpec.updateList(mutableList_InvenSpec, mutableList_InvenSize)
                mAdapters_InvenSpec.notifyDataSetChanged()


//                val callback = ItemTouchHelperCallback(mAdapters_InvenSpec)
//                val touchHelper = ItemTouchHelper(callback)
//                touchHelper.attachToRecyclerView(binding.rViewSpecificationItemSpec)

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

    fun initClick() {

    }
}