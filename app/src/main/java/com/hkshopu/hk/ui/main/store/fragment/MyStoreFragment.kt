package com.hkshopu.hk.ui.main.store.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventAddShopBriefSuccess
import com.hkshopu.hk.data.bean.ProductInfoBean

import com.hkshopu.hk.data.bean.ShopProductBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.activity.AddNewProductActivity
import com.hkshopu.hk.ui.main.store.activity.AddShopBriefActivity
import com.hkshopu.hk.ui.main.store.adapter.ShopProductAdapter
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MyStoreFragment : Fragment() {

    companion object {
        fun newInstance(): MyStoreFragment {
            val args = Bundle()
            val fragment = MyStoreFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var storeBrief:RelativeLayout
    lateinit var shopBrief:TextView
    lateinit var shopBrief_edit:TextView
    lateinit var addShopBrief:RelativeLayout
    lateinit var newProduct_null :RelativeLayout
    lateinit var newProduct :RecyclerView
    lateinit var btn_addNewMerchant:ImageView
    private val adapter = ShopProductAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_mystore, container, false)
        val shopId = MMKV.mmkvWithID("http").getInt("ShopId",0)
        var url = ApiConstants.API_HOST+"/product/"+shopId+"/shop_product/"
        getShopProduct(url)


        storeBrief = v.find<RelativeLayout>(R.id.layout_store_brief)
        shopBrief = v.find<TextView>(R.id.tv_shop_brief)
        shopBrief_edit = v.find<TextView>(R.id.tv_shop_brief_more)
        shopBrief_edit.setOnClickListener {
            val intent = Intent(activity, AddShopBriefActivity::class.java)
            activity!!.startActivity(intent)
        }
        addShopBrief = v.find<RelativeLayout>(R.id.layout_store_addbrief)

        val btn_addShopBrief = v.find<ImageButton>(R.id.iv_addshopbrief)
        btn_addShopBrief.setOnClickListener {
            val intent = Intent(activity, AddShopBriefActivity::class.java)
            activity!!.startActivity(intent)
        }
        val btn_addNewProduct = v.find<RelativeLayout>(R.id.layout_add_product)
        btn_addNewProduct.setOnClickListener {
            val intent = Intent(activity, AddNewProductActivity::class.java)
            activity!!.startActivity(intent)
        }

        btn_addNewMerchant = v.find<ImageView>(R.id.iv_addmerchant)

        btn_addNewMerchant.setOnClickListener {
            val intent = Intent(activity, AddNewProductActivity::class.java)
            activity!!.startActivity(intent)
        }
        newProduct_null = v.find<RelativeLayout>(R.id.layout_new_product)
        newProduct = v.find<RecyclerView>(R.id.recyclerview_newproduct)

        initView()
        initEvent()

        return v
    }

    private fun initView(){
        val description = MMKV.mmkvWithID("http").getString("description","")
        Log.d(
            "MyStoreFragment",
            "資料 description：" + description
        )
        if(description!!.length > 0 ){
            storeBrief.visibility = View.VISIBLE
            shopBrief.text = description

        }else{
            addShopBrief.visibility = View.VISIBLE
        }
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(activity!!, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventAddShopBriefSuccess -> {
                        addShopBrief.visibility = View.INVISIBLE
                        storeBrief.visibility = View.VISIBLE
                        shopBrief.text = it.description
                    }
                }

            })
    }







    private fun initRecyclerView(){


        val layoutManager = GridLayoutManager(activity!!,2)
        newProduct.layoutManager = layoutManager

        newProduct.adapter = adapter
        adapter.itemClick = {

        }
        newProduct.visibility = View.VISIBLE
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
                            btn_addNewMerchant.visibility = View.VISIBLE
                            newProduct_null.visibility = View.GONE

                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }

}