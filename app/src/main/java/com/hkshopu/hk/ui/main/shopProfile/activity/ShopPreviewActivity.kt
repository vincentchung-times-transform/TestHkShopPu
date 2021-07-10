package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventShopPreViewRankAll
import com.HKSHOPU.hk.data.bean.ResourceProductRanking
import com.HKSHOPU.hk.data.bean.ShopPreviewBean
import com.HKSHOPU.hk.databinding.ActivityShoppreviewBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.activity.GoShopActivity
import com.HKSHOPU.hk.ui.main.homepage.activity.ShopBriefActivity
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class ShopPreviewActivity : BaseActivity() {
    private lateinit var binding: ActivityShoppreviewBinding
    var shopId: String = ""
    var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shopId = intent.getBundleExtra("bundle")!!.getString("shopId","")
        userId = intent.getBundleExtra("bundle")!!.getString("userId","")
        val url = ApiConstants.API_HOST+"/shop/"+shopId+"/get_specific_recommended_shop/"
        do_ShopPreviewData(url,userId.toString())
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
            var bundle = Bundle()
            bundle.putString("shopId",shopId)
            val intent = Intent(this@ShopPreviewActivity, ShopBriefActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.ivShopcarClick.setOnClickListener {
            val intent = Intent(this@ShopPreviewActivity, GoShopActivity::class.java)
            startActivity(intent)
        }

        binding.ivNotifyClick.setOnClickListener {
            val intent = Intent(this@ShopPreviewActivity, ShopNotifyActivity::class.java)
            startActivity(intent)
        }
    }
    private fun do_ShopPreviewData(url: String,userId:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopPreviewBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopPreviewActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopPreviewActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        val shopPreviewBean: ShopPreviewBean =
                            Gson().fromJson(jsonObject.toString(), ShopPreviewBean::class.java)
                        list.add(shopPreviewBean)

                        runOnUiThread {
                            binding.ivShopImg.loadNovelCover(list[0].shop_icon)
                            binding.ivShopbackgnd.loadNovelCover(list[0].background_pic)
                            binding.myProduct.text = list[0].product_nums_of_shop.toString()
                            binding.myLikes.text = list[0].follower_nums_of_shop.toString()
                            binding.mySold.text = list[0].sum_of_sales.toString()
                            binding.tvRating.text = list[0].average_of_shop_ratings.toString()
                            binding.ratingBar.setRating(list[0].average_of_shop_ratings.toFloat())
                            val description = list[0].long_description.replace("\n", "")
                            binding.tvShopBrief.text = description

                        }


                    }else{
                        runOnUiThread {
                            Toast.makeText(this@ShopPreviewActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

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
}