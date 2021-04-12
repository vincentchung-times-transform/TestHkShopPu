package com.hkshopu.hk.ui.main.activity

import MyLinearLayoutManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.ui.main.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareAdapter
import com.hkshopu.hk.ui.main.adapter.ShippingFareExistedAdapter
import com.hkshopu.hk.ui.main.fragment.SpecificationInfoDialogFragment
import com.hkshopu.hk.ui.main.fragment.StoreOrNotDialogFragment
import org.jetbrains.anko.backgroundDrawable
import vn.luongvo.widget.iosswitchview.SwitchView
import java.io.FileNotFoundException


class AddNewProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewProductBinding

    lateinit var switchView: SwitchView
    val REQUEST_EXTERNAL_STORAGE = 100

    var mutableList_pics = mutableListOf<ItemPics>()

    val mAdapters_shippingFareExisted = ShippingFareExistedAdapter()

    //宣告運費項目陣列變數
    var mutableList_itemShipingFareExisted = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFareExisted_filtered = mutableListOf<ItemShippingFare>()
    //宣告規格與庫存價格項目陣列變數
    var mutableList_itemInvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_itemInvenSize = mutableListOf<InventoryItemSize>()

    var fare_price_range: String = ""
    var inven_price_range: String = ""
    var inven_quant_range: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //預設containerSpecification的背景為透明無色
        binding.containerSpecification.setBackgroundResource(0)
        binding.imgSpecLine.isVisible = false
        binding.addContainerAddSpecification.isVisible = false
        binding.editTextMerchanPrice.isVisible = true
        binding.editTextMerchanQunt.isVisible = true

        //設定預設資料
        initProCategoryDatas()
        initProFareDatas()
        initInvenDatas()

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

        initView()


    }

    fun initView() {

        //設置containerSpecification中的iosSwitchSpecification開關功能
        binding.iosSwitchSpecification.setOnCheckedChangeListener(SwitchView.OnCheckedChangeListener { switchView, isChecked ->
            if (isChecked) {
                binding.containerSpecification.setBackgroundResource(R.drawable.customborder_addproduct)
                binding.addContainerAddSpecification.isVisible = true
                binding.imgSpecLine.isVisible = true
                binding.editTextMerchanPrice.isVisible = false
                binding.editTextMerchanQunt.isVisible = false
                binding.textViewMerchanPriceRange.isVisible = true
                binding.textViewMerchanQuntRange.isVisible = true
            } else {
                binding.containerSpecification.setBackgroundResource(0)
                binding.addContainerAddSpecification.isVisible = false
                binding.imgSpecLine.isVisible = false
                binding.editTextMerchanPrice.isVisible = true
                binding.editTextMerchanQunt.isVisible = true
                binding.textViewMerchanPriceRange.isVisible = false
                binding.textViewMerchanQuntRange.isVisible = false
            }
        })

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

        binding.titleBackAddproduct.setOnClickListener {

            StoreOrNotDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")

        }

        //choose product inventory status
        binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
        binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
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
        binding.addContainerAddSpecification.setOnClickListener {
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
                        if (i == 0) {
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

                        //新增所選圖片以及第一張cover image至mutableList_pics中
                        mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))


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

                    try {
                        Thread.sleep(3000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
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
                    mutableList_itemShipingFareExisted.add(intent.getBundleExtra("bundle_ShippingFareActivity")?.getParcelable<ItemShippingFare> (i.toString())!!)
                }

                //篩選所有已勾選的運費方式
                for (f in 0..datas_size-1!!) {
                    if(mutableList_itemShipingFareExisted[f].is_checked ==true ){
                        mutableList_itemShipingFareExisted_filtered.add(mutableList_itemShipingFareExisted[f])
                    }
                }

                //挑選最大宇最小金額，回傳價格區間
                fare_price_range = fare_pick_max_and_min_num(datas_size)
                binding.txtViewFareRange.text = fare_price_range



                if(mutableList_itemShipingFareExisted_filtered.size >0){
                    //自訂layoutManager
                    binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this,false))
                    binding.rViewFareItem.adapter = mAdapters_shippingFareExisted

                    mAdapters_shippingFareExisted.updateList(mutableList_itemShipingFareExisted_filtered)
                    mAdapters_shippingFareExisted.notifyDataSetChanged()
                }else{

                    binding.rViewFareItem.isVisible = false
                    binding.imgLineFare.isVisible = false

                }

            }else{

                binding.rViewFareItem.isVisible = false
                binding.imgLineFare.isVisible = false

            }

        } else {
            binding.rViewFareItem.isVisible = false
            binding.imgLineFare.isVisible = false
        }

    }

    fun fare_pick_max_and_min_num(size : Int): String {
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

    fun inven_price_pick_max_and_min_num(size : Int): String {
        //挑出最大與最小的數字
        var min: Int =mutableList_itemInvenSize[0].price.toInt()
        var max: Int =mutableList_itemInvenSize[0].price.toInt()

        for (f in 1..size-1) {
            if(mutableList_itemInvenSize[f].price.toInt() >= min ){
                max = mutableList_itemInvenSize[f].price.toInt()
            }else{
                min = mutableList_itemInvenSize[f].price.toInt()
            }
        }

        return "HKD$${min}-HKD$${max}"

    }

    fun inven_quant_pick_max_and_min_num(size : Int): String {
        //挑出最大與最小的數字
        var min: Int =mutableList_itemInvenSize[0].quantity.toInt()
        var max: Int =mutableList_itemInvenSize[0].quantity.toInt()

        for (f in 1..size-1) {
            if(mutableList_itemInvenSize[f].quantity.toInt() >= min ){
                max = mutableList_itemInvenSize[f].quantity.toInt()
            }else{
                min = mutableList_itemInvenSize[f].quantity.toInt()
            }
        }

        return "HKD$${min}-HKD$${max}"

    }

    fun initInvenDatas() {


        //取得Bundle傳來的分類資料
        var datas_invenSpec_size: Int? =
            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("datas_invenSpec_size")
        var datas_invenSize_size: Int? =
            intent.getBundleExtra("InventoryAndPriceActivity")?.getInt("datas_invenSize_size")

        Log.d("checkSize",datas_invenSpec_size.toString()+datas_invenSize_size )

        if (datas_invenSpec_size != null && datas_invenSize_size != null ) {

            if(datas_invenSpec_size > 0 || datas_invenSize_size>0) {

                binding.iosSwitchSpecification.isChecked =true
                binding.containerSpecification.setBackgroundResource(R.drawable.customborder_addproduct)
                binding.addContainerAddSpecification.isVisible = true
                binding.imgSpecLine.isVisible = true
                binding.editTextMerchanPrice.isVisible = false
                binding.editTextMerchanQunt.isVisible = false
                binding.textViewMerchanPriceRange.isVisible = true
                binding.textViewMerchanQuntRange.isVisible = true

                //從bundle載入所有添加的運費方式
                for (i in 0..datas_invenSpec_size-1!!) {
                    mutableList_itemInvenSpec.add(intent.getBundleExtra("InventoryAndPriceActivity")?.getParcelable<InventoryItemSpec> ("spec"+i.toString())!!)
                }


                for (i in 0..datas_invenSize_size-1!!) {
                    mutableList_itemInvenSize.add(intent.getBundleExtra("InventoryAndPriceActivity")?.getParcelable<InventoryItemSize> ("size"+i.toString())!!)
                }



                //挑選最大宇最小金額，回傳價格區間
                inven_price_range = inven_price_pick_max_and_min_num(datas_invenSize_size!!)
                inven_quant_range = inven_quant_pick_max_and_min_num(datas_invenSize_size!!)

                binding.textViewMerchanPriceRange.text = inven_price_range
                binding.textViewMerchanQuntRange.text = inven_quant_range


            }else{

                binding.iosSwitchSpecification.isChecked =false
                binding.containerSpecification.setBackgroundResource(0)
                binding.addContainerAddSpecification.isVisible = false
                binding.imgSpecLine.isVisible = false
                binding.editTextMerchanPrice.isVisible = true
                binding.editTextMerchanQunt.isVisible = true
                binding.textViewMerchanPriceRange.isVisible = false
                binding.textViewMerchanQuntRange.isVisible = false

            }

        } else {

            binding.iosSwitchSpecification.isChecked =false
            binding.containerSpecification.setBackgroundResource(0)
            binding.addContainerAddSpecification.isVisible = false
            binding.imgSpecLine.isVisible = false
            binding.editTextMerchanPrice.isVisible = true
            binding.editTextMerchanQunt.isVisible = true
            binding.textViewMerchanPriceRange.isVisible = false
            binding.textViewMerchanQuntRange.isVisible = false

        }
    }



}