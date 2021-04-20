package com.hkshopu.hk.ui.main.activity

import MyLinearLayoutManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
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

    val mAdapters_shippingFare = ShippingFareAdapter(this)
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()

    var weight_check = false
    var length_check = false
    var width_check = false
    var height_check = false

    //資料變數宣告
    var datas_packagesWeights : Int = 0
    var datas_lenght : String = ""
    var datas_width : String = ""
    var datas_height : String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingFareBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()
    }

    fun initView() {

        initRecyclerView_ShippingFareItem()

        binding.btnEditFareOn.isVisible = true
        binding.btnEditFareOn.isEnabled = true
        binding.btnEditFareOff.isVisible = false
        binding.btnEditFareOff.isEnabled = false
        binding.btnShippingFareStore.isEnabled = false
        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)



        setMonitor(binding.editPackageWeight, width_check)
        setMonitor(binding.editPackageLength, length_check)
        setMonitor(binding.editPackageWidth, width_check)
        setMonitor(binding.editPackageHeight, height_check)


        generateCustomFare_uneditable()


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

            generateCustomFare_uneditable()

        }


        binding.btnEditFareOn.setOnClickListener {

            //開啟編輯按鍵disable隱藏
            binding.btnEditFareOn.isVisible = false
            binding.btnEditFareOn.isEnabled = false

            //關閉編輯按鍵enable出現
            binding.btnEditFareOff.isVisible = true
            binding.btnEditFareOff.isEnabled = true

            generateCustomFare_editable()

        }

        binding.btnShippingFareStore.setOnClickListener {

            val intent = Intent(this, AddNewProductActivity::class.java)
            var datas_ship_method_and_fare : MutableList<ItemShippingFare> = mAdapters_shippingFare.get_shipping_method_datas()

            var bundle = Bundle()
            bundle.putInt("datas_packagesWeights", datas_packagesWeights)
            bundle.putString("datas_lenght", datas_lenght)
            bundle.putString("datas_width", datas_width)
            bundle.putString("datas_height", datas_height)
            bundle.putInt("datas_size", datas_ship_method_and_fare.size)

            Log.d("checkVariable", datas_ship_method_and_fare.size.toString())


            for(key in 0..datas_ship_method_and_fare.size-1) {
                bundle.putParcelable(key.toString(), datas_ship_method_and_fare.get(key)!!)
            }

            intent.putExtra("bundle_ShippingFareActivity", bundle)

            startActivity(intent)

            finish()

        }

    }

    fun initEdit() {


        binding.editPackageWeight.singleLine = true
        binding.editPackageWeight.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_packagesWeights = binding.editPackageWeight.text.toString().toInt()

                    v.hideKeyboard()
                    binding.editPackageWeight.clearFocus()

                    true
                }
                else -> false
            }
        }

        binding.editPackageLength.singleLine = true
        binding.editPackageLength.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_lenght = binding.editPackageLength.text.toString()

                    v.hideKeyboard()
                    binding.editPackageLength.clearFocus()

                    true
                }
                else -> false
            }
        }


        binding.editPackageWidth.singleLine = true
        binding.editPackageWidth.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_width = binding.editPackageWidth.text.toString()

                    v.hideKeyboard()
                    binding.editPackageWeight.clearFocus()

                    true
                }
                else -> false
            }
        }


        binding.editPackageHeight.singleLine = true
        binding.editPackageHeight.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    datas_height = binding.editPackageHeight.text.toString()

                    v.hideKeyboard()
                    binding.editPackageHeight.clearFocus()

                    true
                }
                else -> false
            }
        }

    }

    fun initRecyclerView_ShippingFareItem() {

        initFareDatas()

        //自訂layoutManager
        binding.rViewFareItemSpec.setLayoutManager(MyLinearLayoutManager(this,false))
        binding.rViewFareItemSpec.adapter = mAdapters_shippingFare

        mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
        mAdapters_shippingFare.notifyDataSetChanged()
    }

    fun initFareDatas() {

        mutableList_itemShipingFare.add(ItemShippingFare("郵局", 0, R.drawable.custom_unit_transparent))
        mutableList_itemShipingFare.add(ItemShippingFare("順豐速運", 0, R.drawable.custom_unit_transparent))
        mutableList_itemShipingFare.add(ItemShippingFare("", 0, R.drawable.custom_unit_transparent))

    }

    //自訂費用項目(不可編輯狀態)
    fun generateCustomFare_uneditable() {

        //進入"不可編輯模式"新增資料或重新新增資料
        mutableList_itemShipingFare = mAdapters_shippingFare.get_shipping_method_datas()

        var mutableList_size = mAdapters_shippingFare.get_shipping_method_datas().size

        if(mutableList_size>=2){
            for(i in 0..mutableList_size-2){
                mutableList_itemShipingFare[i] = ItemShippingFare(mutableList_itemShipingFare[i].ship_method_name, mutableList_itemShipingFare[i].ship_method_fare, R.drawable.custom_unit_transparent)
            }

            mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
            mAdapters_shippingFare.notifyDataSetChanged()
        }



    }

    //自訂費用項目(可編輯部分)
    fun generateCustomFare_editable() {

        //進入"可編輯模式"新增資料或重新新增資料
        mutableList_itemShipingFare = mAdapters_shippingFare.get_shipping_method_datas()

        var mutableList_size = mAdapters_shippingFare.get_shipping_method_datas().size

        if(mutableList_size>=2){
            for(i in 0..mutableList_size-2){
                mutableList_itemShipingFare[i] = ItemShippingFare(mutableList_itemShipingFare[i].ship_method_name, mutableList_itemShipingFare[i].ship_method_fare, R.mipmap.btn_delete_fare)
            }

            mAdapters_shippingFare.updateList(mutableList_itemShipingFare)
            mAdapters_shippingFare.notifyDataSetChanged()

        }

    }


    fun setMonitor(editText : EditText, var_check : Boolean) {

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isNotEmpty()||s.toString()!=""){

                    when (editText) {
                        binding.editPackageWeight ->{
                            weight_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                        binding.editPackageLength ->{
                            length_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                        binding.editPackageWidth ->{
                            width_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                        binding.editPackageHeight ->{
                            height_check=true
                            Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())
                        }
                    }

                    if(weight_check==true && length_check==true && width_check==true && height_check==true ){
                        binding.btnShippingFareStore.isEnabled = true
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
                    }else{
                        binding.btnShippingFareStore.isEnabled = false
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
                    }



                }else{

                    when (editText) {
                        binding.editPackageWeight ->{
                            weight_check=false
                        }
                        binding.editPackageLength ->{
                            length_check=false
                        }
                        binding.editPackageLength ->{
                            width_check=false
                        }
                        binding.editPackageLength ->{
                            height_check=false
                        }
                    }

                    if(weight_check==true && length_check==true && width_check==true && height_check==true ){
                        binding.btnShippingFareStore.isEnabled = true
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore)
                    }else{
                        binding.btnShippingFareStore.isEnabled = false
                        binding.btnShippingFareStore.setImageResource(R.mipmap.btn_shippingfarestore_disable)
                    }

                }
            }
        }
        editText.addTextChangedListener(textWatcher)


        editText.singleLine = true
        editText.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.d("checkvar", weight_check.toString()+length_check.toString()+width_check.toString()+height_check.toString())


                    editText.clearFocus()

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