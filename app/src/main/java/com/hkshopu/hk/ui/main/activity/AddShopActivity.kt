package com.hkshopu.hk.ui.main.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventAddShopSuccess
import com.hkshopu.hk.component.EventShopCatSelected
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.ActivityAddshopBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.*
import okhttp3.Response
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.singleLine
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.min


class AddShopActivity : BaseActivity(), TextWatcher {

    private lateinit var binding: ActivityAddshopBinding
    val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // 发生异常时的捕获
        Log.d("AddShopActivity", "errorHandler" + throwable)
    }
    private val VM = ShopVModel()
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var isSelectImage = false
    val userId = MMKV.mmkvWithID("http").getInt("UserId", 0);
    var shopName: String = ""
    private var shop_category_id1: Int = 0
    private var shop_category_id2: Int = 0
    private var shop_category_id3: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    //settings of textWatcher
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable?) {

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

                    } else {

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

                        Toast.makeText(this@AddShopActivity, it.ret_val.toString(), Toast.LENGTH_SHORT).show()

                    } else {

                        Toast.makeText(this@AddShopActivity, it.ret_val.toString(), Toast.LENGTH_SHORT).show()

                    }


                }

            }
        })

    }

    private fun initView() {
        binding.layoutAddshop.setOnClickListener {
            KeyboardUtil.hideKeyboard(binding.etShopname)
        }
        binding.tvAddnewshop.isClickable = false
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
                            binding.tvAddnewshop.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
                            binding.tvAddnewshop.setTextColor(getColor(R.color.white))
                            binding.tvAddnewshop.isClickable = true
                        }
                    }


                }
            }, {
                it.printStackTrace()
            })

    }

    private fun initClick() {
        binding.titleBackAddshop.setOnClickListener {

            finish()
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
        binding.tvAddnewshop.setOnClickListener {
            if(isSelectImage){
                file = processImage()
            }
            doAddShop(shopName,userId.toString(),shop_category_id1,shop_category_id2,shop_category_id3,file!!)
        }
        binding.tvMoreStoresort.setOnClickListener {
            val intent = Intent(this, ShopCategoryActivity::class.java)
            startActivity(intent)
        }

    }


    private fun initEditText() {
        binding.etShopname.addTextChangedListener(this)
        binding.etShopname.setOnEditorActionListener{ v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    VM.shopnamecheck(this@AddShopActivity, shopName)

                    binding.etShopname.clearFocus()
                    KeyboardUtil.showKeyboard(binding.etShopname)

                    true
                }
                else -> false
            }
        }
//        password1.addTextChangedListener(this)
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

    private fun doAddShop(
        shop_title: String,
        user_id: String,
        shop_category_id1: Int,
        shop_category_id2: Int,
        shop_category_id3: Int,
        postImg: File
    ) {
        val url = ApiConstants.API_HOST+"/shop/save/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("商店與選擇商店分類新增成功!")) {
                        var user_id: Int = json.getInt("user_id")
                        var shop_id:Int = json.getInt("shop_id")
                        MMKV.mmkvWithID("http").putInt("UserId", user_id)
                        MMKV.mmkvWithID("http").putInt("ShopId", shop_id)
                        val intent = Intent(this@AddShopActivity, ShopmenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddShopActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
//                        initRecyclerView()


                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ShopAdd(
            url,
            shop_title,
            user_id,
            shop_category_id1,
            shop_category_id2,
            shop_category_id3,
            postImg
        )
    }

    override fun onStart() {
        super.onStart()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

            try {
                imageUri?.let {
                    if(Build.VERSION.SDK_INT <= 28) {
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
                    }else{
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

                        }else{
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