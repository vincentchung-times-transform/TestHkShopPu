package com.HKSHOPU.hk.ui.main.homepage.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
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
import com.HKSHOPU.hk.ui.main.homepage.activity.SearchActivity
import com.HKSHOPU.hk.ui.main.homepage.adapter.ProductSearchAdapter
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
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

class RankingAllSearchFragment : Fragment() {

    companion object {
        fun newInstance(): RankingAllSearchFragment {
            val args = Bundle()
            val fragment = RankingAllSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var backLayout: RelativeLayout
    lateinit var layout_empty_result: LinearLayout
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = ProductSearchAdapter(currency)
    var keyword = ""
    var categoryId = ""
    var sub_categoryId = ""
    var max_seq = 0
    var userId= ""
    var mode = "overall"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_all, container, false)
        val activity: SearchActivity? = activity as SearchActivity?
        userId = activity!!.getUserId().toString()
        backLayout = v.find<RelativeLayout>(R.id.layout_product_all)

        progressBar = v.find<ProgressBar>(R.id.progressBar_product_all)
        progressBar.visibility = View.VISIBLE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE

        allProduct = v.find<RecyclerView>(R.id.recyclerview_rankall)

        initView()
        initRefresh()
        initEvent()
        return v
    }

    private fun initView(){

        progressBar.isVisible = true
        backLayout.setOnClickListener {
            Log.d("RankingAllSearch", "backLayout Clicked：")
        }

        keyword = MMKV.mmkvWithID("http")!!.getString("keyword","").toString()
        categoryId = MMKV.mmkvWithID("http"!!)!!.getString("product_category_id","").toString()
        sub_categoryId = MMKV.mmkvWithID("http").getString("sub_product_category_id","").toString()
        Log.d("RankingAllSearch", "資料 categoryId：" + categoryId.toString() + " ; sub_categoryId : ${sub_categoryId.toString()}")

        initRecyclerView()
        val url = ApiConstants.API_HOST+"/product/"+mode +"/product_analytics_pages_keyword/"
        getSearchProductOverAll(url, userId, categoryId, sub_categoryId.toString(), max_seq.toString(), keyword!!)

    }
    private fun initRefresh() {

        refreshLayout.setOnRefreshListener {
//            VM.loadShop(this)
            refreshLayout.finishRefresh()
        }
        refreshLayout.setOnLoadMoreListener {
            Log.d("RankingAllSearch", "資料 keyword：" + keyword)
            Log.d("RankingAllSearch", "資料 categoryId：" + categoryId)
            var url = ApiConstants.API_HOST+"product/"+mode+"/product_analytics_pages/"
            max_seq ++
//            if(keyword.isNotEmpty()){
//                categoryId = ""
//            }else{
//                keyword =""
//            }
            getSearchProductOverAllMore(url, userId, categoryId.toString(), sub_categoryId, max_seq.toString(), keyword)
//            VM.loadMore(this)
        }
    }

    private fun initRecyclerView(){

        val layoutManager = GridLayoutManager(requireActivity(),2)
        allProduct.layoutManager = layoutManager
        allProduct.adapter = adapter
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
                        progressBar.isVisible = true

                        keyword = MMKV.mmkvWithID("http")!!.getString("keyword","").toString()
                        categoryId = MMKV.mmkvWithID("http"!!)!!.getString("product_category_id","").toString()
                        sub_categoryId = MMKV.mmkvWithID("http").getString("sub_product_category_id","").toString()
                        Log.d("RankingAllSearch", "資料 categoryId：" + categoryId.toString() + " ; sub_categoryId : ${sub_categoryId.toString()}")

                        val url = ApiConstants.API_HOST+"/product/"+mode+"/product_analytics_pages_keyword/"

                        getSearchProductOverAll(url, userId.toString(), "", "", max_seq.toString(), keyword!!)
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
                    Log.d("RankingAllSearchFragment", "返回資料 resStr：" + resStr)
                    Log.d("RankingAllSearchFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        val jsonArray: JSONArray = jsonObject.getJSONArray("productsList")
                        Log.d("RankingAllSearchFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        if(jsonArray.length()>0){
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                val productSearchBean: ProductSearchBean =
                                    Gson().fromJson(
                                        jsonObject.toString(),
                                        ProductSearchBean::class.java
                                    )
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
                var resStr: String = ""
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

                        }

                        if (list.size > 0) {

                            requireActivity().runOnUiThread {
                                adapter.add(list)
                                refreshLayout.finishLoadMore()
                            }

                        }else{

                            requireActivity().runOnUiThread {
                                refreshLayout.finishLoadMore()
                            }

                        }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getSearchProductOverAll: JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getSearchProductOverAll: IOException：" + e.toString())
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getSearchProductOverAll: ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Do_GetSearchProduct(url,user_id,category_id,sub_category_id,max_seq,keyword)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("lifecycleForFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("lifecycleForFragment", "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lifecycleForFragment", "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("lifecycleForFragment", "onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d("lifecycleForFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycleForFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifecycleForFragment", "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d("lifecycleForFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("lifecycleForFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("lifecycleForFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("lifecycleForFragment", "onDetach")
    }
}