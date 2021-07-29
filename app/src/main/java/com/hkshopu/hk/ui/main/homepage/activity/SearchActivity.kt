package com.HKSHOPU.hk.ui.main.homepage.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventToProductSearch
import com.HKSHOPU.hk.component.EventToShopSearch
import com.HKSHOPU.hk.data.bean.ResourceSearch
import com.HKSHOPU.hk.data.bean.ShoppingCartItemCountBean

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity.ShoppingCartEditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


//import kotlinx.android.synthetic.main.activity_main.*

class SearchActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchBinding
    var keyword:String =""
//    var category_id = ""
//    var sub_category_id = ""
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "");
    var shoppingCartItemCount: ShoppingCartItemCountBean = ShoppingCartItemCountBean()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        keyword = MMKV.mmkvWithID("http")!!.getString("keyword","").toString()
//        category_id = MMKV.mmkvWithID("http")!!.getString("product_category_id","").toString()
//        sub_category_id = MMKV.mmkvWithID("http")!!.getString("sub_product_category_id","").toString()

        if(!userId.isNullOrEmpty()){
            GetShoppingCartItemCountForBuyer(userId)
        }

        initVM()
        initView()
        initFragment()
        initClick()

    }

    private fun initVM() {

    }
    private fun initView() {

        binding.layoutSearchMerchants.setOnClickListener {
            KeyboardUtil.showKeyboard(it)
        }

        binding.etSearchKeyword.setOnClickListener {
            KeyboardUtil.showKeyboard(it)
        }

        binding.etSearchKeyword.setText(keyword)
        binding.etSearchKeyword.doAfterTextChanged {
            keyword = binding.etSearchKeyword.text.toString()

        }
        binding!!.etSearchKeyword.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV.mmkvWithID("http")
                        .putString("keyword",keyword)
                        .putString("product_category_id", "")
                        .putString("sub_product_category_id", "")

                    RxBus.getInstance().post(EventToProductSearch())
                    RxBus.getInstance().post(EventToShopSearch())
                    KeyboardUtil.hideKeyboard(v)
                    true
                }

                else -> false
            }
        }

        binding.icCart.setOnClickListener {

            if(userId.isNullOrEmpty()){

                Log.d("btnAddToShoppingCart", "UserID為空值")
                Toast.makeText(this, "請先登入", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OnBoardActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                val intent = Intent(this, ShoppingCartEditActivity::class.java)
                startActivity(intent)
            }

        }

    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceSearch.pagerFragments_Search[position]
            }

            override fun getItemCount(): Int {
                return ResourceSearch.tabList_search.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceSearch.tabList_search[position])

        }.attach()

        binding!!.mviewPager.isSaveEnabled = false

//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {

        binding.ivBackClick.setOnClickListener {

            finish()
        }

    }
    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
    }

    fun getKeyWord(): String? {
        return keyword!!
    }


    private fun  GetShoppingCartItemCountForBuyer (user_id: String) {

        val url = ApiConstants.API_HOST+"shopping_cart/${user_id}/count/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("GetShoppingCartItemCountForBuyer", "返回資料 resStr：" + resStr)
                    Log.d("GetShoppingCartItemCountForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals( "已取得商品清單!")) {

                        val jsonObject: JSONObject = json.getJSONObject("data")

                        Log.d(
                            "GetShoppingCartItemCountForBuyer",
                            "返回資料 jsonObject：" + jsonObject.toString()
                        )

                        shoppingCartItemCount = Gson().fromJson(
                            jsonObject.toString(),
                            ShoppingCartItemCountBean::class.java
                        )

                        runOnUiThread {
                            binding.tvCartItemCount.setText(shoppingCartItemCount.cartCount.toString())

                            if(shoppingCartItemCount.cartCount > 0){
                                binding.tvCartItemCount.visibility = View.VISIBLE
                            }else{
                                binding.tvCartItemCount.visibility = View.GONE
                            }
                        }
                    }


                } catch (e: JSONException) {

                    Log.d("errormessage", "GetShoppingCartItemCountForBuyer: JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@SearchActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "GetShoppingCartItemCountForBuyer: IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@SearchActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "GetShoppingCartItemCountForBuyer: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@SearchActivity, "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.Get_Data(url)
    }

}