package com.hkshopu.hk.ui.main.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ProductCategoryBean
import com.hkshopu.hk.data.bean.ProductChildCategoryBean
import com.hkshopu.hk.databinding.ActivityMerchanCategoryBinding
import com.hkshopu.hk.databinding.ActivityMerchanChildCategoryBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.adapter.ProductCategoryItemAdapter
import com.hkshopu.hk.ui.main.adapter.ProductChildCategoryItemAdapter

import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MerchanChildCategoryActivity : BaseActivity() {

    var url = "https://hkshopu-20700.df.r.appspot.com/product_sub_category/index/"

    lateinit var binding : ActivityMerchanChildCategoryBinding
    lateinit var product_child_category_list : MutableList<ProductChildCategoryBean>
    val mAdapters_ProChildCateItem = ProductChildCategoryItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMerchanChildCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
    }


    fun initView() {
        generateProductChildCategoryItems()
    }

    fun generateProductChildCategoryItems() {

        Thread(Runnable {
            runOnUiThread {
                getShopCategory(url)
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                binding.rViewCategoryItem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
                binding.rViewCategoryItem.adapter = mAdapters_ProChildCateItem

                mAdapters_ProChildCateItem.updateList(product_child_category_list)
                mAdapters_ProChildCateItem.notifyDataSetChanged()


            }


        }).start()

    }

    private fun getShopCategory(url: String){
        product_child_category_list = java.util.ArrayList<ProductChildCategoryBean>()

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopmenuActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopmenuActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得產品分類清單!")) {

                        val translations: JSONArray = json.getJSONArray("product_category_list")
                        Log.d("ShopmenuActivity", "返回資料 List：" + translations.toString())

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val productChildCategoryBean: ProductChildCategoryBean =
                                Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductChildCategoryBean::class.java
                                )

                            product_child_category_list.add(productChildCategoryBean)

                        }


                        Log.d("ShopmenuActivity", product_child_category_list.toString())

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




}