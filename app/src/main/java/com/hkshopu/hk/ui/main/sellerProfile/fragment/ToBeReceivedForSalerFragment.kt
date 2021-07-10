package com.HKSHOPU.hk.ui.main.sellerProfile.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ToBeReceivedForSalerFragment : Fragment() {

    companion object {
        fun newInstance(): ToBeReceivedForSalerFragment {
            val args = Bundle()
            val fragment = ToBeReceivedForSalerFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = ProductShopPreviewAdapter(currency)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_to_be_received_for_saler, container, false)

        allProduct = v.find<RecyclerView>(R.id.recyclerview_rankall)
        progressBar = v.find<ProgressBar>(R.id.progressBar_product_all)
        progressBar.visibility = View.VISIBLE

        val activity: ShopPreviewActivity? = activity as ShopPreviewActivity?
        val shopId: String? = activity!!.getShopId()
        val userId: String? = activity!!.getUserId()
        Log.d("RankingAllFragment", "返回資料 shopId：" + shopId)
        val url = ApiConstants.API_HOST+"/product/"+shopId+"/"+"overall"+"/shop_product_analytics/"

        getProductOverAll(url,userId!!)
        initView()

        return v
    }

    private fun initView(){

    }


    private fun initRecyclerView(){

        val layoutManager = GridLayoutManager(requireActivity(),2)
        allProduct.layoutManager = layoutManager

        allProduct.adapter = adapter
//        adapter.itemClick = {
//
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
                    Log.d("RankingAllFragment", "返回資料 resStr：" + resStr)
                    Log.d("RankingAllFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("RankingAllFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val productShopPreviewBean: ProductShopPreviewBean =
                                Gson().fromJson(jsonObject.toString(), ProductShopPreviewBean::class.java)
                            list.add(productShopPreviewBean)
                        }

                    }

                    Log.d("RankingAllFragment", "返回資料 list：" + list.toString())

                    if(list.size > 0){
                        adapter.setData(list)
                        activity!!.runOnUiThread {
                            initRecyclerView()
                            progressBar.visibility = View.GONE
                        }
                    }else{
                        activity!!.runOnUiThread {
                            progressBar.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getStoreOverAll: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getStoreOverAll: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getStoreOverAll: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        })
        web.Do_GetShopProduct(url,userId)
    }

}