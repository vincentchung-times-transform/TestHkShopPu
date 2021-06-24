package com.HKSHOPU.hk.ui.main.productSeller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity

import com.HKSHOPU.hk.component.EventProductCatSelected
import com.HKSHOPU.hk.data.bean.ProductCategoryBean
import com.HKSHOPU.hk.data.bean.ProductChildCategoryBean
import com.HKSHOPU.hk.databinding.ActivityMerchanCategoryBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.productSeller.adapter.ProductCategoryItemAdapter
import com.HKSHOPU.hk.ui.main.productSeller.adapter.ProductSubCategoryItemAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class EditMerchanCategoryActivity : BaseActivity() {

    var url = ApiConstants.API_HOST+"product_category/index/"
    var sub_url = ApiConstants.API_HOST+"product_sub_category/index/"

    lateinit var binding : ActivityMerchanCategoryBinding
    lateinit var product_category_list : MutableList<ProductCategoryBean>
    lateinit var product_child_category_list : MutableList<ProductChildCategoryBean>
    lateinit var selected_product_child_category_list :MutableList<ProductChildCategoryBean>

    val mAdapters_ProCateItem = ProductCategoryItemAdapter()
    val mAdapters_SubProCateItem = ProductSubCategoryItemAdapter(this, "edit")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.progressBarProductCategorys.visibility = View.VISIBLE
        binding.ivLoadingBackgroundProductCategorys.visibility = View.VISIBLE

        binding = ActivityMerchanCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initEvent()
        initView()

        binding.progressBarProductCategorys.visibility = View.GONE
        binding.ivLoadingBackgroundProductCategorys.visibility = View.GONE

    }


    fun initView() {

        getProductCategory(url)
        getSubProductCategory(sub_url)
        initClick()

    }

    fun initClick() {
        binding.titleBackProductCategory.setOnClickListener {

            val intent = Intent(this, EditProductActivity::class.java)
            startActivity(intent)

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
                        binding.ivLoadingBackgroundProductCategorys.visibility = View.VISIBLE

                    }

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

                        runOnUiThread {
                            binding.rViewCategoryItem.layoutManager = LinearLayoutManager(
                                this@EditMerchanCategoryActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding.rViewCategoryItem.adapter = mAdapters_ProCateItem

                            mAdapters_ProCateItem.updateList(product_category_list)
                            mAdapters_ProCateItem.notifyDataSetChanged()

                        }


                    } else {
                        Log.d("MerchanCategoryActivity", "您尚未新增任何產品分類!")
                    }

                    try{
                        Thread.sleep(300)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }


                    runOnUiThread {

                        binding.progressBarProductCategorys.visibility = View.GONE
                        binding.ivLoadingBackgroundProductCategorys.visibility = View.GONE

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

        val intent = Intent(this, EditProductActivity::class.java)
        startActivity(intent)
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

                        binding.progressBarProductCategorys.visibility = View.VISIBLE
                        binding.ivLoadingBackgroundProductCategorys.visibility = View.VISIBLE

                    }

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


                        //預設篩選product_category_id為15204092-0379-44fa-aa44-a9f364dcfa73的子項目
                        selected_product_child_category_list = product_child_category_list.filter {
                            it.product_category_id.equals(
                                product_category_list.get(0).id
                            )
                        } as MutableList<ProductChildCategoryBean>


                        runOnUiThread {

                            binding.rViewCategorySubItem.layoutManager = GridLayoutManager(this@EditMerchanCategoryActivity, 3)
                            binding.rViewCategorySubItem.adapter = mAdapters_SubProCateItem

                            mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
                            mAdapters_SubProCateItem.set_c_name(product_category_list.get(0).c_product_category)
                            mAdapters_SubProCateItem.notifyDataSetChanged()

                        }


                    } else {
                        Log.d("ShopmenuActivity", "您尚未新增任何產品分類!")
                    }

                    runOnUiThread {

                        binding.progressBarProductCategorys.visibility = View.GONE
                        binding.ivLoadingBackgroundProductCategorys.visibility = View.GONE
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
                    is EventProductCatSelected -> {

                        selectedId = it.selectdId
                        c_product_category = it.c_product_category

                        Thread(Runnable {

                            runOnUiThread {
                                binding.progressBarProductCategorys.visibility = View.VISIBLE
                                binding.ivLoadingBackgroundProductCategorys.visibility = View.VISIBLE
                            }

                            selected_product_child_category_list = product_child_category_list.filter {
                                it.product_category_id.equals(
                                    selectedId
                                )
                            } as MutableList<ProductChildCategoryBean>

                            runOnUiThread {

                                mAdapters_SubProCateItem.updateList(selected_product_child_category_list)
                                mAdapters_SubProCateItem.set_c_name(c_product_category)
                                mAdapters_SubProCateItem.notifyDataSetChanged()

                                binding.progressBarProductCategorys.visibility = View.GONE
                                binding.ivLoadingBackgroundProductCategorys.visibility = View.GONE

                            }

                        }).start()

                    }

                }
            }, {
                it.printStackTrace()
            })

    }

}