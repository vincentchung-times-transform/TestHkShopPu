package com.hkshopu.hk.ui.main.productBuyer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R

import com.hkshopu.hk.component.EventBuyerDetailedProductBtnStatusFirst
import com.hkshopu.hk.component.EventBuyerDetailedProductBtnStatusSecond
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityProductDetailedPageBuyerViewBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.adapter.ShippingFareAdapter
import com.hkshopu.hk.ui.main.productBuyer.adapter.SpecificationFirstSelectingAdapter
import com.hkshopu.hk.ui.main.productBuyer.adapter.SpecificationSecondSelectingAdapter
import com.hkshopu.hk.ui.main.productBuyer.adapter.LikeProductAdapter
import com.hkshopu.hk.ui.main.shoppingCart.activity.ShoppingCartActivity
import com.hkshopu.hk.ui.main.store.fragment.ShopInfoFragment
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.click
import com.hkshopu.hk.widget.view.setTextColor
import com.squareup.picasso.Picasso
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


class ProductDetailedPageBuyerViewActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    private lateinit var binding : ActivityProductDetailedPageBuyerViewBinding
    lateinit var points: ArrayList<ImageView> //指示器圖片
    val list = ArrayList<ProductImagesObjBean>()

    var MMKV_user_id : Int = 0
    var MMKV_shop_id : Int = 0
    var MMKV_product_id: Int = 0

    var product_status : String = ""


    //new parts

    var ShopDetailedProductForBuyerBean : ShopDetailedProductForBuyerBean = ShopDetailedProductForBuyerBean()
    var mutableList_pics = mutableListOf<ItemPics>()

    var mutablelist_similarProduct : MutableList<ProductDetailedPageForBuyer_RecommendedProductsBean> = mutableListOf()
    var mutablelist_otherShopProduct: MutableList<ProductDetailedPageForBuyer_RecommendedProductsBean> = mutableListOf()
    var mAdapter_likeProduct_forOtherShop = LikeProductAdapter("otherShop", this)
    var mAdapter_likeProduct_forRecommendPro = LikeProductAdapter("recommended", this)


    var detailed_product_specification_bean: DetailedProductSpecificationBean = DetailedProductSpecificationBean()
    var mutableList_first_specifications: MutableList<ItemSpecificationSeleting> = mutableListOf()
    var mAdapter_first_specifications = SpecificationFirstSelectingAdapter()
    var mutableList_second_specifications: MutableList<ItemSpecificationSeleting> = mutableListOf()
    var mAdapter_second_specifications = SpecificationSecondSelectingAdapter(false)

    var specGroup_count = 0
    var shop_follow_status = false
    var max_quantity = 0

    var first_layer_clicked = false
    var second_layer_clicked = false

    //specification _selecting
    var boolean: Boolean = false
    var position: Int = 0
    var spec_id: Int = 0
    var first_spec_name = ""
    var second_spec_name = ""
    var price_range: String = ""
    var quant_range: String = ""
    var total_quantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailedPageBuyerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)



        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 269)


        Thread(Runnable {

        }).start()



        getProductDetailedInfo("",MMKV_product_id)
        getSimilarProducts("", MMKV_product_id)
        getSameShopProducts("25", MMKV_product_id)
        GetDetailedProductSpecification(MMKV_product_id)



        initView()


    }

    fun initView() {

        binding.bottomSheetDlgOderInfoSetting.visibility = View.GONE
        binding.imgViewDialogShowBackground.visibility = View.GONE

        binding.rViewItemSpecFirst.visibility = View.VISIBLE
        binding.rViewItemSpecSecond.visibility = View.VISIBLE


        binding.rViewItemSpecFirst.layoutManager =
            GridLayoutManager(this,4)
        binding.rViewItemSpecFirst.adapter = mAdapter_first_specifications


        binding.rViewItemSpecSecond.layoutManager =
            GridLayoutManager(this,4)
        binding.rViewItemSpecSecond.adapter = mAdapter_second_specifications



        initEvent()
        initClick()

    }

    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            finish()
        }

        binding.btnAddToShoppingCart.setOnClickListener {

            val intent = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent)
            finish()
        }


        //ButtomSheetDialog Settings
        binding.btnProductSpecsSelecting.setOnClickListener {

            if (  binding.bottomSheetDlgOderInfoSetting.visibility == View.GONE){
                binding.bottomSheetDlgOderInfoSetting.visibility = View.VISIBLE
                binding.imgViewDialogShowBackground.visibility = View.VISIBLE

                binding.bottomSheetDlgOderInfoSetting.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tranlate_dialog_in))
                binding.imgViewDialogShowBackground.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in_slowly))

            }

        }
        binding.btnCancelDialog.setOnClickListener {
            if(binding.bottomSheetDlgOderInfoSetting.visibility == View.VISIBLE){
                binding.bottomSheetDlgOderInfoSetting.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tranlate_dialog_out))
                binding.imgViewDialogShowBackground.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha_out_slowly))

                binding.bottomSheetDlgOderInfoSetting.visibility = View.GONE
                binding.imgViewDialogShowBackground.visibility = View.GONE
            }
        }

        binding.btnConfirmSpecsSelecting.setOnClickListener {

            binding.bottomSheetDlgOderInfoSetting.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tranlate_dialog_out))
            binding.imgViewDialogShowBackground.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha_out_slowly))

            binding.bottomSheetDlgOderInfoSetting.visibility = View.GONE
            binding.imgViewDialogShowBackground.visibility = View.GONE

            var tvTitleSpecsFirst = binding.tvTitleSpecsFirst.text.toString()
            var tvTitleSpecsSecond = binding.tvTitleSpecsSecond.text.toString()

            binding.textViewProductPriceRange.setText(price_range.toString())

            binding.txtViewSpinnerContent.setText("${tvTitleSpecsFirst} : ${first_spec_name}"
                    + "\n"+ "${tvTitleSpecsSecond} : ${second_spec_name}" )

        }

