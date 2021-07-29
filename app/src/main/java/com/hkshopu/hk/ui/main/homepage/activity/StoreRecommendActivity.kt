package com.HKSHOPU.hk.ui.main.homepage.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity

import com.HKSHOPU.hk.data.bean.ResourceStoreRanking
import com.HKSHOPU.hk.data.bean.ShoppingCartItemCountBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity.ShoppingCartEditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class StoreRecommendActivity : BaseActivity() {
    private lateinit var binding: ActivityRecommendstoreBinding
    var userId: String = ""
    var shoppingCartItemCount: ShoppingCartItemCountBean = ShoppingCartItemCountBean()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendstoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = intent.getBundleExtra("bundle")!!.getString("userId","")

        if(!userId.isNullOrEmpty()){
            GetShoppingCartItemCountForBuyer(userId)
        }

        initVM()
        initFragment()
        initClick()

    }

    private fun initVM() {

    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceStoreRanking.pagerFragments[position]
            }

            override fun getItemCount(): Int {
                return ResourceStoreRanking.tabList.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceStoreRanking.tabList[position])

        }.attach()
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
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
        binding.icNotification.setOnClickListener {
            val intent = Intent(this, ShopNotifyActivity::class.java)
            startActivity(intent)
        }

    }

    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
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
                        Toast.makeText(this@StoreRecommendActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "GetShoppingCartItemCountForBuyer: IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@StoreRecommendActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "GetShoppingCartItemCountForBuyer: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@StoreRecommendActivity, "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.Get_Data(url)
    }

}