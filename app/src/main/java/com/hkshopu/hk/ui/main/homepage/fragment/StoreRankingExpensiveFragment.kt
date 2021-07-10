package com.HKSHOPU.hk.ui.main.homepage.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ShopProductBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ShopProductAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class StoreRankingExpensiveFragment : Fragment() {

    companion object {
        fun newInstance(): StoreRankingExpensiveFragment {
            val args = Bundle()
            val fragment = StoreRankingExpensiveFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var expensiveStore :RecyclerView
    private val adapter = ShopProductAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_expansive_store, container, false)
        val shopId = MMKV.mmkvWithID("http").getInt("ShopId",0)
        var url = ApiConstants.API_HOST+"/product/"+shopId+"/shop_product/"
        expensiveStore = v.find<RecyclerView>(R.id.recyclerview_expensive_store)
        getShopProduct(url)

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
        expensiveStore.layoutManager = layoutManager

        expensiveStore.adapter = adapter
        adapter.itemClick = {
            val intent = Intent(requireActivity(), ProductDetailedPageBuyerViewActivity::class.java)
            var bundle = Bundle()
            bundle.putString("product_id", it)
            intent.putExtra("bundle_product_id", bundle)
            requireActivity().startActivity(intent)
        }

    }

    private fun getShopProduct(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopProductBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("MyStoreFragment", "返回資料 resStr：" + resStr)
                    Log.d("MyStoreFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("MyStoreFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val shopProductBean: ShopProductBean =
                                Gson().fromJson(jsonObject.toString(), ShopProductBean::class.java)
                            list.add(shopProductBean)
                        }

                    }

                    Log.d("MyStoreFragment", "返回資料 list：" + list.toString())

                    if(list.size > 0){
                        adapter.setData(list)
                        activity!!.runOnUiThread {
                            initRecyclerView()

                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Get_Data(url)
    }

}