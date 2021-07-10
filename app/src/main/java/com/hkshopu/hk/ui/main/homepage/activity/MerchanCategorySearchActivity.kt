package com.HKSHOPU.hk.ui.main.homepage.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity

import com.HKSHOPU.hk.component.EventProductCatSelectedToSearch
import com.HKSHOPU.hk.data.bean.ProductCategoryBean
import com.HKSHOPU.hk.data.bean.ProductChildCategoryBean
import com.HKSHOPU.hk.databinding.ActivityMerchanCategoryBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.adapter.ProductCategorySearchItemAdapter
import com.HKSHOPU.hk.ui.main.homepage.adapter.ProductSubCategorySearchItemAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MerchanCategorySearchActivity : BaseActivity() {

    var url = ApiConstants.API_HOST +"product_category/index/"
    var sub_url = ApiConstants.API_HOST +"product_sub_category/index/"


    lateinit var binding : ActivityMerchanCategoryBinding
    lateinit var product_category_list : MutableList<ProductCategoryBean>
    lateinit var product_child_category_list : MutableList<ProductChildCategoryBean>
    lateinit var selected_product_child_category_list :MutableList<ProductChildCategoryBean>

    val mAdapters_ProCateItem = ProductCategorySearchItemAdapter()
    val mAdapters_SubProCateItem = ProductSubCategorySearchItemAdapter(this, "search")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMerchanCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBarProductCategorys.visibility = View.GONE
        binding.ivLoadingBackgroundProductCategorys.visibility = View.GONE

        initEvent()
        initView()

    }


    fun initView() {

        getProductCategory(url)

        initClick()
    }

    fun initClick() {
        binding.titleBackProductCategory.setOnClickListener {
                finish()

        }
    }

    //取得產品分類清單Api
    private fun getProductCategory(url: String){
        product_category_list = java.util.ArrayList<ProductCategoryBean>()

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    runOnUiThread {
                        binding.progressBarProductCategorys.visibility = View.VISIBLE
                    }

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("MerchanCategorySearch", "返回資料 resStr：" + resStr)
                    Log.d("MerchanCategorySearch", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")

                    if (ret_val.equals("已取得產品分類清單!")) {

                        val translations: JSONArray = json.getJSONArray("product_category_list")
                        Log.d("MerchanCategorySearch", "返回資料 List：" + translations.toString())

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val productCategoryBean: ProductCategoryBean =
                                Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductCategoryBean::class.java
                                )

                            product_category_list.add(productCategoryBean)

                        }

                        runOnUiThread {

                            binding.rViewCategoryItem.layoutManager = LinearLayoutManager(this@MerchanCategorySearchActivity, LinearLayoutManager.VERTICAL, false)
                            binding.rViewCategoryItem.adapter = mAdapters_ProCateItem
                            mAdapters_ProCateItem.updateList(product_category_list)
                            mAdapters_ProCateItem.notifyDataSetChanged()

                        }

                    } else {
                        Log.d("MerchanCategoryActivity", "您尚未新增任何產品分類!")
                    }

                    getSubProductCategory(sub_url)


                    runOnUiThread {
                        binding.progressBarProductCategorys.visibility = View.GONE
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


    override fun onBackPressed() {

        finish()

    }


    //取得子產品分類清單Api
    private fun getSubProductCategory(url: String){
        product_child_category_list = java.util.ArrayList<ProductChildCategoryBean>()

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    runOnUiThread {
                        binding.progressBarProductCategorys.visibility = View.GONE
                    }

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("MerchanCategorySearch", "返回資料 resStr：" + resStr)
                    Log.d("MerchanCategorySearch", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得產品子分類清單!")) {

                        val translations: JSONArray = json.getJSONArray("product_sub_category_list")
                        Log.d("MerchanCategorySearch", "返回資料 List：" + translations.toString())

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val productChildCategoryBean: ProductChildCategoryBean =
                                Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductChildCategoryBean::class.java
                                )

                            product_child_category_list.add(productChildCategoryBean)

                        }


                        //預設篩選product_category_id為1的子項目
                        selected_product_child_category_list = product_child_category_list.filter {
                            it.product_category_id.equals(
                                product_category_list.get(0).id
                            )
                        } as MutableList<ProductChildCategoryBean>


                        runOnUiThread {

                            binding.rViewCategorySubItem.layoutManager = GridLayoutManager(this@MerchanCategorySearchActivity, 3)
                            binding.rViewCategorySubItem.adapter = mAdapters_SubProCateItem

                            mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
                            mAdapters_SubProCateItem.set_c_name(product_category_list.get(0).c_product_category)
                            mAdapters_SubProCateItem.notifyDataSetChanged()

                        }

                    } else {

                    }


                    runOnUiThread {
                        binding.progressBarProductCategorys.visibility = View.GONE
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
        var selectedId: String
        var c_product_category: String

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventProductCatSelectedToSearch -> {

                        selectedId = it.selectrdId
                        c_product_category = it.c_product_category_selected
                        Log.d("MerchanCategorySearchActivity", "ProductCategory_id selected: "+selectedId)
                        Thread(Runnable {

                            selected_product_child_category_list = product_child_category_list.filter {
                                it.product_category_id.equals(
                                    selectedId
                                )
                            } as MutableList<ProductChildCategoryBean>
                            Log.d("MerchanCategorySearchActivity", "ProductCategory_id selected_product_child_category_list: "+selected_product_child_category_list.toString())
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