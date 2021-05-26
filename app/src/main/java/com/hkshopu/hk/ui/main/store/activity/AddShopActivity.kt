package com.hkshopu.hk.ui.main.store.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventAddShopSuccess
import com.hkshopu.hk.component.EventShopCatSelected
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.ActivityAddshopBinding
import com.hkshopu.hk.ui.main.product.fragment.StoreOrNotDialogStoreProductsFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
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
    private var shop_category_id1: Int = 0
    private var shop_category_id2: Int = 0
    private var shop_category_id3: Int = 0
    private lateinit var settings: SharedPreferences
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

        initView()
        initEditText()
        initClick()
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
                        binding.ivStep2Check.visibility = View.VISIBLE
                        isChecked = true

                    } else {
                        binding.ivStep2Check.visibility = View.INVISIBLE
                        isChecked = false
                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT).show()

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
        binding.tvForward.isClickable = false
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        var list: ArrayList<ShopCategoryBean>
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventShopCatSelected -> {
                        list = it.list

                        if (list.size == 1) {
                            shop_category_id1 = list[0].id
                            var storesort1 = list[0].c_shop_category
                            var storesort1_color = "#" + list[0].shop_category_background_color
                            binding.tvStoresort1.text = storesort1
                            binding.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )

                            binding.tvStoresort1.visibility = View.VISIBLE
                            binding.tvStoresort2.visibility = View.INVISIBLE
                            binding.tvStoresort3.visibility = View.INVISIBLE

                        } else if (list.size == 2) {
                            shop_category_id1 = list[0].id
                            shop_category_id2 = list[1].id
                            var storesort1 = list[0].c_shop_category
                            var storesort2 = list[1].c_shop_category
                            var storesort1_color = "#" + list[0].shop_category_background_color
                            var storesort2_color = "#" + list[1].shop_category_background_color
                            binding.tvStoresort1.text = storesort1
                            binding.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding.tvStoresort1.visibility = View.VISIBLE
                            binding.tvStoresort2.text = storesort2
                            binding.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding.tvStoresort2.visibility = View.VISIBLE
                            binding.tvStoresort3.visibility = View.INVISIBLE
                        } else {
                            shop_category_id1 = list[0].id
                            shop_category_id2 = list[1].id
                            shop_category_id3 = list[2].id
                            var storesort1 = list[0].c_shop_category
                            var storesort2 = list[1].c_shop_category
                            var storesort3 = list[2].c_shop_category
                            var storesort1_color = "#" + list[0].shop_category_background_color
                            var storesort2_color = "#" + list[1].shop_category_background_color
                            var storesort3_color = "#" + list[2].shop_category_background_color
                            binding.tvStoresort1.text = storesort1
                            binding.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding.tvStoresort1.visibility = View.VISIBLE
                            binding.tvStoresort2.text = storesort2
                            binding.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding.tvStoresort2.visibility = View.VISIBLE
                            binding.tvStoresort3.text = storesort3
                            binding.tvStoresort3.setBackgroundColor(
                                Color.parseColor(
                                    storesort3_color
                                )
                            )
                            binding.tvStoresort3.visibility = View.VISIBLE
                        }
                        binding.layoutStoresortPri.visibility = View.GONE
                        binding.layoutStoresortAct.visibility = View.VISIBLE
                        binding.ivStep3.setImageResource(R.mipmap.ic_step3_on)
                        binding.ivStep3Check.visibility = View.VISIBLE
                        if (binding.ivStep3Check.visibility == View.VISIBLE && binding.ivStep2Check.visibility == View.VISIBLE && binding.ivStep1Check.visibility == View.VISIBLE) {
                            binding.tvForward.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
                            binding.tvForward.setTextColor(getColor(R.color.white))
                            binding.tvForward.isClickable = true
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
            if (isSelectImage) {
                file = processImage()
            }
//            var uri = Uri.fromFile(file);
            if(isChecked){
                editor.putString("shopname", shopName)
            }
            editor.putString("image", encodeBitmapTobase64())
            editor.putInt("shop_category_id1", shop_category_id1)
            editor.putInt("shop_category_id2", shop_category_id2)
            editor.putInt("shop_category_id3", shop_category_id3)
            editor.apply()

            if(isChecked) {
                val intent = Intent(this, AddBankAccountActivity::class.java)
                startActivity(intent)
//                finish()
            }
        }
        binding.layoutStoresortAct.setOnClickListener {
            var bundle = Bundle()
            bundle.putBoolean("toShopFunction",false)
            val intent = Intent(this, ShopCategoryActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.tvMoreStoresort.setOnClickListener {
            var bundle = Bundle()
            bundle.putBoolean("toShopFunction",false)
            val intent = Intent(this, ShopCategoryActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

    }


    private fun initEditText() {
        binding.etShopname.doAfterTextChanged {
            shopName = binding.etShopname.text.toString()

            binding.etShopname.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        VM.shopnamecheck(this@AddShopActivity, shopName)

                        binding.etShopname.clearFocus()

                        KeyboardUtil.hideKeyboard(binding.etShopname)

                        true
                    }
                    else -> false
                }
            }
        }
//        password1.addTextChangedListener(this)
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

        StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")

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
                            binding.ivStep1Check.visibility = View.VISIBLE
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
                                binding.ivStep1Check.visibility = View.VISIBLE
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