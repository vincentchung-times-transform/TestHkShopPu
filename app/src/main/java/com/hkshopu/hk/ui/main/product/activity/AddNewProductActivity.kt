package com.hkshopu.hk.ui.main.product.activity

import MyLinearLayoutManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ItemShippingFare_Certained
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.GsonProvider.gson
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.product.fragment.StoreOrNotDialogFragment
import com.hkshopu.hk.ui.main.product.adapter.ShippingFareCheckedAdapter
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
import java.util.*
import kotlin.collections.ArrayList


class AddNewProductActivity : BaseActivity() {
    var activity : BaseActivity  = this

    var editMode_or_AddMode = "editMdoe"

    private lateinit var binding: ActivityAddNewProductBinding
    private val VM = ShopVModel()
    val mAdapters_shippingFareChecked = ShippingFareCheckedAdapter()
    val REQUEST_EXTERNAL_STORAGE = 100
    var which_click: String = ""

    //從本地端選取圖片轉換為bitmap後存的list
    var mutableList_pics = mutableListOf<ItemPics>()

    //宣告頁面資料變數
    var MMKV_editTextEntryProductName :String = ""
    var MMKV_editTextEntryProductDiscription :String = ""
    var MMKV_textViewSeletedCategory :String = "0"
    var MMKV_product_spec_on: String = "n"
    var MMKV_editTextMerchanPrice :String = ""
    var MMKV_editTextMerchanQunt :String = ""
    var MMKV_inven_price_range: String = ""
    var MMKV_inven_quant_range: String = ""
    var MMKV_value_txtViewFareRange :String = ""
    var MMKV_boolean_needMoreTimeToStockUp = "y"
    var MMKV_editMoreTimeInput :String = ""
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 0
    var MMKV_proCate_id: String = ""
    var MMKV_proSubCate_id: String = ""
    var MMKV_weight: String = ""
    var MMKV_length:String = ""
    var MMKV_width: String = ""
    var MMKV_height: String = ""
    var MMKV_checked_brandNew = "new"
    var MMKV_jsonTutList_inven : String = "[{ \"spec_desc_1\": \"\",\"spec_desc_2\": \"\",\"spec_dec_1_items\": \"\",\"spec_dec_2_items\": \"\",\"price\": 0,\"quantity\": 0 }]"
    var MMKV_jsonTutList_fare : String = "[{\"shipment_desc\":\"\",\"price\":0,\"onoff\":\"of\",\"shop_id\" : 0 }]"

    var product_add_session = false

    //宣告運費項目陣列變數
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_certained = mutableListOf<ItemShippingFare_Certained>()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        product_add_session =  MMKV.mmkvWithID("http").getBoolean("product_add_session", false)
        Log.d("product_add_session", product_add_session.toString())
        if(product_add_session.equals(false)) {
            product_add_session = true
            MMKV.mmkvWithID("http").putBoolean("product_add_session", product_add_session)
            getShopLogisticsList()
        }else{
            Thread(Runnable {
                initProFareDatas()
            }).start()
        }



        initView()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    fun initMMKV_and_initViewValue() {




        var pics_list_size = MMKV.mmkvWithID("addPro").getInt("value_pics_size", 0)

        Thread(Runnable {

            for (i in 0..pics_list_size - 1) {

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
            Log.d("mutableList_pics", mutableList_pics.toString())

            runOnUiThread {
                val mAdapter = PicsAdapter()
                binding.rView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rView.adapter = mAdapter

                mAdapter.updateList(mutableList_pics)
            }

        }).start()

        MMKV_editTextEntryProductName = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductName",
            MMKV_editTextEntryProductName
        ).toString()
        binding.editTextEntryProductName.setText(MMKV_editTextEntryProductName)

        MMKV_editTextEntryProductDiscription = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductDiscription",
            MMKV_editTextEntryProductDiscription
        ).toString()
        binding.editTextEntryProductDiscription.setText(MMKV_editTextEntryProductDiscription)


