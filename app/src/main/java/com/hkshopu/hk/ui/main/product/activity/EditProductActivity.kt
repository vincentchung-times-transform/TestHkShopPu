package com.hkshopu.hk.ui.main.product.activity

import MyLinearLayoutManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.inputmethod.EditorInfo
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
import com.hkshopu.hk.component.EventTransferToFragmentAfterUpdate
import com.hkshopu.hk.component.EventdeleverFragmentAfterUpdateStatus

import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityEditProductBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.GsonProvider.gson
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.product.adapter.ShippingFareCheckedAdapter
import com.hkshopu.hk.ui.main.product.fragment.StoreOrNotDialogStoreProductsFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class EditProductActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProductBinding

    private val VM = ShopVModel()
    val mAdapters_shippingFareChecked = ShippingFareCheckedAdapter()
    val REQUEST_EXTERNAL_STORAGE = 100

    //從本地端選取圖片轉換為bitmap後存的list
    var mutableList_pics = mutableListOf<ItemPics>()

    var product_edit_session = false

    //宣告頁面資料變數
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1
    var MMKV_editTextEntryProductName :String = ""
    var MMKV_editTextEntryProductDiscription :String = ""
    var MMKV_proCate_id: String = ""
    var MMKV_proSubCate_id: String = ""
    var MMKV_c_product_category: String = ""
    var MMKV_c_product_sub_category: String = ""
    var MMKV_textViewSeletedCategory :String = ""
    var MMKV_product_spec_on: String = ""
    var MMKV_editTextMerchanPrice :String = ""
    var MMKV_editTextMerchanQunt :String = ""
    var MMKV_inven_price_range: String = ""
    var MMKV_inven_quant_range: String = ""
    var MMKV_value_txtViewFareRange :String = ""
    var MMKV_boolean_needMoreTimeToStockUp = "y"
    var MMKV_editMoreTimeInput :String = ""
    var MMKV_weight: String = ""
    var MMKV_length:String = ""
    var MMKV_width: String = ""
    var MMKV_height: String = ""
    var MMKV_checked_brandNew = "new"
    var MMKV_product_status = ""
    var MMKV_jsonTutList_inven: String = "[{ \"spec_desc_1\": \"\",\"spec_desc_2\": \"\",\"spec_dec_1_items\": \"\",\"spec_dec_2_items\": \"\",\"price\": 0,\"quantity\": 0 }]"
    var MMKV_jsonList_shipment_certained: String = "[{\"shipment_desc\":\"\",\"price\":0,\"onoff\":\"of\",\"shop_id\" : 0 }]"

    lateinit var productInfoList :  ProductInfoBean

    //宣告運費項目陣列變數
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare_Filtered>()
    var mutableList_itemShipingFare_certained = mutableListOf<ItemShippingFare_Certained>()


    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar3.isVisible = false

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)

        product_edit_session = MMKV.mmkvWithID("editPro").getBoolean("product_edit_session", false)

        Log.d("product_edit_session_log", product_edit_session.toString())
        if(product_edit_session){
            Thread(Runnable {
                initMMKV_and_initViewValue()
            }).start()

        }else{
            product_edit_session = true
            MMKV.mmkvWithID("editPro").putBoolean("product_edit_session", product_edit_session)
            getProductInfo(MMKV_product_id)
        }


//        try{
//            Thread.sleep(5000)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }


        initVM()
        initView()

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initMMKV_and_initViewValue() {
        //自訂layoutManager

        runOnUiThread {
            binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this, false))
            binding.rViewFareItem.adapter = mAdapters_shippingFareChecked
        }

        MMKV_product_status = MMKV.mmkvWithID("editPro").getString("product_status",  "").toString()

        //商品圖片
        var pics_list_size = MMKV.mmkvWithID("editPro").getInt("value_pics_size", 0)


        //從API以載入過一次，所以重新仔入必須清除，才不會重複
        mutableList_pics.clear()

        for (i in 0..pics_list_size - 1) {

            var previouslyEncodedImage: String? =
                MMKV.mmkvWithID("editPro").getString("value_pic${i}", "")

            if (i == 0) {

                if (!previouslyEncodedImage.equals("")) {
                    val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                    mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))
                }

            } else {

                if (!previouslyEncodedImage.equals("")) {
                    val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                    mutableList_pics.add(ItemPics(bitmap, R.drawable.custom_unit_transparent))
                }

            }
        }
        Log.d("mutableList_pics", pics_list_size.toString())

        val mAdapter = PicsAdapter()

        runOnUiThread {
            binding.rView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rView.adapter = mAdapter
        }

        mAdapter.updateList(mutableList_pics)

        runOnUiThread {
            mAdapter.notifyDataSetChanged()
        }

        MMKV_editTextEntryProductName = MMKV.mmkvWithID("editPro").getString(
            "value_editTextEntryProductName",
            MMKV_editTextEntryProductName
        ).toString()
        runOnUiThread {
            binding.editTextEntryProductName.setText(MMKV_editTextEntryProductName)

        }

        //商品描述
        MMKV_editTextEntryProductDiscription = MMKV.mmkvWithID("editPro").getString(
            "value_editTextEntryProductDiscription",
            MMKV_editTextEntryProductDiscription
        ).toString()
        runOnUiThread {
            binding.editTextEntryProductDiscription.setText(MMKV_editTextEntryProductDiscription)

        }

        //商品分類
        initProCategoryDatas()
        //商品庫存
        initInvenDatas()
        //商品運費
        initProFareDatas()


        //商品保存狀況
        MMKV_checked_brandNew = MMKV.mmkvWithID("editPro").getString(
            "value_checked_brandNew",
            MMKV_checked_brandNew
        ).toString()
        if(MMKV_checked_brandNew=="new"){

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0


            runOnUiThread {
                binding.tvBrandnew.setElevation(e.toFloat())
                binding.tvSecondhand.setElevation(e_zero.toFloat())
                binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
                binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
            }

        }else{

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            runOnUiThread {
                binding.tvBrandnew.setElevation(e_zero.toFloat())
                binding.tvSecondhand.setElevation(e.toFloat())

                binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
                binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)
            }

        }


        //需要較長時間備貨嗎?
        runOnUiThread {
            binding.needMoreTimeToStockUp.text = getString(R.string.textView_more_time_to_stock)
        }
        MMKV_boolean_needMoreTimeToStockUp = MMKV.mmkvWithID("editPro").getString(
            "boolean_needMoreTimeToStockUp",
            "n"
        ).toString()


        if(MMKV_boolean_needMoreTimeToStockUp=="n"){
            runOnUiThread {
                binding.needMoreTimeToStockUp.setText(R.string.textView_questionNeedMoreTimeToStockUp)
                binding.needMoreTimeToStockUp.isChecked =false
            }

        }else{
            runOnUiThread {
                binding.needMoreTimeToStockUp.setText(R.string.textView_needMoreTimeToStockUp)
                binding.needMoreTimeToStockUp.isChecked =true
            }
        }

        if(MMKV_boolean_needMoreTimeToStockUp=="n"){
            runOnUiThread {
                binding.needMoreTimeToStockUp.isChecked =false
            }

        }else{
            runOnUiThread {
                binding.needMoreTimeToStockUp.isChecked =true
            }

        }
        MMKV_editMoreTimeInput = MMKV.mmkvWithID("editPro").getString("value_editMoreTimeInput", "").toString()
        runOnUiThread {
            binding.editMoreTimeInput.setText(MMKV_editMoreTimeInput)
        }

        if(MMKV_editMoreTimeInput.isNotEmpty() && MMKV_editMoreTimeInput.toInt()>0){
            runOnUiThread {
                binding.editMoreTimeInput.isVisible = true
                binding.needMoreTimeToStockUp.isChecked = true
            }
        }else{
            runOnUiThread {
                binding.editMoreTimeInput.isVisible = false
                binding.needMoreTimeToStockUp.isChecked = false
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initView() {


        initEditText()
        initClick()

    }

    fun initEditText() {


        binding.editTextEntryProductName.singleLine = true
        binding.editTextEntryProductName.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editTextEntryProductName =
                        binding.editTextEntryProductName.text.toString()
                    MMKV.mmkvWithID("editPro").putString(
                        "value_editTextEntryProductName",
                        MMKV_editTextEntryProductName
                    )

                    binding.editTextEntryProductName.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductName)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextEntryProductName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                MMKV_editTextEntryProductName =
                    binding.editTextEntryProductName.text.toString()
                MMKV.mmkvWithID("editPro").putString(
                    "value_editTextEntryProductName",
                    MMKV_editTextEntryProductName
                )
            }
        }
        binding.editTextEntryProductName.addTextChangedListener(textWatcher_editTextEntryProductName)

