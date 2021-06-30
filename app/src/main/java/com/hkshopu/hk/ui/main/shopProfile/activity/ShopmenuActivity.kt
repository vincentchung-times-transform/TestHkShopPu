package com.HKSHOPU.hk.ui.main.shopProfile.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.databinding.ActivityMainBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.fragment.*
import com.HKSHOPU.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopmenuActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var manager: FragmentManager
    var page_position = 0
    var url_forShopCategory = ApiConstants.API_HOST + "/shop_category/index/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarShopShopMenu.visibility = View.GONE
        binding.ivLoadingBackgroundShopMenu.visibility = View.GONE

        getShopCategory(url_forShopCategory)

        initFragment()
        initView()
        initClick()
        initEvent()

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
            object : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
            page_position = position
            binding.viewPager.setCurrentItem(position, true);

        }

    }

    fun initClick() {


    }


    override fun onBackPressed() {
//        super.onBackPressed()
        if(page_position !=0){
            binding.bottomNavigationViewLinear.setCurrentActiveItem(0)
            binding.viewPager.setCurrentItem(0, true)
        }else{

            AlertDialog.Builder(this , R.style.AlertDialogTheme)
                .setTitle("")
                .setMessage("您確定要離開 ？")
                .setPositiveButton("確定"){
                    // 此為 Lambda 寫法
                        dialog, which ->

                    finishAffinity()

                }
                .setNegativeButton("取消"){ dialog, which -> dialog.cancel()

                }
                .show()
        }

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun getShopCategory(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getShopCategory", "返回資料 resStr：" + resStr)
                    Log.d("getShopCategory", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商店清單!")) {

                        CommonVariable.shopCategoryListForAdd.clear()
                        CommonVariable.ShopCategory.clear()

                        val translations: JSONArray = json.getJSONArray("shop_category_list")
                        Log.d("getShopCategory", "返回資料 List：" + translations.toString())
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopCategoryBean: ShopCategoryBean =
                                Gson().fromJson(jsonObject.toString(), ShopCategoryBean::class.java)


                            CommonVariable.shopCategoryListForAdd.add(shopCategoryBean)
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
    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventLogout -> {

                        this.finish()

                    }
                    is EventStartLoadingShopmenu->{
                        binding.progressBarShopShopMenu.visibility = View.VISIBLE
                        binding.ivLoadingBackgroundShopMenu.visibility = View.VISIBLE
                    }
                    is EventFinishLoadingShopmenu->{
                        binding.progressBarShopShopMenu.visibility = View.GONE
                        binding.ivLoadingBackgroundShopMenu.visibility = View.GONE
                    }
                }
            }, {
                it.printStackTrace()
            })

    }


}