        MMKV_checked_brandNew = MMKV.mmkvWithID("addPro").getString("value_checked_brandNew", MMKV_checked_brandNew).toString()
        if(MMKV_checked_brandNew=="new"){

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

        binding.needMoreTimeToStockUp.text = getString(R.string.textView_more_time_to_stock)
        MMKV_boolean_needMoreTimeToStockUp = MMKV.mmkvWithID("addPro").getString(
            "boolean_needMoreTimeToStockUp",
            "n"
        ).toString()
        if(MMKV_boolean_needMoreTimeToStockUp=="n"){
            binding.needMoreTimeToStockUp.setText(R.string.textView_questionNeedMoreTimeToStockUp)
            binding.needMoreTimeToStockUp.isChecked =false
        }else{
            binding.needMoreTimeToStockUp.setText(R.string.textView_needMoreTimeToStockUp)
            binding.needMoreTimeToStockUp.isChecked =true
        }
        MMKV_editMoreTimeInput = MMKV.mmkvWithID("addPro").getString("value_editMoreTimeInput", "").toString()
        binding.editMoreTimeInput.setText(MMKV_editMoreTimeInput)

        if(MMKV_editMoreTimeInput.isNotEmpty() && MMKV_editMoreTimeInput.toInt()>0){
            binding.editMoreTimeInput.isVisible = true
            binding.needMoreTimeToStockUp.isChecked = true
        }else{
            binding.editMoreTimeInput.isVisible = false
            binding.needMoreTimeToStockUp.isChecked = false
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initView() {

        initMMKV_and_initViewValue()
        initProCategoryDatas()
        initInvenDatas()
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
                    MMKV.mmkvWithID("addPro").putString(
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
                MMKV.mmkvWithID("addPro").putString(
                    "value_editTextEntryProductName",
                    MMKV_editTextEntryProductName
                )
            }
        }
        binding.editTextEntryProductName.addTextChangedListener(textWatcher_editTextEntryProductName)

        binding.editTextEntryProductDiscription.singleLine = true
        binding.editTextEntryProductDiscription.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editTextEntryProductDiscription =
                        binding.editTextEntryProductDiscription.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextEntryProductDiscription",
                        MMKV_editTextEntryProductDiscription
                    )

                    binding.editTextEntryProductDiscription.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductDiscription)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextEntryProductDiscription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                MMKV_editTextEntryProductDiscription = binding.editTextEntryProductDiscription.text.toString()
                MMKV.mmkvWithID("addPro").putString(
                    "value_editTextEntryProductDiscription",
                    MMKV_editTextEntryProductDiscription
                )

            }
        }
        binding.editTextEntryProductDiscription.addTextChangedListener(textWatcher_editTextEntryProductDiscription)


        binding.editTextMerchanPrice.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus ){
                binding.editTextMerchanPrice.setText("${MMKV_editTextMerchanPrice}")
            }
        }
        binding.editTextMerchanPrice.singleLine = true
        binding.editTextMerchanPrice.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    if(binding.editTextMerchanPrice.text.isNotEmpty()){

                        binding.editTextMerchanPrice.setText("HKD$ ${binding.editTextMerchanPrice.text.toString()}")
                        MMKV_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString().substring(5)
                        MMKV.mmkvWithID("addPro").putString(
                            "value_editTextMerchanPrice",
                            MMKV_editTextMerchanPrice
                        )

                    }else{
                        binding.editTextMerchanPrice.setText("")
                        MMKV_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString()
                        MMKV.mmkvWithID("addPro").putString(
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
                    MMKV.mmkvWithID("addPro").putString(
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
                    MMKV.mmkvWithID("addPro").putString(
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
                MMKV.mmkvWithID("addPro").putString(
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
                    MMKV.mmkvWithID("addPro").putString(
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
                MMKV.mmkvWithID("addPro").putString(
                    "value_editMoreTimeInput",
                    MMKV_editMoreTimeInput
                )
            }
        }
        binding.editMoreTimeInput.addTextChangedListener(textWatcher_editMoreTimeInput)


    }

    fun initClick() {

        binding.btnOnShelf.setOnClickListener {
            which_click="launch"

            var pic_list : ArrayList<File> = arrayListOf()
            var file: File? = null
            for(i in 0..mutableList_pics.size-1){
                file = processImage(mutableList_pics.get(i).bitmap, i)
                pic_list.add(file!!)
            }

            Log.d("addNewPro", mutableList_pics.size.toString())
            Log.d("addNewPro", pic_list.toString())
            Log.d("addNewPro", "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }")
            Log.d("addNewPro", MMKV_jsonTutList_fare)

//            VM.add_product(this, 1, 1, 1, "0", 0, "0", 0, 0, 0, "new", pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", 1, 0, 0, 0, jsonTutList_fare)

            if(pic_list.size >=1){
                if(MMKV_editTextEntryProductName.isNotEmpty()){
                    if(MMKV_editTextEntryProductDiscription.isNotEmpty()){
                        if(MMKV_proCate_id.isNotEmpty()||MMKV_proSubCate_id.isNotEmpty()){
                            if(MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
                                if( !MMKV_editTextMerchanPrice.toString().equals("") && !MMKV_editTextMerchanQunt.equals("") &&  binding.iosSwitchSpecification.isOpened().equals(false) ){
                                    if(mutableList_itemShipingFare.size>0){

//                                        var inven_switch_off_json = "{ \"product_spec_list\" : [{\"price\": ${value_editTextMerchanPrice}, \"quantity\": ${value_editTextMerchanQunt}, \"spec_dec_1_items\":\"\",\"spec_dec_2_items\":\"\",\"spec_desc_1\":\"\",\"spec_desc_2\":\"\"}]}"
//                                        Log.d("inven_switch_off_json", inven_switch_off_json.toString())
                                        var inven_switch_off_json = "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"

                                            if(MMKV_editMoreTimeInput.equals("")){
                                                MMKV_editMoreTimeInput = "0"
                                        }

                                        //quantity and product_price is discarded
                                        doAddProduct( MMKV_shop_id,
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
                                            inven_switch_off_json,
                                            MMKV_user_id.toInt(),
                                            MMKV_length.toInt(),
                                            MMKV_width.toInt(),
                                            MMKV_height.toInt(),
                                            MMKV_jsonTutList_fare,
                                            MMKV_editMoreTimeInput.toInt(),
                                            "active",
                                            MMKV_product_spec_on)

                                        Log.d("MMKV_shop_id" ,
                                            "MMKV_shop_id: ${MMKV_shop_id} ; "
                                                    +"MMKV_proCate_id: ${MMKV_proCate_id} ; "
                                                    +"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "
                                                    +"value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; "
                                                    +"value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; "
                                                    +"value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; "
                                                    +"value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; "
                                                    +"MMKV_weight: ${MMKV_weight} ; "
                                                    +"value_checked_brandNew: ${MMKV_checked_brandNew} ; "
                                                    +"pic_list.size: ${pic_list.size} ; "
                                                    +"pic_list: ${pic_list} ; "
                                                    +"${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; "
                                                    +"MMKV_user_id: ${MMKV_user_id} ; "
                                                    +"MMKV_length: ${MMKV_length} ; "
                                                    +"MMKV_width: ${MMKV_width} ; "
                                                    +"MMKV_width: ${MMKV_width} ; "
                                                    +"MMKV_height: ${MMKV_height} ; "
                                                    +"jsonTutList_fare: ${MMKV_jsonTutList_fare} ; "
                                                    +"MMKV_editMoreTimeInput: ${MMKV_editMoreTimeInput} ; "
                                                    +"MMKV_product_spec_on: ${MMKV_product_spec_on} ; ")
                                        MMKV.mmkvWithID("addPro").clearAll()

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
                                            doAddProduct( MMKV_shop_id,
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
                                                MMKV_jsonTutList_fare,
                                                MMKV_editMoreTimeInput.toInt(),
                                                "active",
                                                MMKV_product_spec_on)

                                            Log.d("MMKV_shop_id" ,
                                                "MMKV_shop_id: ${MMKV_shop_id} ; "
                                                        +"MMKV_proCate_id: ${MMKV_proCate_id} ; "
                                                        +"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "
                                                        +"value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; "
                                                        +"value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; "
                                                        +"value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; "
                                                        +"value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; "
                                                        +"MMKV_weight: ${MMKV_weight} ; "
                                                        +"value_checked_brandNew: ${MMKV_checked_brandNew} ; "
                                                        +"pic_list.size: ${pic_list.size} ; "
                                                        +"pic_list: ${pic_list} ; "
                                                        +"${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; "
                                                        +"MMKV_user_id: ${MMKV_user_id} ; "
                                                        +"MMKV_length: ${MMKV_length} ; "
                                                        +"MMKV_width: ${MMKV_width} ; "
                                                        +"MMKV_width: ${MMKV_width} ; "
                                                        +"MMKV_height: ${MMKV_height} ; "
                                                        +"jsonTutList_fare: ${MMKV_jsonTutList_fare} ; "
                                                        +"MMKV_editMoreTimeInput: ${MMKV_editMoreTimeInput} ; "
                                                        +"MMKV_product_spec_on: ${MMKV_product_spec_on} ; ")
                                            MMKV.mmkvWithID("addPro").clearAll()

                                        }else{
                                            Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Log.d("testtestetest", MMKV_inven_price_range.toString()+MMKV_inven_quant_range.toString())
                                        Toast.makeText(this, "商品庫存尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                } else{
                                    Toast.makeText(this, "商品價格與數量尚未填寫", Toast.LENGTH_SHORT).show()
                                }
                            }else{

                                Log.d("MMKV_shop_id" , "MMKV_shop_id: ${MMKV_shop_id} ; "+"MMKV_proCate_id: ${MMKV_proCate_id} ; "+"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "+"value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; "+"value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; "+"value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; "+"value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; "+"MMKV_weight: ${MMKV_weight} ; "+"value_checked_brandNew: ${MMKV_checked_brandNew} ; "+"pic_list.size: ${pic_list.size} ; "+"pic_list: ${pic_list} ; "+"${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; "+"MMKV_user_id: ${MMKV_user_id} ; "+"MMKV_length: ${MMKV_length} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_height: ${MMKV_height} ; "+"jsonTutList_fare: ${MMKV_jsonTutList_fare}")
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

        binding.needMoreTimeToStockUp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.editMoreTimeInput.isVisible = true
                MMKV.mmkvWithID("addPro").putString(
                    "boolean_needMoreTimeToStockUp",
                    "y"
                )

            } else {
                binding.editMoreTimeInput.isVisible = false
                MMKV.mmkvWithID("addPro").putString(
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

                    MMKV.mmkvWithID("addPro").putString(
                        "product_spec_on",
                        "y"
                    )

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


                    MMKV.mmkvWithID("addPro").putString(
                        "product_spec_on",
                        "n"
                    )


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

            StoreOrNotDialogFragment(activity).show(supportFragmentManager, "MyCustomFragment")

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
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", MMKV_checked_brandNew)
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
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", MMKV_checked_brandNew)

        }

        binding.containerAddSpecification.setOnClickListener {
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)

            finish()

        }
        binding.containerShippingFare.setOnClickListener {
            val intent = Intent(this, AddShippingFareActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.categoryContainer.setOnClickListener {
            val intent = Intent(this, AddMerchanCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnStore.setOnClickListener {

            which_click="store"

            var pic_list : ArrayList<File> = arrayListOf()
            var file: File? = null
            for(i in 0..mutableList_pics.size-1){
                file = processImage(mutableList_pics.get(i).bitmap, i)
                pic_list.add(file!!)
            }

            Log.d("addNewPro", mutableList_pics.size.toString())
            Log.d("addNewPro", pic_list.toString())
            Log.d("addNewPro", "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }")
            Log.d("addNewPro", MMKV_jsonTutList_fare)


            if(pic_list.size >=1){
                if(MMKV_editTextEntryProductName.isNotEmpty()){
                    if(MMKV_editTextEntryProductDiscription.isNotEmpty()){
                        if(MMKV_proCate_id.isNotEmpty()||MMKV_proSubCate_id.isNotEmpty()){
                            if(MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
                                if( !MMKV_editTextMerchanPrice.toString().equals("") && !MMKV_editTextMerchanQunt.equals("") &&  binding.iosSwitchSpecification.isOpened().equals(false) ){
                                    if(mutableList_itemShipingFare.size>0){

//                                        var inven_switch_off_json = "{ \"product_spec_list\" : [{\"price\": ${value_editTextMerchanPrice}, \"quantity\": ${value_editTextMerchanQunt}, \"spec_dec_1_items\":\"\",\"spec_dec_2_items\":\"\",\"spec_desc_1\":\"\",\"spec_desc_2\":\"\"}]}"
//                                        Log.d("inven_switch_off_json", inven_switch_off_json.toString())
                                        var inven_switch_off_json = "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"

                                        if(MMKV_editMoreTimeInput.equals("")){
                                            MMKV_editMoreTimeInput = "0"
                                        }

                                        //quantity and product_price is discarded
                                        doAddProduct( MMKV_shop_id,
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
                                            inven_switch_off_json,
                                            MMKV_user_id,
                                            MMKV_length.toInt(),
                                            MMKV_width.toInt(),
                                            MMKV_height.toInt(),
                                            MMKV_jsonTutList_fare,
                                            MMKV_editMoreTimeInput.toInt(),
                                            "draft",
                                            MMKV_product_spec_on)
                                        Log.d("MMKV_shop_id" ,
                                            "MMKV_shop_id: ${MMKV_shop_id} ; "
                                                    +"MMKV_proCate_id: ${MMKV_proCate_id} ; "
                                                    +"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "
                                                    +"value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; "
                                                    +"value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; "
                                                    +"value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; "
                                                    +"value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; "
                                                    +"MMKV_weight: ${MMKV_weight} ; "
                                                    +"value_checked_brandNew: ${MMKV_checked_brandNew} ; "
                                                    +"pic_list.size: ${pic_list.size} ; "
                                                    +"pic_list: ${pic_list} ; "
                                                    +"${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; "
                                                    +"MMKV_user_id: ${MMKV_user_id} ; "
                                                    +"MMKV_length: ${MMKV_length} ; "
                                                    +"MMKV_width: ${MMKV_width} ; "
                                                    +"MMKV_width: ${MMKV_width} ; "
                                                    +"MMKV_height: ${MMKV_height} ; "
                                                    +"jsonTutList_fare: ${MMKV_jsonTutList_fare} ; "
                                                    +"MMKV_editMoreTimeInput: ${MMKV_editMoreTimeInput} ; "
                                                    +"MMKV_product_spec_on: ${MMKV_product_spec_on} ; ")

                                        MMKV.mmkvWithID("addPro").clearAll()



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
                                            doAddProduct( MMKV_shop_id,
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
                                                MMKV_user_id, MMKV_length.toInt(),
                                                MMKV_width.toInt(),
                                                MMKV_height.toInt(),
                                                MMKV_jsonTutList_fare,
                                                MMKV_editMoreTimeInput.toInt(),
                                                "draft",
                                                MMKV_product_spec_on)

                                                Log.d("MMKV_shop_id" ,
                                                    "MMKV_shop_id: ${MMKV_shop_id} ; "
                                                            +"MMKV_proCate_id: ${MMKV_proCate_id} ; "
                                                            +"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "
                                                            +"value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; "
                                                            +"value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; "
                                                            +"value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; "
                                                            +"value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; "
                                                            +"MMKV_weight: ${MMKV_weight} ; "
                                                            +"value_checked_brandNew: ${MMKV_checked_brandNew} ; "
                                                            +"pic_list.size: ${pic_list.size} ; "
                                                            +"pic_list: ${pic_list} ; "
                                                            +"${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; "
                                                            +"MMKV_user_id: ${MMKV_user_id} ; "
                                                            +"MMKV_length: ${MMKV_length} ; "
                                                            +"MMKV_width: ${MMKV_width} ; "
                                                            +"MMKV_width: ${MMKV_width} ; "
                                                            +"MMKV_height: ${MMKV_height} ; "
                                                            +"jsonTutList_fare: ${MMKV_jsonTutList_fare} ; "
                                                            +"MMKV_editMoreTimeInput: ${MMKV_editMoreTimeInput} ; "
                                                            +"MMKV_product_spec_on: ${MMKV_product_spec_on} ; ")
                                            MMKV.mmkvWithID("addPro").clearAll()


                                        }else{
                                            Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Log.d("testtestetest", MMKV_inven_price_range.toString()+MMKV_inven_quant_range.toString())
                                        Toast.makeText(this, "商品庫存尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                } else{
                                    Toast.makeText(this, "商品價格與數量尚未填寫", Toast.LENGTH_SHORT).show()
                                }
                            }else{

                                Log.d("MMKV_shop_id" , "MMKV_shop_id: ${MMKV_shop_id} ; "+"MMKV_proCate_id: ${MMKV_proCate_id} ; "+"MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; "+"value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; "+"value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; "+"value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; "+"value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; "+"MMKV_weight: ${MMKV_weight} ; "+"value_checked_brandNew: ${MMKV_checked_brandNew} ; "+"pic_list.size: ${pic_list.size} ; "+"pic_list: ${pic_list} ; "+"${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; "+"MMKV_user_id: ${MMKV_user_id} ; "+"MMKV_length: ${MMKV_length} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_width: ${MMKV_width} ; "+"MMKV_height: ${MMKV_height} ; "+"jsonTutList_fare: ${MMKV_jsonTutList_fare}")
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
                        mutableList_pics[i].bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
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
                                80,
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

            }).start()
        }
    }

    fun initProCategoryDatas() {

        MMKV_proCate_id = MMKV.mmkvWithID("addPro").getString("product_category_id", "").toString()
        MMKV_proSubCate_id = MMKV.mmkvWithID("addPro").getString("product_sub_category_id", "").toString()
        MMKV_textViewSeletedCategory = MMKV.mmkvWithID("addPro").getString("value_textViewSeletedCategory", MMKV_textViewSeletedCategory).toString()
        binding.textViewSeletedCategory.setText(MMKV_textViewSeletedCategory)
        Log.d("MMKV_proCate_id", "MMKV_proCate_id: ${MMKV_proCate_id} ; "+"MMKV_proSubCate_id: ${MMKV_proSubCate_id}"+"value_textViewSeletedCategory: ${MMKV_textViewSeletedCategory} ; ")

        if (MMKV_proCate_id.isEmpty() || MMKV_proSubCate_id.isEmpty()) {
            binding.textViewSeletedCategory.isVisible = false
            binding.btnAddcategory.isVisible = true
        } else {
            binding.textViewSeletedCategory.isVisible = true
            binding.btnAddcategory.isVisible = false
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initProFareDatas() {

        MMKV_weight = MMKV.mmkvWithID("addPro").getString("datas_packagesWeights", "").toString()
        MMKV_length = MMKV.mmkvWithID("addPro").getString("datas_length", "").toString()
        MMKV_width = MMKV.mmkvWithID("addPro").getString("datas_width", "").toString()
        MMKV_height = MMKV.mmkvWithID("addPro").getString("datas_height", "").toString()
        var fare_datas_size = MMKV.mmkvWithID("addPro").getString("fare_datas_size", "0").toString().toInt()
        var fare_datas_filtered_size = MMKV.mmkvWithID("addPro").getString("fare_datas_filtered_size","0").toString().toInt()
        MMKV_value_txtViewFareRange = MMKV.mmkvWithID("addPro").getString("value_txtViewFareRange", "").toString()
        MMKV_jsonTutList_fare = MMKV.mmkvWithID("addPro").getString("jsonTutList_fare", MMKV_jsonTutList_fare).toString()
        Log.d("MMKV_weight", "MMKV_weight : ${MMKV_weight}, MMKV_length : ${MMKV_length}, MMKV_width : ${MMKV_width}, MMKV_height : ${MMKV_height}, fare_datas_size : ${fare_datas_size}, fare_datas_filtered_size : ${fare_datas_filtered_size}, MMKV_value_txtViewFareRange: ${MMKV_value_txtViewFareRange}")
        Log.d("MMKV_jsonTutList_fare", "MMKV_jsonTutList_fare : " + MMKV_jsonTutList_fare.toString())

        runOnUiThread {
            binding.txtViewFareRange.text = MMKV_value_txtViewFareRange
        }

        if (fare_datas_size != null) {

            if(fare_datas_size > 0) {

                runOnUiThread {
                    binding.rViewFareItem.isVisible = true
                    binding.imgLineFare.isVisible = true
                }

                //MMKV取出 Fare Item
                for (i in 0..fare_datas_size-1) {
                    var json_invens : String? = MMKV.mmkvWithID("addPro").getString(
                        "value_fare_item${i}",
                        ""
                    )
                    val gson = Gson()
                    val value_fare_item : ItemShippingFare= gson.fromJson(json_invens, ItemShippingFare::class.java)


                    mutableList_itemShipingFare.add(value_fare_item)
                }


                if(fare_datas_filtered_size >0){

                    Log.d("json_invens", fare_datas_filtered_size.toString())
                    //MMKV取出 Filtered Fare Item
                    for (i in 0..fare_datas_filtered_size-1!!) {
                        var json_invens : String? = MMKV.mmkvWithID("addPro").getString("value_fare_item_filtered${i}", "")
                        val json = json_invens
                        val value_fare_item_filtered = gson.fromJson(json, ItemShippingFare::class.java)
                        mutableList_itemShipingFare_filtered.add(value_fare_item_filtered) //顯示在UI
                        Log.d("json_invens", i.toString())
                        Log.d("json_invens", value_fare_item_filtered.toString())
                    }

                    Log.d("MMKV_CheckValue", "mutableList_itemShipingFare: ${mutableList_itemShipingFare}")
                    Log.d("MMKV_CheckValue", "mutableList_itemShipingFare_filtered : ${mutableList_itemShipingFare_filtered}")





                    //自訂layoutManager
                    runOnUiThread {
                        binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this, false))
                        binding.rViewFareItem.adapter = mAdapters_shippingFareChecked


                    }

                    Thread(Runnable {

                        mAdapters_shippingFareChecked.updateList(mutableList_itemShipingFare_filtered)
                        runOnUiThread {
                            mAdapters_shippingFareChecked.notifyDataSetChanged()
                        }


                    }).start()
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
        //挑選最大與最小金額，回傳價格區間
        runOnUiThread {
            binding.txtViewFareRange.text = MMKV_value_txtViewFareRange
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initInvenDatas() {

        MMKV_product_spec_on = MMKV.mmkvWithID("addPro").getString("product_spec_on", "n").toString()
        var inven_datas_size = MMKV.mmkvWithID("addPro").getInt("inven_datas_size", 0)

        MMKV_jsonTutList_inven = MMKV.mmkvWithID("addPro").getString("jsonTutList_inven", MMKV_jsonTutList_inven).toString()
        Log.d("MMKV_jsonTutList_inven", "MMKV_jsonTutList_inven : " + MMKV_jsonTutList_inven.toString())

        //MMKV取出mutableList_InvenDatas
//        for(i in 0..inven_datas_size!!-1){
//
//            var  = MMKV.mmkvWithID("addPro").getString("value_inven${i}", "")
//
//            val gson = Gson()
//            val jsonTutList: String = gson.fromJson(mutableList_InvenDatas.indexOf(i), InventoryItemDatas::class.java)
//        }


        //挑選最大與最小金額，回傳價格區間

        MMKV_inven_price_range = MMKV.mmkvWithID("addPro").getString("inven_price_range", MMKV_inven_price_range).toString()
        MMKV_inven_quant_range = MMKV.mmkvWithID("addPro").getString("inven_quant_range", MMKV_inven_quant_range).toString()
        MMKV_editTextMerchanPrice = MMKV.mmkvWithID("addPro").getString("value_editTextMerchanPrice", MMKV_editTextMerchanPrice).toString()
        MMKV_editTextMerchanQunt = MMKV.mmkvWithID("addPro").getString("value_editTextMerchanQunt", MMKV_editTextMerchanQunt).toString()
        binding.editTextMerchanPrice.setText(MMKV_editTextMerchanPrice)
        binding.editTextMerchanQunt.setText(MMKV_editTextMerchanQunt)

        //預設containerSpecification的背景
        if(MMKV_product_spec_on.equals("y")){

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

            binding.textViewMerchanPriceRange.text = MMKV_inven_price_range
            binding.textViewMerchanQuntRange.text = MMKV_inven_quant_range

        }else{

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
            bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
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

    private fun doAddProduct(shop_id : Int, product_category_id : Int, product_sub_category_id :Int, product_title : String, quantity : Int, product_description : String, product_price :Int, shipping_fee : Int, weight : Int, new_secondhand :String, product_pic_list_size :Int ,product_pic_list : ArrayList<File>, product_spec_list : String, user_id: Int,  length : Int, width : Int, height : Int, shipment_method : String, longterm_stock_up : Int, product_status : String, product_spec_on : String) {
        val url = ApiConstants.API_HOST+"/product/save/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    Log.d("AddShopActivity", "返回資料 resStr：" + resStr)
                    val json = JSONObject(resStr)
                    Log.d("AddShopActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("產品新增成功!")) {

                         when(which_click){
                             "store"->{
                                 runOnUiThread {
                                     Toast.makeText(this@AddNewProductActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                                 }
                             }
                             "Launch"->{
                                 runOnUiThread {
                                     Toast.makeText(this@AddNewProductActivity, "產品上架成功!", Toast.LENGTH_SHORT).show()
                                 }
                             }

                         }
                        RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus("action"))
                        finish()

                    } else {
                        when(which_click){
                            "store"->{
                                runOnUiThread {
                                    Toast.makeText(this@AddNewProductActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                            "Launch"->{
                                runOnUiThread {
                                    Toast.makeText(this@AddNewProductActivity, "產品上架失敗!", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }

                    }
//                        initRecyclerView()


                } catch (e: JSONException) {
                    Log.d("dfsdjfdo", "JSONException: ${e.toString()}")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("dfsdjfdo", "IOException: ${e.toString()}")
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ProductAdd(url, shop_id, product_category_id, product_sub_category_id, product_title, quantity, product_description, product_price, shipping_fee, weight, new_secondhand, product_pic_list_size, product_pic_list, product_spec_list, user_id,  length, width, height, shipment_method,  longterm_stock_up, product_status, product_spec_on)
    }


    override fun onBackPressed() {

        StoreOrNotDialogFragment(activity).show(supportFragmentManager, "MyCustomFragment")
    }


    private fun getShopLogisticsList() {
        var url = ApiConstants.API_HOST + "/shop/" + MMKV_shop_id + "/shipmentSettings/get/"

        val web = Web(object : WebListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                mutableList_itemShipingFare.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("LogisticListActivity", "返回資料 resStr：" + resStr)
                    Log.d("LogisticListActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")

                        if(translations.length().toString() != ""){
                            MMKV.mmkvWithID("addPro").putString("fare_datas_size", translations.length().toString())
                        }else{
                            MMKV.mmkvWithID("addPro").putString("fare_datas_size", "0")
                        }

                        for (i in 0..translations.length()-1) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val itemShippingFare: ItemShippingFare =
                                Gson().fromJson(jsonObject.toString(), ItemShippingFare::class.java)

                            val jsonTutList_mutableList_itemShipingFare: String = GsonProvider.gson.toJson(ItemShippingFare(itemShippingFare.shipment_desc, itemShippingFare.price, itemShippingFare.onoff, MMKV_shop_id))
                            MMKV.mmkvWithID("addPro").putString("value_fare_item${i}", jsonTutList_mutableList_itemShipingFare)
                            Log.d("LogisticList",i.toString() )
                            Log.d("LogisticList",ItemShippingFare(itemShippingFare.shipment_desc, itemShippingFare.price, itemShippingFare.onoff, MMKV_shop_id).toString() )
                            //取出所有Fare Item(拿掉btn_delete參數)
                            mutableList_itemShipingFare_certained.add(ItemShippingFare_Certained(itemShippingFare.shipment_desc, itemShippingFare.price, itemShippingFare.onoff, MMKV_shop_id))
                            var json_shippingItem_certained = GsonProvider.gson.toJson(
                                ItemShippingFare_Certained(itemShippingFare.shipment_desc, itemShippingFare.price, itemShippingFare.onoff, MMKV_shop_id)
                            )
                            MMKV.mmkvWithID("addPro").putString("value_fare_item_certained${i}",json_shippingItem_certained)

                        }

                        //將從API取出的資料以ItemShippingFare的形式存取並裝成mutableList_itemShipingFare_filtered
                        for (i in 0..translations.length()-1) {
                            if(mutableList_itemShipingFare_certained.get(i).onoff.equals("on")){
                                mutableList_itemShipingFare_filtered.add(ItemShippingFare(mutableList_itemShipingFare_certained.get(i).shipment_desc, mutableList_itemShipingFare_certained.get(i).price, mutableList_itemShipingFare_certained.get(i).onoff, MMKV_shop_id))

                            }
                        }
                        MMKV.mmkvWithID("addPro").putString("fare_datas_filtered_size", mutableList_itemShipingFare_filtered.size.toString())

                        //mutableList_itemShipingFare_filtered一個個項目裝進mmkv，避免mmkv filtered item ID錯亂，保持以流水號型式
                        for (i in 0..mutableList_itemShipingFare_filtered.size-1) {
                            var json_shippingItem = GsonProvider.gson.toJson(ItemShippingFare(mutableList_itemShipingFare_filtered.get(i).shipment_desc, mutableList_itemShipingFare_filtered.get(i).price, mutableList_itemShipingFare_filtered.get(i).onoff, MMKV_shop_id))
                            MMKV.mmkvWithID("addPro").putString("value_fare_item_filtered${i}",json_shippingItem)
                        }
                        //存完後清掉，避免後來的mutableList_itemShipingFare_filtered重複裝取
                        mutableList_itemShipingFare_filtered.clear()


                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

                        val jsonTutList_fare: String = gson.toJson(mutableList_itemShipingFare_certained)
                        Log.d("AddNewProductActivity", mutableList_itemShipingFare_certained.toString())
                        val jsonTutListPretty_fare: String = gsonPretty.toJson(mutableList_itemShipingFare_certained)
                        Log.d("AddNewProductActivity", mutableList_itemShipingFare_certained.toString())

                        MMKV.mmkvWithID("addPro").putString("jsonTutList_fare", jsonTutList_fare)


                        initProFareDatas()
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

}