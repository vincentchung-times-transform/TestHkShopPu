package com.HKSHOPU.hk.ui.main.shopProfile.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventAddShopSuccess
import com.HKSHOPU.hk.component.EventShopCatSelected
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.databinding.ActivityAddshopBinding
import com.HKSHOPU.hk.ui.main.productSeller.fragment.StoreOrNotDialogStoreProductsFragment
import com.HKSHOPU.hk.ui.user.vm.ShopVModel
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.HKSHOPU.hk.widget.view.KeyboardUtil.hideKeyboard
import kotlinx.coroutines.*
import java.io.*
import java.util.*


class AddShopActivity : BaseActivity() {

    private lateinit var binding: ActivityAddshopBinding
    val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // 发生异常时的捕获
        Log.d("AddShopActivity", "errorHandler" + throwable)
    }
    private val VM = ShopVModel()
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var isSelectImage = false
    private var isChecked = false

    var shopName: String = ""
    private var shop_category_id1: String = ""
    private var shop_category_id2: String = ""
    private var shop_category_id3: String = ""
    private lateinit var settings: SharedPreferences

    var shopCategoryList: ArrayList<ShopCategoryBean>  = arrayListOf<ShopCategoryBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = getSharedPreferences("shopdata", 0)
        binding = ActivityAddshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(errorHandler) {
            withContext(Dispatchers.IO) {
                // 执行你的耗时操作代码
                doOnUiCode()
            }
        }

        CommonVariable.shopCategoryListForAdd.clear()


        initView()
        initEditText()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initClick()
        }

        initEvent()
        initVM()

    }

    private suspend fun doOnUiCode() {
        withContext(Dispatchers.Main) {
            // 更新你的UI

        }
    }

    private fun initVM() {
        VM.shopnameLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    if (it.ret_val.toString().equals("商店名稱未重複!")) {

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT).show()
                        binding.ivStep2.setImageResource(R.mipmap.ic_step2_check)
                        binding.ivStepShopNameCheck.visibility = View.VISIBLE

                    } else {

                        binding.ivStepShopNameCheck.visibility = View.INVISIBLE
                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT).show()

                    }

                   if (binding.ivStepCategoryCheck.visibility == View.VISIBLE && binding.ivStepShopNameCheck.visibility == View.VISIBLE && binding.ivStepImageCheck.visibility == View.VISIBLE) {
                        binding.tvForward.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
                        binding.tvForward.setTextColor(getColor(R.color.white))
                        binding.tvForward.isClickable = true
                    }else{
                        binding.tvForward.setBackgroundResource(R.drawable.customborder_turquise)
                        binding.tvForward.setTextColor(getColor(R.color.turquoise))
                        binding.tvForward.isClickable = false
                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })

        VM.addnewshopLiveData.observe(this, {
            when (it?.status) {
                Status.Success -> {
                    if (it.ret_val!!.equals("商店新增成功!")) {

                        RxBus.getInstance().post(EventAddShopSuccess())
                        finish()

                        Toast.makeText(
                            this@AddShopActivity,
                            it.ret_val.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@AddShopActivity,
                            it.ret_val.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                }

            }
        })

    }

    private fun initView() {
        binding.layoutAddshop.setOnClickListener {
            KeyboardUtil.hideKeyboard(binding.etShopname)
        }

        if (binding.ivStepCategoryCheck.visibility == View.VISIBLE && binding.ivStepShopNameCheck.visibility == View.VISIBLE && binding.ivStepImageCheck.visibility == View.VISIBLE) {
            binding.tvForward.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            binding.tvForward.setTextColor(getColor(R.color.white))
            binding.tvForward.isClickable = true
        }else{
            binding.tvForward.setBackgroundResource(R.drawable.customborder_turquise)
            binding.tvForward.setTextColor(getColor(R.color.turquoise))
            binding.tvForward.isClickable = false
        }

    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventShopCatSelected -> {
                        shopCategoryList = it.list

                        if (shopCategoryList.size == 1) {
                            shop_category_id1 = shopCategoryList[0].id
                            var storesort1 = shopCategoryList[0].c_shop_category
                            var storesort1_color = "#" + shopCategoryList[0].shop_category_background_color
                            binding.tvStoresort1.text = storesort1
                            binding.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )

                            binding.tvStoresort1.visibility = View.VISIBLE
                            binding.cardViewStoreSort01.visibility = View.VISIBLE
                            binding.tvStoresort2.visibility = View.INVISIBLE
                            binding.cardViewStoreSort02.visibility = View.INVISIBLE
                            binding.tvStoresort3.visibility = View.INVISIBLE
                            binding.cardViewStoreSort03.visibility = View.INVISIBLE

                        } else if (shopCategoryList.size == 2) {
                            shop_category_id1 = shopCategoryList[0].id
                            shop_category_id2 = shopCategoryList[1].id
                            var storesort1 = shopCategoryList[0].c_shop_category
                            var storesort2 = shopCategoryList[1].c_shop_category
                            var storesort1_color = "#" + shopCategoryList[0].shop_category_background_color
                            var storesort2_color = "#" + shopCategoryList[1].shop_category_background_color
                            binding.tvStoresort1.text = storesort1
                            binding.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding.tvStoresort1.visibility = View.VISIBLE
                            binding.cardViewStoreSort01.visibility = View.VISIBLE
                            binding.tvStoresort2.text = storesort2
                            binding.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding.tvStoresort2.visibility = View.VISIBLE
                            binding.cardViewStoreSort02.visibility = View.VISIBLE
                            binding.tvStoresort3.visibility = View.INVISIBLE
                            binding.cardViewStoreSort03.visibility = View.INVISIBLE
                        } else {
                            shop_category_id1 = shopCategoryList[0].id
                            shop_category_id2 = shopCategoryList[1].id
                            shop_category_id3 = shopCategoryList[2].id
                            var storesort1 = shopCategoryList[0].c_shop_category
                            var storesort2 = shopCategoryList[1].c_shop_category
                            var storesort3 = shopCategoryList[2].c_shop_category
                            var storesort1_color = "#" + shopCategoryList[0].shop_category_background_color
                            var storesort2_color = "#" + shopCategoryList[1].shop_category_background_color
                            var storesort3_color = "#" + shopCategoryList[2].shop_category_background_color
                            binding.tvStoresort1.text = storesort1
                            binding.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding.tvStoresort1.visibility = View.VISIBLE
                            binding.cardViewStoreSort01.visibility = View.VISIBLE
                            binding.tvStoresort2.text = storesort2
                            binding.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding.tvStoresort2.visibility = View.VISIBLE
                            binding.cardViewStoreSort02.visibility = View.VISIBLE
                            binding.tvStoresort3.text = storesort3
                            binding.tvStoresort3.setBackgroundColor(
                                Color.parseColor(
                                    storesort3_color
                                )
                            )
                            binding.tvStoresort3.visibility = View.VISIBLE
                            binding.cardViewStoreSort03.visibility = View.VISIBLE
                        }
                        binding.layoutStoresortPri.visibility = View.GONE
                        binding.layoutStoresortAct.visibility = View.VISIBLE
                        binding.ivStep3.setImageResource(R.mipmap.ic_step3_on)
                        binding.ivStepCategoryCheck.visibility = View.VISIBLE


                        if (binding.ivStepCategoryCheck.visibility == View.VISIBLE && binding.ivStepShopNameCheck.visibility == View.VISIBLE && binding.ivStepImageCheck.visibility == View.VISIBLE) {
                            binding.tvForward.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
                            binding.tvForward.setTextColor(getColor(R.color.white))
                            binding.tvForward.isClickable = true
                        }else{
                            binding.tvForward.setBackgroundResource(R.drawable.customborder_turquise)
                            binding.tvForward.setTextColor(getColor(R.color.turquoise))
                            binding.tvForward.isClickable = false
                        }


                    }

                }
            }, {
                it.printStackTrace()
            })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initClick() {
        binding.titleBackAddshop.setOnClickListener {

//            AlertDialog.Builder(this@AddShopActivity)
//                .setTitle("")
//                .setMessage("您尚未儲存變更，確定要離開 ？")
//                .setPositiveButton("捨棄"){
//                    // 此為 Lambda 寫法
//                        dialog, which ->finish()
//                }
//                .setNegativeButton("取消"){ dialog, which -> dialog.cancel()
//
//                }
//                .show()

            StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")

        }

        binding!!.ivShopImg.setOnClickListener {

            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)

        }

