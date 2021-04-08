package com.hkshopu.hk.ui.main.activity

import MyLinearLayoutManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding
import com.hkshopu.hk.databinding.ActivityShippingFareBinding
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareAdapter
import org.jetbrains.anko.singleLine

class ShippingFareActivity : AppCompatActivity(){

    private lateinit var binding : ActivityShippingFareBinding

    val mAdapters_shippingFare = ShippingFareAdapter()
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingFareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    fun initView() {

//        val textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//            override fun afterTextChanged(s: Editable?) {
//
//                if (s.toString().isEmpty()){
//                    binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
//                    binding.btnShippingFareStore.isEnabled = false
//                }else{
//                    binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
//                    binding.btnShippingFareStore.isEnabled = true
//
//                }
//
//            }
//        }
//        binding.editPackageWeight.addTextChangedListener(textWatcher)
        setSingleLine(binding.editPackageWeight)
//        binding.editPackageLength.addTextChangedListener(textWatcher)
        setSingleLine(binding.editPackageLength)
//        binding.editPackageWidth.addTextChangedListener(textWatcher)
        setSingleLine(binding.editPackageWidth)
//        binding.editPackageHeight.addTextChangedListener(textWatcher)
        setSingleLine(binding.editPackageHeight)

        binding

        binding.progressBar.isVisible = true
        generateCustomFare_uneditable()
        binding.progressBar.isVisible = false

        initClick()
        initEdit()
    }

    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
        }

        binding.btnEditFareOff.setOnClickListener {

            //關閉編輯按鍵disable隱藏
            binding.btnEditFareOff.isVisible = false
            binding.btnEditFareOff.isEnabled = false
            //開啟編輯按鍵enable出現
            binding.btnEditFareOn.isVisible = true
            binding.btnEditFareOn.isEnabled = true


            binding.progressBar.isVisible = true
            generateCustomFare_uneditable()
            binding.progressBar.isVisible = false

        }


        binding.btnEditFareOn.setOnClickListener {

            //開啟編輯按鍵disable隱藏
            binding.btnEditFareOn.isVisible = false
            binding.btnEditFareOn.isEnabled = false

            //關閉編輯按鍵enable出現
            binding.btnEditFareOff.isVisible = true
            binding.btnEditFareOff.isEnabled = true

            binding.progressBar.isVisible = true
            generateCustomFare_editable()
            binding.progressBar.isVisible = false

        }

        binding.btnShippingFareStore.setOnClickListener {
            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
        }

    }

    fun initEdit() {

    }

    //自訂費用項目(不可編輯狀態)
    fun generateCustomFare_uneditable() {

        //進入"不可編輯模式"清空mutableList_itemShipingFare
        mutableList_itemShipingFare.clear()

        //進入"不可編輯模式"新增資料或重新新增資料
        mutableList_itemShipingFare.add(ItemShippingFare("郵局", R.drawable.custom_unit_transparent))
        mutableList_itemShipingFare.add(ItemShippingFare("順豐速運", R.drawable.custom_unit_transparent))
        mutableList_itemShipingFare.add(ItemShippingFare("", R.drawable.custom_unit_transparent))


        //原生layoutManager
//        binding.rViewFareItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        //自訂layoutManager
        binding.rViewFareItemSpec.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewFareItemSpec.adapter = mAdapters_shippingFare


        mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
        mAdapters_shippingFare.notifyDataSetChanged()

    }

    //自訂費用項目(可編輯部分)
    fun generateCustomFare_editable() {

        //進入"可編輯模式"清空mutableList_itemShipingFare
        mutableList_itemShipingFare.clear()
        //進入"可編輯模式"新增資料或重新新增資料
        mutableList_itemShipingFare.add(ItemShippingFare("郵局", R.mipmap.btn_delete_fare))
        mutableList_itemShipingFare.add(ItemShippingFare("順豐速運", R.mipmap.btn_delete_fare))
        mutableList_itemShipingFare.add(ItemShippingFare("", R.mipmap.btn_delete_fare))

        //原生layoutManager
//        binding.rViewFareItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        //自訂layoutManager
        binding.rViewFareItemSpec.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewFareItemSpec.adapter = mAdapters_shippingFare

        mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
        mAdapters_shippingFare.notifyDataSetChanged()


    }


    fun setSingleLine(editText : EditText) {
        editText.singleLine = true
        editText.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (editText.text.toString().isEmpty()){
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_store)
                        binding.btnShippingFareStore.isEnabled = false
                    }else{
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
                        binding.btnShippingFareStore.isEnabled = true

                    }

                    editText.clearFocus()

                    true
                }
                else -> false
            }
        }

    }
}