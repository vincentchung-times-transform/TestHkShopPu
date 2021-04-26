package com.hkshopu.hk.ui.main.activity

import MyLinearLayoutManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemDatas
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ItemShippingFare_Certained
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider.gson
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareCheckedAdapter
import com.hkshopu.hk.ui.main.fragment.StoreOrNotDialogFragment
import com.hkshopu.hk.ui.user.activity.RetrieveEmailVerifyActivity
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class AddNewProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewProductBinding
    private val VM = ShopVModel()
    val mAdapters_shippingFareChecked = ShippingFareCheckedAdapter()
    val REQUEST_EXTERNAL_STORAGE = 100


    //從本地端選取圖片轉換為bitmap後存的list
    var mutableList_pics = mutableListOf<ItemPics>()



    //宣告頁面資料變數
    var value_editTextEntryProductName :String = ""
    var value_editTextEntryProductDiscription :String = ""
    var value_textViewSeletedCategory :String = ""
    var value_editTextMerchanPrice :String = ""
    var value_editTextMerchanQunt :String = ""
    var value_txtViewFareRange :String = ""
    var value_needMoreTimeToStockUp = false
    var value_editMoreTimeInput :String = ""

    //宣告運費項目陣列變數
    var mutableList_itemShipingFareExisted = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFareExisted_filtered = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFareExisted_certained = mutableListOf<ItemShippingFare_Certained>()

    //宣告規格與庫存價格項目陣列變數
