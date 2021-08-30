package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.StoreFollowBean

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerProfile_StoreFollowAdapter
import com.HKSHOPU.hk.ui.main.homepage.activity.ShopPreviewActivity
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class BuyerFollowListActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerFollowedBinding
    val keyword = ""
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    private val adapter = BuyerProfile_StoreFollowAdapter(userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerFollowedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClick()
//        doGetFollewList(keyword)
    }

    private fun initView() {
        binding!!.etSearchKeyword.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val keyWord = binding!!.etSearchKeyword.text.toString()
                    if(keyWord.isNotEmpty()){
                        doGetFollewList(keyWord)
                    }else{
                        doGetFollewList("")
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
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerviewFollow.layoutManager = layoutManager
        binding.recyclerviewFollow.adapter = adapter
        adapter.itemClick = {
            val bundle = Bundle()
            bundle.putString("shopId", it)
            bundle.putString("userId", userId)
            val intent = Intent(this, ShopPreviewActivity::class.java)
            intent.putExtra("bundle", bundle)
            this.startActivity(intent)
        }
    }
    private fun doGetFollewList(keyword: String) {
        binding.progressBarBuyerFollowed.visibility = View.GONE
        binding.imgViewLoadingBackgroundBuyerFollowed.visibility = View.GONE

        var url = ApiConstants.API_HOST + "user_detail/user_followed/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<StoreFollowBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerFollowListActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerFollowListActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("BuyerFollowListActivity", "返回資料 jsonArray：" + jsonArray.toString())
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val storeFollowBean: StoreFollowBean =
                                Gson().fromJson(jsonObject.toString(), StoreFollowBean::class.java)
                            list.add(storeFollowBean)
                        }
                    }

                    runOnUiThread {
                        initRecyclerView()
                    }

                    if(list.size > 0){
                        runOnUiThread {
                            adapter.setData(list)
                            binding.progressBarBuyerFollowed.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFollowed.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            adapter.setData(list)
                            binding.progressBarBuyerFollowed.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFollowed.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetFollewList_erorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFollowed.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFollowed.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetFollewList_erorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFollowed.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFollowed.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetFollewList_erorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerFollowed.visibility = View.VISIBLE
                    binding.imgViewLoadingBackgroundBuyerFollowed.visibility = View.VISIBLE
                }
            }
        })
        web.Do_GetBuyerRecordList(url, userId,keyword)
    }

}
