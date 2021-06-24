package com.HKSHOPU.hk.ui.main.shopProfile.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopProductBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.productSeller.activity.AddNewProductActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.AddShopBriefActivity
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ShopProductAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
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

    lateinit var layout_store_brief_content_having: RelativeLayout
    lateinit var tv_shop_brief: TextView
    lateinit var tv_shop_brief_editation: TextView
    lateinit var imgBtn_shop_brief_adding: ImageView

    lateinit var newProductDefault :RelativeLayout
    lateinit var newProduct :RecyclerView


    private val adapter = ShopProductAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_mystore, container, false)

        val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
        var url = ApiConstants.API_HOST+"/product/"+shopId+"/shop_product/"

        newProduct = v.find<RecyclerView>(R.id.recyclerview_newproduct)
        layout_store_brief_content_having = v.find<RelativeLayout>(R.id.layout_store_brief_content_having)
        tv_shop_brief = v.find<TextView>(R.id.tv_shop_brief)
        tv_shop_brief_editation = v.find<TextView>(R.id.tv_shop_brief_editation)

        tv_shop_brief_editation.setOnClickListener {
            val intent = Intent(activity, AddShopBriefActivity::class.java)
            activity!!.startActivity(intent)
        }

        imgBtn_shop_brief_adding = v.find<ImageButton>(R.id.imgBtn_shop_brief_adding)

        imgBtn_shop_brief_adding.setOnClickListener {
            val intent = Intent(activity, AddShopBriefActivity::class.java)
            activity!!.startActivity(intent)
        }

        newProductDefault = v.find<RelativeLayout>(R.id.layout_new_product)
        newProductDefault.setOnClickListener {

            val intent = Intent(activity, AddNewProductActivity::class.java)
            activity!!.startActivity(intent)

        }

        initRecyclerView()
        getShopProduct(url)

        //清掉 MMKV.mmkvWithID("addPro").clear() MMKV.mmkvWithID("editPro").clear()
        MMKV.mmkvWithID("addPro").clear()
        MMKV.mmkvWithID("editPro").clear()


        initView()
        initEvent()

        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentManager!!.beginTransaction().remove((this as Fragment?)!!)
            .commitAllowingStateLoss()

    }

    private fun initView(){


        val description = MMKV.mmkvWithID("http").getString("description","")

        Log.d(
            "MyStoreFragment",
            "資料 description：" + description
        )

        if(!description!!.isNullOrEmpty()){

            layout_store_brief_content_having.visibility = View.VISIBLE
            tv_shop_brief.visibility =View.VISIBLE
            tv_shop_brief.text = description
            tv_shop_brief_editation.visibility =View.VISIBLE

            imgBtn_shop_brief_adding.visibility = View.GONE


        }else{

            layout_store_brief_content_having.visibility = View.GONE
            tv_shop_brief.visibility =View.GONE
            tv_shop_brief.text = description
            tv_shop_brief_editation.visibility =View.GONE

            imgBtn_shop_brief_adding.visibility = View.VISIBLE

        }

    }



    @SuppressLint("CheckResult")
    fun initEvent() {

        var shop_description = ""

        RxBus.getInstance().toMainThreadObservable(activity!!, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventAddShopBriefSuccess -> {

                        shop_description = it.description

                        if(!shop_description!!.isNullOrEmpty()){

                            layout_store_brief_content_having.visibility = View.VISIBLE
                            tv_shop_brief.visibility =View.VISIBLE
                            tv_shop_brief.text = shop_description
                            tv_shop_brief_editation.visibility =View.VISIBLE

                            imgBtn_shop_brief_adding.visibility = View.GONE


                        }else{

                            layout_store_brief_content_having.visibility = View.GONE
                            tv_shop_brief.visibility =View.GONE
                            tv_shop_brief.text = shop_description
                            tv_shop_brief_editation.visibility =View.GONE

                            imgBtn_shop_brief_adding.visibility = View.VISIBLE

                        }


                    }
                    is EventMyStoreFragmentRefresh ->{

                        val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
                        var url = ApiConstants.API_HOST+"/product/"+shopId+"/shop_product/"
                        getShopProduct(url)

                    }

                    is EventLogout ->{
                        this.onDestroy()
                    }

                }

            })
    }



    private fun initRecyclerView(){

        val layoutManager = GridLayoutManager(activity!!,2)
        newProduct.layoutManager = layoutManager
        newProduct.adapter = adapter

        adapter.itemClick = {
            val intent = Intent(activity, AddNewProductActivity::class.java)
            activity!!.startActivity(intent)
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

                        activity!!.runOnUiThread {

                            adapter.setData(list)
                            newProductDefault.visibility = View.GONE

                        }

                        RxBus.getInstance().post(EventAddProductButtonVisibility(true))

                    }else{

                        activity!!.runOnUiThread {

                            adapter.setData(list)
                            newProductDefault.visibility = View.VISIBLE

                        }

                        RxBus.getInstance().post(EventAddProductButtonVisibility(false))

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