//        binding.icMathAdd.setOnClickListener {
//
//            if(first_layer_clicked && second_layer_clicked){
//                var quant =  binding.tvValueQuantitiy.text.toString().toInt()
//
//                if(max_quantity>quant){
//                    quant += 1
//                }
//
//                binding.tvValueQuantitiy.text = quant.toString()
//
//                var total_price_abacus_setting = price_range.toInt()*quant
//                binding.buttomSheetTextViewProductPriceRange.setText(total_price_abacus_setting.toString())
//
//            }else{
//                Toast.makeText(this, "請先選取規格", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//        binding.icMathSubtract.setOnClickListener {
//            if(first_layer_clicked && second_layer_clicked){
//                var quant =  binding.tvValueQuantitiy.text.toString().toInt()
//
//                if(quant>1){
//                    quant -= 1
//                }
//                binding.tvValueQuantitiy.text = quant.toString()
//
//                var total_price_abacus_setting = price_range.toInt()*quant
//                binding.buttomSheetTextViewProductPriceRange.setText(total_price_abacus_setting.toString())
//
//            }else{
//                Toast.makeText(this, "請先選取規格", Toast.LENGTH_SHORT).show()
//            }
//
//        }

    }


    private fun setBoardingData() {

        for ( i in 0..mutableList_pics.size-1){
            list.add(ProductImagesObjBean(mutableList_pics.get(i).bitmap, mutableList_pics.get(i).bitmap))
        }


    }


    private fun initViewPager() {

        val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
        val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

        if(width.equals(1080)){
            val params_layout_product: ViewGroup.LayoutParams = binding.cardViewProductsPicsPager.getLayoutParams()
            var width_scaling =  (width*345)/375
            params_layout_product.width = width_scaling
            params_layout_product.height = width_scaling
            runOnUiThread {
                binding.cardViewProductsPicsPager.setLayoutParams(params_layout_product)
            }

        }

        runOnUiThread {
            binding.productPicsPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {

                }

            })

            binding.productPicsPager.adapter = ImageAdapter(list)
            binding.productPicsPager.addOnPageChangeListener(this)
        }

        initPoints()

    }

    private fun initPoints() {
        points = arrayListOf()
        for (i in 0 until list.size) {
            val point = ImageView(this)
            point.setPadding(10, 10, 10, 10)
            point.scaleType = ImageView.ScaleType.FIT_XY

            if (i == 0) {
                point.setImageResource(R.drawable.selected_points_products)
                point.layoutParams = ViewGroup.LayoutParams(96, 36)
            } else {
                point.setImageResource(R.drawable.unselected_points_products)
                point.layoutParams = ViewGroup.LayoutParams(36, 36)
            }

            runOnUiThread {
                binding.productPicsIndicator.addView(point)
            }

            points.add(point)
        }
    }

    private class ImageAdapter internal constructor(
        arrayList: ArrayList<ProductImagesObjBean>
    ) : PagerAdapter() {
        private val arrayList: ArrayList<ProductImagesObjBean>

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.boarding_view, null)
            val ImagesObj: ProductImagesObjBean = arrayList[position]
            val imageView = view.findViewById<View>(R.id.image_view) as ImageView
            imageView.setImageBitmap(ImagesObj.front_pic)

            if (position == 0) {
                imageView.scaleType = ImageView.ScaleType.FIT_XY

            } else {
                imageView.scaleType = ImageView.ScaleType.FIT_XY

            }

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return arrayList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        init {
            this.arrayList = arrayList
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View?)
        }

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

        for (i in 0 until points.size) {
            val params = points[position].layoutParams
            params.width = 96
            params.height = 36
            points[position].layoutParams = params
            points[position].setImageResource(R.drawable.selected_points_products)

            if (position != i) {
                val params1 = points[i].layoutParams
                params1.width = 36
                params1.height = 36
                points[i].layoutParams = params1
                points[i].setImageResource(R.drawable.unselected_points_products)

            }
        }

    }

    override fun onPageScrollStateChanged(state: Int) {

    }


    private fun getProductDetailedInfo(user_id: String, product_id: Int) {

        val url = ApiConstants.API_HOST+"user/${user_id}/topProductDetail/${product_id}/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductInfoBean>()
//                val product_id_list = ArrayList<String>()
                try {

                    runOnUiThread {
                        binding.progressBarDetailedProductForBuyer.visibility = View.VISIBLE
                        binding.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.VISIBLE
                    }

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getDetailedProductInfo", "返回資料 resStr：" + resStr)
                    Log.d("getDetailedProductInfo", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getDetailedProductInfo", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                            ShopDetailedProductForBuyerBean = Gson().fromJson(
                                jsonObject.toString(),
                                ShopDetailedProductForBuyerBean::class.java
                            )

                        }

                        Log.d("getDetailedProductInfo", "返回資料 ShopDetailedProductForBuyerBean：" + ShopDetailedProductForBuyerBean.toString())
                        Log.d("getDetailedProductInfo", "返回資料 ShopDetailedProductForBuyerBean.pic ：" + ShopDetailedProductForBuyerBean.pic.toString())


                        if(ShopDetailedProductForBuyerBean.pic.isNotEmpty()){
                            for (i in 0..ShopDetailedProductForBuyerBean.pic.size - 1) {

                                mutableList_pics.add(
                                    ItemPics(
                                        getBitmapFromURL(
                                            ShopDetailedProductForBuyerBean.pic.get(
                                                i
                                            )
                                        )!!, R.mipmap.cover_pic
                                    )
                                )
                            }
                        }else{
                            mutableList_pics.add(
                                ItemPics(
                                    getBitmapFromURL("https://st4.depositphotos.com/14953852/24787/v/600/depositphotos_247872612-stock-illustration-no-image-available-icon-vector.jpg")!!, R.mipmap.cover_pic
                                )
                            )
                        }

                        setBoardingData()

                        binding.txtViewAverageRating.setText(ShopDetailedProductForBuyerBean.average_rating.toString())
                        if(ShopDetailedProductForBuyerBean.average_rating>4.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star_half)
                            }
                        }else if (ShopDetailedProductForBuyerBean.average_rating>3.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>3.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star_half)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>2.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>2.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star_half)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>1.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>1.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star_half)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>0.75){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(ShopDetailedProductForBuyerBean.average_rating>0.25){
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star_half)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }else{
                            runOnUiThread {
                                binding.ivStar01.setImageResource(R.mipmap.ic_star)
                                binding.ivStar02.setImageResource(R.mipmap.ic_star)
                                binding.ivStar03.setImageResource(R.mipmap.ic_star)
                                binding.ivStar04.setImageResource(R.mipmap.ic_star)
                                binding.ivStar05.setImageResource(R.mipmap.ic_star)
                            }
                        }




                        var min_quantity = ""
                        var max_quantity = ""
                        var quantity_range = ""
                        if(ShopDetailedProductForBuyerBean.min_quantity.toString().length>=3){
                            var one_thous = 1000
                            var float = ShopDetailedProductForBuyerBean.min_quantity.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()

                            min_quantity = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"

                        }else{

                            min_quantity =  "${ShopDetailedProductForBuyerBean.selling_count.toString()}"

                        }

                        if(ShopDetailedProductForBuyerBean.max_quantity.toString().length>=3){
                            var one_thous = 1000
                            var float = ShopDetailedProductForBuyerBean.selling_count.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()

                            max_quantity = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"

                        }else{

                            max_quantity =  "${ShopDetailedProductForBuyerBean.selling_count.toString()}"

                        }

                        if(ShopDetailedProductForBuyerBean.product_spec_on.equals("y")){

                            quantity_range = "${min_quantity}-${max_quantity}"

                            runOnUiThread {
                                binding.tvQuantity.setText(quantity_range)
                            }

                        }else{

                            runOnUiThread {
                                binding.tvQuantity.setText(max_quantity)
                            }

                        }


                        if(ShopDetailedProductForBuyerBean.selling_count.toString().length>=3){
                            var one_thous = 1000
                            var float = ShopDetailedProductForBuyerBean.selling_count.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.textViewSoldQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.textViewSoldQuantity.text =  "${ShopDetailedProductForBuyerBean.selling_count.toString()}"
                            }
                        }

                        if(ShopDetailedProductForBuyerBean.liked_count.toString().length>=3){
                            var one_thous = 1000
                            var float = ShopDetailedProductForBuyerBean.liked_count.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.textViewLike.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.textViewLike.text =  "${ShopDetailedProductForBuyerBean.liked_count.toString()}"
                            }
                        }


                        if(ShopDetailedProductForBuyerBean.min_price.equals(ShopDetailedProductForBuyerBean.max_price)){
                            runOnUiThread {
                                binding.textViewProductPriceRange.setText("${ShopDetailedProductForBuyerBean.max_price.toString()}")
                            }

                        }else {
                            runOnUiThread {
                                binding.textViewProductPriceRange.setText("${ShopDetailedProductForBuyerBean.min_price}-${
                                    ShopDetailedProductForBuyerBean.max_price
                                }")
                            }
                        }

                        if(ShopDetailedProductForBuyerBean.product_spec_on.equals("y")){
                            runOnUiThread {
                                binding.btnProductSpecsSelecting.visibility = View.VISIBLE
                            }

                        }else{
                            runOnUiThread {
                                binding.btnProductSpecsSelecting.visibility = View.GONE
                            }
                        }

                        binding.tvValueTimeForStocking.setText(ShopDetailedProductForBuyerBean.longterm_stock_up.toString())

                        runOnUiThread {
                            binding.textViewProductName.setText(ShopDetailedProductForBuyerBean.product_title.toString())
                            binding.textViewProductInformation.setText(ShopDetailedProductForBuyerBean.product_description.toString())

                            binding.textViewSeletedCategory.setText(
                                ShopDetailedProductForBuyerBean.category
                            )
                        }

                        initViewPager()

                        runOnUiThread {



                            var like = "N"

                            if(ShopDetailedProductForBuyerBean.liked.equals("Y")){
                                binding.btnDetailedProductForBuyerLike.setImageResource(R.mipmap.btn_detailed_product_for_buyer_like_colorful)
                                like = "Y"
                            }else{
                                binding.btnDetailedProductForBuyerLike.setImageResource(R.mipmap.btn_detailed_product_for_buyer_like_colorless)
                                like = "N"
                            }

                            binding.btnDetailedProductForBuyerLike.setOnClickListener {

                                if(like.equals("Y")){
                                    binding.btnDetailedProductForBuyerLike.setImageResource(R.mipmap.btn_detailed_product_for_buyer_like_colorless)
                                    like = "N"
                                }else{
                                    binding.btnDetailedProductForBuyerLike.setImageResource(R.mipmap.btn_detailed_product_for_buyer_like_colorful)
                                    like = "Y"
                                }

                                var MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)

                                doLikeProductForBuyer(25, 269, like)

                            }
                        }

                        runOnUiThread {
                            binding.textViewShippingFareRange.setText("HKD${ShopDetailedProductForBuyerBean.min_shipment.toString()} - HKD${ShopDetailedProductForBuyerBean.max_shipment.toString()}")

                        }

                        if (ShopDetailedProductForBuyerBean.new_secondhand == "new") {
                            runOnUiThread {
                                binding.statusLebal.setImageResource(R.mipmap.new_lebal)
                            }

                        } else {
                            runOnUiThread {
                                binding.statusLebal.setImageResource(R.mipmap.secondhand_lebal)

                            }
                        }

                    }

                    runOnUiThread {
                        binding.progressBarDetailedProductForBuyer.visibility = View.GONE
                        binding.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE

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


    private fun getSimilarProducts(user_id: String, product_id: Int) {

        val url = ApiConstants.API_HOST+"product/similar_product_list/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    runOnUiThread {
                        binding.progressBarDetailedProductForBuyer.visibility = View.VISIBLE
                        binding.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.VISIBLE
                    }

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getRecommendedProducts", "返回資料 resStr：" + resStr)
                    Log.d("getRecommendedProducts", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getRecommendedProducts", "返回資料 jsonArray：" + jsonArray.toString())

                        if( jsonArray.length()>2 ){
                            //只取前三項"推薦"產品
                            for (i in 0 until 3) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                mutablelist_similarProduct.add(Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductDetailedPageForBuyer_RecommendedProductsBean::class.java
                                ))

                            }
                        }else{
                            for (i in 0 until jsonArray.length()) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                mutablelist_similarProduct.add(Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductDetailedPageForBuyer_RecommendedProductsBean::class.java
                                ))

                            }
                        }

                        Log.d("getRecommendedProducts", "返回資料 mutablelist_recommendedProduct：" + mutablelist_similarProduct.toString())


                    }

                    runOnUiThread {

                        binding.recyclerviewRecommendedProducts.layoutManager =
                            LinearLayoutManager(this@ProductDetailedPageBuyerViewActivity, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerviewRecommendedProducts.adapter = mAdapter_likeProduct_forRecommendPro

                        mAdapter_likeProduct_forRecommendPro.setData(mutablelist_similarProduct)


                    }

                    runOnUiThread {
                        binding.progressBarDetailedProductForBuyer.visibility = View.GONE
                        binding.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE

                    }

                } catch (e: JSONException) {


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.getSimilarProducts(url, user_id, product_id)
    }

    private fun getSameShopProducts(user_id: String, product_id: Int) {

        val url = ApiConstants.API_HOST+"product/same_shop_product/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    runOnUiThread {
                        binding.progressBarDetailedProductForBuyer.visibility = View.VISIBLE
                        binding.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.VISIBLE
                    }

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getSameShopProducts", "返回資料 resStr：" + resStr)
                    Log.d("getSameShopProducts", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getSameShopProducts", "返回資料 jsonArray：" + jsonArray.toString())

                        if( jsonArray.length()>2 ){
                            //只取前三項"推薦"產品
                            for (i in 0 until 3) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                mutablelist_otherShopProduct.add(Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductDetailedPageForBuyer_RecommendedProductsBean::class.java
                                ))

                            }
                        }else{
                            for (i in 0 until jsonArray.length()) {

                                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                mutablelist_otherShopProduct.add(Gson().fromJson(
                                    jsonObject.toString(),
                                    ProductDetailedPageForBuyer_RecommendedProductsBean::class.java
                                ))

                            }
                        }

                        runOnUiThread {

                            Picasso.with(this@ProductDetailedPageBuyerViewActivity).load(mutablelist_otherShopProduct.get(0).shop_icon).into( binding.ivSameShopIcon)

                            binding.tvSameShopTitle.setText(
                                mutablelist_otherShopProduct.get(0).shop_title
                            )
                            binding.tvSameShopRating.setText(
                                mutablelist_otherShopProduct.get(0).shop_rating.toString()
                            )
                            binding.tvSameShopFollowCount.setText(
                                mutablelist_otherShopProduct.get(0).follow_count.toString()
                            )

                        }

                        if(mutablelist_otherShopProduct.get(0).shop_rating>4.25){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star_half)
                            }
                        }else if (mutablelist_otherShopProduct.get(0).shop_rating>3.75){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>3.25){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star_half)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>2.75){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>2.25){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star_half)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>1.75){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>1.25){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star_half)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>0.75){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_fill)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else if(mutablelist_otherShopProduct.get(0).shop_rating>0.25){
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star_half)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }else{
                            runOnUiThread {
                                binding.ivSameShopRating01.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating02.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating03.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating04.setImageResource(R.mipmap.ic_star)
                                binding.ivSameShopRating05.setImageResource(R.mipmap.ic_star)
                            }
                        }

                        Log.d("getSameShopProducts", "返回資料 mutablelist_otherShopProduct：" + mutablelist_otherShopProduct.toString())


                        if(mutablelist_otherShopProduct.get(0).followed.equals("Y")){
                            shop_follow_status = true
                            runOnUiThread {
                                binding.btnShopSubscribing.setImageResource(R.mipmap.btn_shop_subscribed)

                            }

                        }else{
                            shop_follow_status = false
                            runOnUiThread {
                                binding.btnShopSubscribing.setImageResource(R.mipmap.btn_shop_subscribing)
                            }
                        }
                        binding.btnShopSubscribing.setOnClickListener {

                            if(shop_follow_status){
                                doFollowShopForBuyer(25, 127, "N")
                                shop_follow_status = false
                                runOnUiThread {
                                    binding.btnShopSubscribing.setImageResource(R.mipmap.btn_shop_subscribing)
                                }

                            }else{
                                doFollowShopForBuyer(25, 127, "Y")
                                shop_follow_status = true
                                runOnUiThread {
                                    binding.btnShopSubscribing.setImageResource(R.mipmap.btn_shop_subscribed)
                                }

                            }

                        }
                        runOnUiThread {

                            binding.recyclerviewOhtersShopProducts.layoutManager =
                                LinearLayoutManager(this@ProductDetailedPageBuyerViewActivity, LinearLayoutManager.HORIZONTAL, false)
                            binding.recyclerviewOhtersShopProducts.adapter = mAdapter_likeProduct_forOtherShop

                            mAdapter_likeProduct_forOtherShop.setData(mutablelist_otherShopProduct)

                        }

                    }


                    runOnUiThread {
                        binding.progressBarDetailedProductForBuyer.visibility = View.GONE
                        binding.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE

                    }

                } catch (e: JSONException) {


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.getSimilarProducts(url, user_id, product_id)
    }
    //計算費用最大最小範圍
