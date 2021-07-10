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

class StoreRankingTopFragment : Fragment() {

    companion object {
        fun newInstance(): StoreRankingTopFragment {
            val args = Bundle()
            val fragment = StoreRankingTopFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()

    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var layout_empty_result: LinearLayout
    lateinit var topStore :RecyclerView
    lateinit var progressBar: ProgressBar
    private val adapter = StoreRecommendAdapter()
    var max_seq = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_hotsales_store, container, false)
        val activity: StoreRecommendActivity? = activity as StoreRecommendActivity?

        val mode = "top_sale"
        var url = ApiConstants.API_HOST+"shop/get_shop_analytics_in_pages/"
        topStore = v.find<RecyclerView>(R.id.recyclerview_hotsales_store)

        progressBar = v.find<ProgressBar>(R.id.progressBar_top_sales)
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
            val mode = "top_sale"
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
        topStore.layoutManager = layoutManager

        topStore.adapter = adapter
        adapter.itemClick = {
            val bundle = Bundle()
            bundle.putString("shopId",it.toString())
            bundle.putString("userId", userId)
            val intent = Intent(requireActivity(), ShopPreviewActivity::class.java)
            intent.putExtra("bundle",bundle)
            requireActivity().startActivity(intent)
        }

    }

    private fun getStoreOverAll(url: String,userId:String,mode:String,max_seq:Int) {
        Log.d("StoreRankingAll", "資料 url：" + url)
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
                        Log.d("StoreRankingTop", "返回資料 jsonArray：" + jsonArray.toString())

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
                    Log.d("errormessage", "getProductOverAll: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getProductOverAll: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getProductOverAll: ErrorResponse：" + ErrorResponse.toString())
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
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getProductOverAllMore: JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getProductOverAllMore: IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getProductOverAllMore: ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Do_GetShopRecommend(url,userId,mode,max_seq)
    }

    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
    }

}