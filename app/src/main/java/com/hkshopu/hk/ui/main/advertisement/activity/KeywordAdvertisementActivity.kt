package com.HKSHOPU.hk.ui.main.advertisement.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ItemSpecificationSeleting
import com.HKSHOPU.hk.data.bean.KeywordAdBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.databinding.ActivityKeywordAdvertisementBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.advertisement.adapter.AdvertisementAdapter
import com.HKSHOPU.hk.ui.main.buyer.product.adapter.SpecificationSecondSelectingAdapter
import com.HKSHOPU.hk.ui.main.buyer.product.fragment.ProductDetailedPageBuyerViewFragment
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.ShopInfoFragment
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.MyProductsAdapter
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.otaliastudios.zoom.ZoomEngine.Companion.DEFAULT_ANIMATION_DURATION
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

//import kotlinx.android.synthetic.main.activity_main.*

class KeywordAdvertisementActivity : BaseActivity() {
    private lateinit var binding: ActivityKeywordAdvertisementBinding
    var MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var shop_id = ""

    private val adapter_product = AdvertisementAdapter("product_keyword")
    private val adapter_store = AdvertisementAdapter("store_keyword")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeywordAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shop_id = intent.getBundleExtra("bundle")!!.getString("shopId").toString()
        adapter_product.setShopId(shop_id)
        adapter_store.setShopId(shop_id)

