package com.HKSHOPU.hk.ui.main.homepage.fragment

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.TopProductBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.adapter.TopProductAdapter
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class RankingAllTopFragment : Fragment() {

    companion object {
        fun newInstance(): RankingAllTopFragment {
            val args = Bundle()
            val fragment = RankingAllTopFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var layout_empty_result: LinearLayout
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = TopProductAdapter(currency)
    var max_seq = 0
    var userId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_all, container, false)
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        allProduct = v.find<RecyclerView>(R.id.recyclerview_rankall)

        progressBar = v.find<ProgressBar>(R.id.progressBar_product_all)
        progressBar.visibility = View.VISIBLE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST+"product/"+"overall"+"/product_analytics_pages/"
        getProductOverAll(url,userId,max_seq)
        initView()
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

            val mode = "overall"
            var url = ApiConstants.API_HOST+"product/"+mode+"/product_analytics_pages/"
            max_seq ++
            getProductOverAllMore(url,userId,max_seq)
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

    private fun getProductOverAll(url: String,user_id:String,max_seq:Int) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<TopProductBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    Log.d("getProductOverAll", "返回資料 resStr：${resStr.toString()}")
                    Log.d("getProductOverAll", "返回資料 ret_val：${ret_val.toString()}")

                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("RankingTopSaleTop", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val topProductBean: TopProductBean =
                                Gson().fromJson(jsonObject.toString(), TopProductBean::class.java)
                            list.add(topProductBean)
                        }

                    }

                    if(list.size > 0){
                        if(userId.isEmpty()){
                            userId = list[0].user_id
                        }
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
        web.Do_GetShopTopProduct(url,user_id,max_seq)
    }

    private fun getProductOverAllMore(url: String,user_id:String,max_seq:Int) {
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<TopProductBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    Log.d("getProductOverAllMore", "返回資料 resStr：${resStr.toString()}")
                    Log.d("getProductOverAllMore", "返回資料 ret_val：${ret_val.toString()}")

                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("RankingAllTop", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val topProductBean: TopProductBean =
                                Gson().fromJson(jsonObject.toString(), TopProductBean::class.java)
                            list.add(topProductBean)
                        }

                    }

                    if(list.size > 0){
                        activity!!.runOnUiThread {
                            adapter.add(list)
                        }
                    }
                    refreshLayout.finishLoadMore()

                } catch (e: JSONException) {
                    Log.d("errormessage", "getProductOverAllMore: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getProductOverAllMore: IOException：" + e.toString())

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getProductOverAllMore: ErrorResponse：" + ErrorResponse.toString())

            }
        })
        web.Do_GetShopTopProduct(url,user_id,max_seq)
    }
}