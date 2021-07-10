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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventToProductSearch
import com.HKSHOPU.hk.data.bean.ProductSearchBean

import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.adapter.ProductSearchAdapter
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
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
import java.util.*
import kotlin.collections.ArrayList

class RankingLatestSearchFragment : Fragment() {

    companion object {
        fun newInstance(): RankingLatestSearchFragment {
            val args = Bundle()
            val fragment = RankingLatestSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var layout_empty_result: LinearLayout
    lateinit var latestProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = ProductSearchAdapter(currency)
    var keyword = ""
    var categoryId = ""
    var sub_categoryId = ""
    var max_seq = 0
    var userId= ""
    var mode = "new"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_latest, container, false)

        progressBar = v.find<ProgressBar>(R.id.progressBar_product_new)
        progressBar.visibility = View.VISIBLE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE

        latestProduct = v.find<RecyclerView>(R.id.recyclerview_ranklatest)

        initView()
        initRefresh()
        initEvent()
        return v
    }

    private fun initView(){
        progressBar.visibility = View.VISIBLE

        keyword = MMKV.mmkvWithID("http").getString("keyword","").toString()
        categoryId = MMKV.mmkvWithID("http").getString("product_category_id","").toString()
        sub_categoryId = MMKV.mmkvWithID("http").getString("sub_product_category_id","").toString()
        Log.d("RankingAllSearch", "資料 categoryId：" + categoryId.toString() + " ; sub_categoryId : ${sub_categoryId.toString()}")

        initRecyclerView()
        val url = ApiConstants.API_HOST+"/product/"+mode +"/product_analytics_pages_keyword/"
        getSearchProductOverAll(url, userId.toString(), categoryId.toString(), sub_categoryId.toString(), max_seq.toString(), keyword!!)
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

            var url = ApiConstants.API_HOST+"product/"+mode+"/product_analytics_pages_keyword"
            max_seq ++
            if(keyword.isNotEmpty()){
                categoryId = ""
            }else{
                keyword =""
            }

            getSearchProductOverAllMore(url, userId.toString(), categoryId.toString(), sub_categoryId.toString(), max_seq.toString(), keyword)
//            VM.loadMore(this)
        }
    }

    private fun initRecyclerView(){


        val layoutManager = GridLayoutManager(requireActivity(),2)
        latestProduct.layoutManager = layoutManager

        latestProduct.adapter = adapter
//        adapter.itemClick = {
//            val intent = Intent(requireActivity(), ProductDetailedPageBuyerViewActivity::class.java)
//            var bundle = Bundle()
//            bundle.putString("product_id", it)
//            intent.putExtra("bundle_product_id", bundle)
//            requireActivity().startActivity(intent)
//        }

    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventToProductSearch -> {

                        progressBar.visibility = View.VISIBLE
                        Log.d("RankingLatestSearchFragment", "返回資料 Event：" + keyword)

                        keyword = MMKV.mmkvWithID("http").getString("keyword","").toString()
                        categoryId = MMKV.mmkvWithID("http").getString("product_category_id","").toString()
                        sub_categoryId = MMKV.mmkvWithID("http").getString("sub_product_category_id","").toString()
                        Log.d("RankingAllSearch", "資料 categoryId：" + categoryId.toString() + " ; sub_categoryId : ${sub_categoryId.toString()}")

                        val url = ApiConstants.API_HOST+"/product/"+mode +"/product_analytics_pages_keyword/"
                        getSearchProductOverAll(url, userId.toString(), categoryId.toString(), sub_categoryId.toString(), max_seq.toString(), keyword!!)
                    }
                }

            })
    }

    private fun getSearchProductOverAll(url: String, user_id:String, category_id:String, sub_category_id:String, max_seq:String, keyword:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductSearchBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("RankingLatestSearchFragment", "返回資料 resStr：" + resStr)
                    Log.d("RankingLatestSearchFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        val jsonArray:JSONArray = jsonObject.getJSONArray("productsList")
                        Log.d("RankingLatestSearchFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        if(jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                val productSearchBean: ProductSearchBean =
                                    Gson().fromJson(jsonObject.toString(), ProductSearchBean::class.java)
                                list.add(productSearchBean)
                            }
                        }

                    }

                    if(list.size > 0){
                        if(userId.isEmpty()){
                            userId = list[0].user_id
                        }

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
                    Log.d("errormessage", "getSearchProductOverAll: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getSearchProductOverAll: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE

                        layout_empty_result.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getSearchProductOverAll: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE

                    layout_empty_result.visibility = View.VISIBLE
                    refreshLayout.visibility = View.GONE
                }
            }
        })
        web.Do_GetSearchProduct(url,user_id,category_id,sub_category_id,max_seq,keyword)
    }

    private fun getSearchProductOverAllMore(url: String, user_id:String, category_id:String ,sub_category_id:String, max_seq:String, keyword:String) {
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductSearchBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val productSearchBean: ProductSearchBean =
                                Gson().fromJson(jsonObject.toString(), ProductSearchBean::class.java)
                            list.add(productSearchBean)
                        }
                        refreshLayout.finishLoadMore()
                    }

                    if(list.size > 0){

                        requireActivity().runOnUiThread {
                            adapter.add(list)

                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getSearchProductOverAllMore: JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getSearchProductOverAllMore: IOException：" + e.toString())
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getSearchProductOverAllMore: ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Do_GetSearchProduct(url,user_id,category_id,sub_category_id,max_seq,keyword)
    }

}