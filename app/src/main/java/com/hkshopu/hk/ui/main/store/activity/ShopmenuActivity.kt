package com.hkshopu.hk.ui.main.store.activity


import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson


import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.component.CommonVariable
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.ActivityMainBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.fragment.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopmenuActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var manager: FragmentManager
    var url = ApiConstants.API_HOST + "/shop_category/index/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()
        initView()
        initClick()
        getShopCategory(url)

    }

    private val fragments = mutableListOf<Fragment>()
    private fun initFragment() {
        manager = supportFragmentManager
        if (fragments.isNotEmpty()) return
        val FirstFragment = FirstFragment.newInstance()
//        val FirstFragment = ShopInfoFragment.newInstance()
        val SecondFragment = SecondFragment.newInstance()
        val ShopListFragment = ShopListFragment.newInstance()
        fragments.add(FirstFragment)
        fragments.add(SecondFragment)
        fragments.add(ShopListFragment)
        binding.viewPager.adapter =
            object : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = fragments[position]
                override fun getCount() = fragments.size
            }
        binding.viewPager.setPagingEnabled(false)
        binding.viewPager.addOnPageChangeListener(this)
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }

    fun initView() {
        binding.bottomNavigationViewLinear.setNavigationChangeListener { view, position ->
//            Log.d("ShopMenuActivity", "BottomView position：" + position)
            binding.viewPager.setCurrentItem(position, true);

        }

    }

    fun initClick() {


    }

    private fun getShopCategory(url: String) {
        CommonVariable.list.clear()
        CommonVariable.ShopCategory.clear()
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopCategoryActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopCategoryActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商店清單!")) {

                        val translations: JSONArray = json.getJSONArray("shop_category_list")
                        Log.d("ShopCategoryActivity", "返回資料 List：" + translations.toString())
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopCategoryBean: ShopCategoryBean =
                                Gson().fromJson(jsonObject.toString(), ShopCategoryBean::class.java)

                            CommonVariable.list.add(shopCategoryBean)
                            CommonVariable.ShopCategory.put(shopCategoryBean.id.toString(), shopCategoryBean)

                        }

                    }
//                    Log.d("RechargeActivity", "返回值：" + rtnCode)

//                    Log.d("ComicReadActivity", "返回值：" + imgUrl)
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

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }


}
