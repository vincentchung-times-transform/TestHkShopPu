package com.HKSHOPU.hk.ui.main.advertisement.activity

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.util.Pair
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshKeywordAd
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.facebook.FacebookSdk
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//import kotlinx.android.synthetic.main.activity_main.*

class AddEditStoreRecommendedAdvertisementActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditStoreRecommendedAdvertisementBinding
    var bundle: Bundle = Bundle()
    var ad_category = "recommended"
    var shop_id = ""
    var ad_id = ""
    var MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var ad_type = "shop"
    var budget_type = ""
    var budget_amount = ""
    var ad_period_type = ""
    var start_datetime = ""
    var end_datetime = ""
    var details = ""

    // mode: add/edit
    var mode = ""

    var itemKeywordAd = ItemKeywordAd()

    //    user_id (log in user_id)
//    ad_type: [ product | shop ]
//    budget_type: [ unlimit | total | day ]
//    budget_amount: Non-negative Int
//    ad_period_type: [ unlimit | custom ]
//    start_datetime: YYYY-MM-DD hh:mm:ss
//    end_datetime: YYYY-MM-DD hh:mm:ss
//    details ( JSON ){
//        shop_id,
//        product_id,
//        keyword,
//        bid
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditStoreRecommendedAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.getBundleExtra("bundle")!!
        shop_id = bundle!!.getString("shopId").toString()
        mode = bundle!!.getString("mode").toString()

        getShopInfo(shop_id)
        initView(mode)
        initVM()
    }

    fun initView(mode:String){
        binding.layoutBudgetSetYourSelf.visibility = View.GONE
        binding.layoutAdTimeSetYourSelf.visibility = View.GONE

        when(mode){
            "add"->{
                var title = getText(com.HKSHOPU.hk.R.string.add_store_recommended_advertisement)
                binding.tvTitle.setText(title)
                binding.btnRelease.visibility = View.VISIBLE
                binding.layoutBottomButtons.visibility = View.GONE
            }
            "edit"->{
                var title = getText(com.HKSHOPU.hk.R.string.edit_store_recommended_advertisement)
                ad_id = bundle!!.getString("adId").toString()
                binding.tvTitle.setText(title)
                binding.btnRelease.visibility = View.GONE
                binding.layoutBottomButtons.visibility = View.VISIBLE

                getUpdateAdSetting(ad_id)
            }
        }

        initSpinner()
        initEditText()
        initClick()
    }

    fun  initEditText(){
//        binding.etBudgetSetYourself.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
        binding.etBudgetSetYourself.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                KeyboardUtil.hideKeyboard(v)
            }
        })

        binding.etBudgetSetYourself.doAfterTextChanged {
            budget_amount = binding.etBudgetSetYourself.text.toString()
            ReleaseBtnEnable()
        }
        binding.etBudgetSetYourself.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.etBudgetSetYourself.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.etBudgetSetYourself)
                    true
                }
                else -> false
            }
        }
        binding.etBudgetSetYourself.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.etBudgetSetYourself.clearFocus()
                true
            } else {
                false
            }
        }

        binding.etBit.isSingleLine = true

        //        binding.etBit.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
        binding.etBit.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                KeyboardUtil.hideKeyboard(v)
            }
        })

        binding.etBit.doAfterTextChanged {
            itemKeywordAd.bid = binding.etBit.text.toString()

            var queue_recommended__confirmed: Queue<ItemKeywordAd> = LinkedList<ItemKeywordAd>()
            queue_recommended__confirmed.add(itemKeywordAd)
            queue_recommended__confirmed.forEach { item ->
                item.shop_id = shop_id
            }
            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
            val jsonTutList_queue_recommended_confirmed: String = gson.toJson(queue_recommended__confirmed)
            Log.d("jsonTutList_queue_recommended_confirmed", jsonTutList_queue_recommended_confirmed.toString())
            val jsonTutListPretty_queue_recommended_confirmed: String = gsonPretty.toJson(queue_recommended__confirmed)
            Log.d("jsonTutList_queue_recommended_confirmed", jsonTutListPretty_queue_recommended_confirmed.toString())
            details = jsonTutList_queue_recommended_confirmed

            if(queue_recommended__confirmed.size==0){
                details = ""
            }

            ReleaseBtnEnable()

            if(binding.etBit.text.toString().isNullOrEmpty()){
                binding.tvHigherBiddingAd.setText("")
            }else{
                getBidRanking(ad_category, ad_type, "", binding.etBit.text.toString())
            }

        }
        binding.etBit.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    binding.etBit.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.etBit)
                    true
                }
                else -> false
            }
        }
        binding.etBit.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.etBit.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun initVM() {

    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnReturn.setOnClickListener {
            finish()
        }
        binding.btnKnowMore.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        //預算
        binding.cbBudgetUnlimited.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cbBudgetSetItYourself.isChecked = false
                binding.layoutBudgetSetYourSelf.visibility = View.GONE
                budget_type = "unlimit"
                binding.cbBudgetUnlimited.isClickable = false
                binding.cbBudgetSetItYourself.isClickable = true
            }

            ReleaseBtnEnable()
        }
        binding.cbBudgetSetItYourself.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cbBudgetUnlimited.isChecked = false
                binding.layoutBudgetSetYourSelf.visibility = View.VISIBLE
                budget_type = "total"//預設自行設定選項"總預算"
                binding.cbBudgetSetItYourself.isClickable = false
                binding.cbBudgetUnlimited.isClickable = true
            }

            ReleaseBtnEnable()
        }
        //廣告
        binding.cbAdTimeUnlimited.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cbAdDateSetItYourself.isChecked = false
                binding.layoutAdTimeSetYourSelf.visibility = View.GONE
                ad_period_type = "unlimit"
                binding.cbAdTimeUnlimited.isClickable = false
                binding.cbAdDateSetItYourself.isClickable = true
            }

            ReleaseBtnEnable()
        }
        binding.cbAdDateSetItYourself.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cbAdTimeUnlimited.isChecked = false
                binding.layoutAdTimeSetYourSelf.visibility = View.VISIBLE
                ad_period_type = "custom"
                binding.cbAdDateSetItYourself.isClickable = false
                binding.cbAdTimeUnlimited.isClickable = true
            }

            ReleaseBtnEnable()
        }
        binding.layoutAdTimeSetYourSelf.setOnClickListener {
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"))

            calendar.timeInMillis = today
            calendar[Calendar.MONTH] = Calendar.JANUARY
            val janThisYear = calendar.timeInMillis

            calendar.timeInMillis = today
            calendar[Calendar.MONTH] = Calendar.DECEMBER
            val decThisYear = calendar.timeInMillis

            // Build constraints.
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setStart(janThisYear)
                    .setEnd(decThisYear)

            var picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setCalendarConstraints(constraintsBuilder.build())
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()

            picker.addOnPositiveButtonClickListener {
                // Respond to positive button click.
                Log.d("DatePicker Activity", "Date String = ${picker.headerText}::  Date epoch values::${StampToDate(it.first!!, Locale.TAIWAN)}:: to :: ${StampToDate(it.second!!,Locale.TAIWAN)}")
                binding.tvDate.setText("${StampToDate(it.first!!, Locale.TAIWAN)} - ${StampToDate(it.second!!,Locale.TAIWAN)}")
                start_datetime = StampToDateForDB(it.first!!, Locale.TAIWAN).toString()
                end_datetime = StampToDateForDB(it.second!!,Locale.TAIWAN).toString()

                ReleaseBtnEnable()
            }
            picker.addOnNegativeButtonClickListener {
                // Respond to negative button click.
                ReleaseBtnEnable()
            }
            picker.addOnCancelListener {
                // Respond to cancel button click.
                ReleaseBtnEnable()
            }
            picker.addOnDismissListener {
                // Respond to dismiss events.
                ReleaseBtnEnable()
            }

            picker.show(supportFragmentManager, "tag")
        }

        binding.btnRelease.setOnClickListener {
            if(ReleaseBtnEnable()){
                doCreateRecommendAd(MMKV_user_id, ad_type, budget_type, budget_amount, ad_period_type, start_datetime, end_datetime, details)
            }else{
                Toast.makeText(this, "尚有欄位未填", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOnShelf.setOnClickListener {
            if(ReleaseBtnEnable()){
                doUpdateKeywordAd(ad_id, ad_type, budget_type, budget_amount, ad_period_type, start_datetime, end_datetime, details)
            }else{
                Toast.makeText(this, "尚有欄位未填", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnDelete.setOnClickListener {
            doDeleteAd(ad_id)
        }
    }

    fun StampToDate(time: Long, locale: Locale): String {
        // 進來的time以秒為單位，Date輸入為毫秒為單位，要注意
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", locale)

        return simpleDateFormat.format(Date(time))
    }
    fun StampToDateForDB(time: Long, locale: Locale): String {
        // 進來的time以秒為單位，Date輸入為毫秒為單位，要注意
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)

        return simpleDateFormat.format(Date(time))
    }

    fun initSpinner(){
        //    budget_type: [ unlimit | total | day ]
        var arrayList_budgetMethod : ArrayList<BudgetTypeBean> = arrayListOf()
        var budgetTypeBean_day = BudgetTypeBean()
        budgetTypeBean_day.type_c = "總預算"
        budgetTypeBean_day.type_e = "total"
        arrayList_budgetMethod.add(budgetTypeBean_day)
        var budgetTypeBean_total = BudgetTypeBean()
        budgetTypeBean_total.type_c = "每日預算"
        budgetTypeBean_total.type_e = "day"
        arrayList_budgetMethod.add(budgetTypeBean_total)

        val payment_list: ArrayList<String> = arrayListOf<String>()
        for (i in 0 until arrayList_budgetMethod.size) {
            payment_list.add(arrayList_budgetMethod.get(i).type_c)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            FacebookSdk.getApplicationContext(),
            R.layout.simple_spinner_dropdown_item,
            payment_list
        )

        runOnUiThread {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spBudgetMethod.setAdapter(adapter)
            binding.spBudgetMethod.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    budget_type = arrayList_budgetMethod.get(position).type_e
//                    Toast.makeText(this@AddEditProductKeywordAdvertisementActivity, """已選取${budget_type}""", Toast.LENGTH_SHORT).show()
                    ReleaseBtnEnable()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    //    budget_type: [ unlimit | total | day ]
    //    ad_period_type: [ unlimit | custom ]
    fun ReleaseBtnEnable(): Boolean {
        Log.d("ReleaseBtnEnable", "ad_type: ${ad_type}\n " +
                "budget_type: ${budget_type} ; budget_amount: ${budget_amount}\n" +
                "ad_period_type: ${ad_period_type}\n" +
                "start_datetime: ${start_datetime} ; end_datetime: ${end_datetime}\n " +
                "details: ${details}")
        if(shop_id.isNotEmpty()
            && ad_type.isNotEmpty()
            && budget_type.isNotEmpty()
            && (budget_type.equals("unlimit") || (budget_type.equals("total") && budget_amount.isNotEmpty()) || (budget_type.equals("day") && budget_amount.isNotEmpty()))
            && ad_period_type.isNotEmpty()
            && (ad_period_type.equals("unlimit") || (ad_period_type.equals("custom") && (start_datetime.isNotEmpty() && end_datetime.isNotEmpty())))
            && details.isNotEmpty()){
            binding.btnRelease.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_40dp_hkcolor)
            return true
        }else{
            binding.btnRelease.setBackgroundResource(com.HKSHOPU.hk.R.drawable.customborder_40dp_gray_8e8e93)
            return false
        }
    }

    fun doCreateRecommendAd(
        user_id: String,
        ad_type: String,
        budget_type: String,
        budget_amount: String,
        ad_period_type: String,
        start_datetime: String,
        end_datetime: String,
        details: String)
    {
        val url = ApiConstants.API_HOST+"user/${user_id}/adSetting/recommend/${ad_type}/"

        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doCreateKeywordAd", "返回資料 resStr：" + resStr)
                    Log.d("doCreateKeywordAd", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("新增成功")) {
                        runOnUiThread {
                            Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                        Log.d("doCreateKeywordAd", "ret_val: ${ret_val.toString()}")

                        RxBus.getInstance().post(EventRefreshKeywordAd())
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                        Log.d("doCreateKeywordAd", "ret_val: ${ret_val.toString()}")
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doCreateKeywordAd", "JSONException: ${e.toString()}")
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doCreateKeywordAd", "IOException: ${e.toString()}")
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("doCreateKeywordAd", "ErrorResponse: ${ErrorResponse.toString()}")
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.doCreateKeywordAd(
            url,
            user_id,
            budget_type,
            budget_amount,
            ad_period_type,
            start_datetime,
            end_datetime,
            details)
    }

    private fun getShopInfo(shop_id: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST + "shop/" + shop_id + "/show/"
        val web = Web(object : WebListener {

            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var list = ArrayList<ShopInfoBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("getShopInfo", "返回資料 resStr：" + resStr)
                    Log.d("getShopInfo", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("已找到商店資料!")) {
                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("getShopInfo", "返回資料 Object：" + jsonObject.toString())
                        val shopInfoBean: ShopInfoBean =
                            Gson().fromJson(jsonObject.toString(), ShopInfoBean::class.java)
                        list.add(shopInfoBean)

                        runOnUiThread {
                            binding!!.ivShopIcon.loadNovelCover(list[0].shop_icon)
                            binding.tvShopName.text = list[0].shop_title
                            binding!!.tvShopAvergeRating.text = list[0].rating.toString()
                        }


                        if(list[0].rating>4.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_half)
                            }
                        }else if (list[0].rating>3.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>3.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_half)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>2.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>2.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_half)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>1.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>1.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_half)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>0.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else if(list[0].rating>0.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star_half)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }else{
                            runOnUiThread {
                                binding.ivStar01.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar02.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar03.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(com.HKSHOPU.hk.R.mipmap.ic_star)
                            }
                        }
                    }

                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                    Log.d(
                        "getShopInfo_errorMessage",
                        "JSONException：" + e.toString()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                    Log.d(
                        "getShopInfo_errorMessage",
                        "IOException：" + e.toString()
                    )
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
                Log.d(
                    "getShopInfo_errorMessage",
                    "ErrorResponse：" + ErrorResponse.toString()
                )
            }
        })
        web.Get_Data(url)
    }

    private fun getBidRanking(ad_category:String, ad_type:String, keyword:String, bid:String) {
        Log.d("getBidRanking", "ad_category: ${ad_category}, ad_type: ${ad_type}, keyword: ${keyword}, bid: ${bid}")

        // ad_category: keyword | recommend | store
        // ad_type: shop | product
        var url = ApiConstants.API_HOST + "user/adSettingRanking/${ad_category}/${ad_type}/?keyword=${keyword}&bid=${bid}"

        val web = Web(object : WebListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getBidRanking", "返回資料 resStr：" + resStr)
                    Log.d("getBidRanking", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val translations: JSONObject = json.getJSONObject("data")
                        val ranking: String = translations.getString("ranking")

                        runOnUiThread {
                            binding.tvHigherBiddingAd.setText(ranking.toString())
                        }

                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        Log.d("getBidRanking", "JSONException: ${e.toString()}")
                    }


                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Log.d("getBidRanking", "IOException: ${e.toString()}")
                    }


                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Log.d("getBidRanking", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.Do_GetBidRanking(url)
    }

    private fun getUpdateAdSetting(ad_header_id:String) {
        Log.d("getGetUpdateAdSetting", "ad_header_id: ${ad_header_id.toString()}")
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val url = ApiConstants.API_HOST+"user/${ad_header_id}/adInfo/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getGetUpdateAdSetting", "返回資料 resStr：${resStr.toString()}")
                    Log.d("getGetUpdateAdSetting", "返回資料 ret_val：${ret_val.toString()}")

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("getGetUpdateAdSetting", "返回資料 jsonObject：" + jsonObject.toString())

                        val advertisementDetailedInfoBean: AdvertisementDetailedInfoBean =
                            Gson().fromJson(jsonObject.toString(), AdvertisementDetailedInfoBean::class.java)

                        runOnUiThread {

                            when(advertisementDetailedInfoBean.budget_type){
                                "unlimit"->{
                                    budget_type = "unlimit"
                                    binding.cbBudgetUnlimited.isChecked = true
                                    binding.cbBudgetSetItYourself.isChecked = false
                                    binding.layoutBudgetSetYourSelf.visibility = View.GONE
                                    budget_amount = ""
                                }
                                "total"->{
                                    budget_type = "total"
                                    binding.cbBudgetUnlimited.isChecked = false
                                    binding.cbBudgetSetItYourself.isChecked = true
                                    binding.layoutBudgetSetYourSelf.visibility = View.VISIBLE
                                    binding.spBudgetMethod.setSelection(0)
                                    binding.etBudgetSetYourself.setText(advertisementDetailedInfoBean.budget_amount)
                                    budget_amount = advertisementDetailedInfoBean.budget_amount.toString()
                                }
                                "day"->{
                                    budget_type = "day"
                                    binding.cbBudgetUnlimited.isChecked = false
                                    binding.cbBudgetSetItYourself.isChecked = true
                                    binding.layoutBudgetSetYourSelf.visibility = View.VISIBLE
                                    binding.spBudgetMethod.setSelection(1)
                                    binding.etBudgetSetYourself.setText(advertisementDetailedInfoBean.budget_amount)
                                    budget_amount = advertisementDetailedInfoBean.budget_amount.toString()
                                }
                            }

                            when(advertisementDetailedInfoBean.ad_period_type){
                                "unlimit"->{
                                    ad_period_type="unlimit"
                                    binding.cbAdTimeUnlimited.isChecked = true
                                    binding.cbAdDateSetItYourself.isChecked = false
                                    start_datetime=""
                                    end_datetime=""
                                }
                                "custom"->{
                                    ad_period_type="custom"
                                    binding.cbAdDateSetItYourself.isChecked = true
                                    binding.cbAdTimeUnlimited.isChecked = false
                                    binding.tvDate.setText("${trans_date_format(advertisementDetailedInfoBean.start_datetime)}-${trans_date_format(advertisementDetailedInfoBean.end_datetime)}")
                                    start_datetime=trans_date_format_for_db(advertisementDetailedInfoBean.start_datetime).toString()
                                    end_datetime=trans_date_format_for_db(advertisementDetailedInfoBean.end_datetime).toString()
                                }
                            }

                            binding.etBit.setText(advertisementDetailedInfoBean.bid.toString())
                            itemKeywordAd.bid = advertisementDetailedInfoBean.bid.toString()

                            var queue_recommended__confirmed: Queue<ItemKeywordAd> = LinkedList<ItemKeywordAd>()
                            queue_recommended__confirmed.add(itemKeywordAd)
                            queue_recommended__confirmed.forEach { item ->
                                item.shop_id = shop_id
                            }
                            val gson = Gson()
                            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                            val jsonTutList_queue_recommended_confirmed: String = gson.toJson(queue_recommended__confirmed)
                            Log.d("jsonTutList_queue_recommended_confirmed", jsonTutList_queue_recommended_confirmed.toString())
                            val jsonTutListPretty_queue_recommended_confirmed: String = gsonPretty.toJson(queue_recommended__confirmed)
                            Log.d("jsonTutList_queue_recommended_confirmed", jsonTutListPretty_queue_recommended_confirmed.toString())
                            details = jsonTutList_queue_recommended_confirmed

                            if(queue_recommended__confirmed.size==0){
                                details = ""
                            }

                            ReleaseBtnEnable()
                        }

                    }

                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getOnShelfProducts: JSONException：" + e.toString())
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getOnShelfProducts: IOException：" + e.toString())
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getOnShelfProducts: ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    fun trans_date_format(date:String): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date: Date = format.parse(date)
        var date_transformed = SimpleDateFormat("dd/MM/yyyy").format(date)
        return date_transformed
    }
    fun trans_date_format_for_db(date:String): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date: Date = format.parse(date)
        var date_transformed = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        return date_transformed
    }

    fun doUpdateKeywordAd(
        ad_setting_header_id: String,
        ad_type: String,
        budget_type: String,
        budget_amount: String,
        ad_period_type: String,
        start_datetime: String,
        end_datetime: String,
        details: String)
    {
        val url = ApiConstants.API_HOST+"user/updateAdSetting/recommend/${ad_type}/${ad_setting_header_id}/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doUpdateKeywordAd", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateKeywordAd", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("更新成功")) {
                        runOnUiThread {
                            Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                        Log.d("doUpdateKeywordAd", "ret_val: ${ret_val.toString()}")

                        RxBus.getInstance().post(EventRefreshKeywordAd())
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                        Log.d("doUpdateKeywordAd", "ret_val: ${ret_val.toString()}")
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateKeywordAd", "JSONException: ${e.toString()}")

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateKeywordAd", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("doUpdateKeywordAd", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.doCreateKeywordAd(
            url,
            ad_setting_header_id,
            budget_type,
            budget_amount,
            ad_period_type,
            start_datetime,
            end_datetime,
            details)
    }
    fun doDeleteAd(
        ad_header_id: String,
    )
    {
        val url = ApiConstants.API_HOST+"user/${ad_header_id}/adInfo/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doUpdateAdStatus", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateAdStatus", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("刪除廣告成功")) {

                        runOnUiThread {
                            Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                        Log.d("doUpdateAdStatus", "ret_val: ${ret_val.toString()}")

                        RxBus.getInstance().post(EventRefreshKeywordAd())
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                        Log.d("doUpdateAdStatus", "ret_val: ${ret_val.toString()}")
                    }

                } catch (e: JSONException) {
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateAdStatus", "JSONException: ${e.toString()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateAdStatus", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                com.paypal.pyplcheckout.sca.runOnUiThread {
                    Toast.makeText(this@AddEditStoreRecommendedAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("doUpdateAdStatus", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.Delete_Data(
            url
        )
    }




}