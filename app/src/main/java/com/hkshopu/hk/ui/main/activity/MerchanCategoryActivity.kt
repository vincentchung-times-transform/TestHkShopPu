package com.hkshopu.hk.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.data.bean.ProductCategoryBean
import com.hkshopu.hk.data.bean.ProductChildCategoryBean
import com.hkshopu.hk.databinding.ActivityMerchanCategoryBinding
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.adapter.ProductCategoryItemAdapter
import com.hkshopu.hk.ui.main.adapter.ProductSubCategoryItemAdapter
import com.hkshopu.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MerchanCategoryActivity : BaseActivity() {

    var url = "https://hkshopu.df.r.appspot.com/product_category/index/"
    var sub_url = "https://hkshopu.df.r.appspot.com/product_sub_category/index/"


    lateinit var binding : ActivityMerchanCategoryBinding
    lateinit var product_category_list : MutableList<ProductCategoryBean>
    lateinit var product_child_category_list : MutableList<ProductChildCategoryBean>
    lateinit var selected_product_child_category_list :MutableList<ProductChildCategoryBean>

    val mAdapters_ProCateItem = ProductCategoryItemAdapter()
    val mAdapters_SubProCateItem = ProductSubCategoryItemAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMerchanCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()


    }


    fun initView() {

        initClick()

        generateProductCategoryItems()

        try{
            Thread.sleep(800)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        generateSubProductCategoryItems()
    }

    fun initClick() {
        binding.titleBackProductCategory.setOnClickListener {

            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)

            finish()
        }
    }
    //新增主選單項目
    fun generateProductCategoryItems() {

        binding.rViewCategoryItem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rViewCategoryItem.adapter = mAdapters_ProCateItem

        Thread(Runnable {

            getProductCategory(url)

            try{
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            runOnUiThread {

                mAdapters_ProCateItem.updateList(product_category_list)
                mAdapters_ProCateItem.notifyDataSetChanged()

            }


        }).start()

    }

    //取得產品分類清單Api
    private fun getProductCategory(url: String){
        product_category_list = java.util.ArrayList<ProductCategoryBean>()

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("MerchanCategoryActivityApi", "返回資料 resStr：" + resStr)
                    Log.d("MerchanCategoryActivityApi", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")

                    if (ret_val.equals("已取得產品分類清單!")) {

                        val translations: JSONArray = json.getJSONArray("product_category_list")
                        Log.d("MerchanCategoryActivityApi", "返回資料 List：" + translations.toString())

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val productCategoryBean: ProductCategoryBean =
                                Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductCategoryBean::class.java
                                )

                            product_category_list.add(productCategoryBean)

                        }
                        Log.d(
                            "MerchanCategoryActivityApi",
                            "返回資料 product_category_list：" + product_category_list.toString()
                        )

                    } else {
                        Log.d("MerchanCategoryActivity", "您尚未新增任何產品分類!")
                    }

                } catch (e: JSONException) {
                    Log.d("ShopmenuActivity", e.toString())

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })

        web.Get_Data(url)

    }

    //新增子選單項目
    fun generateSubProductCategoryItems() {


        binding.rViewCategorySubItem.layoutManager = GridLayoutManager(this, 3)
        binding.rViewCategorySubItem.adapter = mAdapters_SubProCateItem


        Thread(Runnable {

            getSubProductCategory(sub_url)

            try{
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }


            //預設篩選product_category_id為1的子項目
            selected_product_child_category_list = product_child_category_list.filter {
                it.product_category_id.equals(
                    1
                )
            } as MutableList<ProductChildCategoryBean>


            runOnUiThread {


                mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
                mAdapters_SubProCateItem.set_c_name(product_category_list.get(0).c_product_category)
                mAdapters_SubProCateItem.notifyDataSetChanged()

            }

        }).start()

    }


    //取得子產品分類清單Api
    private fun getSubProductCategory(url: String){
        product_child_category_list = java.util.ArrayList<ProductChildCategoryBean>()

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("MerchanCategoryActivityApi", "返回資料 resStr：" + resStr)
                    Log.d("MerchanCategoryActivityApi", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得產品子分類清單!")) {

                        val translations: JSONArray = json.getJSONArray("product_sub_category_list")
                        Log.d("MerchanCategoryActivityApi", "返回資料 List：" + translations.toString())

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val productChildCategoryBean: ProductChildCategoryBean =
                                Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductChildCategoryBean::class.java
                                )

                            product_child_category_list.add(productChildCategoryBean)

                        }

                        Log.d(
                            "MerchanCategoryActivityApi",
                            "返回資料 product_child_category_list :" + product_child_category_list.toString()
                        )

                    } else {
                        Log.d("ShopmenuActivity", "您尚未新增任何產品分類!")
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

    @SuppressLint("CheckResult")
    fun initEvent() {
        var selectedId: Int
        var c_product_category: String

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventProductCatSelected -> {

                        selectedId = it.selectrdId
                        c_product_category = it.c_product_category

                        Thread(Runnable {

                            selected_product_child_category_list = product_child_category_list.filter {
                                it.product_category_id.equals(
                                    selectedId
                                )
                            } as MutableList<ProductChildCategoryBean>

                            runOnUiThread {


                                mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
                                mAdapters_SubProCateItem.set_c_name(c_product_category)
                                mAdapters_SubProCateItem.notifyDataSetChanged()


                            }

                        }).start()

                    }

                }
            }, {
                it.printStackTrace()
            })

    }

}