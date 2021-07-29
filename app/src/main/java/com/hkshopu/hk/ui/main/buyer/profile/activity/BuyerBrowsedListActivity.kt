package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.ProductLikedBean

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerProfile_BrowsedAdapter
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class BuyerBrowsedListActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerBrowseBinding
    val keyword = ""

    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    private val adapter = BuyerProfile_BrowsedAdapter(currency, userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClick()
//        doGetBrowsedList(keyword)
    }

    private fun initView() {
        binding!!.etSearchKeyword.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val keyWord = binding!!.etSearchKeyword.text.toString()
                    if(keyWord.isNotEmpty()){
                        doGetBrowsedList(keyWord)
                    }else{
                        doGetBrowsedList("")
                    }
                    KeyboardUtil.hideKeyboard(v)
                    true
                }
                else -> false
            }
        }
        binding!!.ivMic.setOnClickListener {
            KeyboardUtil.showKeyboard(it)
        }
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
    }
    private fun initRecyclerView(){
        val layoutManager = GridLayoutManager(this@BuyerBrowsedListActivity,2)
        binding.recyclerviewCollect.layoutManager = layoutManager
        binding.recyclerviewCollect.adapter = adapter
//        adapter.itemClick = {
//
//        }
    }

    private fun doGetBrowsedList(keyword: String) {

        binding.progressBarBuyerBrowse.visibility = View.GONE
        binding.imgViewLoadingBackgroundBuyerBrowse.visibility = View.GONE

        var url = ApiConstants.API_HOST + "user_detail/user_browsed/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductLikedBean>()
                list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerBrowsedListActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerBrowsedListActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("BuyerBrowsedListActivity", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val productLikedBean: ProductLikedBean =
                                Gson().fromJson(jsonObject.toString(), ProductLikedBean::class.java)
                            list.add(productLikedBean)
                        }
                    }

                    runOnUiThread {
                        initRecyclerView()
                    }

                    if(list.size > 0){

                        runOnUiThread {
                            adapter.setData(list)
                            binding.progressBarBuyerBrowse.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerBrowse.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            adapter.setData(list)
                            binding.progressBarBuyerBrowse.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerBrowse.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetBrowsedList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerBrowse.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerBrowse.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetBrowsedList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerBrowse.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerBrowse.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetBrowsedList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerBrowse.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerBrowse.visibility = View.GONE
                }
            }
        })
        web.Do_GetBuyerRecordList(url, userId,keyword)
    }

}