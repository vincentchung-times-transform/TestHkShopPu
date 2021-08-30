package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventShopPreViewRankAll
import com.HKSHOPU.hk.component.EventShopmenuToSpecificPage
import com.HKSHOPU.hk.component.EventSyncBank
import com.HKSHOPU.hk.data.bean.ResourceProductRanking
import com.HKSHOPU.hk.data.bean.ShoppingCartItemCountBean
import com.HKSHOPU.hk.databinding.ActivityShoppreviewBinding
import com.HKSHOPU.hk.databinding.ActivityShoppreviewsellerBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerFollowListActivity
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity.ShoppingCartEditActivity
import com.HKSHOPU.hk.ui.main.homepage.activity.GoShopActivity
import com.HKSHOPU.hk.ui.main.homepage.activity.ShopBriefActivity
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.HKSHOPU.hk.data.bean.ShopPreviewBean as ShopPreviewBean

//import kotlinx.android.synthetic.main.activity_main.*

class ShopPreviewSellerActivity : BaseActivity() {

    private lateinit var binding: ActivityShoppreviewsellerBinding
    var shopId: String = ""
    var userId: String = ""
    lateinit var shopPreviewBean: ShopPreviewBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppreviewsellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shopId = intent.getBundleExtra("bundle")!!.getString("shopId","")
        userId = intent.getBundleExtra("bundle")!!.getString("userId","")
        Log.d("ShopPreviewActivity_value", "shopId: ${shopId.toString()}")
        Log.d("ShopPreviewActivity_value", "userId: ${userId.toString()}")

        val url = ApiConstants.API_HOST+"shop/"+shopId+"/get_specific_recommended_shop/"
        do_ShopPreviewData(url, userId.toString())

        initVM()
        initFragment()
        initClick()
    }

    private fun initVM() {
        RxBus.getInstance().post(EventShopPreViewRankAll(shopId))
        binding
    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return ResourceProductRanking.pagerFragments_seller[position]
            }
            override fun getItemCount(): Int {
                return ResourceProductRanking.tabList.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceProductRanking.tabList[position])

        }.attach()
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))

    }
    private fun initClick() {
        binding.ivBackClick.setOnClickListener {
            finish()
        }
        binding.tvShopBriefMore.setOnClickListener {
            val intent = Intent(this@ShopPreviewSellerActivity, ShopBriefActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId",shopId)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

    }

    private fun do_ShopPreviewData(url: String,userId:String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.ivLoadingBackground.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("do_ShopPreviewData", "返回資料 resStr：" + resStr)
                    Log.d("do_ShopPreviewData", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonObject: JSONObject = json.getJSONObject("data")
                        shopPreviewBean=
                            Gson().fromJson(jsonObject.toString(), ShopPreviewBean::class.java)

                        runOnUiThread {
                            binding.ivShopImg.loadNovelCover(shopPreviewBean.shop_icon)
                            binding.tvShoptitle.setText(shopPreviewBean.shop_title.toString())
                            binding.ivShopbackgnd.loadNovelCover(shopPreviewBean.background_pic)
                            binding.myProduct.text = shopPreviewBean.product_nums_of_shop.toString()
                            binding.myLikes.text = shopPreviewBean.follower_nums_of_shop.toString()
                            binding.mySold.text = shopPreviewBean.sum_of_sales.toString()
                            binding.tvRating.text = shopPreviewBean.average_of_shop_ratings.toString()
                            binding.ratingBar.setRating(shopPreviewBean.average_of_shop_ratings.toFloat())
                            binding.tvRatings.setText(shopPreviewBean.shop_rating_nums.toString())
                            if(shopPreviewBean.long_description.isNullOrEmpty()){
                                binding.tvShopBrief.text = ""
                            }else{
                                val description = shopPreviewBean.long_description.replace("\n", "")
                                binding.tvShopBrief.text = description
                            }

                            if(shopPreviewBean.followed.equals("Y")){
                                binding.ivPayAttention.setImageResource(R.mipmap.btn_shop_followed_long)
                            }else{
                                binding.ivPayAttention.setImageResource(R.mipmap.ic_payattention)
                            }

                            binding.ivPayAttention.setOnClickListener {

//                                if(userId.isNullOrEmpty()){
//                                    Log.d("btnAddToShoppingCart", "UserID為空值")
//                                    Toast.makeText(this@ShopPreviewSellerActivity, "請先登入", Toast.LENGTH_SHORT).show()
//                                    val intent = Intent(this@ShopPreviewSellerActivity, OnBoardActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//
//                                }else{
//                                    Log.d("do_ShopPreviewData", "shopPreviewBean_followed: ${shopPreviewBean.followed}")
//                                    if(shopPreviewBean.followed.equals("Y")){
//                                        doStoreFollow(userId, shopId, "N")
//                                    }else{
//                                        doStoreFollow(userId, shopId, "Y")
//                                    }
//                                }
                            }

                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }

                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShopPreviewSellerActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("do_ShopPreviewData_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("do_ShopPreviewData_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("do_ShopPreviewData_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.ivLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Do_GetRecommendedShops(url,userId)
    }

    @JvmName("getShopId1")
    fun getShopId(): String {
        return shopId
    }
    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
    }




    private fun doStoreFollow(userId: String, shop_id: String, follow: String) {
        Log.d("doStoreFollow", "userId: ${userId} \n " +
                "shop_id: ${shop_id} \n " +
                "follow: ${follow}")
        binding.progressBar.visibility = View.VISIBLE
        binding.ivLoadingBackground.visibility = View.VISIBLE
        val url_follow = ApiConstants.API_HOST + "user/" + userId + "/followShop/" + shop_id + "/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doStoreFollow", "返回資料 resStr：" + resStr)
                    Log.d("doStoreFollow", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ShopPreviewSellerActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            if (follow.equals("Y")) {
                                binding.ivPayAttention.setImageResource(R.mipmap.btn_shop_followed_long)

                                var update_likeCout = binding!!.myLikes.text.toString().toInt()+1
                                binding!!.myLikes.setText(update_likeCout.toString())
                                shopPreviewBean.followed =  "Y"
//                                val intent = Intent(this@ShopPreviewActivity, BuyerFollowListActivity::class.java)
//                                startActivity(intent)
//                                RxBus.getInstance().post(EventShopmenuToSpecificPage(1))
                            } else {
                                binding.ivPayAttention.setImageResource(R.mipmap.ic_payattention)

                                var update_likeCout = binding!!.myLikes.text.toString().toInt()-1
                                binding!!.myLikes.setText(update_likeCout.toString())
                                shopPreviewBean.followed =  "N"
//                                val intent = Intent(this@ShopPreviewActivity, BuyerFollowListActivity::class.java)
//                                startActivity(intent)
//                                RxBus.getInstance().post(EventShopmenuToSpecificPage(1))
                            }
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@ShopPreviewSellerActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doStoreFollow_errorMessage", "JSONException：" + e.toString())
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doStoreFollow_errorMessage", "IOException：" + e.toString())
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.ivLoadingBackground.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doStoreFollow_errorMessage", "onErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.ivLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Store_Follow(url_follow, follow)
    }
}