//    var mutableList_itemInvenSpec = mutableListOf<InventoryItemSpec>()
//    var mutableList_itemInvenSize = mutableListOf<InventoryItemSize>()
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    var fare_price_range: String = ""
    var inven_price_range: String = ""
    var inven_quant_range: String = ""

    var value_checked_brandNew = "全新"


    var jsonTutList_inven : String = ""
    var jsonTutList_fare : String = ""

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()

        //設定預設資料
        initProCategoryDatas()
        initProFareDatas()
        initInvenDatas()

        initMMKV_and_initViewValue()

        initView()

    }


    fun initMMKV_and_initViewValue() {


        Thread(Runnable {

            //預設從MMKV取得資料(無資料則取預設值)
            var pics_size = MMKV.mmkvWithID("addPro").getInt("value_pics_size", 0)

            for (i in 0..pics_size - 1) {

                var previouslyEncodedImage: String? =
                    MMKV.mmkvWithID("addPro").getString("value_pic${i}", "")

                if(i == 0){

                    if (!previouslyEncodedImage.equals("")) {
                        val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                        mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))
                    }

                }else{

                    if (!previouslyEncodedImage.equals("")) {
                        val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                        mutableList_pics.add(ItemPics(bitmap, R.drawable.custom_unit_transparent))
                    }

                }
            }

            runOnUiThread {

                val mAdapter = PicsAdapter()

                mAdapter.updateList(mutableList_pics)     //傳入資料
                binding.rView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rView.adapter = mAdapter

            }

        }).start()

        value_editTextEntryProductName = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductName",
            ""
        ).toString()
        binding.editTextEntryProductName.setText(value_editTextEntryProductName)

        value_editTextEntryProductDiscription = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductDiscription",
            ""
        ).toString()
        binding.editTextEntryProductDiscription.setText(value_editTextEntryProductDiscription)

        value_textViewSeletedCategory = MMKV.mmkvWithID("addPro").getString(
            "value_textViewSeletedCategory",
            ""
        ).toString()
        binding.textViewSeletedCategory.setText(value_textViewSeletedCategory)

        value_checked_brandNew = MMKV.mmkvWithID("addPro").getString("value_checked_brandNew", "全新").toString()
        if(value_checked_brandNew=="全新"){
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
        }else{
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)
        }

        var inven_datas_size = MMKV.mmkvWithID("addPro").getInt("inven_datas_size", 0)

        jsonTutList_inven = MMKV.mmkvWithID("addPro").getString("jsonTutList_inven", "").toString()
        Log.d("AddNewProductActivity_MMKV", "jsonTutList_inven : " + jsonTutList_inven.toString())

        //MMKV取出mutableList_InvenDatas
        for(i in 0..inven_datas_size!!-1){

//            var  = MMKV.mmkvWithID("addPro").getString("value_inven${i}", "")
//
//            val gson = Gson()
//            val jsonTutList: String = gson.fromJson(mutableList_InvenDatas.indexOf(i), InventoryItemDatas::class.java)
        }


        //挑選最大與最小金額，回傳價格區間

        inven_price_range = MMKV.mmkvWithID("addPro").getString("inven_price_range", inven_price_range).toString()
        inven_quant_range = MMKV.mmkvWithID("addPro").getString("inven_quant_range", inven_quant_range).toString()
        value_editTextMerchanPrice = MMKV.mmkvWithID("addPro").getString("value_editTextMerchanPrice", "").toString()
        value_editTextMerchanQunt = MMKV.mmkvWithID("addPro").getString("value_editTextMerchanQunt", "").toString()


        //預設containerSpecification的背景
        if(inven_price_range != "" && inven_quant_range != "" ){
            binding.imgSpecLine.isVisible = false
            binding.containerAddSpecification.isVisible = false
            binding.editTextMerchanPrice.isVisible = true
            binding.editTextMerchanQunt.isVisible = true
        }else{
            binding.imgSpecLine.isVisible = true
            binding.containerAddSpecification.isVisible = true
            binding.editTextMerchanPrice.isVisible = false
            binding.editTextMerchanQunt.isVisible = false
        }





        value_txtViewFareRange = MMKV.mmkvWithID("addPro").getString("value_txtViewFareRange", "").toString()
        binding.txtViewFareRange.text = fare_price_range

        var datas_packagesWeights = MMKV.mmkvWithID("addPro").getString("datas_packagesWeights", "")
        var datas_lenght = MMKV.mmkvWithID("addPro").getString("datas_lenght", "")
        var datas_width = MMKV.mmkvWithID("addPro").getString("datas_width", "")
        var datas_height = MMKV.mmkvWithID("addPro").getString("datas_height", "")
        var fare_datas_size = MMKV.mmkvWithID("addPro").getInt("fare_datas_size", 0)
        var fare_datas_filtered_size = MMKV.mmkvWithID("addPro").getInt("value_fare_existed_filtered_size",0)

        Log.d("AddNewProductActivity_MMKV", "datas_packagesWeights : ${datas_packagesWeights}, datas_lenght : ${datas_lenght}, datas_width : ${datas_width}, datas_height : ${datas_height}, fare_datas_size : ${fare_datas_size}, fare_datas_filtered_size : ${fare_datas_filtered_size}")