//        binding.etShopname.setOnClickListener{
//            Timer().schedule(1000){
//                KeyboardUtil.showKeyboard(it)
//            }
//        }
        var file: File? = null
        val editor = settings.edit()
        binding.tvForward.setOnClickListener {

            if(isSelectImage){

                file = processImage()

                if(  binding.ivStepShopNameCheck.visibility == View.VISIBLE){

                    editor.putString("shopname", shopName)

                    val intent = Intent(this, AddBankAccountBeforeBuildedActivity::class.java)
                    startActivity(intent)

                }else{
                    Toast.makeText(this, "請填入並確認商店名稱", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "請選擇圖片", Toast.LENGTH_SHORT).show()
            }

            editor.putString("image", encodeBitmapTobase64())
            editor.putString("shop_category_id1", shop_category_id1)
            editor.putString("shop_category_id2", shop_category_id2)
            editor.putString("shop_category_id3", shop_category_id3)
            editor.apply()

        }

        binding.layoutStoresortAct.setOnClickListener {

            if(  binding.ivStepShopNameCheck.visibility == View.VISIBLE){
                var bundle = Bundle()
                bundle.putBoolean("toShopFunction",false)
                val intent = Intent(this, ShopCategoryForAddShopActivity::class.java)
                intent.putExtra("bundle",bundle)
                startActivity(intent)
            }else{
                Toast.makeText(this, "請先填寫商店名稱", Toast.LENGTH_SHORT).show()
            }

        }

        binding.layoutStoresortPri.setOnClickListener {

            if(  binding.ivStepShopNameCheck.visibility == View.VISIBLE){
                var bundle = Bundle()
                bundle.putBoolean("toShopFunction",false)


                val intent = Intent(this, ShopCategoryForAddShopActivity::class.java)
                intent.putExtra("bundle",bundle)
                startActivity(intent)
            }else{
                Toast.makeText(this, "請先填寫商店名稱", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun initEditText() {
        binding.etShopname.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
        binding.etShopname.setOnTouchListener (object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                if(isSelectImage){

                    binding.etShopname.hasFocus()
                    KeyboardUtil.showKeyboard(v)
                }else{
                    Toast.makeText(this@AddShopActivity, "請先選擇商店圖片", Toast.LENGTH_SHORT).show()
                }

                return true
            }
        })

        binding.etShopname.doAfterTextChanged {

            shopName = binding.etShopname.text.toString()

            binding.ivStepShopNameCheck.visibility = View.INVISIBLE
            if (binding.ivStepCategoryCheck.visibility == View.VISIBLE && binding.ivStepShopNameCheck.visibility == View.VISIBLE && binding.ivStepImageCheck.visibility == View.VISIBLE) {
                binding.tvForward.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
                binding.tvForward.setTextColor(getColor(R.color.white))
                binding.tvForward.isClickable = true
            }else{
                binding.tvForward.setBackgroundResource(R.drawable.customborder_turquise)
                binding.tvForward.setTextColor(getColor(R.color.turquoise))
                binding.tvForward.isClickable = false
            }

        }
        binding.etShopname.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {

                if(shopName.isNullOrEmpty()){

                    Toast.makeText(this, "名稱不能為空值", Toast.LENGTH_SHORT).show()
                    binding.ivStepShopNameCheck.visibility = View.INVISIBLE

                }else{

                    VM.shopnamecheck(this@AddShopActivity, shopName)

                }

                hideKeyboard(v)
            }
        })
        binding.etShopname.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    binding.etShopname.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.etShopname)

                    true
                }
                else -> false
            }
        }


