package com.hkshopu.hk.ui.main.product.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.product.fragment.EditProductRemindDialogFragment
import com.hkshopu.hk.ui.main.product.fragment.StoreOrNotDialogFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL


class MerchandiseActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    private lateinit var binding : ActivityMerchandiseBinding
    lateinit var points: ArrayList<ImageView> //指示器圖片
    val list = ArrayList<ProductImagesObjBean>()

    private val VM = ShopVModel()

    lateinit var productInfoList :  ProductInfoBean
    var mutableList_pics = mutableListOf<ItemPics>()

    var value_editTextEntryProductName :String = ""

    var MMKV_product_id: Int = 1

    var product_status : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMerchandiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)
        getProductInfo(MMKV_product_id)



        initVM()
        initView()

    }

    fun initView() {

        initClick()

    }

    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            MMKV.mmkvWithID("addPro").clearAll()
            finish()
        }

        binding.btnEditmerchandise.setOnClickListener {

            EditProductRemindDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")
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

        binding.productPicsPager.adapter = MerchandiseActivity.ImageAdapter(list)
        binding.productPicsPager.addOnPageChangeListener(this)
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

            binding.productPicsIndicator.addView(point)
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


    private fun getProductInfo(product_id: Int) {

        val url = ApiConstants.API_HOST+"product/${product_id}/product_info_forAndroid/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductInfoBean>()
//                val product_id_list = ArrayList<String>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getProductInfo", "返回資料 resStr：" + resStr)
                    Log.d("getProductInfo", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品資訊!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getProductInfo", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            productInfoList = Gson().fromJson(
                                jsonObject.toString(),
                                ProductInfoBean::class.java
                            )

                        }
                        Log.d("getProductInfo", "返回資料 productInfoList：" + productInfoList.toString())

                        if(productInfoList.pic_path.isNotEmpty()){
                            for (i in 0..productInfoList.pic_path.size - 1) {

                                mutableList_pics.add(
                                    ItemPics(
                                        getBitmapFromURL(
                                            productInfoList.pic_path.get(
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

                        runOnUiThread {
                            binding.tvQuantity.setText(productInfoList.sum_quantity.toString())
                            binding.textViewSoldQuantity.setText(productInfoList.sold_quantity.toString())
                            binding.textViewLike.setText(productInfoList.like.toString())
                            binding.tvLongestStockUpDays.setText(productInfoList.longterm_stock_up.toString())
                        }

                        runOnUiThread {

                            when(productInfoList.product_status){
                                "active"->{
                                    binding.btnLaunch.setImageResource(R.mipmap.btn_draft)
                                    product_status = productInfoList.product_status
                                }
                                "draft"->{
                                    binding.btnLaunch.setImageResource(R.mipmap.btn_launch)
                                    product_status = productInfoList.product_status
                                }
                            }


                            if(productInfoList.product_spec_on.equals("y")){
                                binding.textViewProductPriceRange.setText("HKD$${productInfoList.shipment_min_price}-HKD$${
                                    productInfoList.shipment_max_price
                                }")
                            }else{
                                binding.textViewProductPriceRange.setText("HKD$${productInfoList.product_price.toString()}")
                            }

                            binding.textViewProductName.setText(productInfoList.product_title.toString())
                            binding.textViewProductInformation.setText(productInfoList.product_description.toString())
                            binding.textViewSoldQuantity.setText(productInfoList.sold_quantity.toString())
                            binding.textViewLike.setText(productInfoList.like.toString())

                            binding.textViewSeletedCategory.setText(
                                "${productInfoList.c_product_category} > ${
                                    productInfoList.c_sub_product_category
                                }"
                            )

                            initViewPager()

                            if(productInfoList.product_spec_on.equals("y")){

                                binding.invenContainer.isVisible = true

                                var mutableSet_spec_dec_1_items : MutableSet<String> = productInfoList.spec_dec_1_items.toMutableSet()
                                var mutableSet_spec_dec_2_items : MutableSet<String> = productInfoList.spec_dec_2_items.toMutableSet()

                                binding.firstLayerSpec01.setText(productInfoList.spec_desc_1.get(0))
                                binding.firstLayerSpec02.setText(productInfoList.spec_desc_1.get(0))
                                binding.firstLayerSpec03.setText(productInfoList.spec_desc_1.get(0))

                                binding.firstLayerColumn01.setText(productInfoList.spec_desc_2.get(0))
                                binding.firstLayerColumn02.setText(productInfoList.spec_desc_2.get(0))
                                binding.firstLayerColumn03.setText(productInfoList.spec_desc_2.get(0))

                                if(mutableSet_spec_dec_1_items.size>0 && mutableSet_spec_dec_2_items.size>0){
                                    when(mutableSet_spec_dec_1_items.size){
                                        1->{

                                            binding.containerInvenItem01.isVisible = true
                                            binding.containerInvenItem02.isVisible = false
                                            binding.containerInvenItem03.isVisible = false

                                            when(mutableSet_spec_dec_2_items.size){

                                                1->{
                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = false
                                                    binding.secondLayerItemContainer03.isVisible = false

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())

                                                }
                                                2->{
                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = true
                                                    binding.secondLayerItemContainer03.isVisible = false

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())

                                                }
                                                3->{
                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = true
                                                    binding.secondLayerItemContainer03.isVisible = true

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))
                                                    binding.secondLayerItemName03.setText(productInfoList.spec_dec_2_items.get(2))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemPrice03.setText(productInfoList.price.get(2).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())
                                                    binding.secondLayerItemQuant03.setText(productInfoList.spec_quantity.get(2).toString())

                                                }

                                            }

                                        }
                                        2->{

                                            binding.containerInvenItem01.isVisible = true
                                            binding.containerInvenItem02.isVisible = true
                                            binding.containerInvenItem03.isVisible = false

                                            when(mutableSet_spec_dec_2_items.size){
                                                1->{
                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = false
                                                    binding.secondLayerItemContainer03.isVisible = false
                                                    binding.secondLayerItemContainer04.isVisible = true
                                                    binding.secondLayerItemContainer05.isVisible = false
                                                    binding.secondLayerItemContainer06.isVisible = false

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.firstLayerTitle02.setText(productInfoList.spec_dec_1_items.get(1))

                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName04.setText(productInfoList.spec_dec_2_items.get(1))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice04.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant04.setText(productInfoList.spec_quantity.get(1).toString())


                                                }
                                                2->{

                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = true
                                                    binding.secondLayerItemContainer03.isVisible = false
                                                    binding.secondLayerItemContainer04.isVisible = true
                                                    binding.secondLayerItemContainer05.isVisible = true
                                                    binding.secondLayerItemContainer06.isVisible = false

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.firstLayerTitle02.setText(productInfoList.spec_dec_1_items.get(1))

                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))
                                                    binding.secondLayerItemName04.setText(productInfoList.spec_dec_2_items.get(2))
                                                    binding.secondLayerItemName05.setText(productInfoList.spec_dec_2_items.get(3))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemPrice04.setText(productInfoList.price.get(2).toString())
                                                    binding.secondLayerItemPrice05.setText(productInfoList.price.get(3).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())
                                                    binding.secondLayerItemQuant04.setText(productInfoList.spec_quantity.get(2).toString())
                                                    binding.secondLayerItemQuant05.setText(productInfoList.spec_quantity.get(3).toString())


                                                }
                                                3->{

                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = true
                                                    binding.secondLayerItemContainer03.isVisible = true
                                                    binding.secondLayerItemContainer04.isVisible = true
                                                    binding.secondLayerItemContainer05.isVisible = true
                                                    binding.secondLayerItemContainer06.isVisible = true

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.firstLayerTitle02.setText(productInfoList.spec_dec_1_items.get(1))

                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))
                                                    binding.secondLayerItemName03.setText(productInfoList.spec_dec_2_items.get(2))
                                                    binding.secondLayerItemName04.setText(productInfoList.spec_dec_2_items.get(3))
                                                    binding.secondLayerItemName05.setText(productInfoList.spec_dec_2_items.get(4))
                                                    binding.secondLayerItemName06.setText(productInfoList.spec_dec_2_items.get(5))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemPrice03.setText(productInfoList.price.get(2).toString())
                                                    binding.secondLayerItemPrice04.setText(productInfoList.price.get(3).toString())
                                                    binding.secondLayerItemPrice05.setText(productInfoList.price.get(4).toString())
                                                    binding.secondLayerItemPrice06.setText(productInfoList.price.get(5).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())
                                                    binding.secondLayerItemQuant03.setText(productInfoList.spec_quantity.get(2).toString())
                                                    binding.secondLayerItemQuant04.setText(productInfoList.spec_quantity.get(3).toString())
                                                    binding.secondLayerItemQuant05.setText(productInfoList.spec_quantity.get(4).toString())
                                                    binding.secondLayerItemQuant06.setText(productInfoList.spec_quantity.get(5).toString())

                                                }
                                            }

                                        }
                                        3->{

                                            binding.containerInvenItem01.isVisible = true
                                            binding.containerInvenItem02.isVisible = true
                                            binding.containerInvenItem03.isVisible = true

                                            when(mutableSet_spec_dec_2_items.size){
                                                1->{
                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = false
                                                    binding.secondLayerItemContainer03.isVisible = false
                                                    binding.secondLayerItemContainer04.isVisible = true
                                                    binding.secondLayerItemContainer05.isVisible = false
                                                    binding.secondLayerItemContainer06.isVisible = false
                                                    binding.secondLayerItemContainer07.isVisible = true
                                                    binding.secondLayerItemContainer08.isVisible = false
                                                    binding.secondLayerItemContainer09.isVisible = false

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.firstLayerTitle02.setText(productInfoList.spec_dec_1_items.get(1))
                                                    binding.firstLayerTitle03.setText(productInfoList.spec_dec_1_items.get(2))
                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName04.setText(productInfoList.spec_dec_2_items.get(1))
                                                    binding.secondLayerItemName07.setText(productInfoList.spec_dec_2_items.get(2))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice04.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemPrice07.setText(productInfoList.price.get(2).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant04.setText(productInfoList.spec_quantity.get(1).toString())
                                                    binding.secondLayerItemQuant07.setText(productInfoList.spec_quantity.get(2).toString())


                                                }
                                                2->{

                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = true
                                                    binding.secondLayerItemContainer03.isVisible = false
                                                    binding.secondLayerItemContainer04.isVisible = true
                                                    binding.secondLayerItemContainer05.isVisible = true
                                                    binding.secondLayerItemContainer06.isVisible = false
                                                    binding.secondLayerItemContainer07.isVisible = true
                                                    binding.secondLayerItemContainer08.isVisible = true
                                                    binding.secondLayerItemContainer09.isVisible = false

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.firstLayerTitle02.setText(productInfoList.spec_dec_1_items.get(1))
                                                    binding.firstLayerTitle03.setText(productInfoList.spec_dec_1_items.get(2))

                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))
                                                    binding.secondLayerItemName04.setText(productInfoList.spec_dec_2_items.get(2))
                                                    binding.secondLayerItemName05.setText(productInfoList.spec_dec_2_items.get(3))
                                                    binding.secondLayerItemName07.setText(productInfoList.spec_dec_2_items.get(4))
                                                    binding.secondLayerItemName08.setText(productInfoList.spec_dec_2_items.get(5))


                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemPrice04.setText(productInfoList.price.get(2).toString())
                                                    binding.secondLayerItemPrice05.setText(productInfoList.price.get(3).toString())
                                                    binding.secondLayerItemPrice07.setText(productInfoList.price.get(4).toString())
                                                    binding.secondLayerItemPrice08.setText(productInfoList.price.get(5).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())
                                                    binding.secondLayerItemQuant04.setText(productInfoList.spec_quantity.get(2).toString())
                                                    binding.secondLayerItemQuant05.setText(productInfoList.spec_quantity.get(3).toString())
                                                    binding.secondLayerItemQuant07.setText(productInfoList.spec_quantity.get(4).toString())
                                                    binding.secondLayerItemQuant08.setText(productInfoList.spec_quantity.get(5).toString())

                                                }
                                                3->{

                                                    binding.secondLayerItemContainer01.isVisible = true
                                                    binding.secondLayerItemContainer02.isVisible = true
                                                    binding.secondLayerItemContainer03.isVisible = true
                                                    binding.secondLayerItemContainer04.isVisible = true
                                                    binding.secondLayerItemContainer05.isVisible = true
                                                    binding.secondLayerItemContainer06.isVisible = true
                                                    binding.secondLayerItemContainer07.isVisible = true
                                                    binding.secondLayerItemContainer08.isVisible = true
                                                    binding.secondLayerItemContainer09.isVisible = true

                                                    binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                                    binding.firstLayerTitle02.setText(productInfoList.spec_dec_1_items.get(1))
                                                    binding.firstLayerTitle03.setText(productInfoList.spec_dec_1_items.get(2))

                                                    binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                                    binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))
                                                    binding.secondLayerItemName03.setText(productInfoList.spec_dec_2_items.get(2))
                                                    binding.secondLayerItemName04.setText(productInfoList.spec_dec_2_items.get(3))
                                                    binding.secondLayerItemName05.setText(productInfoList.spec_dec_2_items.get(4))
                                                    binding.secondLayerItemName06.setText(productInfoList.spec_dec_2_items.get(5))
                                                    binding.secondLayerItemName07.setText(productInfoList.spec_dec_2_items.get(6))
                                                    binding.secondLayerItemName08.setText(productInfoList.spec_dec_2_items.get(7))
                                                    binding.secondLayerItemName09.setText(productInfoList.spec_dec_2_items.get(8))

                                                    binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                                    binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                                    binding.secondLayerItemPrice03.setText(productInfoList.price.get(2).toString())
                                                    binding.secondLayerItemPrice04.setText(productInfoList.price.get(3).toString())
                                                    binding.secondLayerItemPrice05.setText(productInfoList.price.get(4).toString())
                                                    binding.secondLayerItemPrice06.setText(productInfoList.price.get(5).toString())
                                                    binding.secondLayerItemPrice07.setText(productInfoList.price.get(6).toString())
                                                    binding.secondLayerItemPrice08.setText(productInfoList.price.get(7).toString())
                                                    binding.secondLayerItemPrice09.setText(productInfoList.price.get(8).toString())
                                                    binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                                    binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())
                                                    binding.secondLayerItemQuant03.setText(productInfoList.spec_quantity.get(2).toString())
                                                    binding.secondLayerItemQuant04.setText(productInfoList.spec_quantity.get(3).toString())
                                                    binding.secondLayerItemQuant05.setText(productInfoList.spec_quantity.get(4).toString())
                                                    binding.secondLayerItemQuant06.setText(productInfoList.spec_quantity.get(5).toString())
                                                    binding.secondLayerItemQuant07.setText(productInfoList.spec_quantity.get(6).toString())
                                                    binding.secondLayerItemQuant08.setText(productInfoList.spec_quantity.get(7).toString())
                                                    binding.secondLayerItemQuant09.setText(productInfoList.spec_quantity.get(8).toString())

                                                }
                                            }
                                        }
                                    }

                                }else if(mutableSet_spec_dec_1_items.size>0 && mutableSet_spec_dec_2_items.size==0){

                                    binding.containerInvenItem01.isVisible = true
                                    binding.containerFistLayerItemTitle.isVisible = false
                                    binding.containerInvenItem02.isVisible = false
                                    binding.containerInvenItem03.isVisible = false


                                    when(mutableSet_spec_dec_1_items.size){

                                        1->{
                                            binding.secondLayerItemContainer01.isVisible = true
                                            binding.secondLayerItemContainer02.isVisible = false
                                            binding.secondLayerItemContainer03.isVisible = false

                                            binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                            binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                            binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                            binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())

                                        }
                                        2->{
                                            binding.secondLayerItemContainer01.isVisible = true
                                            binding.secondLayerItemContainer02.isVisible = true
                                            binding.secondLayerItemContainer03.isVisible = false

                                            binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                            binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                            binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))

                                            binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                            binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                            binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                            binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())

                                        }
                                        3->{
                                            binding.secondLayerItemContainer01.isVisible = true
                                            binding.secondLayerItemContainer02.isVisible = true
                                            binding.secondLayerItemContainer03.isVisible = true

                                            binding.firstLayerTitle01.setText(productInfoList.spec_dec_1_items.get(0))
                                            binding.secondLayerItemName01.setText(productInfoList.spec_dec_2_items.get(0))
                                            binding.secondLayerItemName02.setText(productInfoList.spec_dec_2_items.get(1))
                                            binding.secondLayerItemName03.setText(productInfoList.spec_dec_2_items.get(2))

                                            binding.secondLayerItemPrice01.setText(productInfoList.price.get(0).toString())
                                            binding.secondLayerItemPrice02.setText(productInfoList.price.get(1).toString())
                                            binding.secondLayerItemPrice03.setText(productInfoList.price.get(2).toString())
                                            binding.secondLayerItemQuant01.setText(productInfoList.spec_quantity.get(0).toString())
                                            binding.secondLayerItemQuant02.setText(productInfoList.spec_quantity.get(1).toString())
                                            binding.secondLayerItemQuant03.setText(productInfoList.spec_quantity.get(2).toString())

                                        }
                                    }
                                }
                            }else{

                                binding.invenContainer.isVisible = false

                            }

                        }


                        if (productInfoList.new_secondhand == "new") {
                            runOnUiThread {
                                binding.statusLebal.setImageResource(R.mipmap.new_lebal)
                            }

                        } else {
                            runOnUiThread {
                                binding.statusLebal.setImageResource(R.mipmap.secondhand_lebal)

                            }
                        }


                    } else {

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


    //計算費用最大最小範圍
    fun pick_max_and_min_num(): String {
        //挑出最大與最小的數字
        var min: Int =  productInfoList.min_price.toInt()
        var max: Int =productInfoList.max_price.toInt()

        return "HKD$${min}-${max}"
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
                        if (it.ret_val.toString().equals("上架/下架成功!")) {

                            when(product_status){
                                "active"->{
                                    runOnUiThread {
                                        Toast.makeText(this, "下架成功", Toast.LENGTH_LONG).show()
                                        product_status = "draft"
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_launch)
                                    }

                                }
                                "draft"->{
                                    runOnUiThread {
                                        Toast.makeText(this, "上架成功", Toast.LENGTH_LONG).show()
                                        product_status = "active"
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_draft)
                                    }

                                }
                            }

                        } else {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        }

                    }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
                }
            }
        )

    }

    override fun onBackPressed() {
        MMKV.mmkvWithID("addPro").clearAll()
        finish()
    }


}