        //<ad_type> : product 或 shop
        doGetKeywordAdForProduct(MMKV_user_id, "product")
        doGetKeywordAdForShop(MMKV_user_id, "shop")
        initVM()
        initClick()
        initEvent()
    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnKnowMore.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.layoutAddProductAd.setOnClickListener {
            val intent = Intent(this, AddEditProductKeywordAdvertisementActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId", shop_id)
            bundle.putString("mode", "add")
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

        binding.layoutAddStoreAd.setOnClickListener {
            val intent = Intent(this, AddEditStoreKeywordAdvertisementActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId", shop_id)
            bundle.putString("mode", "add")
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

        var productAdDropDown = false
        var storeAdDropDown = false
        //商品關鍵字廣告
        binding.recyclerviewProductAd.visibility = View.GONE
        binding.layoutProductDropDown.setOnClickListener {
            if(productAdDropDown){
                productAdDropDown = false
                binding.recyclerviewProductAd.visibility = View.GONE

                val valueAnimator = ValueAnimator.ofFloat(180f, 0f)
                valueAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.ivProductDropDown.rotation = value
                }
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.duration = DEFAULT_ANIMATION_DURATION
                valueAnimator.start()


//                binding.ivProductDropDown.setRotation(0.0F)
            }else{
                productAdDropDown = true
                binding.recyclerviewProductAd.visibility = View.VISIBLE

                val valueAnimator = ValueAnimator.ofFloat(0f, 180f)
                valueAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.ivProductDropDown.rotation = value
                }
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.duration = DEFAULT_ANIMATION_DURATION
                valueAnimator.start()


//                binding.ivProductDropDown.setRotation(180.0F)
            }
        }

        //店鋪關鍵字廣告
        binding.layoutRecyclerview.visibility = View.GONE
        binding.layoutStoreDropDown.setOnClickListener {

            if(storeAdDropDown){
                storeAdDropDown = false
                binding.layoutRecyclerview.visibility = View.GONE

                val valueAnimator = ValueAnimator.ofFloat(180f, 0f)
                valueAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.ivStoreDropDown.rotation = value
                }
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.duration = DEFAULT_ANIMATION_DURATION
                valueAnimator.start()

//                binding.ivStoreDropDown.setRotation(0.0F)
            }else{
                storeAdDropDown = true
                binding.layoutRecyclerview.visibility = View.VISIBLE

                val valueAnimator = ValueAnimator.ofFloat(0f, 180f)
                valueAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.ivStoreDropDown.rotation = value
                }
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.duration = DEFAULT_ANIMATION_DURATION
                valueAnimator.start()

//                binding.ivStoreDropDown.setRotation(180.0F)
            }
        }
    }

      private fun doGetKeywordAdForProduct(user_id: String, ad_type: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST + "user/${user_id}/adSetting/keyword/${ad_type}/"
        val web = Web(object : WebListener {

            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var list = ArrayList<KeywordAdBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doGetKeywordAdForProduct", "返回資料 resStr：" + resStr)
                    Log.d("doGetKeywordAdForProduct", "返回資料 ret_val：" + ret_val)

                    val jsonObject: JSONObject = json.getJSONObject("data")
                    Log.d("doGetKeywordAdForProduct", "返回資料 Object：" + jsonObject.toString())

                    var JSONArray: JSONArray = jsonObject.getJSONArray("productList")
                    if(JSONArray.length()>0) {
                        runOnUiThread {
                            binding.layoutProductAdList.visibility = View.VISIBLE
                            binding.ivProductDropDown.visibility = View.VISIBLE
                        }

                        if(JSONArray.length()>0){
                            for(i in 0..JSONArray.length()-1){
                                val keywordAdBean: KeywordAdBean =
                                    Gson().fromJson(JSONArray.get(i).toString(), KeywordAdBean::class.java)
                                list.add(keywordAdBean)
                            }
                        }


                        runOnUiThread {
                            renderAdType("product_keyword", list.get(0), list.get(0).status)

                            val layoutManager = LinearLayoutManager(this@KeywordAdvertisementActivity)
                            binding.recyclerviewProductAd.layoutManager = layoutManager
                            binding.recyclerviewProductAd.adapter = adapter_product

                            var list_no_first = ArrayList<KeywordAdBean>()
                            list_no_first = list
                            list_no_first.removeAt(0)
                            adapter_product.setData(list_no_first)
                        }
                    }else{
                        runOnUiThread {
                            binding.layoutProductAdList.visibility = View.GONE
                            binding.ivProductDropDown.visibility = View.GONE
                        }
                    }


                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                    Log.d(
                        "doGetKeywordAdForProduct",
                        "JSONException：" + e.toString()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                    Log.d(
                        "doGetKeywordAdForProduct",
                        "IOException：" + e.toString()
                    )
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
                Log.d(
                    "doGetKeywordAdForProduct",
                    "ErrorResponse：" + ErrorResponse.toString()
                )
            }
        })
        web.Get_Data(url)
    }

    private fun doGetKeywordAdForShop(user_id: String, ad_type: String) {
        Log.d("doGetKeywordAdForShop", "user_id: ${user_id}, ad_type: ${ad_type} ")
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE


        var url = ApiConstants.API_HOST + "user/${user_id}/adSetting/keyword/${ad_type}/"
        val web = Web(object : WebListener {

            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var list = ArrayList<KeywordAdBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doGetKeywordAdForShop", "返回資料 resStr：" + resStr)
                    Log.d("doGetKeywordAdForShop", "返回資料 ret_val：" + ret_val)

                    val jsonObject: JSONObject = json.getJSONObject("data")
                    Log.d("doGetKeywordAdForShop", "返回資料 Object：" + jsonObject.toString())

                    var JSONArray: JSONArray = jsonObject.getJSONArray("shopList")
                    if(JSONArray.length()>0){
                        runOnUiThread {
                            binding.layoutStoreAdList.visibility = View.VISIBLE
                            binding.layoutStoreDropDown.visibility = View.VISIBLE
                        }
                        if(JSONArray.length()>0){
                            for(i in 0..JSONArray.length()-1){
                                val keywordAdBean: KeywordAdBean =
                                    Gson().fromJson(JSONArray.get(i).toString(), KeywordAdBean::class.java)
                                list.add(keywordAdBean)
                            }
                        }

                        runOnUiThread {
                            renderAdType("store_keyword", list.get(0), list.get(0).status)

                            val layoutManager = LinearLayoutManager(this@KeywordAdvertisementActivity)
                            binding.recyclerviewStoreAd.layoutManager = layoutManager
                            binding.recyclerviewStoreAd.adapter = adapter_store

                            var list_no_first = ArrayList<KeywordAdBean>()
                            list_no_first = list
                            list_no_first.removeAt(0)
                            adapter_store.setData(list_no_first)
                        }
                    }else{
                        runOnUiThread {
                            binding.layoutStoreAdList.visibility = View.GONE
                            binding.layoutStoreDropDown.visibility = View.GONE
                        }
                    }



                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                    Log.d(
                        "doGetKeywordAdForShop",
                        "JSONException：" + e.toString()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                    Log.d(
                        "doGetKeywordAdForShop",
                        "IOException：" + e.toString()
                    )
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
                Log.d(
                    "doGetKeywordAdForShop",
                    "ErrorResponse：" + ErrorResponse.toString()
                )
            }
        })
        web.Get_Data(url)
    }


    fun renderAdType(type:String, bean: KeywordAdBean, status: String){
        fun toEditPage(type:String){
            when(type) {
                "product_keyword" -> {
                    val intent =
                        Intent(this, AddEditProductKeywordAdvertisementActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("shopId", shop_id)
                    bundle.putString("mode", "edit")
                    bundle.putString("adId", bean.ad_header_id)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
                "product_recommended" -> {
                    val intent = Intent(
                        this,
                        AddEditProductRecommendedAdvertisementActivity::class.java
                    )
                    var bundle = Bundle()
                    bundle.putString("shopId", shop_id)
                    bundle.putString("mode", "edit")
                    bundle.putString("adId", bean.ad_header_id)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
                "product_market" -> {
                    val intent =
                        Intent(this, AddEditProductMarketAdvertisementActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("shopId", shop_id)
                    bundle.putString("mode", "edit")
                    bundle.putString("adId", bean.ad_header_id)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
                "store_keyword" -> {
                    val intent =
                        Intent(this, AddEditStoreKeywordAdvertisementActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("shopId", shop_id)
                    bundle.putString("mode", "edit")
                    bundle.putString("adId", bean.ad_header_id)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
                "store_recommended" -> {
                    val intent = Intent(
                        this,
                        AddEditStoreRecommendedAdvertisementActivity::class.java
                    )
                    var bundle = Bundle()
                    bundle.putString("shopId", shop_id)
                    bundle.putString("mode", "edit")
                    bundle.putString("adId", bean.ad_header_id)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
                "store_market" -> {
                    val intent =
                        Intent(this, AddEditStoreMarketAdvertisementActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("shopId", shop_id)
                    bundle.putString("mode", "edit")
                    bundle.putString("adId", bean.ad_header_id)
                    intent.putExtra("bundle", bundle)
                    startActivity(intent)
                }
            }
        }

        when(type){
            "product_keyword" ->{
                binding.tvFirstProductAdAfterDaysOver.setText(bean.count_down)
                binding.tvFirstProductAdKeywordCount.setText(bean.keyword_count)
                binding.tvFirstProductAdExpenditure.setText(bean.budget_amount)


                binding.layoutFirstProductAdStatusBtn.setOnClickListener {
                    when(bean.status){
                        "running"->{
                            doUpdateAdStatus(bean, bean.ad_header_id, bean.status, type)
                        }
                        "reviewing"->{
                            toEditPage(type)
                        }
                        "editable"->{
                            toEditPage(type)
                        }
                    }
                }

                binding.ivFirstProductAdIcon.loadNovelCover(bean.product_pic)
                binding.tvFirstProductAdName.setText(bean.product_title)

                binding.ivFirstProductAdKeyword.visibility = View.VISIBLE
                binding.tvFirstProductAdKeyword.visibility = View.VISIBLE
                binding.tvFirstProductAdKeywordCount.visibility = View.VISIBLE
                when(status){
                    "running"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_purple_7b61ff)
                        binding.ivFirstProductAdStatus.visibility = View.VISIBLE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.pause))
                        binding.ivFirstProductAdEdit.visibility = View.GONE
                    }
                    "reviewing"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                        binding.ivFirstProductAdStatus.visibility = View.GONE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.reviewing))
                        binding.ivFirstProductAdEdit.visibility = View.GONE
                    }
                    "editable"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                        binding.ivFirstProductAdStatus.visibility = View.GONE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.edit))
                        binding.ivFirstProductAdEdit.visibility = View.VISIBLE
                    }
                }
            }
            "store_keyword" ->{
                binding.tvFirstStoreAdAfterDaysOver.setText(bean.count_down)
                binding.tvFirstStoreAdKeywordCount.setText(bean.keyword_count)
                binding.tvFirstStoreAdExpenditure.setText(bean.budget_amount)

                binding.layoutFirstStoreAdStatusBtn.setOnClickListener {
                    when(bean.status){
                        "running"->{
                            doUpdateAdStatus(bean, bean.ad_header_id, bean.status, type)
                        }
                        "reviewing"->{
                            toEditPage(type)
                        }
                        "editable"->{
                            toEditPage(type)
                        }
                    }
                }


                binding.ivFirstStoreAdIcon.loadNovelCover(bean.shop_icon)
                binding.tvFirstStoreAdName.setText(bean.shop_title)

                binding.ivFirstStoreAdKeyword.visibility = View.VISIBLE
                binding.tvFirstStoreAdKeyword.visibility = View.VISIBLE
                binding.tvFirstStoreAdKeywordCount.visibility = View.VISIBLE

                when(status){
                    "running"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                        binding.ivFirstStoreAdStatus.visibility = View.VISIBLE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.pause))
                        binding.ivFirstStoreAdEdit.visibility = View.GONE
                    }
                    "reviewing"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                        binding.ivFirstStoreAdStatus.visibility = View.GONE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.reviewing))
                        binding.ivFirstStoreAdEdit.visibility = View.GONE
                    }
                    "editable"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                        binding.ivFirstStoreAdStatus.visibility = View.GONE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.edit))
                        binding.ivFirstStoreAdEdit.visibility = View.VISIBLE
                    }
                }
            }
            "product_recommended" ->{
                binding.tvFirstProductAdAfterDaysOver.setText(bean.count_down)
                binding.tvFirstProductAdKeywordCount.setText(bean.keyword_count)
                binding.tvFirstProductAdExpenditure.setText(bean.budget_amount)

                binding.layoutFirstProductAdStatusBtn.setOnClickListener {
                    when(bean.status){
                        "running"->{
                            doUpdateAdStatus(bean, bean.ad_header_id, bean.status, type)
                        }
                        "reviewing"->{
                            toEditPage(type)
                        }
                        "editable"->{
                            toEditPage(type)
                        }
                    }
                }

                binding.ivFirstProductAdIcon.loadNovelCover(bean.product_pic)
                binding.tvFirstProductAdName.setText(bean.product_title)

                binding.ivFirstProductAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstProductAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstProductAdKeywordCount.visibility = View.INVISIBLE

                when(status){
                    "running"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_purple_7b61ff)
                        binding.ivFirstProductAdStatus.visibility = View.VISIBLE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.pause))
                        binding.ivFirstProductAdEdit.visibility = View.GONE
                    }
                    "reviewing"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                        binding.ivFirstProductAdStatus.visibility = View.GONE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.reviewing))
                        binding.ivFirstProductAdEdit.visibility = View.GONE
                    }
                    "editable"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                        binding.ivFirstProductAdStatus.visibility = View.GONE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.edit))
                        binding.ivFirstProductAdEdit.visibility = View.VISIBLE
                    }
                }
            }
            "store_recommended" ->{
                binding.tvFirstStoreAdAfterDaysOver.setText(bean.count_down)
                binding.tvFirstStoreAdKeywordCount.setText(bean.keyword_count)
                binding.tvFirstStoreAdExpenditure.setText(bean.budget_amount)

                binding.layoutFirstStoreAdStatusBtn.setOnClickListener {
                    when(bean.status){
                        "running"->{
                            doUpdateAdStatus(bean, bean.ad_header_id, bean.status, type)
                        }
                        "reviewing"->{
                            toEditPage(type)
                        }
                        "editable"->{
                            toEditPage(type)
                        }
                    }
                }


                binding.ivFirstStoreAdIcon.loadNovelCover(bean.shop_icon)
                binding.tvFirstStoreAdName.setText(bean.shop_title)

                binding.ivFirstStoreAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstStoreAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstStoreAdKeywordCount.visibility = View.INVISIBLE

                when(status){
                    "running"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                        binding.ivFirstStoreAdStatus.visibility = View.VISIBLE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.pause))
                        binding.ivFirstStoreAdEdit.visibility = View.GONE
                    }
                    "reviewing"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                        binding.ivFirstStoreAdStatus.visibility = View.GONE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.reviewing))
                        binding.ivFirstStoreAdEdit.visibility = View.GONE
                    }
                    "editable"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                        binding.ivFirstStoreAdStatus.visibility = View.GONE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.edit))
                        binding.ivFirstStoreAdEdit.visibility = View.VISIBLE
                    }
                }
            }
            "product_market" ->{
                binding.tvFirstProductAdAfterDaysOver.setText(bean.count_down)
                binding.tvFirstProductAdKeywordCount.setText(bean.keyword_count)
                binding.tvFirstProductAdExpenditure.setText(bean.budget_amount)


                binding.layoutFirstProductAdStatusBtn.setOnClickListener {
                    when(bean.status){
                        "running"->{
                            doUpdateAdStatus(bean, bean.ad_header_id, bean.status, type)
                        }
                        "reviewing"->{
                            toEditPage(type)
                        }
                        "editable"->{
                            toEditPage(type)
                        }
                    }
                }

                binding.ivFirstProductAdIcon.loadNovelCover(bean.product_pic)
                binding.tvFirstProductAdName.setText(bean.product_title)

                binding.ivFirstProductAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstProductAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstProductAdKeywordCount.visibility = View.INVISIBLE

                when(status){
                    "running"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_purple_7b61ff)
                        binding.ivFirstProductAdStatus.visibility = View.VISIBLE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.pause))
                        binding.ivFirstProductAdEdit.visibility = View.GONE
                    }
                    "reviewing"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                        binding.ivFirstProductAdStatus.visibility = View.GONE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.reviewing))
                        binding.ivFirstProductAdEdit.visibility = View.GONE
                    }
                    "editable"->{
                        binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                        binding.ivFirstProductAdStatus.visibility = View.GONE
                        binding.tvFirstProductAdStatus.setText(getText(R.string.edit))
                        binding.ivFirstProductAdEdit.visibility = View.VISIBLE
                    }
                }
            }
            "store_market" ->{
                binding.tvFirstStoreAdAfterDaysOver.setText(bean.count_down)
                binding.tvFirstStoreAdKeywordCount.setText(bean.keyword_count)
                binding.tvFirstStoreAdExpenditure.setText(bean.budget_amount)

                binding.layoutFirstStoreAdStatusBtn.setOnClickListener {
                    when(bean.status){
                        "running"->{
                            doUpdateAdStatus(bean, bean.ad_header_id, bean.status, type)
                        }
                        "reviewing"->{
                            toEditPage(type)
                        }
                        "editable"->{
                            toEditPage(type)
                        }
                    }
                }

                binding.ivFirstStoreAdIcon.loadNovelCover(bean.shop_icon)
                binding.tvFirstStoreAdName.setText(bean.shop_title)

                binding.ivFirstStoreAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstStoreAdKeyword.visibility = View.INVISIBLE
                binding.tvFirstStoreAdKeywordCount.visibility = View.INVISIBLE

                when(status){
                    "running"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_hkcolor)
                        binding.ivFirstStoreAdStatus.visibility = View.VISIBLE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.pause))
                        binding.ivFirstStoreAdEdit.visibility = View.GONE
                    }
                    "reviewing"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                        binding.ivFirstStoreAdStatus.visibility = View.GONE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.reviewing))
                        binding.ivFirstStoreAdEdit.visibility = View.GONE
                    }
                    "editable"->{
                        binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_8e8e93)
                        binding.ivFirstStoreAdStatus.visibility = View.GONE
                        binding.tvFirstStoreAdStatus.setText(getText(R.string.edit))
                        binding.ivFirstStoreAdEdit.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshKeywordAd -> {
                            doGetKeywordAdForProduct(MMKV_user_id, "product")
                            doGetKeywordAdForShop(MMKV_user_id, "shop")
                    }
                }
            }, {
                it.printStackTrace()
            })

    }
    fun doUpdateAdStatus(
        bean: KeywordAdBean,
        ad_header_id: String,
        current_status: String,
        type:String)
    {
        Log.d("doUpdateAdStatus", "ad_header_id: ${ad_header_id}, current_status: ${current_status}")
        //current_status : 現在的狀態 (running、reviewing、editable)
        val url = ApiConstants.API_HOST+"user/updateAdStatus/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doUpdateAdStatus", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateAdStatus", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("廣告狀態更新成功")) {

                        bean.status = "reviewing"
                        runOnUiThread {
                            when (type) {
                                "product_keyword" -> {
                                    binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                    binding.ivFirstProductAdStatus.visibility = View.GONE
                                    binding.tvFirstProductAdStatus.setText(getText(R.string.reviewing))
                                    binding.ivFirstProductAdEdit.visibility = View.GONE
                                }
                                "store_keyword" -> {
                                    binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                    binding.ivFirstStoreAdStatus.visibility = View.GONE
                                    binding.tvFirstStoreAdStatus.setText(getText(R.string.reviewing))
                                    binding.ivFirstStoreAdEdit.visibility = View.GONE
                                }
                                "product_recommended" -> {
                                    binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                    binding.ivFirstProductAdStatus.visibility = View.GONE
                                    binding.tvFirstProductAdStatus.setText(getText(R.string.reviewing))
                                    binding.ivFirstProductAdEdit.visibility = View.GONE
                                }
                                "store_recommended" -> {
                                    binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                    binding.ivFirstStoreAdStatus.visibility = View.GONE
                                    binding.tvFirstStoreAdStatus.setText(getText(R.string.reviewing))
                                    binding.ivFirstStoreAdEdit.visibility = View.GONE
                                }
                                "product_market" -> {
                                    binding.layoutFirstProductAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                    binding.ivFirstProductAdStatus.visibility = View.GONE
                                    binding.tvFirstProductAdStatus.setText(getText(R.string.reviewing))
                                    binding.ivFirstProductAdEdit.visibility = View.GONE
                                }
                                "store_market" -> {
                                    binding.layoutFirstStoreAdStatusBtn.setBackgroundResource(R.drawable.customborder_8dp_gray_c4c4c4)
                                    binding.ivFirstStoreAdStatus.visibility = View.GONE
                                    binding.tvFirstStoreAdStatus.setText(getText(R.string.reviewing))
                                    binding.ivFirstStoreAdEdit.visibility = View.GONE
                                }
                            }
                        }
                        Log.d("doUpdateAdStatus", "ret_val: ${ret_val.toString()}")
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@KeywordAdvertisementActivity, ret_val.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                        Log.d("doUpdateAdStatus", "ret_val: ${ret_val.toString()}")
                    }

                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@KeywordAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateAdStatus", "JSONException: ${e.toString()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@KeywordAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("doUpdateAdStatus", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                com.paypal.pyplcheckout.sca.runOnUiThread {
                    Toast.makeText(this@KeywordAdvertisementActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("doUpdateAdStatus", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.doUpdateAdStatus(
            url,
            ad_header_id,
            current_status,
        )
    }

}