package com.hkshopu.hk.ui.main.activity

import android.R
import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.component.EventProductCatLastPostion
import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.data.bean.ProductCategoryBean
import com.hkshopu.hk.data.bean.ProductChildCategoryBean
import com.hkshopu.hk.databinding.ActivityMerchanCategoryBinding
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.adapter.ProductCategoryItemAdapter
import com.hkshopu.hk.ui.main.adapter.ProductChildCategoryItemAdapter
import com.hkshopu.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MerchanCategoryActivity : BaseActivity() {

    var url = "https://hkshopu-20700.df.r.appspot.com/product_category/index/"
    var sub_url = "https://hkshopu-20700.df.r.appspot.com/product_sub_category/index/"

    var selected_main_category_id = 1

    lateinit var binding : ActivityMerchanCategoryBinding
    lateinit var product_category_list : MutableList<ProductCategoryBean>
    lateinit var product_child_category_list : MutableList<ProductChildCategoryBean>
    lateinit var selected_product_child_category_list :MutableList<ProductChildCategoryBean>

    val mAdapters_ProCateItem = ProductCategoryItemAdapter()
    val mAdapters_SubProCateItem = ProductChildCategoryItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMerchanCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        initEvent()
    }


    fun initView() {
        generateProductCategoryItems()
        generateSubProductCategoryItems()
    }


    //新增主選單項目
    fun generateProductCategoryItems() {

        Thread(Runnable {
            runOnUiThread {
                getShopCategory(url)
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                binding.rViewCategoryItem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rViewCategoryItem.adapter = mAdapters_ProCateItem

                mAdapters_ProCateItem.updateList(product_category_list)
                mAdapters_ProCateItem.notifyDataSetChanged()

            }


        }).start()

    }

    //取得產品分類清單Api
    private fun getShopCategory(url: String){
        product_category_list = java.util.ArrayList<ProductCategoryBean>()

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
                            val productCategoryBean: ProductCategoryBean =
                                Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductCategoryBean::class.java
                                )

                            product_category_list.add(productCategoryBean)

                        }
                        Log.d(
                            "ShopmenuActivity",
                            "返回資料 product_category_list：" + product_category_list.toString()
                        )


                    } else {
                        Log.d("ShopmenuActivity", "您尚未新增任何產品分類!")
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

        Thread(Runnable {
            runOnUiThread {

                getSubProductCategory(sub_url)

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                //預設篩選product_category_id為1的子項目
                selected_product_child_category_list = product_child_category_list.filter {
                    it.product_category_id.equals(
                        1
                    )
                } as MutableList<ProductChildCategoryBean>


                binding.rViewCategorySubItem.layoutManager = GridLayoutManager(this, 3)
                binding.rViewCategorySubItem.adapter = mAdapters_SubProCateItem

                mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
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
                    Log.d("ShopmenuActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopmenuActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得產品子分類清單!")) {

                        val translations: JSONArray = json.getJSONArray("product_sub_category_list")
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

                        Log.d(
                            "ShopmenuActivity",
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
        var position: Int
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventProductCatSelected -> {
                        selectedId = it.selectrdId

                        selected_product_child_category_list = product_child_category_list.filter {
                            it.product_category_id.equals(
                                selectedId
                            )
                        } as MutableList<ProductChildCategoryBean>


                        Log.d(
                            "ShopmenuActivity",
                            " test ${selectedId} : " + selected_product_child_category_list.toString()
                        )

                        mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
                        mAdapters_SubProCateItem.notifyDataSetChanged()




                    }

                }
            }, {
                it.printStackTrace()
            })

    }





}