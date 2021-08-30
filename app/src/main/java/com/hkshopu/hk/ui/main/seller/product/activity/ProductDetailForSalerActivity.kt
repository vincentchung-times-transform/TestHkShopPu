package com.HKSHOPU.hk.ui.main.seller.product.activity

import MyLinearLayoutManager
import android.R.attr.height
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventMyStoreFragmentRefresh
import com.HKSHOPU.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityMerchandiseBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.login.vm.ShopVModel
import com.HKSHOPU.hk.ui.main.seller.product.adapter.InventoryAndPriceFirstLayerNestedAdapter
import com.HKSHOPU.hk.ui.main.seller.product.fragment.EditProductRemindDialogFragment
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL


class ProductDetailForSalerActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    private lateinit var binding : ActivityMerchandiseBinding
    lateinit var points: ArrayList<ImageView> //指示器圖片
    val list = ArrayList<ProductImagesObjBean>()

    private val VM = ShopVModel()
    lateinit var productInfoBean :  ProductInfoBean
    var mutableList_pics = mutableListOf<ItemPics>()

    var MMKV_product_id: String = ""
    var product_status : String = ""

    var hkd_dollarSign = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMerchandiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hkd_dollarSign = getResources().getString(R.string.hkd_dollarSign)

        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

        getProductInfo(MMKV_product_id)

        initVM()
        initView()

    }

    fun initView() {
        initClick()
    }

    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            RxBus.getInstance().post(EventMyStoreFragmentRefresh())
            finish()
        }

        binding.btnEditmerchandise.setOnClickListener {
            EditProductRemindDialogFragment(this, MMKV_product_id).show(supportFragmentManager, "MyCustomFragment")
        }

        binding.btnLaunch.setOnClickListener {
           when(product_status){
               "active"->{
                    VM.updateProductStatus(this, MMKV_product_id, "draft")

               }
               "draft"->{
                   VM.updateProductStatus(this, MMKV_product_id, "active")

               }
           }
        }
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
            val params_layout_product: ViewGroup.LayoutParams = binding.productPicsPager.getLayoutParams()
            var width_scaling =  (width*345)/375
            params_layout_product.width = width_scaling
            params_layout_product.height = width_scaling
            runOnUiThread {
                binding.productPicsPager.setLayoutParams(params_layout_product)
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

            binding.productPicsPager.adapter = ProductDetailForSalerActivity.ImageAdapter(list)
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

    private fun getProductInfo(product_id: String) {

        val url = ApiConstants.API_HOST+"product/${product_id}/product_info_forAndroid/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {

                    runOnUiThread {
                        binding.progressBarMerchandise.visibility = View.VISIBLE
                        binding.imgViewLoadingBackgroundMerchandise.visibility = View.VISIBLE
                    }

                    resStr = response.body()!!.string()

                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("getProductInfo", "返回資料 resStr：" + resStr)
                    Log.d("getProductInfo", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("已取得商品資訊!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getProductInfo", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            productInfoBean = Gson().fromJson(
                                jsonObject.toString(),
                                ProductInfoBean::class.java
                            )

                        }
                        Log.d("getProductInfo", "返回資料 productInfoBean：" + productInfoBean.toString())

                        if(productInfoBean.pic_path.isNotEmpty()){
                            for (i in 0..productInfoBean.pic_path.size - 1) {

                                mutableList_pics.add(
                                    ItemPics(
                                        getBitmapFromURL(
                                            productInfoBean.pic_path.get(
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

                        if(productInfoBean.product_spec_on.equals("y")){

                            if(productInfoBean.sum_quantity.toString().length>=3){
                                var one_thous = 1000
                                var float = productInfoBean.sum_quantity.toDouble()/one_thous.toDouble()
                                var bigDecimal = float.toBigDecimal()
                                runOnUiThread {
                                    binding.tvQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                                }

                            }else{
                                runOnUiThread {
                                    binding.tvQuantity.text =  "${productInfoBean.sum_quantity.toString()}"
                                }
                            }

                        }else{
                            runOnUiThread {

                                if(productInfoBean.quantity.toString().length>=3){
                                    var one_thous = 1000
                                    var float = productInfoBean.quantity.toDouble()/one_thous.toDouble()
                                    var bigDecimal = float.toBigDecimal()
                                    runOnUiThread {
                                        binding.tvQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                                    }

                                }else{
                                    runOnUiThread {
                                        binding.tvQuantity.text =  "${productInfoBean.quantity.toString()}"
                                    }
                                }
                            }
                        }


                        if(productInfoBean.sold_quantity.toString().length>=3){

                            var one_thous = 1000
                            var float = productInfoBean.sold_quantity.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.textViewSoldQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.textViewSoldQuantity.text =  "${productInfoBean.sold_quantity.toString()}"
                            }
                        }

                        if(productInfoBean.like.toString().length>=3){
                            var one_thous = 1000
                            var float = productInfoBean.like.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.textViewLike.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.textViewLike.text =  "${productInfoBean.like.toString()}"
                            }
                        }


                        runOnUiThread {
                            binding.tvLongestStockUpDays.text =  "${productInfoBean.longterm_stock_up.toString()}"
                        }


                        runOnUiThread {

                            when(productInfoBean.product_status){
                                "active"->{

                                    runOnUiThread {
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_draft)
                                    }
                                    product_status = productInfoBean.product_status
                                }
                                "draft"->{

                                    runOnUiThread {
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_launch)
                                    }
                                    product_status = productInfoBean.product_status
                                }
                            }


                            if(productInfoBean.product_spec_on.equals("y")){
                                runOnUiThread {
                                    binding.textViewProductPriceRange.setText("${hkd_dollarSign}${productInfoBean.min_price}-${
                                        productInfoBean.max_price
                                    }")
                                }

                            }else{
                                runOnUiThread {
                                    binding.textViewProductPriceRange.setText("${hkd_dollarSign}${productInfoBean.product_price.toString()}")
                                }
                            }

                            if(productInfoBean.shipment_min_price == productInfoBean.shipment_max_price){
                                runOnUiThread {
                                    binding.textViewShippingFareRange.setText("${productInfoBean.shipment_max_price}")
                                }
                            }else{
                                runOnUiThread {
                                    binding.textViewShippingFareRange.setText("${productInfoBean.shipment_min_price}-${
                                        productInfoBean.shipment_max_price
                                    }")
                                }
                            }

                            runOnUiThread {
                                binding.textViewProductName.setText(productInfoBean.product_title.toString())
                                binding.textViewProductInformation.setText(productInfoBean.product_description.toString())

                                binding.textViewSeletedCategory.setText(
                                    "${productInfoBean.c_product_category} > ${
                                        productInfoBean.c_sub_product_category
                                    }"
                                )
                            }

                            initViewPager()

                            if(productInfoBean.product_spec_on.equals("y")){

                                runOnUiThread {
                                    binding.rViewInventory.visibility = View.VISIBLE
                                }

                                val mAdapter = InventoryAndPriceFirstLayerNestedAdapter()
                                var mutableList_spec = productInfoBean.spec_dec_1_items
                                var mutableList_size = productInfoBean.spec_dec_2_items

                                var specGroup_only:Boolean = false
                                var datas_spec_title_first = productInfoBean.spec_desc_1
                                var datas_spec_title_second = productInfoBean.spec_desc_2
                                var datas_spec_size = productInfoBean.spec_dec_1_items.size
                                var datas_size_size = productInfoBean.spec_dec_2_items.size
                                var mutableList_price = productInfoBean.price
                                var mutableList_quant = productInfoBean.spec_quantity
                                var mutableList_sold_quant = productInfoBean.spec_sold_quantity
                                var datas_price_size = 0
                                var datas_quant_size = 0

                                for(i in 0..productInfoBean.price.size-1){
                                    for(j in 0..productInfoBean.price.get(i).size-1){
                                        datas_price_size  = datas_price_size+1
                                    }
                                }
                                for(i in 0..productInfoBean.spec_quantity.size-1){
                                    for(j in 0..productInfoBean.spec_quantity.get(i).size-1){
                                        datas_quant_size  = datas_quant_size+1
                                    }
                                }

                                var mutableList_first_layer = mutableListOf<ItemInvenFirstNestedLayer>()

                                if(!datas_spec_title_first.equals("") && datas_spec_title_second.equals("")){

                                    specGroup_only = true

                                        var mutableList_second_layer = mutableListOf<ItemInvenSecondNestedLayer>()

                                        for(i in 0..datas_spec_size-1){
                                            mutableList_second_layer.add(ItemInvenSecondNestedLayer(mutableList_spec.get(i),"", "", "") )
                                        }
                                        mutableList_first_layer.add(ItemInvenFirstNestedLayer(datas_spec_title_first, "", mutableList_spec.get(0), mutableList_second_layer))

                                        for(i in 0..datas_spec_size-1){
                                            mutableList_first_layer.get(0).mutableList_itemInvenSecondLayer.get(i).price = mutableList_price.get(i).get(0).toString()
                                            mutableList_first_layer.get(0).mutableList_itemInvenSecondLayer.get(i).quantity = mutableList_quant.get(i).get(0).toString()
                                            mutableList_first_layer.get(0).mutableList_itemInvenSecondLayer.get(i).sold_quantity = mutableList_sold_quant.get(i).get(0).toString()
                                        }

                                }else{

                                    specGroup_only = false

                                    for(i in 0..datas_spec_size-1){
                                        var mutableList_second_layer = mutableListOf<ItemInvenSecondNestedLayer>()
                                        for(i in 0..datas_size_size-1){
                                            mutableList_second_layer.add(ItemInvenSecondNestedLayer(mutableList_size.get(i),"", "", "") )
                                        }
                                        mutableList_first_layer.add(ItemInvenFirstNestedLayer(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i), mutableList_second_layer))
                                    }

                                    for(i in 0..datas_spec_size-1){
                                        for(j in 0..datas_size_size-1){
                                            mutableList_first_layer.get(i).mutableList_itemInvenSecondLayer.get(j).price = mutableList_price.get(i).get(j).toString()
                                            mutableList_first_layer.get(i).mutableList_itemInvenSecondLayer.get(j).quantity = mutableList_quant.get(i).get(j).toString()
                                            mutableList_first_layer.get(i).mutableList_itemInvenSecondLayer.get(j).sold_quantity = mutableList_sold_quant.get(i).get(j).toString()
                                        }
                                    }

                                }

                                binding.rViewInventory.setLayoutManager(MyLinearLayoutManager(this@ProductDetailForSalerActivity,false))
                                binding.rViewInventory.adapter = mAdapter

                                mAdapter.updateList(mutableList_first_layer, specGroup_only)

                            }else{
                                runOnUiThread {
                                    binding.rViewInventory.visibility = View.GONE
                                }
                            }
                        }

                        if (productInfoBean.new_secondhand == "new") {
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
                        binding.progressBarMerchandise.visibility = View.GONE
                        binding.imgViewLoadingBackgroundMerchandise.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    Log.d("errorMessage", "JSONException: ${e}")
                    runOnUiThread {
                        binding.progressBarMerchandise.visibility = View.GONE
                        binding.imgViewLoadingBackgroundMerchandise.visibility = View.GONE

                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errorMessage", "IOException: ${e}")
                    runOnUiThread {
                        binding.progressBarMerchandise.visibility = View.GONE
                        binding.imgViewLoadingBackgroundMerchandise.visibility = View.GONE

                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errorMessage", "ErrorResponse: ${ErrorResponse}")
                runOnUiThread {
                    binding.progressBarMerchandise.visibility = View.GONE
                    binding.imgViewLoadingBackgroundMerchandise.visibility = View.GONE

                }
            }
        })
        web.Get_Data(url)
    }

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

    private fun initVM() {

        VM.updateProductStatusData.observe(
            this,
            Observer {
                when (it?.status) {
                    Status.Success -> {

                        runOnUiThread {
                            binding.progressBarMerchandise.visibility = View.VISIBLE
                            binding.imgViewLoadingBackgroundMerchandise.visibility = View.VISIBLE
                        }

                        if (it.ret_val.toString().equals("上架/下架成功!")) {

                            when(product_status){

                                "active"->{
                                    runOnUiThread {
                                        Toast.makeText(this, "下架成功", Toast.LENGTH_LONG).show()
                                        product_status = "draft"
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_launch)
                                    }

                                    RxBus.getInstance().post(EventMyStoreFragmentRefresh())
                                    RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus())

                                }
                                "draft"->{
                                    runOnUiThread {
                                        Toast.makeText(this, "上架成功", Toast.LENGTH_LONG).show()
                                        product_status = "active"
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_draft)
                                    }

                                    RxBus.getInstance().post(EventMyStoreFragmentRefresh())
                                    RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus())

                                }
                            }


                        } else {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        }

                        runOnUiThread {
                            binding.progressBarMerchandise.visibility = View.GONE
                            binding.imgViewLoadingBackgroundMerchandise.visibility = View.GONE
                        }

                    }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
                }
            }
        )

    }

    override fun onBackPressed() {
        RxBus.getInstance().post(EventMyStoreFragmentRefresh())
        finish()
    }


}
