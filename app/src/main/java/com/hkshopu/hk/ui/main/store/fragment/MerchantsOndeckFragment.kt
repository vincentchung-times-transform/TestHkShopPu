package com.hkshopu.hk.ui.main.store.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventProductSearch
import com.hkshopu.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.hkshopu.hk.data.bean.MyProductBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.adapter.MyProductsAdapter
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MerchantsOndeckFragment: Fragment() {

    companion object {
        fun newInstance(): MerchantsOndeckFragment {
            val args = Bundle()
            val fragment = MerchantsOndeckFragment()
            fragment.arguments = args
            return fragment
        }
    }
    var shopId : Int = 0

    lateinit var recyclerview_myProducts: RecyclerView
    private val adapter = MyProductsAdapter(this,"active")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_my_products, container, false)

        recyclerview_myProducts = v.findViewById<RecyclerView>(R.id.recyclerview_myProducts)
        initRecyclerView()

        shopId = MMKV.mmkvWithID("http").getInt("ShopId",0)
        Log.d("mkjgjs", shopId.toString())

        getMyProductsList(shopId.toString(), "none", "active", "1")

        initEvent()

        return v
    }



    private fun initRecyclerView() {

        val layoutManager = LinearLayoutManager(activity!!)
        recyclerview_myProducts.layoutManager = layoutManager
        recyclerview_myProducts.adapter = adapter
        adapter.itemClick = {
            val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
            val newFragment: ShopInfoFragment = ShopInfoFragment.newInstance()
            val args = Bundle()
            args.putInt("shop_id", it)
            newFragment.arguments = args
            ft.replace(R.id.layout_shopInfo, newFragment, "ShopInfoFragment")
            ft.commit()
        }


    }



    //[keyword]:關鍵字搜尋，未輸入關鍵字→none
    //[product_status] : active(架上商品)、draft(未上架)
    //[quantity] : 0(已售完)
    private fun getMyProductsList(shop_id: String, keyword: String, product_status:String, quantity: String) {

        val url = ApiConstants.API_HOST+"product/${shop_id}/${keyword}/${product_status}/${quantity}/product_list/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<MyProductBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getMyProductsList", "返回資料 resStr：" + resStr)
                    Log.d("getMyProductsList", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val translations: JSONArray = json.getJSONArray("data")

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            Log.d("getMyProductsList", "返回資料 Object：" + jsonObject.toString())
                            val myProductBean: MyProductBean =
                                Gson().fromJson(jsonObject.toString(), MyProductBean::class.java)
                            list.add(myProductBean)
                        }


                        activity!!.runOnUiThread {
                            adapter.setData(list)
                        }


                    } else {
//                        activity!!.runOnUiThread {
//                            binding!!.container1.visibility = View.VISIBLE
//                        }

                    }
//                        initRecyclerView()

                } catch (e: JSONException) {
                    Log.d("getMyProductsList", "JSONException：" + e)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getMyProductsList", "IOException：" + e)
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        var keyword: String
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventProductSearch -> {

                        keyword = it.keyword

                        if(keyword.equals("")){
                            keyword = "none"
                        }

                        getMyProductsList(shopId.toString(), keyword, "active", "1")

                    }

                    is EventdeleverFragmentAfterUpdateStatus -> {
                        var action = it.action

                        getMyProductsList(shopId.toString(), "none", "active", "1")
                        adapter.notifyDataSetChanged()

                    }


                }
            }, {
                it.printStackTrace()
            })

    }


}