//    fun pick_max_and_min_num(): String {
//        //挑出最大與最小的數字
//        var min: Int =  productInfoList.min_price.toInt()
//        var max: Int =productInfoList.max_price.toInt()
//
//        return "HKD$${min}-${max}"
//    }

    fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    @SuppressLint("CheckResult")
    fun initEvent() {


        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventBuyerDetailedProductBtnStatusFirst -> {

                        first_layer_clicked = true

                        boolean = it.boolean
                        position = it.position
                        first_spec_name = it.spec_name
                        price_range = it.price_range
                        quant_range = it.quant_range
                        total_quantity = it.total_quant

                        mAdapter_second_specifications = SpecificationSecondSelectingAdapter(first_layer_clicked)

                        runOnUiThread {

                            binding.rViewItemSpecSecond.layoutManager =
                                LinearLayoutManager(this@ProductDetailedPageBuyerViewActivity, LinearLayoutManager.HORIZONTAL, false)
                            binding.rViewItemSpecSecond.adapter = mAdapter_second_specifications

                        }

                        mAdapter_second_specifications.setDatas(
                            getSecondLayerSpecs(position)
                        )


                        runOnUiThread {
                            binding.buttomSheetTextViewProductPriceRange.setText(price_range)
                            binding.tvValueProductQuantity.setText(quant_range)
                            max_quantity = total_quantity
                        }


                        if(boolean){

                            if (specGroup_count.equals(1)){

                                var first_layer_selected_count = false

                                for(i in 0..mAdapter_first_specifications.get_spec_list().size-1){
                                    if(  mAdapter_first_specifications.get_spec_list().get(i).seleted_status){
                                        first_layer_selected_count = true
                                    }
                                }

                                if(first_layer_selected_count){
                                    binding.btnConfirmSpecsSelecting.isEnabled = true
                                    binding.btnConfirmSpecsSelecting.setImageResource(R.mipmap.btn_confirm_specs_selecting_enable)

                                }else{

                                    binding.btnConfirmSpecsSelecting.isEnabled = false
                                    binding.btnConfirmSpecsSelecting.setImageResource(R.mipmap.btn_confirm_specs_selecting_disable)

                                }

                            }else{

                                var first_layer_selected_count = false
                                var second_layer_selected_count = false

                                for(i in 0..mAdapter_first_specifications.get_spec_list().size-1){
                                    if(  mAdapter_first_specifications.get_spec_list().get(i).seleted_status){
                                        first_layer_selected_count = true
                                    }
                                }

                                for(i in 0..mAdapter_second_specifications.get_spec_list().size-1){
                                    if(  mAdapter_second_specifications.get_spec_list().get(i).seleted_status){
                                        second_layer_selected_count = true
                                    }
                                }

                                if(first_layer_selected_count&&second_layer_selected_count){
                                    binding.btnConfirmSpecsSelecting.isEnabled = true
                                    binding.btnConfirmSpecsSelecting.setImageResource(R.mipmap.btn_confirm_specs_selecting_enable)

                                }else{

                                    binding.btnConfirmSpecsSelecting.isEnabled = false
                                    binding.btnConfirmSpecsSelecting.setImageResource(R.mipmap.btn_confirm_specs_selecting_disable)

                                }

                            }

                        }

                    }
                    is EventBuyerDetailedProductBtnStatusSecond-> {
                        second_layer_clicked = true

                        if(first_layer_clicked){

                            boolean = it.boolean
                            position = it.position
                            spec_id = it.spec_id
                            second_spec_name = it.spec_name
                            price_range = it.price_range
                            quant_range = it.quant_range
                            total_quantity = it.total_quant


                            runOnUiThread {
                                binding.buttomSheetTextViewProductPriceRange.setText(price_range)
                                binding.tvValueProductQuantity.setText(quant_range)
                                max_quantity = total_quantity
                            }

                            if(boolean){
                                var first_layer_selected_count = false
                                var second_layer_selected_count = false

                                for(i in 0..mAdapter_first_specifications.get_spec_list().size-1){
                                    if(  mAdapter_first_specifications.get_spec_list().get(i).seleted_status){
                                        first_layer_selected_count = true
                                    }
                                }

                                for(i in 0..mAdapter_second_specifications.get_spec_list().size-1){
                                    if(  mAdapter_second_specifications.get_spec_list().get(i).seleted_status){
                                        second_layer_selected_count = true
                                    }
                                }

                                if(first_layer_selected_count && second_layer_selected_count){
                                    binding.btnConfirmSpecsSelecting.isEnabled = true
                                    binding.btnConfirmSpecsSelecting.setImageResource(R.mipmap.btn_confirm_specs_selecting_enable)

                                }else{

                                    binding.btnConfirmSpecsSelecting.isEnabled = false
                                    binding.btnConfirmSpecsSelecting.setImageResource(R.mipmap.btn_confirm_specs_selecting_disable)

                                }
                            }
                        }

                    }
                }
            }, {
                it.printStackTrace()
            })

    }

    private fun doFollowShopForBuyer (user_id: Int , shop_id: Int, follow: String) {

        val url = ApiConstants.API_HOST+"user/${user_id}/followShop/${shop_id}/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {


                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doFollowShopForBuyer", "返回資料 resStr：" + resStr)
                    Log.d("doFollowShopForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("收藏成功")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("doFollowShopForBuyer", "返回資料 jsonArray：" + jsonArray.toString())


                        runOnUiThread {
                            Toast.makeText(
                                this@ProductDetailedPageBuyerViewActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT).show()

                        }

                    }else{


                        runOnUiThread {
                            Toast.makeText(
                                this@ProductDetailedPageBuyerViewActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT).show()

                        }

                    }



                } catch (e: JSONException) {


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.doFollowShopForBuyer(url, user_id, shop_id, follow)
    }


    private fun doLikeProductForBuyer (user_id: Int , product_id: Int, like: String) {

        val url = ApiConstants.API_HOST+"product/like_product/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {


                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doLikeProductForBuyer", "返回資料 resStr：" + resStr)
                    Log.d("doLikeProductForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("商品收藏成功!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("doLikeProductForBuyer", "返回資料 jsonArray：" + jsonArray.toString())





                        runOnUiThread {
                            Toast.makeText(
                                this@ProductDetailedPageBuyerViewActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT).show()

                        }


                    }else{


                        runOnUiThread {
                            Toast.makeText(
                                this@ProductDetailedPageBuyerViewActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT).show()

                        }

                    }



                } catch (e: JSONException) {


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.doLikeProductForBuyer(url, user_id, product_id, like)
    }

    private fun GetDetailedProductSpecification (product_id: Int) {

        val url = ApiConstants.API_HOST+"product/${product_id}/get_specification_of_product/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {


                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("GetDetailedProductSpecification", "返回資料 resStr：" + resStr)
                    Log.d("GetDetailedProductSpecification", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("取得產品規格成功!")) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d(
                            "GetDetailedProductSpecification",
                            "返回資料 jsonObject：" + jsonObject.toString()
                        )

                        detailed_product_specification_bean = Gson().fromJson(
                            jsonObject.toString(),
                            DetailedProductSpecificationBean::class.java
                        )

                        runOnUiThread {
                            binding.tvTitleSpecsFirst.setText(detailed_product_specification_bean.spec_desc_1.toString())
                            binding.tvTitleSpecsSecond.setText(detailed_product_specification_bean.spec_desc_2.toString())
                        }


                        var total_quantity = {first_position:Int,second_layer_size:Int->
                            var total:Int = 0

                            for(i in 0 until second_layer_size){
                                total+=detailed_product_specification_bean.quantity.get(first_position).get(i)
                            }

                           total
                        }

                        for (i in 0..detailed_product_specification_bean.spec_dec_1_items.size-1){
                            mutableList_first_specifications.add(
                                ItemSpecificationSeleting(
                                    0,
                                    detailed_product_specification_bean.spec_dec_1_items.get(i).toString(),
                                    pick_max_and_min_value(detailed_product_specification_bean.price.get(i)),
                                    pick_max_and_min_value(detailed_product_specification_bean.quantity.get(i)),
                                    total_quantity(i, detailed_product_specification_bean.quantity.get(i).size),
                                false

                                )
                            )
                        }

                        for (i in 0..detailed_product_specification_bean.spec_dec_2_items.size-1){
                            mutableList_second_specifications.add(
                                ItemSpecificationSeleting(
                                    detailed_product_specification_bean.id.get(0).get(i),
                                    detailed_product_specification_bean.spec_dec_2_items.get(i).toString(),
                                    detailed_product_specification_bean.price.get(0).get(i).toString(),
                                    detailed_product_specification_bean.quantity.get(0).get(i).toString(),
                                    detailed_product_specification_bean.quantity.get(0).get(i),
                                    false
                                )
                            )
                        }



                        runOnUiThread {
                            mAdapter_first_specifications.setDatas(
                                mutableList_first_specifications
                            )
                            mAdapter_second_specifications.setDatas(
                                mutableList_second_specifications
                            )
                        }

                        if (mutableList_first_specifications.size > 0 && mutableList_second_specifications.size == 0) {

                            runOnUiThread {
                                binding.containerSpecsSecondSelectingLayer.visibility = View.GONE
                            }

                            specGroup_count = 1

                        } else {

                            runOnUiThread {
                                binding.containerSpecsSecondSelectingLayer.visibility = View.VISIBLE
                            }

                            specGroup_count = 2
                        }


                    }


                    runOnUiThread {
                        Toast.makeText(
                            this@ProductDetailedPageBuyerViewActivity,
                            ret_val.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

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

    fun getSecondLayerSpecs(position:Int): MutableList<ItemSpecificationSeleting> {

        mutableList_second_specifications.clear()

        for (i in 0..detailed_product_specification_bean.spec_dec_2_items.size-1){
            mutableList_second_specifications.add(
                ItemSpecificationSeleting(
                    detailed_product_specification_bean.id.get(position).get(i),
                    detailed_product_specification_bean.spec_dec_2_items.get(i).toString(),
                    detailed_product_specification_bean.price.get(position).get(i).toString(),
                    detailed_product_specification_bean.quantity.get(position).get(i).toString(),
                    detailed_product_specification_bean.quantity.get(position).get(i),
                    false,
                )
            )
        }
        return mutableList_second_specifications

    }


    //計算費用最大最小範圍
    fun pick_max_and_min_value(list:MutableList<Int>): String {
        //挑出最大與最小的數字

        if(list.size>0){
            //挑出最大與最小的數字
            var min: Int =list[0].toInt()
            var max: Int =list[0].toInt()

            for (f in 1..list.size-1) {
                if(list[f].toInt() >= min ){
                    max = list[f].toInt()
                }else{
                    min = list[f].toInt()
                }
            }

            return "${min}-${max}"
        }else{
            return ""
        }

    }
}

