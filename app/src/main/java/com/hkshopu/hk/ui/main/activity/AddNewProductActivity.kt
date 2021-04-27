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
    var value_textViewSeletedCategory :String = "0"
    var value_editTextMerchanPrice :String = ""
    var value_editTextMerchanQunt :String = ""
    var inven_price_range: String = ""
    var inven_quant_range: String = ""
    var value_txtViewFareRange :String = ""
    var value_needMoreTimeToStockUp = false
    var value_editMoreTimeInput :String = ""
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 0
    var MMKV_proCate_id: String = ""
    var MMKV_proSubCate_id: String = ""
    var MMKV_weight: String = "0"
    var MMKV_length:String = "0"
    var MMKV_width: String = "0"
    var MMKV_height: String = "0"

    //宣告運費項目陣列變數
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_certained = mutableListOf<ItemShippingFare_Certained>()

    //宣告規格與庫存價格項目陣列變數
//    var mutableList_itemInvenSpec = mutableListOf<InventoryItemSpec>()
//    var mutableList_itemInvenSize = mutableListOf<InventoryItemSize>()
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

//    var fare_price_range: String = "no fare price range"
//    var inven_price_range: String = "no inven price range"
//    var inven_quant_range: String = "no inven quant range"



    var value_checked_brandNew = "全新"

    var jsonTutList_inven : String = "[{ \"spec_desc_1\": \"\",\"spec_desc_2\": \"\",\"spec_dec_1_items\": \"\",\"spec_dec_2_items\": \"\",\"price\": 0,\"quantity\": 0 }]"

    var jsonTutList_fare : String = "[{\"shipment_desc\":\"\",\"price\":0,\"onoff\":\"on\",\"shop_id\" : 0 }]"




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

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)

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
            value_editTextEntryProductName
        ).toString()
        binding.editTextEntryProductName.setText(value_editTextEntryProductName)

        value_editTextEntryProductDiscription = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductDiscription",
            value_editTextEntryProductDiscription
        ).toString()
        binding.editTextEntryProductDiscription.setText(value_editTextEntryProductDiscription)




        value_checked_brandNew = MMKV.mmkvWithID("addPro").getString("value_checked_brandNew", value_checked_brandNew).toString()
        if(value_checked_brandNew=="全新"){

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e.toFloat())
            binding.tvSecondhand.setElevation(e_zero.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
        }else{

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e_zero.toFloat())
            binding.tvSecondhand.setElevation(e.toFloat())

            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)
        }



        value_needMoreTimeToStockUp = MMKV.mmkvWithID("addPro").getBoolean(
            "value_needMoreTimeToStockUp",
            false
        )
        value_editMoreTimeInput = MMKV.mmkvWithID("addPro").getString("value_editMoreTimeInput", "").toString()

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

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0



            binding.tvBrandnew.setElevation(e.toFloat())
            binding.tvSecondhand.setElevation(e_zero.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)

            value_checked_brandNew = "全新"
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", value_checked_brandNew)
        }
        binding.tvSecondhand.setOnClickListener {

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e_zero.toFloat())
            binding.tvSecondhand.setElevation(e.toFloat())
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


            var inven_price_range: String = ""

            if(pic_list.size >=1){
                if(value_editTextEntryProductName !== ""){

                    if(value_editTextEntryProductDiscription !==""){
                        if(MMKV_proCate_id !==""||MMKV_proSubCate_id !==""){
                            if(MMKV_weight !=="" && MMKV_length !=="" && MMKV_width !=="" && MMKV_height !==""){

                                if( !(value_editTextMerchanPrice =="" && value_editTextMerchanQunt=="") &&  binding.iosSwitchSpecification.isOpened().equals(false) ){

                                    if(value_txtViewFareRange !==""){

                                        doAddProduct( MMKV_shop_id, MMKV_proCate_id.toInt(), MMKV_proSubCate_id.toInt(), value_editTextEntryProductName, value_editTextMerchanQunt.toInt(), value_editTextEntryProductDiscription, value_editTextMerchanPrice.toInt(), 0, MMKV_weight.toInt(), value_checked_brandNew, pic_list.size.toInt(), pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", MMKV_user_id, MMKV_length.toInt(), MMKV_width.toInt(), MMKV_height.toInt(), jsonTutList_fare)
                                        Log.d("MMKV_shop_id" , "MMKV_shop_id: ${MMKV_shop_id} ; "+"MMKV_proCate_id: ${MMKV_proCate_id} ; "+"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "+"value_editTextEntryProductName: ${value_editTextEntryProductName} ; "+"value_editTextMerchanQunt: ${value_editTextMerchanQunt} ; "+"value_editTextEntryProductDiscription: ${value_editTextEntryProductDiscription} ; "+"value_editTextMerchanPrice: ${value_editTextMerchanPrice} ; "+"MMKV_weight: ${MMKV_weight} ; "+"value_checked_brandNew: ${value_checked_brandNew} ; "+"pic_list.size: ${pic_list.size} ; "+"pic_list: ${pic_list} ; "+"${"{ \"product_spec_list\" : ${jsonTutList_inven} }"} ; "+"MMKV_user_id: ${MMKV_user_id} ; "+"MMKV_length: ${MMKV_length} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_height: ${MMKV_height} ; "+"jsonTutList_fare: ${jsonTutList_fare}")

                                    }else{

                                        Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                    }


                                }else if( binding.iosSwitchSpecification.isOpened()){

                                    if( inven_price_range !="" && inven_quant_range !=""){

                                        if(value_txtViewFareRange !==""){

                                            doAddProduct( MMKV_shop_id, MMKV_proCate_id.toInt(), MMKV_proSubCate_id.toInt(), value_editTextEntryProductName, value_editTextMerchanQunt.toInt(), value_editTextEntryProductDiscription, value_editTextMerchanPrice.toInt(), 0, MMKV_weight.toInt(), value_checked_brandNew, pic_list.size.toInt(), pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", MMKV_user_id, MMKV_length.toInt(), MMKV_width.toInt(), MMKV_height.toInt(), jsonTutList_fare)
                                            Log.d("MMKV_shop_id" , "MMKV_shop_id: ${MMKV_shop_id} ; "+"MMKV_proCate_id: ${MMKV_proCate_id} ; "+"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "+"value_editTextEntryProductName: ${value_editTextEntryProductName} ; "+"value_editTextMerchanQunt: ${value_editTextMerchanQunt} ; "+"value_editTextEntryProductDiscription: ${value_editTextEntryProductDiscription} ; "+"value_editTextMerchanPrice: ${value_editTextMerchanPrice} ; "+"MMKV_weight: ${MMKV_weight} ; "+"value_checked_brandNew: ${value_checked_brandNew} ; "+"pic_list.size: ${pic_list.size} ; "+"pic_list: ${pic_list} ; "+"${"{ \"product_spec_list\" : ${jsonTutList_inven} }"} ; "+"MMKV_user_id: ${MMKV_user_id} ; "+"MMKV_length: ${MMKV_length} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_height: ${MMKV_height} ; "+"jsonTutList_fare: ${jsonTutList_fare}")

                                        }else{

                                            Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                        }


                                    }else{

                                        Toast.makeText(this, "商品庫存尚未設定", Toast.LENGTH_SHORT).show()

                                    }

                                } else{
                                    Toast.makeText(this, "商品價格與數量尚未填寫", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this, "包裹大小尚未輸入完成", Toast.LENGTH_SHORT).show()
                            }

                        }else{
                            Toast.makeText(this, "商品分類尚未選擇", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "請輸入商品描述", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "請輸入商品名稱", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "請選取至少一張照片", Toast.LENGTH_SHORT).show()
            }

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

                        for (i in 0..mutableList_pics.size-1) {

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
        var product_sub_category_id: String? = intent.getBundleExtra("bundle")?.getString("id")
        var product_category_id: String? = intent.getBundleExtra("bundle")?.getString("product_category_id")
        var c_product_category: String? = intent.getBundleExtra("bundle")?.getString("c_product_category")
        var c_product_sub_category: String? = intent.getBundleExtra("bundle")?.getString("c_product_sub_category")



        MMKV_proCate_id = MMKV.mmkvWithID("addPro").getString("product_category_id", "").toString()
        MMKV_proSubCate_id = MMKV.mmkvWithID("addPro").getString("product_sub_category_id", "").toString()
        value_textViewSeletedCategory = MMKV.mmkvWithID("addPro").getString("value_textViewSeletedCategory", value_textViewSeletedCategory).toString()
        binding.textViewSeletedCategory.setText(value_textViewSeletedCategory)


        Log.d("product_category_id", "product_sub_category_id: ${product_sub_category_id} ; "+"product_category_id: ${product_category_id}"+"c_product_category: ${c_product_category} ; "+"c_product_sub_category: ${c_product_sub_category} ; ")


        if (MMKV_proCate_id.equals("") || MMKV_proSubCate_id.equals("")) {
            binding.textViewSeletedCategory.isVisible = false
            binding.btnAddcategory.isVisible = true

        } else {

            binding.textViewSeletedCategory.isVisible = true
            binding.btnAddcategory.isVisible = false
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initProFareDatas() {

        //取得Bundle傳來的分類資料
//        var sharedPreferences : SharedPreferences = getSharedPreferences("add_product_categery", Context.MODE_PRIVATE)
//        var datas_packagesWeights: String? =
//            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_packagesWeights", "")
//        var datas_lenght: String? =
//            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_lenght", "")
//        var datas_width: String? =
//            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_width", "")
//        var datas_height: String? =
//            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_height", "")
//        var fare_datas_size_bundle: Int? = intent.getBundleExtra("bundle_ShippingFareActivity")?.getInt("datas_size", 0)




        MMKV_weight = MMKV.mmkvWithID("addPro").getString("datas_packagesWeights", "0").toString()
        MMKV_length = MMKV.mmkvWithID("addPro").getString("datas_lenght", "0").toString()
        MMKV_width = MMKV.mmkvWithID("addPro").getString("datas_width", "0").toString()
        MMKV_height = MMKV.mmkvWithID("addPro").getString("datas_height", "0").toString()

        var fare_datas_size = MMKV.mmkvWithID("addPro").getString("fare_datas_size", "0").toString().toInt()
        var fare_datas_filtered_size = MMKV.mmkvWithID("addPro").getString("fare_datas_filtered_size","0").toString().toInt()


        Log.d("MMKV_weight", "datas_packagesWeights : ${MMKV_weight}, datas_lenght : ${MMKV_length}, datas_width : ${MMKV_width}, datas_height : ${MMKV_height}, fare_datas_size : ${fare_datas_size}, fare_datas_filtered_size : ${fare_datas_filtered_size}")
        value_txtViewFareRange = MMKV.mmkvWithID("addPro").getString("value_txtViewFareRange", "").toString()
        binding.txtViewFareRange.text = value_txtViewFareRange
        jsonTutList_fare = MMKV.mmkvWithID("addPro").getString("jsonTutList_fare", jsonTutList_fare).toString()




//        jsonTutList_fare = MMKV.mmkvWithID("addPro").getString("jsonTutList_fare", "").toString()
//        Log.d("AddNewProductActivity_MMKV", "jsonTutList_fare : " + jsonTutList_fare.toString())

        if (fare_datas_size != null) {

            if(fare_datas_size > 0) {

                binding.rViewFareItem.isVisible = true
                binding.imgLineFare.isVisible = true

                //MMKV取出過濾後的Fare Item
                for (i in 0..fare_datas_filtered_size-1!!) {

                    var json_invens : String? = MMKV.mmkvWithID("addPro").getString("value_fare_item_filtered${i}", "")
                    val json = json_invens
                    val value_fare_item_filtered = gson.fromJson(json, ItemShippingFare::class.java)

                    mutableList_itemShipingFare_filtered.add(value_fare_item_filtered) //顯示在UI
                }

                Log.d("MMKV_CheckValue", "mutableList_itemShipingFare: ${mutableList_itemShipingFare}")
                Log.d("MMKV_CheckValue", "mutableList_itemShipingFare_filtered : ${mutableList_itemShipingFare_filtered}")


                if(fare_datas_filtered_size >0){
                    //自訂layoutManager
                    binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this, false))
                    binding.rViewFareItem.adapter = mAdapters_shippingFareChecked

                    Thread(Runnable {

                        mAdapters_shippingFareChecked.updateList(mutableList_itemShipingFare_filtered)

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
        //挑選最大與最小金額，回傳價格區間
        binding.txtViewFareRange.text = value_txtViewFareRange

        //清空以勾選運送陣列，避免重複新增資料(initMMKV已新增一次)
//                mutableList_itemShipingFare_filtered.clear()

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initInvenDatas() {


        var inven_datas_size = MMKV.mmkvWithID("addPro").getInt("inven_datas_size", 0)

        jsonTutList_inven = MMKV.mmkvWithID("addPro").getString("jsonTutList_inven", jsonTutList_inven).toString()
        Log.d("jsonTutList_inven", "jsonTutList_inven : " + jsonTutList_inven.toString())

        //MMKV取出mutableList_InvenDatas
//        for(i in 0..inven_datas_size!!-1){
//
//            var  = MMKV.mmkvWithID("addPro").getString("value_inven${i}", "")
//
//            val gson = Gson()
//            val jsonTutList: String = gson.fromJson(mutableList_InvenDatas.indexOf(i), InventoryItemDatas::class.java)
//        }


        //挑選最大與最小金額，回傳價格區間

        inven_price_range = MMKV.mmkvWithID("addPro").getString("inven_price_range", inven_price_range).toString()
        inven_quant_range = MMKV.mmkvWithID("addPro").getString("inven_quant_range", inven_quant_range).toString()
        value_editTextMerchanPrice = MMKV.mmkvWithID("addPro").getString("value_editTextMerchanPrice", value_editTextMerchanPrice).toString()
        value_editTextMerchanQunt = MMKV.mmkvWithID("addPro").getString("value_editTextMerchanQunt", value_editTextMerchanQunt).toString()


        //預設containerSpecification的背景
        if(inven_price_range != "" && inven_quant_range != "" ){

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

            binding.textViewMerchanPriceRange.text = inven_price_range
            binding.textViewMerchanQuntRange.text = inven_quant_range

        }else{


            binding.editTextMerchanPrice.setText(value_editTextMerchanPrice)
            binding.editTextMerchanQunt.setText(value_editTextMerchanQunt)

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

            binding.containerProductSpecSwitch.setElevation(e.toFloat())
            binding.containerProductSpecPrice.setElevation(e.toFloat())
            binding.containerProductSpecQuant.setElevation(e.toFloat())

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


//                for(key in 0..inven_datas_size!!-1){
//
//                    mutableList_InvenDatas.add(
//                        intent.getBundleExtra("InventoryAndPriceActivity")
//                            ?.getParcelable<InventoryItemDatas>("InvenDatas" + key.toString())!!
//                    )
//
//                }


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

    private fun doAddProduct(shop_id : Int, product_category_id : Int, product_sub_category_id :Int, product_title : String, quantity : Int, product_description : String, product_price :Int, shipping_fee : Int, weight : Int, new_secondhand :String, product_pic_list_size :Int ,product_pic_list : ArrayList<File>, product_spec_list : String, user_id: Int,  length : Int, width : Int, height : Int, shipment_method : String) {
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


    override fun onBackPressed() {
//        AlertDialog.Builder(this@AddShopActivity)
//            .setTitle("")
//            .setMessage("您尚未儲存變更，確定要離開 ？")
//            .setPositiveButton("捨棄"){
//                // 此為 Lambda 寫法
//                    dialog, which ->finish()
//            }
//            .setNegativeButton("取消"){ dialog, which -> dialog.cancel()
//
//            }
//            .show()

        StoreOrNotDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")

    }


}