//        binding.etShopname.setOnKeyListener { v, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                if(keyCode == KeyEvent.KEYCODE_ENTER ){
//                    //keyCode == KeyEvent.KEYCODE_ENTER  回车键
//                    binding.etShopname.clearFocus()
//                }
//                true
//            }
//            false
//        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeBitmapTobase64(): String? {
        val drawable = binding.ivShopImg.drawable as BitmapDrawable
        val bmp = drawable.bitmap
        val bmpCompress = getResizedBitmap(bmp, 200)
        val os = ByteArrayOutputStream()
        bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 85, os)
        val byteArray: ByteArray = os.toByteArray()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    private fun processImage(): File? {
        val drawable = binding.ivShopImg.drawable as BitmapDrawable
        val bmp = drawable.bitmap
        val bmpCompress = getResizedBitmap(bmp, 200)
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + ".jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            stream.flush()
            stream.close()
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


    override fun onStart() {
        super.onStart()

    }


    override fun onBackPressed() {


//        AlertDialog.Builder(this@AddShopActivity)
//            .setTitle("")
//            .setMessage("您尚未儲存變更，確定要離開 ？")
//            .setPositiveButton("捨棄"){
//                // 此為 Lambda 寫法
//                    dialog, which ->finish()
//            }
//            .setNegativeButton("取消"){ dialog, which -> dialog.cancel()
//
//            }
//            .show()

        if(binding.etShopname.hasFocus()){
            binding.etShopname.clearFocus()
        }else{
            StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

            try {
                imageUri?.let {
                    if (Build.VERSION.SDK_INT <= 28) {

                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
                        if (bitmap != null) {

                            binding!!.ivShopImg.setImageBitmap(bitmap)
                            binding.ivStepImageCheck.visibility = View.VISIBLE
                            binding.ivStep1.setImageResource(R.mipmap.ic_step1_check)
                            binding.ivStep2.setImageResource(R.mipmap.ic_step2_on)
                            isSelectImage = true

                        } else {

                            binding!!.ivShopImg.setImageDrawable(getDrawable(R.mipmap.ic_no_image))
                            isSelectImage = false

                        }
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {

                            runOnUiThread {
                                binding!!.ivShopImg.setImageBitmap(bitmap)
                                binding.ivStepImageCheck.visibility = View.VISIBLE
                                binding.ivStep1.setImageResource(R.mipmap.ic_step1_check)
                                binding.ivStep2.setImageResource(R.mipmap.ic_step2_on)
                            }

                            isSelectImage = true

                        } else {
                            binding!!.ivShopImg.setImageDrawable(getDrawable(R.mipmap.ic_no_image))
                            isSelectImage = false
                        }

                    }
                }

            } catch (e: Exception) {

                binding!!.ivShopImg.setImageResource(R.mipmap.ic_no_image)
                isSelectImage = false
                e.printStackTrace()
                //handle exception

            }

        }
    }
}