package com.HKSHOPU.hk.ui.main.homepage.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ShopRecommendBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.activity.StoreRecommendActivity
import com.HKSHOPU.hk.ui.main.homepage.adapter.StoreRecommendAdapter
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopPreviewActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class StoreRankingLatestFragment : Fragment() {

    companion object {
        fun newInstance(): StoreRankingLatestFragment {
            val args = Bundle()
            val fragment = StoreRankingLatestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var layout_empty_result: LinearLayout
    lateinit var latestStore :RecyclerView
    lateinit var progressBar: ProgressBar
    private val adapter = StoreRecommendAdapter()
    var max_seq = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_latest_store, container, false)
        val activity: StoreRecommendActivity? = activity as StoreRecommendActivity?

        val mode = "new"
        var url = ApiConstants.API_HOST+"shop/get_shop_analytics_in_pages/"
        latestStore = v.find<RecyclerView>(R.id.recyclerview_latest_store)

        progressBar = v.find<ProgressBar>(R.id.progressBar_all_store)
        progressBar.visibility = View.VISIBLE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE

        getStoreOverAll(url,userId!!,mode,max_seq)

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
            val activity: StoreRecommendActivity? = activity as StoreRecommendActivity?
            val userId: String? = activity!!.getUserId()
            val mode = "new"
            var url = ApiConstants.API_HOST+"shop/get_shop_analytics_in_pages/"
            max_seq ++
            getStoreOverAllMore(url,userId!!,mode,max_seq)
//            VM.loadMore(this)
        }
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(requireActivity(), Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
//                    is EventAddShopBriefSuccess -> {
//
//                    }
                }

            })
    }

    private fun initRecyclerView(){

        val layoutManager = LinearLayoutManager(requireActivity())
        latestStore.layoutManager = layoutManager

        latestStore.adapter = adapter
        adapter.itemClick = {
            val intent = Intent(requireActivity(), ShopPreviewActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId",it.toString())
            bundle.putString("userId", userId)
            intent.putExtra("bundle",bundle)
            requireActivity().startActivity(intent)
        }

    }

    private fun getStoreOverAll(url: String,userId:String,mode:String,max_seq:Int) {
        Log.d("StoreRankingLatest", "資料 url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopRecommendBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    Log.d("getStoreOverAll", "返回資料 resStr：${resStr.toString()}")
                    Log.d("getStoreOverAll", "返回資料 ret_val：${ret_val.toString()}")

                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("StoreRankingLatest", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val shopRecommendBean: ShopRecommendBean =
                                Gson().fromJson(jsonObject.toString(), ShopRecommendBean::class.java)
                            list.add(shopRecommendBean)
                        }
                    }


                    if(list.size > 0){
                        activity!!.runOnUiThread {
                            adapter.setData(list)
                            initRecyclerView()
                            progressBar.visibility = View.GONE

                            layout_empty_result.visibility = View.GONE
                            refreshLayout.visibility = View.VISIBLE
                        }
                    }else{
                        activity!!.runOnUiThread {
                            progressBar.visibility = View.GONE

                            layout_empty_result.visibility = View.VISIBLE
                            refreshLayout.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getStoreOverAll: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getStoreOverAll: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getStoreOverAll: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE

                    layout_empty_result.visibility = View.VISIBLE
                    refreshLayout.visibility = View.GONE
                }
            }
        })
        web.Do_GetShopRecommend(url,userId,mode,max_seq)
    }

    private fun getStoreOverAllMore(url: String,userId:String,mode:String,max_seq:Int) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopRecommendBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    Log.d("getStoreOverAllMore", "返回資料 resStr：${resStr.toString()}")
                    Log.d("getStoreOverAllMore", "返回資料 ret_val：${ret_val.toString()}")

                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("StoreRankingAll", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val shopRecommendBean: ShopRecommendBean =
                                Gson().fromJson(jsonObject.toString(), ShopRecommendBean::class.java)
                            list.add(shopRecommendBean)
                        }
                        refreshLayout.finishLoadMore()
                    }

                    if(list.size > 0){
                        activity!!.runOnUiThread {
                            adapter.add(list)
//                            initRecyclerView()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getStoreOverAllMore: JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getStoreOverAllMore: IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getStoreOverAllMore: ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Do_GetShopRecommend(url,userId,mode,max_seq)
    }

}