//        binding.editTextEntryProductDiscription.singleLine = true
//        binding.editTextEntryProductDiscription.setOnEditorActionListener() { v, actionId, event ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE -> {
//
//                    MMKV_editTextEntryProductDiscription =
//                        binding.editTextEntryProductDiscription.text.toString()
//                    MMKV.mmkvWithID("editPro").putString(
//                        "value_editTextEntryProductDiscription",
//                        MMKV_editTextEntryProductDiscription
//                    )
//
//                    binding.editTextEntryProductDiscription.clearFocus()
//                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductDiscription)
//
//                    true
//                }
//
//                else -> false
//            }
//        }
        val textWatcher_editTextEntryProductDiscription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                MMKV_editTextEntryProductDiscription =
                    binding.editTextEntryProductDiscription.text.toString()
                MMKV.mmkvWithID("editPro").putString(
                    "value_editTextEntryProductDiscription",
                    MMKV_editTextEntryProductDiscription
                )

            }
        }

        binding.editTextEntryProductDiscription.addTextChangedListener(
            textWatcher_editTextEntryProductDiscription
        )


        binding.editTextMerchanPrice.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){

                if(MMKV_editTextMerchanPrice.equals("-1")){
                    binding.editTextMerchanPrice.setText("")
                }else{
                    binding.editTextMerchanPrice.setText("${MMKV_editTextMerchanPrice}")
                }

            }
        }
        binding.editTextMerchanPrice.singleLine = true
        binding.editTextMerchanPrice.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {


                    if(binding.editTextMerchanPrice.text.isNotEmpty()){

                        binding.editTextMerchanPrice.setText("HKD$ ${binding.editTextMerchanPrice.text.toString()}")
                        MMKV_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString().substring(5)
                        MMKV.mmkvWithID("editPro").putString(
                            "value_editTextMerchanPrice",
                            MMKV_editTextMerchanPrice
                        )

                    }else{

                        binding.editTextMerchanPrice.setText("")
                        MMKV_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString()
                        MMKV.mmkvWithID("editPro").putString(
                            "value_editTextMerchanPrice",
                            MMKV_editTextMerchanPrice
                        )

                    }

                    binding.editTextMerchanPrice.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextMerchanPrice)



                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextMerchanPrice = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(binding.editTextMerchanPrice.text.startsWith("HKD$ ")){

                    MMKV_editTextMerchanPrice =
                        binding.editTextMerchanPrice.text.toString().substring(5)
                    MMKV.mmkvWithID("editPro").putString(
                        "value_editTextMerchanPrice",
                        MMKV_editTextMerchanPrice
                    )

                }else{
                    if(binding.editTextMerchanPrice.text.toString().length >= 2 && binding.editTextMerchanPrice.text.toString().startsWith("0")){
                        binding.editTextMerchanPrice.setText(binding.editTextMerchanPrice.text.toString().replace("0", "", false))
                        binding.editTextMerchanPrice.setSelection(binding.editTextMerchanPrice.text.toString().length)
                    }

                }

            }
        }
        binding.editTextMerchanPrice.addTextChangedListener(textWatcher_editTextMerchanPrice)


        binding.editTextMerchanQunt.singleLine = true
        binding.editTextMerchanQunt.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editTextMerchanQunt = binding.editTextMerchanQunt.text.toString()
                    MMKV.mmkvWithID("editPro").putString(
                        "value_editTextMerchanQunt",
                        MMKV_editTextMerchanQunt
                    )

                    binding.editTextMerchanQunt.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextMerchanQunt)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextMerchanQunt = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(binding.editTextMerchanQunt.text.toString().length >= 2 && binding.editTextMerchanQunt.text.toString().startsWith("0")){
                    binding.editTextMerchanQunt.setText(binding.editTextMerchanQunt.text.toString().replace("0", "", false))
                    binding.editTextMerchanQunt.setSelection(binding.editTextMerchanQunt.text.toString().length)
                }

                MMKV_editTextMerchanQunt = binding.editTextMerchanQunt.text.toString()
                MMKV.mmkvWithID("editPro").putString(
                    "value_editTextMerchanQunt",
                    MMKV_editTextMerchanQunt
                )


            }
        }
        binding.editTextMerchanQunt.addTextChangedListener(textWatcher_editTextMerchanQunt)



        binding.editMoreTimeInput.singleLine = true
        binding.editMoreTimeInput.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editMoreTimeInput = binding.editMoreTimeInput.text.toString()
                    MMKV.mmkvWithID("editPro").putString(
                        "value_editMoreTimeInput",
                        MMKV_editMoreTimeInput
                    )
                    binding.editMoreTimeInput.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editMoreTimeInput)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editMoreTimeInput = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                MMKV_editMoreTimeInput = binding.editMoreTimeInput.text.toString()
                MMKV.mmkvWithID("editPro").putString(
                    "value_editMoreTimeInput",
                    MMKV_editMoreTimeInput
                )
            }
        }
        binding.editMoreTimeInput.addTextChangedListener(textWatcher_editMoreTimeInput)


    }

    fun initClick() {

        binding.btnOnShelf.setOnClickListener {


            when(MMKV_product_status){
                "active"->{
                    VM.updateProductStatus(this, MMKV_product_id, "draft")
                }
                "draft"->{
                    VM.updateProductStatus(this, MMKV_product_id, "active")

                }
            }
        }

        binding.needMoreTimeToStockUp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.editMoreTimeInput.isVisible = true
                MMKV.mmkvWithID("editPro").putString(
                    "boolean_needMoreTimeToStockUp",
                    "y"
                )

            } else {
                binding.editMoreTimeInput.isVisible = false
                MMKV.mmkvWithID("editPro").putString(
                    "boolean_needMoreTimeToStockUp",
                    "n"
                )
            }
        }

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

                    MMKV.mmkvWithID("editPro").putString("product_spec_on" , "y")
                    MMKV_product_spec_on = MMKV.mmkvWithID("editPro").getString("product_spec_on", "n").toString()


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


                    MMKV.mmkvWithID("editPro").putString("product_spec_on" , "n")
                    MMKV_product_spec_on = MMKV.mmkvWithID("editPro").getString("product_spec_on", "n").toString()



                }
            }
        })


        binding.titleBackAddproduct.setOnClickListener {

            StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")

        }


        binding.tvBrandnew.setOnClickListener {

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 0
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e.toFloat())
            binding.tvSecondhand.setElevation(e_zero.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)

            MMKV_checked_brandNew = "new"
            MMKV.mmkvWithID("editPro").putString("value_checked_brandNew", MMKV_checked_brandNew)
        }
        binding.tvSecondhand.setOnClickListener {

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 0
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e_zero.toFloat())
            binding.tvSecondhand.setElevation(e.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)

            MMKV_checked_brandNew = "secondhand"
            MMKV.mmkvWithID("editPro").putString("value_checked_brandNew", MMKV_checked_brandNew)

        }

        binding.containerAddSpecification.setOnClickListener {

            MMKV.mmkvWithID("editPro_temp").putBoolean("get_temp", false)

            val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.containerShippingFare.setOnClickListener {
            val intent = Intent(this, EditShippingFareActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.categoryContainer.setOnClickListener {
            val intent = Intent(this, EditMerchanCategoryActivity::class.java)
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
            Log.d("addNewPro", "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }")
            Log.d("addNewPro", MMKV_jsonList_shipment_certained)


            if(pic_list.size >=1){
                if(MMKV_editTextEntryProductName.isNotEmpty()){
                    if(MMKV_editTextEntryProductDiscription.isNotEmpty()){
                        if(MMKV_proCate_id.isNotEmpty()||MMKV_proSubCate_id.isNotEmpty()){
                            if(MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
                                if( !MMKV_editTextMerchanPrice.toString().equals("") && !MMKV_editTextMerchanQunt.equals(
                                        ""
                                    ) &&  binding.iosSwitchSpecification.isOpened().equals(false) ){
                                    if(mutableList_itemShipingFare.size>0){

//                                        var inven_switch_off_json = "{ \"product_spec_list\" : [{\"price\": ${value_editTextMerchanPrice}, \"quantity\": ${value_editTextMerchanQunt}, \"spec_dec_1_items\":\"\",\"spec_dec_2_items\":\"\",\"spec_desc_1\":\"\",\"spec_desc_2\":\"\"}]}"
                                        var inven_switch_off_json = "[{ \"spec_desc_1\": \"\",\"spec_desc_2\": \"\",\"spec_dec_1_items\": \"\",\"spec_dec_2_items\": \"\",\"price\": 0,\"quantity\": 0 }]"

                                        Log.d(
                                            "inven_switch_off_json",
                                            inven_switch_off_json.toString()
                                        )

                                        if(MMKV_editMoreTimeInput.equals("")){
                                            MMKV_editMoreTimeInput = "0"
                                        }

                                        //quantity and product_price is discarded
                                        doUpdateProduct(
                                            MMKV_product_id,
                                            MMKV_proCate_id.toInt(),
                                            MMKV_proSubCate_id.toInt(),
                                            MMKV_editTextEntryProductName,
                                            MMKV_editTextMerchanQunt.toInt(),
                                            MMKV_editTextEntryProductDiscription,
                                            MMKV_editTextMerchanPrice.toInt(),
                                            0,
                                            MMKV_weight.toInt(),
                                            MMKV_checked_brandNew,
                                            pic_list.size.toInt(),
                                            pic_list,
                                            "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }",
                                            MMKV_user_id,
                                            MMKV_length.toInt(),
                                            MMKV_width.toInt(),
                                            MMKV_height.toInt(),
                                            MMKV_jsonList_shipment_certained,
                                            MMKV_editMoreTimeInput.toInt(),
                                            "draft",
                                            MMKV_product_spec_on
                                        )

                                        product_edit_session=false
                                        MMKV.mmkvWithID("editPro").putBoolean("product_edit_session", product_edit_session)
                                        Log.d(
                                            "doUpdateProduct",
                                            "MMKV_product_id: ${MMKV_product_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "MMKV_jsonList_shipment_certained: ${MMKV_jsonList_shipment_certained} ; " + "value_editMoreTimeInput: ${MMKV_editMoreTimeInput}"
                                        )


                                    }else{
                                        Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                }else if( binding.iosSwitchSpecification.isOpened()){
                                    if( MMKV_inven_price_range.isNotEmpty() && MMKV_inven_quant_range.isNotEmpty()){
                                        if(mutableList_itemShipingFare.size>0){


                                            MMKV_editTextMerchanPrice = "0"
                                            MMKV_editTextMerchanQunt = "0"

                                            if(MMKV_editMoreTimeInput.equals("")){
                                                MMKV_editMoreTimeInput = "0"
                                            }

                                            //quantity and product_price is discarded
                                            doUpdateProduct(
                                                MMKV_product_id,
                                                MMKV_proCate_id.toInt(),
                                                MMKV_proSubCate_id.toInt(),
                                                MMKV_editTextEntryProductName,
                                                MMKV_editTextMerchanQunt.toInt(),
                                                MMKV_editTextEntryProductDiscription,
                                                MMKV_editTextMerchanPrice.toInt(),
                                                0,
                                                MMKV_weight.toInt(),
                                                MMKV_checked_brandNew,
                                                pic_list.size.toInt(),
                                                pic_list,
                                                "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }",
                                                MMKV_user_id,
                                                MMKV_length.toInt(),
                                                MMKV_width.toInt(),
                                                MMKV_height.toInt(),
                                                MMKV_jsonList_shipment_certained,
                                                MMKV_editMoreTimeInput.toInt(),
                                                "draft",
                                                MMKV_product_spec_on
                                            )

                                            product_edit_session=false
                                            MMKV.mmkvWithID("editPro").putBoolean("product_edit_session", product_edit_session)

                                            Log.d(
                                                "doUpdateProduct",
                                                "MMKV_product_id: ${MMKV_product_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "MMKV_jsonList_shipment_certained: ${MMKV_jsonList_shipment_certained} ; " + "value_editMoreTimeInput: ${MMKV_editMoreTimeInput}"
                                            )

                                        }else{
                                            Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Log.d(
                                            "testtestetest",
                                            MMKV_inven_price_range.toString() + MMKV_inven_quant_range.toString()
                                        )
                                        Toast.makeText(this, "商品庫存尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                } else{
                                    Toast.makeText(this, "商品價格與數量尚未填寫", Toast.LENGTH_SHORT).show()
                                }
                            }else{

                                Log.d(
                                    "MMKV_shop_id",
                                    "MMKV_shop_id: ${MMKV_shop_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "MMKV_jsonList_shipment_certained: ${MMKV_jsonList_shipment_certained}"
                                )
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
                    if (mutableList_pics.size == 0) {
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
                    } else {
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

                    MMKV.mmkvWithID("editPro").putInt(
                        "value_pics_size",
                        mutableList_pics.size.toInt()
                    )

                    for (i in 0..mutableList_pics.size - 1) {
                        //transfer to Base64
                        val baos = ByteArrayOutputStream()
                        mutableList_pics[i].bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                        MMKV.mmkvWithID("editPro").putString("value_pic${i}", encodedImage)
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

                        MMKV.mmkvWithID("editPro").putInt("value_pics_size", mutableList_pics.size)

                        for (i in 0..mutableList_pics.size - 1) {
                            //transfer to Base64
                            val baos = ByteArrayOutputStream()
                            mutableList_pics[i].bitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                100,
                                baos
                            )
                            val b = baos.toByteArray()
                            val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                            MMKV.mmkvWithID("editPro").putString("value_pic${i}", encodedImage)
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

            }).start()
        }
    }

    fun initProCategoryDatas() {

        MMKV_proCate_id = MMKV.mmkvWithID("editPro").getString("product_category_id", "").toString()
        MMKV_proSubCate_id = MMKV.mmkvWithID("editPro").getString("product_sub_category_id", "").toString()
        MMKV_c_product_category = MMKV.mmkvWithID("editPro").getString("c_product_category", "").toString()
        MMKV_c_product_sub_category = MMKV.mmkvWithID("editPro").getString("c_product_sub_category", "").toString()
        MMKV_textViewSeletedCategory = MMKV.mmkvWithID("editPro").getString(
            "value_textViewSeletedCategory",
            MMKV_textViewSeletedCategory
        ).toString()

        runOnUiThread {
            binding.textViewSeletedCategory.setText(MMKV_textViewSeletedCategory)
//        binding.textViewSeletedCategory.setText("${MMKV_c_product_category} > ${MMKV_c_product_sub_category}")

        }

        Log.d(
            "MMKV_proCate_id",
            "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_textViewSeletedCategory: ${MMKV_textViewSeletedCategory} ; "
        )

        if (MMKV_proCate_id.isEmpty() || MMKV_proSubCate_id.isEmpty()) {
            runOnUiThread {
                binding.textViewSeletedCategory.isVisible = false
                binding.btnAddcategory.isVisible = true
            }
        } else {
            runOnUiThread {
                binding.textViewSeletedCategory.isVisible = true
                binding.btnAddcategory.isVisible = false
            }
        }
    }


    fun initProFareDatas() {

        MMKV_weight = MMKV.mmkvWithID("editPro").getString("datas_packagesWeights", "").toString()
        MMKV_length = MMKV.mmkvWithID("editPro").getString("datas_length", "").toString()
        MMKV_width = MMKV.mmkvWithID("editPro").getString("datas_width", "").toString()
        MMKV_height = MMKV.mmkvWithID("editPro").getString("datas_height", "").toString()
        var fare_datas_size = MMKV.mmkvWithID("editPro").getString("fare_datas_size", "0").toString().toInt()
        var fare_datas_filtered_size = MMKV.mmkvWithID("editPro").getString("fare_datas_filtered_size", "0").toString().toInt()
        var fare_datas_certained_size = MMKV.mmkvWithID("editPro").getString("fare_datas_certained_size", "0").toString().toInt()
        MMKV_value_txtViewFareRange = MMKV.mmkvWithID("editPro").getString("value_txtViewFareRange", "").toString()
        Log.d("MMKV_weight_Edit", "MMKV_weight : ${MMKV_weight}, MMKV_length : ${MMKV_length}, MMKV_width : ${MMKV_width}, MMKV_height : ${MMKV_height}, fare_datas_size : ${fare_datas_size}, fare_datas_filtered_size : ${fare_datas_filtered_size}, MMKV_value_txtViewFareRange: ${MMKV_value_txtViewFareRange}")


        //挑選最大與最小金額，回傳價格區間
        runOnUiThread {
            binding.txtViewFareRange.text = MMKV_value_txtViewFareRange
        }

        if (fare_datas_size != null) {

            if(fare_datas_size > 0) {


                runOnUiThread {
                    binding.rViewFareItem.isVisible = true
                    binding.imgLineFare.isVisible = true
                }

                mutableList_itemShipingFare.clear()
                //MMKV取出 Fare Item
                for (i in 0..fare_datas_size-1!!) {
                    var json_invens : String? = MMKV.mmkvWithID("editPro").getString(
                        "value_fare_item${i}",
                        ""
                    )
                    val json = json_invens
                    val value_fare_item = gson.fromJson(json, ItemShippingFare::class.java)
                    mutableList_itemShipingFare.add(value_fare_item)
                }

                mutableList_itemShipingFare_filtered.clear()
                //MMKV取出 Filtered Fare Item
                for (i in 0..fare_datas_filtered_size-1!!) {
                    var json_invens : String? = MMKV.mmkvWithID("editPro").getString(
                        "value_fare_item_filtered${i}",
                        ""
                    )
                    val json = json_invens
                    val value_fare_item_filtered = gson.fromJson(json, ItemShippingFare_Filtered::class.java)
                    mutableList_itemShipingFare_filtered.add(
                        ItemShippingFare_Filtered(value_fare_item_filtered.shipment_desc, value_fare_item_filtered.price.toInt(), value_fare_item_filtered.onoff, value_fare_item_filtered.shop_id)

                    )
                }

                mutableList_itemShipingFare_certained.clear()

                for(i in 0..fare_datas_certained_size!!-1){

                    var json_invens : String? = MMKV.mmkvWithID("editPro").getString(
                        "value_fare_item_certained${i}",
                        ""
                    )
                    val json = json_invens
                    val value_fare_item_certained = gson.fromJson(json, ItemShippingFare_Certained::class.java)
                    mutableList_itemShipingFare_certained.add(value_fare_item_certained)
                }

                MMKV_jsonList_shipment_certained= MMKV.mmkvWithID("editPro").getString("jsonList_shipment_certained", MMKV_jsonList_shipment_certained).toString()
                Log.d("MMKV_jsonList_shipment_certained", "MMKV_jsonList_shipment_certained : " + MMKV_jsonList_shipment_certained.toString())



                Log.d(
                    "MMKV_CheckValue",
                    "mutableList_itemShipingFare: ${mutableList_itemShipingFare}"
                )
                Log.d(
                    "MMKV_CheckValue",
                    "mutableList_itemShipingFare_filtered : ${mutableList_itemShipingFare_filtered}"
                )


                if(fare_datas_filtered_size >0){

                    mAdapters_shippingFareChecked.updateList(
                        mutableList_itemShipingFare_certained
                    )
                    runOnUiThread {
                        mAdapters_shippingFareChecked.notifyDataSetChanged()
                    }

                }else{
                    runOnUiThread {
                        binding.rViewFareItem.isVisible = false
                        binding.imgLineFare.isVisible = false
                    }
                }
            }
            else{
                runOnUiThread {
                    binding.rViewFareItem.isVisible = false
                    binding.imgLineFare.isVisible = false
                }
            }
        } else {
            runOnUiThread {
                binding.rViewFareItem.isVisible = false
                binding.imgLineFare.isVisible = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initInvenDatas() {

        MMKV_product_spec_on = MMKV.mmkvWithID("editPro").getString("product_spec_on", "n").toString()
//        var inven_datas_size = MMKV.mmkvWithID("addPro").getInt("inven_datas_size", 0)

        MMKV_jsonTutList_inven = MMKV.mmkvWithID("editPro").getString(
            "jsonTutList_inven",
            MMKV_jsonTutList_inven
        ).toString()
        Log.d(
            "MMKV_jsonTutList_inven",
            "MMKV_jsonTutList_inven : " + MMKV_jsonTutList_inven.toString()
        )

        //挑選最大與最小金額，回傳價格區間
        MMKV_inven_price_range = MMKV.mmkvWithID("editPro").getString(
            "inven_price_range",
            MMKV_inven_price_range
        ).toString()
        MMKV_inven_quant_range = MMKV.mmkvWithID("editPro").getString(
            "inven_quant_range",
            MMKV_inven_quant_range
        ).toString()

        MMKV_editTextMerchanPrice = MMKV.mmkvWithID("editPro").getString(
            "value_editTextMerchanPrice",
            MMKV_editTextMerchanPrice
        ).toString()
        MMKV_editTextMerchanQunt = MMKV.mmkvWithID("editPro").getString(
            "value_editTextMerchanQunt",
            MMKV_editTextMerchanQunt
        ).toString()

        if(MMKV_editTextMerchanPrice.equals("-1")){
            runOnUiThread {
                binding.editTextMerchanPrice.setText("")
            }

        }else{
            runOnUiThread {
                binding.editTextMerchanPrice.setText(MMKV_editTextMerchanPrice)
            }
        }
        if(MMKV_editTextMerchanQunt.equals("-1")){
            runOnUiThread {
                binding.editTextMerchanQunt.setText("")
            }
        }else{
            runOnUiThread {
                binding.editTextMerchanQunt.setText(MMKV_editTextMerchanQunt)
            }
        }

        runOnUiThread {
            binding.textViewMerchanPriceRange.setText(MMKV_inven_price_range)
            binding.textViewMerchanQuntRange.setText(MMKV_inven_quant_range)
        }

        if(MMKV_product_spec_on.equals("n")){

            runOnUiThread {
                binding.iosSwitchSpecification.closeSwitcher()

                binding.containerAddSpecification.isVisible = false
                binding.imgSpecLine.isVisible = false

                binding.editTextMerchanPrice.isVisible = true
                binding.editTextMerchanQunt.isVisible = true
                binding.textViewMerchanPriceRange.isVisible = false
                binding.textViewMerchanQuntRange.isVisible = false
            }


            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt()

            runOnUiThread {
                binding.containerProductSpecSwitch.setElevation(e.toFloat())
                binding.containerProductSpecPrice.setElevation(e.toFloat())
                binding.containerProductSpecQuant.setElevation(e.toFloat())
            }

        }else{


            runOnUiThread {
                binding.iosSwitchSpecification.openSwitcher()

                binding.containerAddSpecification.isVisible = true
                binding.imgSpecLine.isVisible = true

                binding.editTextMerchanPrice.isVisible = false
                binding.editTextMerchanQunt.isVisible = false
                binding.textViewMerchanPriceRange.isVisible = true
                binding.textViewMerchanQuntRange.isVisible = true
            }



            val scale = baseContext.resources.displayMetrics.density
            var elevation = 0
            val e = (elevation * scale + 0.5f).toInt()


            runOnUiThread {
                binding.containerProductSpecPrice.setElevation(e.toFloat())
                binding.containerProductSpecQuant.setElevation(e.toFloat())
                binding.containerProductSpecSwitch.setElevation(e.toFloat())
            }

        }
    }




    private fun initVM() {

        VM.updateProductStatusData.observe(
            this,
            Observer {
                when (it?.status) {
                    Status.Success -> {
                        if (it.ret_val.toString().equals("上架/下架成功!")) {

                            when(MMKV_product_status){
                                "active"->{
                                    runOnUiThread {
                                        Toast.makeText(this, "下架成功", Toast.LENGTH_LONG).show()
                                        binding.btnOnShelf.setImageResource(R.mipmap.btn_launch)
                                    }

                                    RxBus.getInstance().post(EventTransferToFragmentAfterUpdate(2))

                                }
                                "draft"->{
                                    runOnUiThread {
                                        Toast.makeText(this, "上架成功", Toast.LENGTH_LONG).show()
                                        MMKV_product_status = "active"
                                        binding.btnOnShelf.setImageResource(R.mipmap.btn_draft)
                                    }

                                    RxBus.getInstance().post(EventTransferToFragmentAfterUpdate(0))

                                }
                            }

                            RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus("action"))

                            MMKV.mmkvWithID("addPro").clear()
                            MMKV.mmkvWithID("editPro").clear()
                            finish()

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


    override fun onBackPressed() {
        StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")
    }



    private fun doUpdateProduct(
        product_id: Int,
        product_category_id: Int,
        product_sub_category_id: Int,
        product_title: String,
        quantity: Int,
        product_description: String,
        product_price: Int,
        shipping_fee: Int,
        weight: Int,
        new_secondhand: String,
        product_pic_list_size: Int,
        product_pic_list: ArrayList<File>,
        product_spec_list: String,
        user_id: Int,
        length: Int,
        width: Int,
        height: Int,
        shipment_method: String,
        longterm_stock_up: Int,
        product_status: String,
        product_spec_on: String
    ) {
        val url = ApiConstants.API_HOST+"product/${product_id}/update/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doUpdateProduct", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateProduct", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("商品更新成功!")) {

                        runOnUiThread {
                            Toast.makeText(
                                this@EditProductActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        RxBus.getInstance().post(EventTransferToFragmentAfterUpdate(2))
                        RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus("action"))
                        MMKV.mmkvWithID("addPro").clear()
                        MMKV.mmkvWithID("editPro").clear()

                        finish()

                    }else{
                        runOnUiThread {
                            Toast.makeText(
                                this@EditProductActivity,
                                "yes",
                                Toast.LENGTH_SHORT
                            ).show()
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
        web.Do_ProductUpdate(
            url,
            product_id,
            product_category_id,
            product_sub_category_id,
            product_title,
            quantity,
            product_description,
            product_price,
            shipping_fee,
            weight,
            new_secondhand,
            product_pic_list_size,
            product_pic_list,
            product_spec_list,
            user_id,
            length,
            width,
            height,
            shipment_method,
            longterm_stock_up,
            product_status,
            product_spec_on
        )
    }

    private fun getProductInfo(product_id: Int) {

        val url = ApiConstants.API_HOST+"product/${product_id}/product_info_forAndroid/"
        val web = Web(object : WebListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductInfoBean>()
//                val product_id_list = ArrayList<String>()
                try {

                    runOnUiThread {
                        binding.progressBar3.isVisible = true
                    }
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getProductInfo", "返回資料 resStr：" + resStr)
                    Log.d("getProductInfo", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品資訊!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getProductInfo", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            productInfoList = Gson().fromJson(
                                jsonObject.toString(),
                                ProductInfoBean::class.java
                            )
                        }

                        Log.d("getProductInfo", "返回資料 productInfoList：" + productInfoList.toString())

                        //Pictures
                        MMKV.mmkvWithID("editPro").putInt("value_pics_size", productInfoList.pic_path.size)

                        for(i in 0..productInfoList.pic_path.size-1){
                            mutableList_pics.add(ItemPics(getBitmapFromURL(productInfoList.pic_path.get(i))!!, R.drawable.custom_unit_transparent))
                        }

                        for (i in 0..productInfoList.pic_path.size - 1) {

                            //transfer to Base64
                            val baos = ByteArrayOutputStream()
                            mutableList_pics[i].bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val b = baos.toByteArray()
                            val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

                            MMKV.mmkvWithID("editPro").putString("value_pic${i}", encodedImage)
                        }


                        //Others
                        MMKV.mmkvWithID("editPro").putString("value_editTextEntryProductName", productInfoList.product_title.toString())
                        MMKV.mmkvWithID("editPro").putString("value_editTextEntryProductDiscription", productInfoList.product_description.toString())
                        MMKV.mmkvWithID("editPro").putString("product_category_id", productInfoList.product_category_id.toString())
                        MMKV.mmkvWithID("editPro").putString("product_sub_category_id", productInfoList.product_sub_category_id.toString())
                        MMKV.mmkvWithID("editPro").putString("c_product_category", productInfoList.c_product_category.toString())
                        MMKV.mmkvWithID("editPro").putString("c_product_sub_category", productInfoList.c_sub_product_category.toString())
                        MMKV.mmkvWithID("editPro").putString("value_textViewSeletedCategory", "${productInfoList.c_product_category.toString()}>${ productInfoList.c_sub_product_category.toString()}")
                        MMKV.mmkvWithID("editPro").putString("value_checked_brandNew", productInfoList.new_secondhand.toString())
                        MMKV.mmkvWithID("editPro").putString("product_spec_on", productInfoList.product_spec_on.toString())
                        MMKV.mmkvWithID("editPro").putString("product_price", productInfoList.product_price.toString())
                        MMKV.mmkvWithID("editPro").putString("quantity", productInfoList.quantity.toString())
                        MMKV.mmkvWithID("editPro").putString("datas_packagesWeights", productInfoList.weight.toString())
                        MMKV.mmkvWithID("editPro").putString("datas_length", productInfoList.length.toString())
                        MMKV.mmkvWithID("editPro").putString("datas_width",  productInfoList.width.toString())
                        MMKV.mmkvWithID("editPro").putString("datas_height", productInfoList.height.toString())
                        MMKV.mmkvWithID("editPro").putString("value_editMoreTimeInput", productInfoList.longterm_stock_up.toString())
                        MMKV.mmkvWithID("editPro").putString("inven_price_range", "HKD$${productInfoList.min_price}-HKD${productInfoList.max_price}")
                        MMKV.mmkvWithID("editPro").putString("inven_quant_range", "HKD$${productInfoList.min_quantity}-HKD${productInfoList.max_quantity}")
                        MMKV.mmkvWithID("editPro").putString("value_editTextMerchanPrice", productInfoList.product_price.toString())
                        MMKV.mmkvWithID("editPro").putString("value_editTextMerchanQunt", productInfoList.quantity.toString())
                        MMKV.mmkvWithID("editPro").putInt("inven_datas_size", 0)
                        MMKV.mmkvWithID("editPro").putString("product_status",  productInfoList.product_status.toString())


                        //EditShippingFareActivity
                        MMKV.mmkvWithID("editPro").putString("fare_datas_size", productInfoList.product_shipment_list.size.toString())
                        MMKV.mmkvWithID("editPro").putString("fare_datas_certained_size", productInfoList.product_shipment_list.size.toString())
                        MMKV.mmkvWithID("editPro").putString("value_txtViewFareRange", "HKD$${productInfoList.shipment_min_price.toString()}-HKD$${productInfoList.shipment_max_price.toString()}" )


                        when(productInfoList.product_status){
                            "active"->{
                                runOnUiThread {
                                    binding.btnOnShelf.setImageResource(R.mipmap.btn_draft)

                                }
                            }
                            "draft"->{
                                runOnUiThread {
                                    binding.btnOnShelf.setImageResource(R.mipmap.btn_launch)

                                }
                            }
                        }


                        if(productInfoList.product_shipment_list.size>0){
                            for (i in 0..productInfoList.product_shipment_list.size - 1) {

                                var json_shippingItem = GsonProvider.gson.toJson(ItemShippingFare(productInfoList.product_shipment_list.get(i).shipment_desc, productInfoList.product_shipment_list.get(i).price.toString(), productInfoList.product_shipment_list.get(i).onoff, MMKV_shop_id))
                                MMKV.mmkvWithID("editPro").putString("value_fare_item${i}",json_shippingItem)

                            }
                        }else{
                            var json_shippingItem = GsonProvider.gson.toJson(ItemShippingFare("", "", "off", MMKV_shop_id))
                            MMKV.mmkvWithID("editPro").putString("value_fare_item${0}",json_shippingItem)
                        }

                        //將從API取出的資料以ItemShippingFare的形式存取並裝成mutableList_itemShipingFare_filtered
                        for (i in 0..productInfoList.product_shipment_list.size - 1) {
                            mutableList_itemShipingFare_filtered.add( ItemShippingFare_Filtered(productInfoList.product_shipment_list.get(i).shipment_desc, productInfoList.product_shipment_list.get(i).price.toInt(), productInfoList.product_shipment_list.get(i).onoff, MMKV_shop_id))
                        }
                        MMKV.mmkvWithID("editPro").putString("fare_datas_filtered_size", mutableList_itemShipingFare_filtered.size.toString())

                        //mutableList_itemShipingFare_filtered一個個項目裝進mmkv，避免mmkv filtered item ID錯亂，保持以流水號型式
                        for(i in 0..mutableList_itemShipingFare_filtered.size-1){
                            var json_shippingItem = GsonProvider.gson.toJson(mutableList_itemShipingFare_filtered.get(i))
                            MMKV.mmkvWithID("editPro").putString("value_fare_item_filtered${i}",json_shippingItem)

                        }


                        //存完後清掉，避免後來的mutableList_itemShipingFare_filtered重複裝取
                        mutableList_itemShipingFare_filtered.clear()

                        //取出所有Fare Item(拿掉btn_delete參數)

                        for (i in 0..productInfoList.product_shipment_list.size - 1) {
                            if(productInfoList.product_shipment_list.get(i).onoff.equals("on")){
                                mutableList_itemShipingFare_certained.add(ItemShippingFare_Certained(productInfoList.product_shipment_list.get(i).shipment_desc, productInfoList.product_shipment_list.get(i).price.toString(), productInfoList.product_shipment_list.get(i).onoff, MMKV_shop_id))
                            }
                        }

                        for (i in 0..mutableList_itemShipingFare_certained.size - 1) {
                            var json_shippingItem_certained = GsonProvider.gson.toJson(mutableList_itemShipingFare_certained.get(i))
                            MMKV.mmkvWithID("editPro").putString("value_fare_item_certained${i}",json_shippingItem_certained)
                        }




                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                        val jsonList_shipment_all: String = gson.toJson(mutableList_itemShipingFare_filtered)
                        Log.d("AddNewProductActivity", mutableList_itemShipingFare_filtered.toString())
                        val jsonList_shipment_all_pretty: String = gsonPretty.toJson(mutableList_itemShipingFare_filtered)
                        Log.d("AddNewProductActivity", mutableList_itemShipingFare_filtered.toString())

                        val jsonList_shipment_certained: String = gson.toJson(mutableList_itemShipingFare_certained)
                        Log.d("AddNewProductActivity", mutableList_itemShipingFare_certained.toString())
                        val jsonList_shipment_certained_pretty: String = gsonPretty.toJson(mutableList_itemShipingFare_certained)
                        Log.d("AddNewProductActivity", mutableList_itemShipingFare_certained.toString())


                        MMKV.mmkvWithID("editPro").putString("jsonList_shipment_all", jsonList_shipment_all)
                        MMKV.mmkvWithID("editPro").putString("jsonList_shipment_certained", jsonList_shipment_certained)


                        if(  productInfoList.product_spec_on.equals("y") ){


                            //EditProductSpecificationMainActivity
                            MMKV.mmkvWithID("editPro").putString(
                                "value_editTextProductSpecFirst",
                                productInfoList.spec_desc_1.get(0)
                            )
                            MMKV.mmkvWithID("editPro").putString(
                                "value_editTextProductSpecSecond",
                                productInfoList.spec_desc_2.get(0)
                            )

                            var mutableSet_spec_dec_1_items: MutableSet<String> =
                                productInfoList.spec_dec_1_items.toMutableSet()
                            var mutableSet_spec_dec_2_items: MutableSet<String> =
                                productInfoList.spec_dec_2_items.toMutableSet()
                            var mutableList_spec_dec_1_items: MutableList<String> =
                                mutableSet_spec_dec_1_items.toMutableList()
                            var mutableList_spec_dec_2_items: MutableList<String> =
                                mutableSet_spec_dec_2_items.toMutableList()

                            MMKV.mmkvWithID("editPro").putString(
                                "datas_spec_size",
                                mutableSet_spec_dec_1_items.size.toString()
                            )

                            for (i in 0..mutableSet_spec_dec_1_items.size - 1) {
                                MMKV.mmkvWithID("editPro").putString(
                                    "datas_spec_item${i}",
                                    mutableList_spec_dec_1_items.get(i)
                                )
                            }

                            if(productInfoList.spec_desc_2.get(0).isNullOrEmpty()){

                                MMKV.mmkvWithID("editPro").putString(
                                    "datas_size_size",
                                    "0"
                                )

                                for (i in 0..mutableSet_spec_dec_2_items.size - 1) {
                                    MMKV.mmkvWithID("editPro").putString(
                                        "datas_size_item${i}",
                                        mutableList_spec_dec_2_items.get(i)
                                    )
                                }


                            }else{

                                MMKV.mmkvWithID("editPro").putString(
                                    "datas_size_size",
                                    mutableSet_spec_dec_2_items.size.toString()
                                )

                                for (i in 0..mutableSet_spec_dec_2_items.size - 1) {
                                    MMKV.mmkvWithID("editPro").putString(
                                        "datas_size_item${i}",
                                        mutableList_spec_dec_2_items.get(i)
                                    )
                                }

                            }


                            MMKV.mmkvWithID("editPro").putString(
                                "datas_price_size",
                                productInfoList.price.size.toString()
                            )

                            MMKV.mmkvWithID("editPro").putString(
                                "datas_quant_size",
                                productInfoList.spec_quantity.size.toString()
                            )


                            for (i in 0..productInfoList.price.size - 1) {

                                MMKV.mmkvWithID("editPro").putString(
                                    "spec_price${i}",
                                    productInfoList.price.get(i).toString()
                                )
                            }

                            for (i in 0..productInfoList.spec_quantity.size - 1) {
                                MMKV.mmkvWithID("editPro").putString(
                                    "spec_quantity${i}",
                                    productInfoList.spec_quantity.get(i).toString()
                                )
                            }



                            for (i in 0.. productInfoList.spec_desc_1.size-1){

                                mutableList_InvenDatas.add(
                                    InventoryItemDatas(
                                        productInfoList.spec_desc_1.get(i),
                                        productInfoList.spec_desc_2.get(i),
                                        productInfoList.spec_dec_1_items.get(i),
                                        productInfoList.spec_dec_2_items.get(i),
                                        productInfoList.price.get(i),
                                        productInfoList.spec_quantity.get(i))
                                )

                            }


                            val gson = Gson()
                            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                            val jsonTutList_inven: String = gson.toJson(mutableList_InvenDatas)
                            Log.d("AddNewProductActivity", jsonTutList_inven.toString())
                            val jsonTutListPretty_inven: String = gsonPretty.toJson(mutableList_InvenDatas)
                            Log.d("AddNewProductActivity", jsonTutListPretty_inven.toString())

                            MMKV.mmkvWithID("editPro").putString("jsonTutList_inven", jsonTutList_inven)


                        }

                        initMMKV_and_initViewValue()

                    }else{
                    }
                    runOnUiThread {
                        binding.progressBar3.isVisible = false
                    }
                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }


    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }
}