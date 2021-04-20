package com.hkshopu.hk.ui.main.activity

import MyLinearLayoutManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemDatas
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.ui.main.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareCheckedAdapter
import com.hkshopu.hk.ui.main.fragment.StoreOrNotDialogFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.zilchzz.library.widgets.EasySwitcher
import java.io.*


class AddNewProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewProductBinding

    private val VM = ShopVModel()

    //從本地端選取圖片轉換為bitmap後存的list
    var mutableList_pics = mutableListOf<ItemPics>()
    //用來裝圖片file的list(目前尚未成功)
    var pic_list : MutableList<File> = mutableListOf()

    val REQUEST_EXTERNAL_STORAGE = 100

    //
    val mAdapters_shippingFareChecked = ShippingFareCheckedAdapter()

    //宣告運費項目陣列變數
    var mutableList_itemShipingFareExisted = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFareExisted_filtered = mutableListOf<ItemShippingFare>()

    //宣告規格與庫存價格項目陣列變數
//    var mutableList_itemInvenSpec = mutableListOf<InventoryItemSpec>()
//    var mutableList_itemInvenSize = mutableListOf<InventoryItemSize>()
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    var fare_price_range: String = ""
    var inven_price_range: String = ""
    var inven_quant_range: String = ""


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initVM()


        //設定預設資料
        initProCategoryDatas()
        initProFareDatas()
        initInvenDatas()



        initView()

    }

    fun initView() {

        //預設containerSpecification的背景為透明無色
        binding.imgSpecLine.isVisible = false
        binding.containerAddSpecification.isVisible = false
        binding.editTextMerchanPrice.isVisible = true
        binding.editTextMerchanQunt.isVisible = true

        //choose product inventory status
        binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
        binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)


        //預設較長備貨時間設定
        binding.editMoreTimeInput.isVisible = false
        binding.needMoreTimeToStockUp.text = getString(R.string.textView_more_time_to_stock)
        binding.needMoreTimeToStockUp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.editMoreTimeInput.isVisible = true

            } else {
                binding.editMoreTimeInput.isVisible = false

            }
        }

        initClick()
    }

    fun initClick() {

        binding.btnAddPics.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE
                );
//                    return;
            } else {
                launchGalleryIntent()
            }

        }

        //設置containerSpecification中的iosSwitchSpecification開關功能
        binding.iosSwitchSpecification.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onStateChanged(isOpen: Boolean) {
                if (isOpen) {

                    binding.containerAddSpecification.isVisible = true
                    binding.imgSpecLine.isVisible = true
                    binding.editTextMerchanPrice.isVisible = false
                    binding.editTextMerchanQunt.isVisible = false
                    binding.textViewMerchanPriceRange.isVisible = true
                    binding.textViewMerchanQuntRange.isVisible = true

                    val scale = baseContext.resources.displayMetrics.density
                    var elevation = 0
                    val e = (elevation * scale + 0.5f).toInt()

                    binding.containerProductSpecPrice.setElevation(e.toFloat())
                    binding.containerProductSpecQuant.setElevation(e.toFloat())
                    binding.containerProductSpecSwitch.setElevation(e.toFloat())

                } else {

                    binding.containerAddSpecification.isVisible = false
                    binding.imgSpecLine.isVisible = false
                    binding.editTextMerchanPrice.isVisible = true
                    binding.editTextMerchanQunt.isVisible = true
                    binding.textViewMerchanPriceRange.isVisible = false
                    binding.textViewMerchanQuntRange.isVisible = false

                    val scale = baseContext.resources.displayMetrics.density
                    var elevation = 10
                    val e = (elevation * scale + 0.5f).toInt()

                    binding.containerProductSpecSwitch.setElevation(e.toFloat())
                    binding.containerProductSpecPrice.setElevation(e.toFloat())
                    binding.containerProductSpecQuant.setElevation(e.toFloat())


                }
            }
        })


        binding.titleBackAddproduct.setOnClickListener {

            StoreOrNotDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")

        }


        binding.tvBrandnew.setOnClickListener {
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
        }
        binding.tvSecondhand.setOnClickListener {
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)
        }

        //go to category page
        binding.btnAddcategory.setOnClickListener {

//            val intent = Intent(this, LoginPasswordActivity::class.java)
//            startActivity(intent)

        }

        //go to AddProductSpecificationMainActivity
        binding.containerAddSpecification.setOnClickListener {
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)

        }

        binding.containerShippingFare.setOnClickListener {
            val intent = Intent(this, ShippingFareActivity::class.java)
            startActivity(intent)
        }


        binding.categoryContainer.setOnClickListener {
            val intent = Intent(this, MerchanCategoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnStore.setOnClickListener {



            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonTutList: String = gson.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutList.toString())
            Log.d("AddNewProductActivity","test")
            val jsonTutListPretty: String = gsonPretty.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutListPretty.toString())

            var file: File? = null

            for(i in 0..mutableList_pics.size-1){

                file = processImage(mutableList_pics.get(i).bitmap, i)

                pic_list.add(file!!)
            }


            if (file != null) {
//                VM.add_product(this, 1, 1, 1, "0", 0, "0", 0, 0, 0, "0", pic_list,  jsonTutList,1)
            }

        }

    }

    fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            val imageView = findViewById<ImageView>(R.id.image_view)