//        jsonTutList_fare = MMKV.mmkvWithID("addPro").getString("jsonTutList_fare", "").toString()
//        Log.d("AddNewProductActivity_MMKV", "jsonTutList_fare : " + jsonTutList_fare.toString())


        if (fare_datas_size != null) {

            if(fare_datas_size > 0) {

                binding.rViewFareItem.isVisible = true
                binding.imgLineFare.isVisible = true

                //MMKV取出所有Fare Item
                for(i in 0..fare_datas_size!!-1){
                    var json : String? = MMKV.mmkvWithID("addPro").getString("value_fare_item_existed${i}", "")
                    val value_fare_item_existed : ItemShippingFare = gson.fromJson(json, ItemShippingFare::class.java)
                    mutableList_itemShipingFareExisted.add(value_fare_item_existed)

                    //去除btn_delete參數重新創造List(資料庫存取用)
                    mutableList_itemShipingFareExisted_certained.add(ItemShippingFare_Certained(value_fare_item_existed.shipment_desc, value_fare_item_existed.price, value_fare_item_existed.onoff, value_fare_item_existed.shop_id)) //傳輸API需要
                }

                Log.d("value_fare_item_existed", mutableList_itemShipingFareExisted.toString())

                //MMKV取出過濾後的Fare Item
                for (i in 0..fare_datas_filtered_size-1!!) {

                    var jason_invens : String? = MMKV.mmkvWithID("addPro").getString("value_fare_item_existed_filtered${i}", "")

                    val json = jason_invens
                    val value_fare_item_existed_filtered = gson.fromJson(json, ItemShippingFare::class.java)
                    mutableList_itemShipingFareExisted_filtered.add(value_fare_item_existed_filtered) //顯示在UI
                }

                Log.d("MMKV_CheckValue", "mutableList_itemShipingFareExisted : ${mutableList_itemShipingFareExisted}")
                Log.d("MMKV_CheckValue", "mutableList_itemShipingFareExisted_filtered : ${mutableList_itemShipingFareExisted_filtered}")

                //將要傳給API的Fare資料包裝成Jason(該List相較於mutableList_itemShipingFareExisted會少btn_delete參數)
                val gson = Gson()
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                val jsonTutList: String = gson.toJson(mutableList_itemShipingFareExisted_certained)
                Log.d("AddNewProductActivity", jsonTutList_fare.toString())
                val jsonTutListPretty_fare: String = gsonPretty.toJson(mutableList_itemShipingFareExisted_certained)
                Log.d("AddNewProductActivity", jsonTutListPretty_fare.toString())

                jsonTutList_fare = jsonTutList

                if(fare_datas_filtered_size >0){
                    //自訂layoutManager
                    binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this, false))
                    binding.rViewFareItem.adapter = mAdapters_shippingFareChecked

                    Thread(Runnable {

                        mAdapters_shippingFareChecked.updateList(mutableList_itemShipingFareExisted_filtered)

                        runOnUiThread {

                            mAdapters_shippingFareChecked.notifyDataSetChanged()

                        }

                    }).start()


                }else{

                    binding.rViewFareItem.isVisible = false
                    binding.imgLineFare.isVisible = false

                }
            }
            else{

                binding.rViewFareItem.isVisible = false
                binding.imgLineFare.isVisible = false

            }

        } else {

            binding.rViewFareItem.isVisible = false
            binding.imgLineFare.isVisible = false

        }




        value_needMoreTimeToStockUp = MMKV.mmkvWithID("addPro").getBoolean(
            "value_needMoreTimeToStockUp",
            false
        )
        value_editMoreTimeInput = MMKV.mmkvWithID("addPro").getString("value_editMoreTimeInput", "").toString()

        binding.editTextEntryProductName.setText(value_editTextEntryProductName)
        binding.editTextEntryProductDiscription.setText(value_editTextEntryProductDiscription)
        binding.textViewSeletedCategory.setText(value_textViewSeletedCategory)



        binding.editTextMerchanPrice.setText(value_editTextMerchanPrice)
        binding.editTextMerchanQunt.setText(value_editTextMerchanQunt)
        binding.txtViewFareRange.setText(value_txtViewFareRange)
        if(value_needMoreTimeToStockUp==false){
            binding.needMoreTimeToStockUp.isChecked = false
        }else{
            binding.needMoreTimeToStockUp.isChecked = true
        }
        binding.editMoreTimeInput.setText(value_editMoreTimeInput)


    }

    fun initView() {


        //預設較長備貨時間設定
        binding.editMoreTimeInput.isVisible = false
        binding.needMoreTimeToStockUp.text = getString(R.string.textView_more_time_to_stock)
        binding.needMoreTimeToStockUp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.editMoreTimeInput.isVisible = true
                MMKV.mmkvWithID("addPro").putBoolean("value_needMoreTimeToStockUp", true)


            } else {
                binding.editMoreTimeInput.isVisible = false
                MMKV.mmkvWithID("addPro").putBoolean("value_needMoreTimeToStockUp", false)
            }
        }

        initEditText()
        initClick()
    }

    fun initEditText() {

        binding.editTextEntryProductName.singleLine = true
        binding.editTextEntryProductName.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    value_editTextEntryProductName =
                        binding.editTextEntryProductName.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextEntryProductName",
                        value_editTextEntryProductName
                    )

                    binding.editTextEntryProductName.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductName)

                    true
                }

                else -> false
            }
        }

        binding.editTextEntryProductDiscription.singleLine = true
        binding.editTextEntryProductDiscription.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    value_editTextEntryProductDiscription =
                        binding.editTextEntryProductDiscription.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextEntryProductDiscription",
                        value_editTextEntryProductDiscription
                    )


                    binding.editTextEntryProductDiscription.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductDiscription)

                    true
                }

                else -> false
            }
        }

        binding.editTextMerchanPrice.singleLine = true
        binding.editTextMerchanPrice.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    value_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextMerchanPrice",
                        value_editTextMerchanPrice
                    )

                    binding.editTextMerchanPrice.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextMerchanPrice)

                    true
                }

                else -> false
            }
        }

        binding.editTextMerchanQunt.singleLine = true
        binding.editTextMerchanQunt.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    value_editTextMerchanQunt = binding.editTextMerchanQunt.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextMerchanQunt",
                        value_editTextMerchanQunt
                    )

                    binding.editTextMerchanQunt.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextMerchanQunt)

                    true
                }

                else -> false
            }
        }

        binding.editMoreTimeInput.singleLine = true
        binding.editMoreTimeInput.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    value_editMoreTimeInput = binding.editMoreTimeInput.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editMoreTimeInput",
                        value_editMoreTimeInput
                    )
                    binding.editMoreTimeInput.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editMoreTimeInput)

                    true
                }

                else -> false
            }
        }
    }

    fun initClick() {

        binding.btnAddPics.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE
                )
