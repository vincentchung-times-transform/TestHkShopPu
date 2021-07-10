package com.HKSHOPU.hk.ui.main.homepage.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventToShopSearch

import com.HKSHOPU.hk.data.bean.ShopRecommendBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.activity.SearchActivity
import com.HKSHOPU.hk.ui.main.homepage.adapter.StoreRecommendAdapter
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopPreviewActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class StoreSearchLatestFragment : Fragment() {

    companion object {
        fun newInstance(): StoreSearchLatestFragment {
            val args = Bundle()
            val fragment = StoreSearchLatestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var layout_empty_result: LinearLayout
    lateinit var latestStore :RecyclerView
    lateinit var progressBar: ProgressBar
    private val adapter = StoreRecommendAdapter()
    var keyword = ""
    var categoryId = ""
    var sub_categoryId = ""
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var max_seq = 0
    val mode = "new"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_latest_store, container, false)
        val activity: SearchActivity? = activity as SearchActivity?

        progressBar = v.find<ProgressBar>(R.id.progressBar_latest_store)
        progressBar.visibility = View.VISIBLE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE

        keyword = MMKV.mmkvWithID("http").getString("keyword","").toString()
        categoryId = MMKV.mmkvWithID("http").getString("product_category_id","").toString()
        sub_categoryId = MMKV.mmkvWithID("http").getString("sub_product_category_id","").toString()
        Log.d("RankingAllSearch", "資料 categoryId：" + categoryId.toString() + " ; sub_categoryId : ${sub_categoryId.toString()}")

        latestStore = v.find<RecyclerView>(R.id.recyclerview_latest_store)

        initRecyclerView()

        var url = ApiConstants.API_HOST+"shop/get_shop_analytics_in_pages/"
        getSearchStoreOverAll(url, userId!!, mode, max_seq.toString(), categoryId, sub_categoryId, keyword!!)

        initView()
        initEvent()
        initRefresh()
        return v
    }

    private fun initView(){

    }
    private fun initRefresh() {
        refreshLayout.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        refreshLayout.setOnRefreshListener {
//            VM.loadShop(this)
            refreshLayout.finishRefresh()
        }
        refreshLayout.setOnLoadMoreListener {

            progressBar.visibility = View.VISIBLE

            val url = ApiConstants.API_HOST+"/shop/get_shop_analytics_with_keyword_in_pages/"
            max_seq++
            if(keyword.isNotEmpty()){
                categoryId = ""
            }else{
                keyword =""
            }
            getSearchStoreOverAllMore(
                url,
                userId,
                mode,
                max_seq.toString(),
                categoryId,
                sub_categoryId,
                keyword
            )
//            VM.loadMore(this)
        }
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventToShopSearch -> {

                        progressBar.visibility = View.VISIBLE

                        keyword = MMKV.mmkvWithID("http").getString("keyword","").toString()
                        categoryId = MMKV.mmkvWithID("http").getString("product_category_id","").toString()
                        sub_categoryId = MMKV.mmkvWithID("http").getString("sub_product_category_id","").toString()
                        Log.d("RankingAllSearch", "資料 categoryId：" + categoryId.toString() + " ; sub_categoryId : ${sub_categoryId.toString()}")

                        val url = ApiConstants.API_HOST+"/shop/get_shop_analytics_with_keyword_in_pages/"

                        getSearchStoreOverAll(url, userId!!, mode,max_seq.toString(), categoryId, sub_categoryId, keyword!!)
                    }
                }

            })
    }

    private fun initRecyclerView(){


        val layoutManager = LinearLayoutManager(requireActivity())
        latestStore.layoutManager = layoutManager

        latestStore.adapter = adapter
        adapter.itemClick = {
            val bundle = Bundle()
            bundle.putString("shopId",it.toString())
            bundle.putString("userId",userId)
            val intent = Intent(requireActivity(), ShopPreviewActivity::class.java)
            intent.putExtra("bundle",bundle)
            requireActivity().startActivity(intent)
        }

    }

    private fun getSearchStoreOverAll(url:String, userId:String, mode:String, max_seq:String, product_category_id:String, sub_categoryId:String, keyword:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopRecommendBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getSearchStoreOverAll", "返回資料 resStr：" + resStr)
                    Log.d("getSearchStoreOverAll", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getSearchStoreOverAll", "返回資料 jsonArray：" + jsonArray.toString())

                        if( jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                val shopRecommendBean: ShopRecommendBean =
                                    Gson().fromJson(jsonObject.toString(), ShopRecommendBean::class.java)
                                list.add(shopRecommendBean)
                            }
                        }

                    }

                    Log.d("getSearchStoreOverAll", "返回資料 list：" + list.toString())

                    if(list.size > 0){

                        activity!!.runOnUiThread {
                            adapter.setData(list)

                            progressBar.visibility = View.GONE

                            layout_empty_result.visibility = View.GONE
                            refreshLayout.visibility = View.VISIBLE
                        }
                    }else{
                        activity!!.runOnUiThread {
                            adapter.clear()

                            progressBar.visibility = View.GONE

                            layout_empty_result.visibility = View.VISIBLE
                            refreshLayout.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getSearchStoreOverAll: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getSearchStoreOverAll: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getSearchStoreOverAll: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE

                    layout_empty_result.visibility = View.VISIBLE
                    refreshLayout.visibility = View.GONE
                }
            }
        })
        web.Do_GetSearchStore(url, userId, mode, max_seq, product_category_id, sub_categoryId, keyword)
    }

    private fun getSearchStoreOverAllMore(url:String, userId:String, mode:String, max_seq:String, product_category_id:String, sub_categoryId:String, keyword:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopRecommendBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("StoreSearchAllFragment", "返回資料 resStr：" + resStr)
                    Log.d("StoreSearchAllFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        val jsonArray: JSONArray = jsonObject.getJSONArray("shops")
                        Log.d("StoreSearchAllFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val shopRecommendBean: ShopRecommendBean =
                                Gson().fromJson(jsonObject.toString(), ShopRecommendBean::class.java)
                            list.add(shopRecommendBean)
                        }

                    }

                    Log.d("RankingAllFragment", "返回資料 list：" + list.toString())

                    if(list.size > 0){

                        activity!!.runOnUiThread {
                            progressBar.visibility = View.GONE

                            adapter.add(list)
                            refreshLayout.finishLoadMore()
                        }
                    }else{
                        activity!!.runOnUiThread {
                            progressBar.visibility = View.GONE

                            refreshLayout.finishLoadMore()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getSearchStoreOverAllMore: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getSearchStoreOverAllMore: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getSearchStoreOverAllMore: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        })
        web.Do_GetSearchStore(url, userId, mode, max_seq, product_category_id, sub_categoryId, keyword)
    }
}