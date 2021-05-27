package com.hkshopu.hk.ui.main.product.activity

import MyLinearLayoutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecAdapter
import com.hkshopu.hk.ui.main.product.adapter.InventoryAndPriceSpecNestedAdapter
import com.hkshopu.hk.ui.main.product.fragment.EditProductRemindDialogFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
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

        binding.progressBarMerchandise.isVisible = false

        initVM()
        initView()

    }

    fun initView() {

        initClick()

    }

    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {

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

            binding.productPicsPager.adapter = MerchandiseActivity.ImageAdapter(list)
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


    private fun getProductInfo(product_id: Int) {

        val url = ApiConstants.API_HOST+"product/${product_id}/product_info_forAndroid/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductInfoBean>()
//                val product_id_list = ArrayList<String>()
                try {

                    runOnUiThread {
                        binding.progressBarMerchandise.isVisible = true
                    }

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

                        if(productInfoList.product_spec_on.equals("y")){


                            if(productInfoList.sum_quantity.toString().length>=3){
                                var one_thous = 1000
                                var float = productInfoList.sum_quantity.toDouble()/one_thous.toDouble()
                                var bigDecimal = float.toBigDecimal()
                                runOnUiThread {
                                    binding.tvQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                                }

                            }else{
                                runOnUiThread {
                                    binding.tvQuantity.text =  "${productInfoList.sum_quantity.toString()}"
                                }
                            }


                        }else{
                            runOnUiThread {

                                if(productInfoList.quantity.toString().length>=3){
                                    var one_thous = 1000
                                    var float = productInfoList.quantity.toDouble()/one_thous.toDouble()
                                    var bigDecimal = float.toBigDecimal()
                                    runOnUiThread {
                                        binding.tvQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                                    }

                                }else{
                                    runOnUiThread {
                                        binding.tvQuantity.text =  "${productInfoList.quantity.toString()}"
                                    }
                                }
                            }
                        }


                        if(productInfoList.sold_quantity.toString().length>=3){
                            var one_thous = 1000
                            var float = productInfoList.sold_quantity.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.textViewSoldQuantity.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.textViewSoldQuantity.text =  "${productInfoList.sold_quantity.toString()}"
                            }
                        }

                        if(productInfoList.like.toString().length>=3){
                            var one_thous = 1000
                            var float = productInfoList.like.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.textViewLike.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.textViewLike.text =  "${productInfoList.like.toString()}"
                            }
                        }

                        if(productInfoList.longterm_stock_up.toString().length>=3){
                            var one_thous = 1000
                            var float = productInfoList.longterm_stock_up.toDouble()/one_thous.toDouble()
                            var bigDecimal = float.toBigDecimal()
                            runOnUiThread {
                                binding.tvLongestStockUpDays.text = "${bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}k"
                            }

                        }else{
                            runOnUiThread {
                                binding.tvLongestStockUpDays.text =  "${productInfoList.longterm_stock_up.toString()}"
                            }
                        }





                        runOnUiThread {

                            when(productInfoList.product_status){
                                "active"->{

                                    runOnUiThread {
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_draft)
                                    }

                                    product_status = productInfoList.product_status
                                }
                                "draft"->{

                                    runOnUiThread {
                                        binding.btnLaunch.setImageResource(R.mipmap.btn_launch)
                                    }

                                    product_status = productInfoList.product_status
                                }
                            }


                            if(productInfoList.product_spec_on.equals("y")){
                                runOnUiThread {
                                    binding.textViewProductPriceRange.setText("HKD$${productInfoList.shipment_min_price}-HKD$${
                                        productInfoList.shipment_max_price
                                    }")
                                }

                            }else{
                                runOnUiThread {
                                    binding.textViewProductPriceRange.setText("HKD$${productInfoList.product_price.toString()}")
                                }
                            }

                            runOnUiThread {
                                binding.textViewProductName.setText(productInfoList.product_title.toString())
                                binding.textViewProductInformation.setText(productInfoList.product_description.toString())

                                binding.textViewSeletedCategory.setText(
                                    "${productInfoList.c_product_category} > ${
                                        productInfoList.c_sub_product_category
                                    }"
                                )

                            }

                            initViewPager()

                            if(productInfoList.product_spec_on.equals("y")){

                                runOnUiThread {
                                    binding.rViewInventory.visibility = View.VISIBLE
                                }

                                var mutableList_Inventory = mutableListOf<ItemInventory>()
//                                val mAdapter = InventoryAndPriceSpecAdapter()
                                val mAdapter = InventoryAndPriceSpecNestedAdapter()
                                var mutableList_spec = mutableListOf<ItemSpecification>()
                                var mutableList_size = mutableListOf<ItemSpecification>()
                                var mutableList_price = mutableListOf<Int>()
                                var mutableList_quant = mutableListOf<Int>()
                                var datas_price_size: Int = 0
                                var datas_quant_size: Int = 0
                                var specGroup_only:Boolean = false

                                var datas_spec_title_first = productInfoList.spec_desc_1.get(0)
                                var datas_spec_title_second = productInfoList.spec_desc_2.get(0)


                                var mutableSet_spec_dec_1_items: MutableSet<String> =
                                    productInfoList.spec_dec_1_items.toMutableSet()
                                var mutableSet_spec_dec_2_items: MutableSet<String> =
                                    productInfoList.spec_dec_2_items.toMutableSet()
                                var mutableList_spec_dec_1_items: MutableList<String> =
                                    mutableSet_spec_dec_1_items.toMutableList()
                                var mutableList_spec_dec_2_items: MutableList<String> =
                                    mutableSet_spec_dec_2_items.toMutableList()

                                var datas_spec_size = mutableList_spec_dec_1_items.size
                                var datas_size_size = mutableList_spec_dec_2_items.size

                                for(i in 0..datas_spec_size-1){
                                    var item_name = mutableList_spec_dec_1_items.get(i)
                                    mutableList_spec.add(ItemSpecification(item_name.toString(), R.drawable.custom_unit_transparent))
                                }


                                for(i in 0..datas_size_size-1){
                                    var item_name = mutableList_spec_dec_2_items.get(i)
                                    mutableList_size.add(ItemSpecification(item_name.toString(), R.drawable.custom_unit_transparent))
                                }

                                datas_price_size = productInfoList.price.size.toString().toInt()
                                datas_quant_size = productInfoList.spec_quantity.size.toString().toInt()

                                for (i in 0..datas_price_size - 1) {
                                    var price_item =   productInfoList.price.get(i)
                                    mutableList_price.add(price_item)
                                }

                                for (i in 0..datas_quant_size - 1) {
                                    var quant_item =  productInfoList.spec_quantity.get(i)
                                    mutableList_quant.add(quant_item)
                                }

//
//                                if(!datas_spec_title_first.equals("") && datas_spec_title_second.equals("") ){
//
//                                    specGroup_only = true
//
//
//                                    for(i in 0..datas_spec_size-1){
//                                        mutableList_Inventory.add(ItemInventory(datas_spec_title_first, "", mutableList_spec.get(i).spec_name, "","", ""))
//
//                                    }
//
//
//                                    for(i in 0..datas_spec_size-1){
//                                        mutableList_Inventory.get(i).price = mutableList_price.get(i).toString()
//                                        mutableList_Inventory.get(i).quantity = mutableList_quant.get(i).toString()
//                                    }
//
//
//                                }else{
//                                    specGroup_only = false
//
//
//                                    for(i in 0..datas_spec_size-1){
//
//                                        for(j in 0..datas_size_size-1){
//
//                                            mutableList_Inventory.add(ItemInventory(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i).spec_name, mutableList_size.get(j).spec_name,"", ""))
//
//                                        }
//                                    }
//
//                                    for(i in 0..datas_spec_size*datas_size_size-1){
//                                        mutableList_Inventory.get(i).price = mutableList_price.get(i).toString()
//                                        mutableList_Inventory.get(i).quantity = mutableList_quant.get(i).toString()
//                                    }
//
//                                }


//                                runOnUiThread {
//                                    binding.rViewInventory.setLayoutManager(MyLinearLayoutManager(this@MerchandiseActivity,false))
//                                    binding.rViewInventory.adapter = mAdapter
//
//                                    mAdapter.updateList(mutableList_Inventory, specGroup_only, datas_size_size)
//                                }


                                var mutableList_first_layer = mutableListOf<ItemInvenFirstNestedLayer>()


                                if(!datas_spec_title_first.equals("") && datas_spec_title_second.equals("")){
                                    specGroup_only = true

                                        var mutableList_second_layer = mutableListOf<ItemInvenSecondNestedLayer>()

                                        for(i in 0..datas_spec_size-1){
                                            mutableList_second_layer.add(ItemInvenSecondNestedLayer(mutableList_spec.get(i).spec_name,"", "") )
                                        }
                                        mutableList_first_layer.add(ItemInvenFirstNestedLayer(datas_spec_title_first, "", mutableList_spec.get(0).spec_name, mutableList_second_layer))


                                        for(i in 0..datas_spec_size-1){
                                            mutableList_first_layer.get(0).mutableList_itemInvenSecondLayer.get(i).price = mutableList_price.get(i).toString()
                                            mutableList_first_layer.get(0).mutableList_itemInvenSecondLayer.get(i).quantity = mutableList_quant.get(i).toString()
                                        }

                                }else{
                                    specGroup_only = false


                                    for(i in 0..datas_spec_size-1){

                                        var mutableList_second_layer = mutableListOf<ItemInvenSecondNestedLayer>()
                                        for(i in 0..datas_size_size-1){
                                            mutableList_second_layer.add(ItemInvenSecondNestedLayer(mutableList_size.get(i).spec_name,"", "") )
                                        }
                                        mutableList_first_layer.add(ItemInvenFirstNestedLayer(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i).spec_name, mutableList_second_layer))
                                    }


                                    val second_size = datas_size_size
                                    val first_size: Int = datas_spec_size

                                    var priceData_firstLayer: MutableList<MutableList<Int>> = mutableListOf()
                                    for (i in 0..first_size-1){

                                        var priceData_secondLayer: MutableList<Int>  = mutableListOf()

                                        for(i in 0..second_size-1){
                                            priceData_secondLayer.add(0)
                                        }

                                        priceData_firstLayer.add(priceData_secondLayer)
                                    }


                                    var quant_Data_firstLayer: MutableList<MutableList<Int>> = mutableListOf()

                                    for (i in 0..first_size-1){

                                        var quant_Data_secondLayer: MutableList<Int>  = mutableListOf()

                                        for(i in 0..second_size-1){
                                            quant_Data_secondLayer.add(0)
                                        }
                                        quant_Data_firstLayer.add(quant_Data_secondLayer)
                                    }


                                    for (r in 0 until first_size) {
                                        for (c in 0 until second_size) {

                                            var index = r*second_size+c

                                            priceData_firstLayer[r][c] = mutableList_price.get(index)
                                            quant_Data_firstLayer[r][c] = mutableList_quant.get(index)

                                        }
                                    }

                                    Log.d("dsdsdsd" ,  "priceData_firstLayer : ${priceData_firstLayer.toString()}")
                                    Log.d("dsdsdsd" ,  "quant_Data_secondLayer : ${quant_Data_firstLayer.toString()}")

                                    for(i in 0..datas_spec_size-1){
                                        for(j in 0..datas_size_size-1){
                                            mutableList_first_layer.get(i).mutableList_itemInvenSecondLayer.get(j).price = priceData_firstLayer.get(i).get(j).toString()
                                            mutableList_first_layer.get(i).mutableList_itemInvenSecondLayer.get(j).quantity = quant_Data_firstLayer.get(i).get(j).toString()
                                            Log.d("dsdsdsdsdaaaa" ,  "priceData_firstLayer.get(i).get(j) : ${priceData_firstLayer.get(i).get(j).toString()}")
                                            Log.d("dsdsdsdsdaaaa" ,  "quant_Data_firstLayer.get(i).get(j) : ${quant_Data_firstLayer.get(i).get(j).toString()}")
                                        }
                                    }

                                    Log.d("dsdsdsd" ,  "mutableList_first_layer : ${mutableList_first_layer.toString()}")

                                }


                                binding.rViewInventory.setLayoutManager(MyLinearLayoutManager(this@MerchandiseActivity,false))
                                binding.rViewInventory.adapter = mAdapter

                                mAdapter.updateList(mutableList_first_layer, specGroup_only)
















                            }else{
                                runOnUiThread {
                                    binding.rViewInventory.visibility = View.GONE
                                }

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


                    }

                    runOnUiThread {
                        binding.progressBarMerchandise.isVisible = false
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

                        runOnUiThread {
                            binding.progressBarMerchandise.isVisible = true
                        }

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

                        runOnUiThread {
                            binding.progressBarMerchandise.isVisible = false
                        }

                    }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
                }
            }
        )

    }

    override fun onBackPressed() {

        finish()
    }


}