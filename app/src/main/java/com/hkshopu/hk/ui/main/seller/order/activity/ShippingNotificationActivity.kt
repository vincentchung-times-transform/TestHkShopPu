package com.HKSHOPU.hk.ui.main.seller.order.activity


import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.databinding.ActivityShippingNotificationBinding
import com.HKSHOPU.hk.ui.login.vm.ShopVModel
import com.HKSHOPU.hk.ui.main.seller.order.fragment.DeliveryNotifyApplyDialogFragment
import com.HKSHOPU.hk.ui.main.seller.product.fragment.StoreOrNotDialogStoreProductsFragment
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.HKSHOPU.hk.widget.view.KeyboardUtil.hideKeyboard
import kotlinx.coroutines.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShippingNotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityShippingNotificationBinding
    val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // 发生异常时的捕获
        Log.d("AddShopActivity", "errorHandler" + throwable)
    }
    private val VM = ShopVModel()
    var shopCategoryList: ArrayList<ShopCategoryBean>  = arrayListOf<ShopCategoryBean>()

    //value
    var order_id = ""
    var shipping_number = ""
    var expected_received_date = ""
    var attachment_pics_list:ArrayList<String> = arrayListOf()
    private val pickImage = 100
    val REQUEST_EXTERNAL_STORAGE = 100
    private var imageUri: Uri? = null
    lateinit var manager: FragmentManager
    var file_name = ""
    lateinit var drawable: Drawable
    var attachmentSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.getBundleExtra("bundle")
        order_id = bundle!!.getString("order_id").toString()

        GlobalScope.launch(errorHandler) {
            withContext(Dispatchers.IO) {
                // 执行你的耗时操作代码
                doOnUiCode()
            }
        }

        initView()
        initEditText()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initClick()
        }
    }


    private suspend fun doOnUiCode() {
        withContext(Dispatchers.Main) {
            // 更新你的UI
        }
    }


    private fun initView() {
        binding.ivForward.setOnClickListener {
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initClick() {
        binding.titleBack.setOnClickListener {
            StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")
        }
        binding!!.btnAddPics.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
//        binding.etShopname.setOnClickListener{
//            Timer().schedule(1000){
//                KeyboardUtil.showKeyboard(it)
//            }
//        }


        manager = supportFragmentManager
        var file: File? = null
        binding.ivForward.setOnClickListener {
                file = processImage()

            if(shipping_number.isNullOrEmpty()){
                Toast.makeText(this, "請輸入寄件編號", Toast.LENGTH_SHORT).show()
            }else{
                if(checkDateformat(binding.etEnterShippingDate.text.toString())){

                    var TransferTime_db=changeDateFormat_forDB(binding.etEnterShippingDate.text.toString())

                    DeliveryNotifyApplyDialogFragment(order_id, shipping_number, TransferTime_db, file_name , file!!, drawable, attachmentSelected).show(
                        manager,
                        "MyCustomFragment"
                    )

                }else{
                    Toast.makeText(this, "日期格式不符合", Toast.LENGTH_SHORT).show()
                }
            }

        }
        binding.etEnterShippingNumber.setOnClickListener {
            if(  binding.etEnterShippingNumber.visibility == View.VISIBLE){
                var bundle = Bundle()
                bundle.putBoolean("toShopFunction",false)
            }else{
                Toast.makeText(this, "請先填寫並完成商店名稱編輯", Toast.LENGTH_SHORT).show()
            }
        }
        binding.showDateBtn.setOnClickListener {
            ShowDatePick(it)
        }
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
                )
            } else {
                launchGalleryIntent()
            }
        }
        binding.btnDeletePic.setOnClickListener {
            binding!!.btnAddPics.setImageResource(com.HKSHOPU.hk.R.mipmap.btn_add_pics)
            binding.btnDeletePic.visibility = View.GONE
        }
    }

    fun initEditText() {
        binding.etEnterShippingNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
        binding.etEnterShippingNumber.doAfterTextChanged {
            shipping_number = binding.etEnterShippingNumber.text.toString()
        }
        binding.etEnterShippingNumber.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })
        binding.etEnterShippingNumber.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    binding.etEnterShippingNumber.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.etEnterShippingNumber)

                    true
                }
                else -> false
            }
        }
        binding.etEnterShippingNumber.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //keyCode == KeyEvent.KEYCODE_ENTER  回車鍵
                binding.etEnterShippingNumber.clearFocus()
                true
            } else {
                false
            }
        }
    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun encodeBitmapTobase64(): String? {
//        val drawable = binding.btnAddPics.drawable as BitmapDrawable
//        val bmp = drawable.bitmap
//        val bmpCompress = getResizedBitmap(bmp, 200)
//        val os = ByteArrayOutputStream()
//        bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 85, os)
//        val byteArray: ByteArray = os.toByteArray()
//        return Base64.getEncoder().encodeToString(byteArray)
//    }
    fun processImage(): File? {
        drawable = binding.btnAddPics.drawable
        var drawableToBitmap = binding.btnAddPics.drawable as BitmapDrawable
        val bmp = drawableToBitmap.bitmap
        val bmpCompress = getResizedBitmap(bmp, 200)
        val file: File
        val path = getExternalFilesDir(null).toString()
//        Log.d("processImage", "before: ${path}")
//        file_name = path.substringAfterLast("/")
//        Log.d("processImage", "after: ${file_name}")
        file_name = "attachment"
        file = File(path, file_name.toString() + ".jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) // Catch the exception
        {
            e.printStackTrace()
        }
        return file
    }

    fun launchGalleryIntent() {
        val gallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }
    override fun onStart() {
        super.onStart()
    }
    override fun onBackPressed() {
        StoreOrNotDialogStoreProductsFragment(this).show(supportFragmentManager, "MyCustomFragment")
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
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            Log.d("onActivityResult", "imageUri: ${imageUri}")
            try {
                imageUri?.let {
                    if (Build.VERSION.SDK_INT <= 28) {

                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
                        if (bitmap != null) {
                            binding!!.btnAddPics.setImageBitmap(bitmap)
                            binding.btnDeletePic.visibility = View.VISIBLE

                            attachmentSelected = true
                        } else {
                            binding!!.btnAddPics.setImageResource(com.HKSHOPU.hk.R.mipmap.btn_add_pics)
                            binding.btnDeletePic.visibility = View.GONE

                            attachmentSelected = false
                        }
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding!!.btnAddPics.setImageBitmap(bitmap)
                                binding.btnDeletePic.visibility = View.VISIBLE

                                attachmentSelected = true
                            }
                        } else {
                            binding!!.btnAddPics.setImageResource(com.HKSHOPU.hk.R.mipmap.btn_add_pics)
                            binding.btnDeletePic.visibility = View.GONE

                            attachmentSelected = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("onActivityResult", "Exception: ${e.toString()}")
                binding!!.btnAddPics.setImageResource(com.HKSHOPU.hk.R.mipmap.btn_add_pics)
                e.printStackTrace()
                //handle exception
            }
        }
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        width = maxSize
        height = (width / bitmapRatio).toInt()
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun inspect_value(){
        if(shipping_number.isNotEmpty() && expected_received_date.isNotEmpty() && attachment_pics_list.size > 0 ){
            //dialog show
        }else{
            Toast.makeText(this, "尚有欄位未填寫", Toast.LENGTH_SHORT).show()
        }
    }

    fun ShowDatePick(view: View) {
        if (view.getId() === R.id.show_date_btn) {
            var calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]
            calendar.add(Calendar.MONTH,3)
            var afterTwoMonthsinMilli=calendar.getTimeInMillis()

            var dialog = DatePickerDialog(
                this, R.style.DateTimeDialogTheme,
                { datePicker, year, month, day ->
                    val month_actual = month + 1

                    binding.etEnterShippingDate.setText(changeDateFormat_forApp("$day/$month_actual/$year").toString())
                }, mYear, mMonth, mDay
            )

            dialog.getDatePicker().setMaxDate(afterTwoMonthsinMilli)
            dialog.show()
        }
    }
    fun changeDateFormat_forApp(item : String): String {
        val parser = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val output: String = formatter.format(parser.parse(item))

        return output
    }
    fun changeDateFormat_forDB(item : String): String {
        val parser = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val output: String = formatter.format(parser.parse(item))

        return output
    }

    fun checkDateformat(dateToCheck:String): Boolean {
        var rex="""^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}${'$'}""".toRegex()
        return(dateToCheck.matches(rex))
    }

}