//            val bitmaps: MutableList<Bitmap> = ArrayList()


            Thread(Runnable {

                val clipData = data?.clipData
                if (clipData != null) {
                    //multiple images selecetd
                    for (i in 0 until clipData.itemCount) {
                        if (i == 0 ) {
                            //取得圖片uri存到變數imageUri並轉成bitmap
                            val imageUri = clipData.getItemAt(i).uri
                            Log.d("URI", imageUri.toString())
                            try {
                                val inputStream =
                                    contentResolver.openInputStream(imageUri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                //新增所選圖片以及第一張cover image至mutableList_pics中
                                mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))

                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }

                        } else {
                            //取得圖片uri存到變數imageUri並轉成bitmap
                            val imageUri = clipData.getItemAt(i).uri
                            Log.d("URI", imageUri.toString())
                            try {
                                val inputStream =
                                    contentResolver.openInputStream(imageUri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                //新增所選圖片以及第一張cover image至mutableList_pics中
                                mutableList_pics.add(
                                    ItemPics(
                                        bitmap,
                                        R.drawable.custom_unit_transparent
                                    )
                                )



                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }

                        }


                    }
                } else {
                    //single image selected
                    val imageUri = data?.data
                    Log.d("URI", imageUri.toString())
                    try {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        if(mutableList_pics.size==0){
                            //新增所選圖片以及第一張cover image至mutableList_pics中
                            mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))


                        }else{
                            mutableList_pics.add(ItemPics(bitmap, R.drawable.custom_unit_transparent))


                        }




                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {

                    val mAdapter = PicsAdapter()

                    mAdapter.updateList(mutableList_pics)     //傳入資料
                    binding.rView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.rView.adapter = mAdapter

                }

//                for (b in bitmaps) {
//                    runOnUiThread { imageView.setImageBitmap(b) }
//                    try {
//                        Thread.sleep(3000)
//                    } catch (e: InterruptedException) {
//                        e.printStackTrace()
//                    }
//                }



            }).start()
        }
    }

    fun initProCategoryDatas() {

        //取得Bundle傳來的分類資料
//        var sharedPreferences : SharedPreferences = getSharedPreferences("add_product_categery", Context.MODE_PRIVATE)
        var id: String? = intent.getBundleExtra("bundle")?.getString("id")
        var product_category_id: String? =
            intent.getBundleExtra("bundle")?.getString("product_category_id")
        var c_product_category: String? =
            intent.getBundleExtra("bundle")?.getString("c_product_category")
        var c_product_sub_category: String? =
            intent.getBundleExtra("bundle")?.getString("c_product_sub_category")

        if (c_product_category.equals(null) || c_product_sub_category.equals(null)) {
            binding.textViewSeletedCategory.isVisible = false
            binding.btnAddcategory.isVisible = true
        } else {
            binding.textViewSeletedCategory.isVisible = true
            binding.textViewSeletedCategory.text = c_product_category + ">" + c_product_sub_category
            binding.btnAddcategory.isVisible = false
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initProFareDatas() {


        //取得Bundle傳來的分類資料
//        var sharedPreferences : SharedPreferences = getSharedPreferences("add_product_categery", Context.MODE_PRIVATE)
        var datas_packagesWeights: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_packagesWeights")
        var datas_lenght: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_lenght")
        var datas_width: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_width")
        var datas_height: String? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getString("datas_height")
        var datas_size: Int? =
            intent.getBundleExtra("bundle_ShippingFareActivity")?.getInt("datas_size")


        if (datas_size != null) {

            if(datas_size > 0) {

                binding.rViewFareItem.isVisible = true
                binding.imgLineFare.isVisible = true


                //從bundle載入所有添加的運費方式
                for (i in 0..datas_size-1!!) {
                    mutableList_itemShipingFareExisted.add(
                        intent.getBundleExtra("bundle_ShippingFareActivity")
                            ?.getParcelable<ItemShippingFare>(
                                i.toString()
                            )!!
                    )
                }

                //篩選所有已勾選的運費方式
                for (f in 0..datas_size-1!!) {
                    if(mutableList_itemShipingFareExisted[f].is_checked ==true ){
                        mutableList_itemShipingFareExisted_filtered.add(
                            mutableList_itemShipingFareExisted[f]
                        )
                    }
                }

                //挑選最大宇最小金額，回傳價格區間
                fare_price_range = fare_pick_max_and_min_num(datas_size)
                binding.txtViewFareRange.text = fare_price_range



                if(mutableList_itemShipingFareExisted_filtered.size >0){
                    //自訂layoutManager
                    binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this, false))
                    binding.rViewFareItem.adapter = mAdapters_shippingFareChecked

                    mAdapters_shippingFareChecked.updateList(
                        mutableList_itemShipingFareExisted_filtered
                    )
                    mAdapters_shippingFareChecked.notifyDataSetChanged()
                }else{

                    binding.rViewFareItem.isVisible = false
                    binding.imgLineFare.isVisible = false


                }

            }

        } else {

            binding.rViewFareItem.isVisible = false
            binding.imgLineFare.isVisible = false


        }

    }

    //計算費用最大最小範圍
    fun fare_pick_max_and_min_num(size: Int): String {
        //挑出最大與最小的數字
        var min: Int =mutableList_itemShipingFareExisted[0].ship_method_fare.toInt()
        var max: Int =mutableList_itemShipingFareExisted[0].ship_method_fare.toInt()

        for (f in 1..size-1) {
            if(mutableList_itemShipingFareExisted[f].ship_method_fare.toInt() >= min ){
                max = mutableList_itemShipingFareExisted[f].ship_method_fare.toInt()
            }else{
                min = mutableList_itemShipingFareExisted[f].ship_method_fare.toInt()
            }
        }

        return "HKD$${min}-HKD$${max}"

    }

    //計算庫存"費用"最大最小範圍
    fun inven_price_pick_max_and_min_num(size: Int): String {
        //挑出最大與最小的數字
        var min: Int = mutableList_InvenDatas[0]!!.price.toInt()
        var max: Int =mutableList_InvenDatas[0]!!.price.toInt()

        for (f in 1..size-1) {
            if(mutableList_InvenDatas[f]!!.price.toInt() >= min ){
                max = mutableList_InvenDatas[f]!!.price.toInt()
            }else{
                min = mutableList_InvenDatas[f]!!.price.toInt()
            }
        }

        return "HKD$${min}-HKD$${max}"

    }

    //計算庫存"數量"最大最小範圍
    fun inven_quant_pick_max_and_min_num(size: Int): String {
        //挑出最大與最小的數字
        var min: Int =mutableList_InvenDatas[0]!!.quantity.toInt()
        var max: Int =mutableList_InvenDatas[0]!!.quantity.toInt()

        for (f in 1..size-1) {
            if(mutableList_InvenDatas[f]!!.quantity.toInt() >= min ){
                max = mutableList_InvenDatas[f]!!.quantity.toInt()
            }else{
                min = mutableList_InvenDatas[f]!!.quantity.toInt()
            }
        }

        return "HKD$${min}-HKD$${max}"

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initInvenDatas() {

        //取得Bundle傳來的分類資料
//        var datas_invenSpec_size: Int? =
//            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("datas_invenSpec_size")
//        var datas_invenSize_size: Int? =
//            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("datas_invenSize_size")
        var inven_datas_size: Int? =
            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("InvenDatas_size")

        if (inven_datas_size != null) {

            if (inven_datas_size>0) {

                binding.iosSwitchSpecification.openSwitcher()
                binding.containerAddSpecification.isVisible = true
                binding.imgSpecLine.isVisible = true
                binding.editTextMerchanPrice.isVisible = false
                binding.editTextMerchanQunt.isVisible = false
                binding.textViewMerchanPriceRange.isVisible = true
                binding.textViewMerchanQuntRange.isVisible = true


                val scale = baseContext.resources.displayMetrics.density
                var elevation = 0
                val e = (elevation * scale + 0.5f).toInt()

                binding.containerProductSpecPrice.setElevation(e.toFloat())
                binding.containerProductSpecQuant.setElevation(e.toFloat())
                binding.containerProductSpecSwitch.setElevation(e.toFloat())

                //從bundle載入所有添加的運費方式
//                    for (i in 0..datas_invenSpec_size-1!!) {
//                        mutableList_itemInvenSpec.add(intent.getBundleExtra("InventoryAndPriceActivity")?.getParcelable<InventoryItemSpec> ("spec"+i.toString())!!)
//                    }
//
//
//                    for (i in 0..datas_invenSize_size-1!!) {
//                        mutableList_itemInvenSize.add(intent.getBundleExtra("InventoryAndPriceActivity")?.getParcelable<InventoryItemSize> ("size"+i.toString())!!)
//                    }


                for(key in 0..inven_datas_size!!-1){
                    mutableList_InvenDatas.add(
                        intent.getBundleExtra("InventoryAndPriceActivity")
                            ?.getParcelable<InventoryItemDatas>(
                                "InvenDatas" + key.toString()
                            )!!
                    )

                }
                Log.d("checkList", inven_datas_size.toString())

                //挑選最大宇最小金額，回傳價格區間
                inven_price_range = inven_price_pick_max_and_min_num(inven_datas_size!!)
                inven_quant_range = inven_quant_pick_max_and_min_num(inven_datas_size!!)

                binding.textViewMerchanPriceRange.text = inven_price_range
                binding.textViewMerchanQuntRange.text = inven_quant_range

            }


        } else {

            binding.iosSwitchSpecification.closeSwitcher()
            binding.containerAddSpecification.isVisible = false
            binding.imgSpecLine.isVisible = false
            binding.editTextMerchanPrice.isVisible = true
            binding.editTextMerchanQunt.isVisible = true
            binding.textViewMerchanPriceRange.isVisible = false
            binding.textViewMerchanQuntRange.isVisible = false

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt()

            binding.containerProductSpecPrice.setElevation(e.toFloat())
            binding.containerProductSpecQuant.setElevation(e.toFloat())
            binding.containerProductSpecSwitch.setElevation(e.toFloat())


        }

    }

    //Discarded
    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            val scale = baseContext.resources.displayMetrics.density
            // convert the DP into pixel
            val l = (left * scale + 0.5f).toInt()
            val r = (right * scale + 0.5f).toInt()
            val t = (top * scale + 0.5f).toInt()
            val b = (bottom * scale + 0.5f).toInt()
            p.setMargins(l, t, r, b)
            view.requestLayout()
        }
    }

    private fun initVM() {
        VM.addProductData.observe(this, androidx.lifecycle.Observer {
            when (it?.status) {
                Status.Success -> {

                    Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT ).show()

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })


    }



    private fun processImage(bitmap: Bitmap, i :Int): File? {

        val bmp = bitmap
        val bmpCompress = getResizedBitmap(bmp, 200)
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + i + ".jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            stream?.flush()
            stream?.close()
        } catch (e: IOException) // Catch the exception
        {
            e.printStackTrace()
        }
        return file
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        width = maxSize
        height = (width / bitmapRatio).toInt()
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


}