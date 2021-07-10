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

import com.HKSHOPU.hk.data.bean.ProductShopPreviewBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.adapter.ProductShopPreviewAdapter
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopPreviewActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class RankingCheapFragment : Fragment() {

    companion object {
        fun newInstance(): RankingCheapFragment {
            val args = Bundle()
            val fragment = RankingCheapFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var layout_empty_result: LinearLayout
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var cheapProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = ProductShopPreviewAdapter(currency)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_cheap, container, false)

        progressBar = v.find<ProgressBar>(R.id.progressBar_product_cheap)
        progressBar.visibility = View.GONE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE

        val activity: ShopPreviewActivity? = activity as ShopPreviewActivity?
        val shopId: String? = activity!!.getShopId()
        val userId: String? = activity!!.getUserId()
        var url = ApiConstants.API_HOST+"/product/"+shopId+"/"+"lower_price"+"/shop_product_analytics/"
        cheapProduct = v.find<RecyclerView>(R.id.recyclerview_cheap)
        getProductOverAll(url,userId!!)

        initView()
        initEvent()

        return v
    }

    private fun initView(){

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


        val layoutManager = GridLayoutManager(requireActivity(),2)
        cheapProduct.layoutManager = layoutManager

        cheapProduct.adapter = adapter
//        adapter.itemClick = {
//            val intent = Intent(requireActivity(), ProductDetailedPageBuyerViewActivity::class.java)
//            var bundle = Bundle()
//            bundle.putString("product_id", it)
//            intent.putExtra("bundle_product_id", bundle)
//            requireActivity().startActivity(intent)
//        }

    }

    private fun getProductOverAll(url: String,userId:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductShopPreviewBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("RankingCheapFragment", "返回資料 resStr：" + resStr)
                    Log.d("RankingCheapFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("RankingCheapFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val productShopPreviewBean: ProductShopPreviewBean =
                                Gson().fromJson(jsonObject.toString(), ProductShopPreviewBean::class.java)
                            list.add(productShopPreviewBean)
                        }

                    }

                    Log.d("RankingCheapFragment", "返回資料 list：" + list.toString())

                    if(list.size > 0){
                        adapter.setData(list)
                        activity!!.runOnUiThread {
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
        web.Do_GetShopProduct(url,userId)
    }

}