//                    return;
            } else {
                launchGalleryIntent()
            }

        }

        //設置containerSpecification中的iosSwitchSpecification開關功能
        binding.iosSwitchSpecification.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onStateChanged(isOpen: Boolean) {
                if (isOpen) {

                    binding.containerAddSpecification.isVisible = true
                    binding.imgSpecLine.isVisible = true
                    binding.editTextMerchanPrice.isVisible = false
                    binding.editTextMerchanQunt.isVisible = false
                    binding.textViewMerchanPriceRange.isVisible = true
                    binding.textViewMerchanQuntRange.isVisible = true

                    val scale = baseContext.resources.displayMetrics.density
                    var elevation = 0
                    val e = (elevation * scale + 0.5f).toInt()

                    binding.containerProductSpecPrice.setElevation(e.toFloat())
                    binding.containerProductSpecQuant.setElevation(e.toFloat())
                    binding.containerProductSpecSwitch.setElevation(e.toFloat())

                } else {

                    binding.containerAddSpecification.isVisible = false
                    binding.imgSpecLine.isVisible = false
                    binding.editTextMerchanPrice.isVisible = true
                    binding.editTextMerchanQunt.isVisible = true
                    binding.textViewMerchanPriceRange.isVisible = false
                    binding.textViewMerchanQuntRange.isVisible = false

                    val scale = baseContext.resources.displayMetrics.density
                    var elevation = 10
                    val e = (elevation * scale + 0.5f).toInt()

                    binding.containerProductSpecSwitch.setElevation(e.toFloat())
                    binding.containerProductSpecPrice.setElevation(e.toFloat())
                    binding.containerProductSpecQuant.setElevation(e.toFloat())


                }
            }
        })


        binding.titleBackAddproduct.setOnClickListener {

            StoreOrNotDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")

        }


        binding.tvBrandnew.setOnClickListener {
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)

            value_checked_brandNew = "全新"
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", value_checked_brandNew)
        }
        binding.tvSecondhand.setOnClickListener {
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)

            value_checked_brandNew = "二手"
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", value_checked_brandNew)

        }

        //go to category page
        binding.btnAddcategory.setOnClickListener {

//            val intent = Intent(this, LoginPasswordActivity::class.java)
//            startActivity(intent)

        }

        //go to AddProductSpecificationMainActivity
        binding.containerAddSpecification.setOnClickListener {
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.containerShippingFare.setOnClickListener {
            val intent = Intent(this, ShippingFareActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.categoryContainer.setOnClickListener {
            val intent = Intent(this, MerchanCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnStore.setOnClickListener {

            var pic_list : ArrayList<File> = arrayListOf()

            var file: File? = null

            for(i in 0..mutableList_pics.size-1){

                file = processImage(mutableList_pics.get(i).bitmap, i)

                pic_list.add(file!!)
            }



            Log.d("addNewPro", mutableList_pics.size.toString())

            Log.d("addNewPro", pic_list.toString())
            Log.d("addNewPro", "{ \"product_spec_list\" : ${jsonTutList_inven} }")
            Log.d("addNewPro", jsonTutList_fare)

//            VM.add_product(this, 1, 1, 1, "0", 0, "0", 0, 0, 0, "new", pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", 1, 0, 0, 0, jsonTutList_fare)
            doAddProduct( 1, 1, 1, "0", 0, "0", 0, 0, 0, "new",pic_list.size, pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", 1, 0, 0, 0, jsonTutList_fare)

        }
    }

    fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {

            Thread(Runnable {

                val clipData = data?.clipData
                if (clipData != null) {
                    //multiple images selecetd
                    if(mutableList_pics.size == 0) {

                        for (i in 0 until clipData.itemCount) {
                            if (i == 0) {
                                //取得圖片uri存到變數imageUri並轉成bitmap
                                val imageUri = clipData.getItemAt(i).uri
                                Log.d("URI", imageUri.toString())
                                try {
                                    val inputStream =
                                        contentResolver.openInputStream(imageUri)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)

                                    //新增所選圖片以及第一張cover image至mutableList_pics中
                                    mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))

                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }

                            } else {
                                //取得圖片uri存到變數imageUri並轉成bitmap
                                val imageUri = clipData.getItemAt(i).uri
                                Log.d("URI", imageUri.toString())
                                try {
                                    val inputStream =
                                        contentResolver.openInputStream(imageUri)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)

                                    //新增所選圖片以及第一張cover image至mutableList_pics中
                                    mutableList_pics.add(
                                        ItemPics(
                                            bitmap,
                                            R.drawable.custom_unit_transparent
                                        )
                                    )


                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }

                            }

                        }

                    }else{

                        for (i in 0 until clipData.itemCount) {

                            //取得圖片uri存到變數imageUri並轉成bitmap
                            val imageUri = clipData.getItemAt(i).uri
                            Log.d("URI", imageUri.toString())
                            try {
                                val inputStream =
                                    contentResolver.openInputStream(imageUri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                //新增所選圖片以及第一張cover image至mutableList_pics中
                                mutableList_pics.add(
                                    ItemPics(
                                        bitmap,
                                        R.drawable.custom_unit_transparent
                                    )
                                )


                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }

                        }
                    }



                    MMKV.mmkvWithID("addPro").putInt(
                        "value_pics_size",
                        mutableList_pics.size.toInt()
                    )

                    for (i in 0..mutableList_pics.size-1) {

                        //transfer to Base64
                        val baos = ByteArrayOutputStream()
                        mutableList_pics[i].bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()

                        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

                        MMKV.mmkvWithID("addPro").putString("value_pic${i}", encodedImage)
                    }


                } else {
                    //single image selected
                    val imageUri = data?.data
                    Log.d("URI", imageUri.toString())
                    try {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        if (mutableList_pics.size == 0) {
                            //新增所選圖片以及第一張cover image至mutableList_pics中
                            mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))


                        } else {
                            mutableList_pics.add(
                                ItemPics(bitmap, R.drawable.custom_unit_transparent)
                            )

                        }

                        MMKV.mmkvWithID("addPro").putInt("value_pics_size", mutableList_pics.size)

                        for (i in 0..mutableList_pics.size) {

                            //transfer to Base64
                            val baos = ByteArrayOutputStream()
                            mutableList_pics[i].bitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                100,
                                baos
                            )
                            val b = baos.toByteArray()

                            val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

                            MMKV.mmkvWithID("addPro").putString("value_pic${i}", encodedImage)

                        }


                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }


                runOnUiThread {

                    val mAdapter = PicsAdapter()

                    mAdapter.updateList(mutableList_pics)     //傳入資料
                    binding.rView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.rView.adapter = mAdapter

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
        }
    }

    fun initProCategoryDatas() {

        //取得Bundle傳來的分類資料
//        var sharedPreferences : SharedPreferences = getSharedPreferences("add_product_categery", Context.MODE_PRIVATE)
        var id: String? = intent.getBundleExtra("bundle")?.getString("id")
        var product_category_id: String? = intent.getBundleExtra("bundle")?.getString("product_category_id")
        var c_product_category: String? = intent.getBundleExtra("bundle")?.getString("c_product_category")
        var c_product_sub_category: String? = intent.getBundleExtra("bundle")?.getString("c_product_sub_category")

        if (c_product_category.equals(null) || c_product_sub_category.equals(null)) {
            binding.textViewSeletedCategory.isVisible = false
            binding.btnAddcategory.isVisible = true
        } else {
            binding.textViewSeletedCategory.isVisible = true
            MMKV.mmkvWithID("addPro").putString(
                "value_textViewSeletedCategory",
                c_product_category + ">" + c_product_sub_category
            )
            binding.textViewSeletedCategory.text = c_product_category + ">" + c_product_sub_category
            binding.btnAddcategory.isVisible = false
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initProFareDatas() {


        //取得Bundle傳來的分類資料
//        var sharedPreferences : SharedPreferences = getSharedPreferences("add_product_categery", Context.MODE_PRIVATE)
        var datas_packagesWeights: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_packagesWeights", "")
        var datas_lenght: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_lenght", "")
        var datas_width: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_width", "")
        var datas_height: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_height", "")
        var fare_datas_size: Int? = intent.getBundleExtra("bundle_ShippingFareActivity")?.getInt("datas_size", 0)

        MMKV.mmkvWithID("addPro").putString("datas_packagesWeights", datas_packagesWeights)
        MMKV.mmkvWithID("addPro").putString("datas_lenght", datas_lenght)
        MMKV.mmkvWithID("addPro").putString("datas_width", datas_width)
        MMKV.mmkvWithID("addPro").putString("datas_height", datas_height)



        if (fare_datas_size != null) {

            if(fare_datas_size > 0) {

                binding.rViewFareItem.isVisible = true
                binding.imgLineFare.isVisible = true

                MMKV.mmkvWithID("addPro").putInt("fare_datas_size", fare_datas_size)

                //從bundle載入所有添加的運費方式(未勾選與勾選)
                for (i in 0..fare_datas_size-1!!) {
                    mutableList_itemShipingFareExisted.add(
                        intent.getBundleExtra("bundle_ShippingFareActivity")?.getParcelable<ItemShippingFare>(i.toString())!!
                    )
                }

                val gson = Gson()
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                val jsonTutList_fare: String = gson.toJson(mutableList_itemShipingFareExisted)
                Log.d("AddNewProductActivity", jsonTutList_fare.toString())
                val jsonTutListPretty_fare: String = gsonPretty.toJson(mutableList_itemShipingFareExisted)
                Log.d("AddNewProductActivity", jsonTutListPretty_fare.toString())

                MMKV.mmkvWithID("addPro").putString("jsonTutList_fare", jsonTutList_fare)



                for (i in 0..fare_datas_size-1!!) {
                    val jsonTutList_mutableList_itemShipingFareExisted: String = gson.toJson(mutableList_itemShipingFareExisted[i])
                    MMKV.mmkvWithID("addPro").putString("value_fare_item_existed${i}", jsonTutList_mutableList_itemShipingFareExisted)
                }


                //篩選所有已勾選的運費方式
                for (i in 0..fare_datas_size-1!!) {
                    if(mutableList_itemShipingFareExisted[i].onoff ==true ){
                        mutableList_itemShipingFareExisted_filtered.add(
                            mutableList_itemShipingFareExisted[i]
                        )
                    }
                }

                //MMKV放入已經確定勾選的Fare Item Size
                MMKV.mmkvWithID("addPro").putInt("value_fare_existed_filtered_size", mutableList_itemShipingFareExisted_filtered.size)
                Log.d("check_content","value_fare_existed_filtered_size : ${mutableList_itemShipingFareExisted_filtered.size.toString()}")

                for (i in 0..mutableList_itemShipingFareExisted_filtered.size-1!!) {
                    val jsonTutList_mutableList_itemShipingFareExisted_filtered: String = gson.toJson(mutableList_itemShipingFareExisted_filtered[i])
                    MMKV.mmkvWithID("addPro").putString("value_fare_item_existed_filtered${i}", jsonTutList_mutableList_itemShipingFareExisted_filtered)

                }

                //挑選最大與最小金額，回傳價格區間
                fare_price_range = fare_pick_max_and_min_num(mutableList_itemShipingFareExisted_filtered.size)
                MMKV.mmkvWithID("addPro").putString("value_txtViewFareRange", fare_price_range)
                binding.txtViewFareRange.text = fare_price_range

                //清空以勾選運送陣列，避免重複新增資料(initMMKV已新增一次)
                mutableList_itemShipingFareExisted_filtered.clear()

            }

        } else {

            binding.rViewFareItem.isVisible = false
            binding.imgLineFare.isVisible = false


        }

    }

    //計算費用最大最小範圍
    fun fare_pick_max_and_min_num(size: Int): String {
        //挑出最大與最小的數字
        var min: Int =mutableList_itemShipingFareExisted_filtered[0].price.toInt()
        var max: Int =mutableList_itemShipingFareExisted_filtered[0].price.toInt()

        for (f in 1..size-1) {
            if(mutableList_itemShipingFareExisted_filtered[f].price.toInt() >= min ){
                max = mutableList_itemShipingFareExisted_filtered[f].price.toInt()
            }else{
                min = mutableList_itemShipingFareExisted_filtered[f].price.toInt()
            }
        }

        return "HKD$${min}-HKD$${max}"

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

        return "HKD$${min}-HKD$${max}"

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initInvenDatas() {

        //取得Bundle傳來的分類資料
//        var datas_invenSpec_size: Int? =
//            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("datas_invenSpec_size")
//        var datas_invenSize_size: Int? =
//            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("datas_invenSize_size")
        var inven_datas_size: Int? =
            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("InvenDatas_size", 0)

        if (inven_datas_size != null) {
            MMKV.mmkvWithID("addPro").putInt("inven_datas_size", inven_datas_size)
        }else{
            MMKV.mmkvWithID("addPro").putInt("inven_datas_size", 0)
        }



        if (inven_datas_size != null) {

            if (inven_datas_size>0) {

                binding.iosSwitchSpecification.openSwitcher()
                binding.containerAddSpecification.isVisible = true
                binding.imgSpecLine.isVisible = true
                binding.editTextMerchanPrice.isVisible = false
                binding.editTextMerchanQunt.isVisible = false
                binding.textViewMerchanPriceRange.isVisible = true
                binding.textViewMerchanQuntRange.isVisible = true


                val scale = baseContext.resources.displayMetrics.density
                var elevation = 0
                val e = (elevation * scale + 0.5f).toInt()

                binding.containerProductSpecPrice.setElevation(e.toFloat())
                binding.containerProductSpecQuant.setElevation(e.toFloat())
                binding.containerProductSpecSwitch.setElevation(e.toFloat())

                //從bundle載入所有添加的運費方式
//                    for (i in 0..datas_invenSpec_size-1!!) {
//                        mutableList_itemInvenSpec.add(intent.getBundleExtra("InventoryAndPriceActivity")?.getParcelable<InventoryItemSpec> ("spec"+i.toString())!!)
//                    }
//
//
//                    for (i in 0..datas_invenSize_size-1!!) {
//                        mutableList_itemInvenSize.add(intent.getBundleExtra("InventoryAndPriceActivity")?.getParcelable<InventoryItemSize> ("size"+i.toString())!!)
//                    }




                for(key in 0..inven_datas_size!!-1){

                    mutableList_InvenDatas.add(
                        intent.getBundleExtra("InventoryAndPriceActivity")
                            ?.getParcelable<InventoryItemDatas>("InvenDatas" + key.toString())!!
                    )

                }

                val gson = Gson()
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                val jsonTutList_inven: String = gson.toJson(mutableList_InvenDatas)
                Log.d("AddNewProductActivity", jsonTutList_inven.toString())
                val jsonTutListPretty_inven: String = gsonPretty.toJson(mutableList_InvenDatas)
                Log.d("AddNewProductActivity", jsonTutListPretty_inven.toString())

                MMKV.mmkvWithID("addPro").putString("jsonTutList_inven", jsonTutList_inven)

                //MMKV放入mutableList_InvenDatas
                for(i in 0..inven_datas_size!!-1){

                    val gson = Gson()
                    val jsonTutList: String = gson.toJson(mutableList_InvenDatas.indexOf(i))

                    MMKV.mmkvWithID("addPro").putString("value_inven${i}", jsonTutList)

                }


                //挑選最大與最小金額，回傳價格區間
                inven_price_range = inven_price_pick_max_and_min_num(inven_datas_size!!)
                inven_quant_range = inven_quant_pick_max_and_min_num(inven_datas_size!!)
                MMKV.mmkvWithID("addPro").putString("inven_price_range", inven_price_range)
                MMKV.mmkvWithID("addPro").putString("inven_quant_range", inven_quant_range)

                binding.textViewMerchanPriceRange.text = inven_price_range
                binding.textViewMerchanQuntRange.text = inven_quant_range

            }


        } else {

            binding.iosSwitchSpecification.closeSwitcher()
            binding.containerAddSpecification.isVisible = false
            binding.imgSpecLine.isVisible = false
            binding.editTextMerchanPrice.isVisible = true
            binding.editTextMerchanQunt.isVisible = true
            binding.textViewMerchanPriceRange.isVisible = false
            binding.textViewMerchanQuntRange.isVisible = false

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt()

            binding.containerProductSpecPrice.setElevation(e.toFloat())
            binding.containerProductSpecQuant.setElevation(e.toFloat())
            binding.containerProductSpecSwitch.setElevation(e.toFloat())


        }

    }



    private fun initVM() {

        VM.addProductData.observe(
            this,
            Observer {
                when (it?.status) {
                    Status.Success -> {
                        if (it.ret_val.toString().equals("產品新增成功!!")) {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        } else {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        }

                    }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
                }
            }
        )

    }

    private fun processImage(bitmap: Bitmap, i: Int): File? {

        val bmp = bitmap
        val bmpCompress = getResizedBitmap(bmp, 200)
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + i + ".jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            stream?.flush()
            stream?.close()
        } catch (e: IOException) // Catch the exception
        {
            e.printStackTrace()
        }
        return file
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        width = maxSize
        height = (width / bitmapRatio).toInt()
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun doAddProduct(shop_id : Int, product_category_id : Int, product_sub_category_id :Int, product_title : String, quantity : Int, product_description : String, product_price :Int, shipping_fee : Int, weight : Int, new_secondhand :String,product_pic_list_size :Int ,product_pic_list : ArrayList<File>, product_spec_list : String, user_id: Int,  length : Int, width : Int, height : Int, shipment_method : String) {
        val url = ApiConstants.API_HOST+"/product/save/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("產品新增成功!")) {

                        runOnUiThread {
                            Toast.makeText(this@AddNewProductActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
//                        var user_id: Int = json.getInt("user_id")
//                        var shop_id:Int = json.getInt("shop_id")
//                        MMKV.mmkvWithID("http").putInt("UserId", user_id)
//                        MMKV.mmkvWithID("http").putInt("ShopId", shop_id)
//                        val intent = Intent(this@AddShopActivity, ShopmenuActivity::class.java)
//                        startActivity(intent)
//                        finish()

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddNewProductActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()


                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ProductAdd(url, shop_id, product_category_id, product_sub_category_id, product_title, quantity, product_description, product_price, shipping_fee, weight, new_secondhand, product_pic_list_size, product_pic_list, product_spec_list, user_id,  length, width, height, shipment_method)
    }

}