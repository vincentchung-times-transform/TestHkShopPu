package com.HKSHOPU.hk.ui.main.homepage.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerFollowListActivity
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity.ShoppingCartEditActivity
import com.HKSHOPU.hk.ui.main.homepage.activity.GoShopActivity
import com.HKSHOPU.hk.ui.main.homepage.activity.ShopBriefActivity
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.ShopInfoFragment
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

class ShopPreviewActivity : BaseActivity() {

    private lateinit var binding: ActivityShoppreviewBinding
    var shopId: String = ""
    var userId: String = ""
    var shoppingCartItemCount: ShoppingCartItemCountBean = ShoppingCartItemCountBean()
    lateinit var shopPreviewBean: ShopPreviewBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shopId = intent.getBundleExtra("bundle")!!.getString("shopId","")
        userId = intent.getBundleExtra("bundle")!!.getString("userId","")
        Log.d("ShopPreviewActivity_value", "shopId: ${shopId.toString()}")
        Log.d("ShopPreviewActivity_value", "userId: ${userId.toString()}")

        val url = ApiConstants.API_HOST+"shop/"+shopId+"/get_specific_recommended_shop/"
        do_ShopPreviewData(url, userId.toString())
        if (!userId.isNullOrEmpty()){
            GetShoppingCartItemCountForBuyer(userId.toString())
        }
        initVM()
        initFragment()
        initClick()
        getNotificationItemCount(userId)
    }

    private fun initVM() {
        RxBus.getInstance().post(EventShopPreViewRankAll(shopId))
        binding
    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return ResourceProductRanking.pagerFragments[position]
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
            val intent = Intent(this@ShopPreviewActivity, ShopBriefActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId",shopId)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }
        binding.layoutIcCart.setOnClickListener {
//            val intent = Intent(this@ShopPreviewActivity, GoShopActivity::class.java)
//            startActivity(intent)
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
        binding!!.layoutNotify.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
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


                            when(shopPreviewBean.identity){
                                "尊榮"->{
                                    when(shopPreviewBean.background_is_show){
                                        "Y"->{
                                            binding.container1.setBackgroundResource(R.drawable.sponsor_honorable_gradual_bg_16dp)
                                            if (shopPreviewBean.followed.equals("Y")) {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            } else {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.sponsor_honorable_gradual_btn_bg_45dp)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                        "N"->{
                                            binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                        }
                                    }
                                    when(shopPreviewBean.badge_is_show){
//                                        "Y"->{
//                                            iv_badge.visibility = View.VISIBLE
//                                            iv_badge.setImageResource(R.mipmap.badge_sponsor_honor)
//                                        }
//                                        "N"->{
//                                            iv_badge.visibility = View.GONE
//                                        }
                                    }
                                    when(shopPreviewBean.frame_is_show){
                                        "Y"->{
                                            binding!!.ivForgroundFrame.visibility = View.VISIBLE
                                            binding!!.ivForgroundFrame.setImageResource(R.mipmap.frame_sponsor_honor)

                                            setMarginFromDpToPixel(this@ShopPreviewActivity,  binding!!.ivForgroundFrame, 0, 0, 0, 4)
                                        }
                                        "N"->{
                                            binding!!.ivForgroundFrame.visibility = View.GONE
                                        }
                                    }
                                }
                                "至尊"->{
                                    when(shopPreviewBean.background_is_show){
                                        "Y"->{
                                            binding.container1.setBackgroundResource(R.drawable.sponsor_supreme_gradual_bg_16dp)
                                            if (shopPreviewBean.followed.equals("Y")) {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            } else {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.sponsor_supreme_gradual_btn_bg_45dp)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                        "N"->{
                                            binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                        }
                                    }
                                    when(shopPreviewBean.badge_is_show){
//                                        "Y"->{
//                                            iv_badge.visibility = View.VISIBLE
//                                            iv_badge.setImageResource(R.mipmap.badge_sponsor_honor)
//                                        }
//                                        "N"->{
//                                            iv_badge.visibility = View.GONE
//                                        }
                                    }
                                    when(shopPreviewBean.frame_is_show){
                                        "Y"->{
                                            binding!!.ivForgroundFrame.visibility = View.VISIBLE
                                            binding!!.ivForgroundFrame.setImageResource(R.mipmap.frame_sponsor_supreme)

                                            setMarginFromDpToPixel(this@ShopPreviewActivity,  binding!!.ivForgroundFrame, 0, 0, 0, 4)
                                        }
                                        "N"->{
                                            binding!!.ivForgroundFrame.visibility = View.GONE
                                        }
                                    }
                                }
                                "榮耀"->{
                                    when(shopPreviewBean.background_is_show){
                                        "Y"->{
                                            binding.container1.setBackgroundResource(R.drawable.sponsor_glory_bg_16dp)
                                            if (shopPreviewBean.followed.equals("Y")) {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            } else {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                        "N"->{
                                            binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                        }
                                    }
                                    when(shopPreviewBean.badge_is_show){
//                                        "Y"->{
//                                            iv_badge.visibility = View.VISIBLE
//                                            iv_badge.setImageResource(R.mipmap.badge_sponsor_honor)
//                                        }
//                                        "N"->{
//                                            iv_badge.visibility = View.GONE
//                                        }
                                    }
                                    when(shopPreviewBean.frame_is_show){
                                        "Y"->{
                                            binding!!.ivForgroundFrame.visibility = View.VISIBLE
                                            binding!!.ivForgroundFrame.setImageResource(R.mipmap.frame_sponsor_glory)

                                            setMarginFromDpToPixel(this@ShopPreviewActivity,  binding!!.ivForgroundFrame, 0, 0, 0, 0)
                                        }
                                        "N"->{
                                            binding!!.ivForgroundFrame.visibility = View.GONE
                                        }
                                    }
                                }
                                "卓越"->{
                                    when(shopPreviewBean.background_is_show){
                                        "Y"->{
                                            binding.container1.setBackgroundResource(R.drawable.sponsor_excellence_bg_16dp)
                                            if (shopPreviewBean.followed.equals("Y")) {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            } else {
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                        "N"->{
                                            binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                        }
                                    }
                                    when(shopPreviewBean.badge_is_show){
//                                        "Y"->{
//                                            iv_badge.visibility = View.VISIBLE
//                                            iv_badge.setImageResource(R.mipmap.badge_sponsor_honor)
//                                        }
//                                        "N"->{
//                                            iv_badge.visibility = View.GONE
//                                        }
                                    }
                                    when(shopPreviewBean.frame_is_show){
                                        "Y"->{
                                            binding!!.ivForgroundFrame.visibility = View.VISIBLE
                                            binding!!.ivForgroundFrame.setImageResource(R.mipmap.frame_sponsor_excel)

                                            setMarginFromDpToPixel(this@ShopPreviewActivity,  binding!!.ivForgroundFrame, 0, 0, 0, 0)
                                        }
                                        "N"->{
                                            binding!!.ivForgroundFrame.visibility = View.GONE
                                        }
                                    }
                                }
                                else->{
                                    if (shopPreviewBean.followed.equals("Y")) {
                                        binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                        binding.tvAdd.visibility = View.GONE
                                        binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                    } else {
                                        binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                        binding.tvAdd.visibility = View.VISIBLE
                                        binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                    }
                                }
                            }


                            binding.btnPayAttention.setOnClickListener {

                                if(userId.isNullOrEmpty()){
                                    Log.d("btnAddToShoppingCart", "UserID為空值")
                                    Toast.makeText(this@ShopPreviewActivity, "請先登入", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@ShopPreviewActivity, OnBoardActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                }else{
                                    Log.d("do_ShopPreviewData", "shopPreviewBean_followed: ${shopPreviewBean.followed}")
                                    if(shopPreviewBean.followed.equals("Y")){
                                        doStoreFollow(userId, shopId, "N")
                                    }else{
                                        doStoreFollow(userId, shopId, "Y")
                                    }
                                }

                            }

                            binding.progressBar.visibility = View.GONE
                            binding.ivLoadingBackground.visibility = View.GONE
                        }

                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShopPreviewActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@ShopPreviewActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "GetShoppingCartItemCountForBuyer: IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@ShopPreviewActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "GetShoppingCartItemCountForBuyer: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@ShopPreviewActivity, "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.Get_Data(url)
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
                                this@ShopPreviewActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            if (follow.equals("Y")) {
                                when(shopPreviewBean.identity){
                                    "尊榮"->{
                                        when(shopPreviewBean.background_is_show){
                                            "Y"->{
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_honorable_gradual_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))

                                            }
                                            "N"->{
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            }
                                        }
                                    }
                                    "至尊"->{
                                        when(shopPreviewBean.background_is_show) {
                                            "Y" -> {
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_supreme_gradual_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            }
                                            "N" -> {
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            }
                                        }
                                    }
                                    "榮耀"->{
                                        when(shopPreviewBean.background_is_show){
                                            "Y"->{
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_glory_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            }
                                            "N"->{
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            }
                                        }

                                    }
                                    "卓越"->{
                                        when(shopPreviewBean.background_is_show){
                                            "Y"->{
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_excellence_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                            }
                                            "N"->{
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                                binding.tvAdd.visibility = View.GONE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_followed))

                                            }
                                        }
                                    }
                                    else->{
                                        binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_gray_8e8e93)
                                        binding.tvAdd.visibility = View.GONE
                                        binding.tvAttentionStatus.setText(getText(R.string.shop_followed))
                                    }
                                }

                                var update_likeCout = binding!!.myLikes.text.toString().toInt()+1
                                binding!!.myLikes.setText(update_likeCout.toString())
                                shopPreviewBean.followed =  "Y"
//                                val intent = Intent(this@ShopPreviewActivity, BuyerFollowListActivity::class.java)
//                                startActivity(intent)
//                                RxBus.getInstance().post(EventShopmenuToSpecificPage(1))
                            } else {
                                when(shopPreviewBean.identity){
                                    "尊榮"->{
                                        when(shopPreviewBean.background_is_show){
                                            "Y"->{
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_honorable_gradual_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.sponsor_honorable_gradual_btn_bg_45dp)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                            "N"->{
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                    }
                                    "至尊"->{
                                        when(shopPreviewBean.background_is_show) {
                                            "Y" -> {
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_supreme_gradual_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.sponsor_supreme_gradual_btn_bg_45dp)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                            "N" -> {
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                    }
                                    "榮耀"->{
                                        when(shopPreviewBean.background_is_show){
                                            "Y"->{
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_glory_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                            "N"->{
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }

                                    }
                                    "卓越"->{
                                        when(shopPreviewBean.background_is_show){
                                            "Y"->{
                                                binding.container1.setBackgroundResource(R.drawable.sponsor_excellence_bg_16dp)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                            "N"->{
                                                binding.container1.setBackgroundResource(R.drawable.customborder_shop)
                                                binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                                binding.tvAdd.visibility = View.VISIBLE
                                                binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                            }
                                        }
                                    }
                                    else->{
                                        binding.btnPayAttention.setBackgroundResource(R.drawable.customborder_40dp_hkcolor)
                                        binding.tvAdd.visibility = View.VISIBLE
                                        binding.tvAttentionStatus.setText(getText(R.string.shop_attention))
                                    }
                                }



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
                                this@ShopPreviewActivity, ret_val.toString(), Toast.LENGTH_SHORT
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

    private fun  getNotificationItemCount (user_id: String) {
        val url = ApiConstants.API_HOST+"user_detail/${user_id}/notification_count/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var notificationItemCount : String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getNotificationItemCount", "返回資料 resStr：" + resStr)
                    Log.d("getNotificationItemCount", "返回資料 ret_val：" + ret_val)
                    if (status == 0) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            notificationItemCount = jsonArray.get(i).toString()
                        }
                        Log.d(
                            "getNotificationItemCount",
                            "返回資料 jsonArray：" + notificationItemCount
                        )

                        runOnUiThread {
//                            binding!!.tvNotifycount.text = notificationItemCount
                            if(notificationItemCount!!.equals("0")){
                                binding!!.tvNotifycount.visibility = View.GONE
                            }else{
                                binding!!.tvNotifycount.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        runOnUiThread {
//                            binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: JSONException: ${e.toString()}")
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: IOException: ${e.toString()}")
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
//                    binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    fun setMarginFromDpToPixel(
        context: ShopPreviewActivity, view: View, dp_left: Int,
        dp_top: Int, dp_right: Int, dp_bottom: Int,  ){
        val scale: Float =
            context.getResources().getDisplayMetrics().density
        // convert the DP into pixel
        val pixel_left = (dp_left * scale + 0.5f).toInt()
        val pixel_top = (dp_top * scale + 0.5f).toInt()
        val pixel_right = (dp_right * scale + 0.5f).toInt()
        val pixel_bottom = (dp_bottom * scale + 0.5f).toInt()

        val s = view.layoutParams as ViewGroup.MarginLayoutParams
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom)
        view.setLayoutParams(s)
    }
}