package com.hkshopu.hk.ui.main.store.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.R
import com.hkshopu.hk.component.*
import com.hkshopu.hk.component.CommonVariable.Companion.addresslist
import com.hkshopu.hk.data.bean.ShopAddressBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.databinding.ActivityShopinfomodifyBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener

import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.extension.load
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class ShopInfoModifyActivity : BaseActivity() {
    private lateinit var binding: ActivityShopinfomodifyBinding

    private val VM = AuthVModel()
    private val pickImage = 100
    private val pickImage_s = 200
    private var imageUri: Uri? = null
    private var isSelectImage = false
    private var isSelectImage_s = false
    val list = ArrayList<ShopInfoBean>()
    var addresslist = ArrayList<ShopAddressBean>()
    val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/show/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopinfomodifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getShopInfo(url)
        initVM()
        initEvent()
        initClick()

    }

    private fun initVM() {
//        VM.socialloginLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    if (url.isNotEmpty()) {
//                        toast("登录成功")
//
//                    }
//
//                    finish()
//                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
//            }
//        })
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventChangeShopTitleSuccess -> {
                        binding.tvShopName.text = it.shopname
                        getShopInfo(url)
                    }
                    is EventAddShopBriefSuccess -> {
                        binding.tvShopBrief.text = it.description
                        getShopInfo(url)
                    }

                    is EventChangeShopPhoneSuccess -> {
                        binding.tvShopPhone.text = it.phone
                        getShopInfo(url)
                    }

                    is EventChangeShopEmailSuccess -> {
                        binding.tvUserEmail.text = it.email
                        getShopInfo(url)
                    }
                }

            })
    }


    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.tvShoppicBAdd.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        binding.ivShopImgEdit.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage_s)
        }

        binding.ivChevronShopName.setOnClickListener {
            val addressId = addresslist[0].id
            val shopName = list[0].shop_title
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("shop_name",shopName)
            val intent = Intent(this, ShopNameEditActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.ivChevronShopBrief.setOnClickListener {
            val addressId = addresslist[0].id
            val shopIcon = list[0].shop_icon
            val shopPic = list[0].shop_pic
            val shopDes = list[0].shop_description
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("shop_icon",shopIcon)
            bundle.putString("shop_pic",shopPic)
            bundle.putString("shop_des",shopDes)
            val intent = Intent(this, AddShopBriefActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.ivChevronShopPhone.setOnClickListener {
            val addressId = addresslist[0].id
            val phone_old = list[0].shop_phone
            val phone_is_show = list[0].shop_is_phone_show
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("phone_old",phone_old)
            bundle.putString("phone_is_show",phone_is_show)
            val intent = Intent(this, PhoneEditActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.ivChevronUserEmail.setOnClickListener {
            val addressId = addresslist[0].id
            val email_old = list[0].shop_email
            val email_on = list[0].email_on
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("email_old",email_old)
            bundle.putString("email_on",email_on)
            val intent = Intent(this, EmailAdd1Activity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.ivChevronUserSocialaccount.setOnClickListener {
            val addressId = addresslist[0].id
            val facebook_on = list[0].facebook_on
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("facebook_on",facebook_on)
            val intent = Intent(this, SocialAccountSetActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

//        binding.ivChevronPwchange.setOnClickListener{
//
//        }

        binding.tvSave.setOnClickListener {

        }

    }

    private fun getShopInfo(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                list.clear()
                addresslist.clear()
                val shop_category_id_list = ArrayList<String>()
                shop_category_id_list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopInfoFragment", "返回資料 resStr：" + resStr)
                    Log.d("ShopInfoFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("ShopInfoFragment", "返回資料 Object：" + jsonObject.toString())
                        val shopInfoBean: ShopInfoBean =
                            Gson().fromJson(jsonObject.toString(), ShopInfoBean::class.java)
                        list.add(shopInfoBean)

                        val shopaddress: JSONArray = jsonObject.getJSONArray("shop_address")
                        if (shopaddress.length() > 0) {
                            for (i in 0 until shopaddress.length()) {
                                val address = shopaddress.get(i)
                                val shopAddressBean: ShopAddressBean =
                                    Gson().fromJson(address.toString(), ShopAddressBean::class.java)
                                addresslist.add(shopAddressBean)
                            }
                        }

                        runOnUiThread {
                            binding!!.tvShopName.text = list[0].shop_title
                            val shop_is_phone_show = list[0].shop_is_phone_show

//                            if(shop_is_phone_show.equals("Y")) {
//                                binding.tvShopPhone.text = list[0].shop_phone
//                            }else{
//                                binding.tvShopPhone.text = list[0].shop_phone
//                            }
                            binding.tvShopPhone.text = list[0].shop_phone

                            list[0].email_on ?. let {
                                if (list[0].email_on.equals("Y")) {

                                }
                                null // finally returns null
                            } ?: let {

                            }
                            val email_old = list[0].shop_email

                            list[0].email_on ?. let {
//                                if (list[0].email_on.equals("Y")) {
//                                }
                                binding.tvUserEmail.text = email_old
                                null // finally returns null
                            } ?: let {

                            }

                            binding.tvShopBrief.text = list[0].shop_description
                            binding!!.ivShopImg.load(list[0].shop_icon)
                            binding!!.ivShoppicB.load(list[0].shop_pic)
                            if(list[0].shop_pic.length > 0) {
                                binding.tvShoppicBAdd.setText(R.string.modify_newbg)
                            }
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
        web.Get_Data(url)
    }

    private fun doShopIconUpdate(postImg: File) {
        val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        var url = ApiConstants.API_PATH + "shop/" + shopId + "/update/"
        val addressId = addresslist[0].id
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopInfoModifyActivity", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopIconUpdate(url, addressId,postImg)
    }

    private fun doShopPicUpdate(postImg: File) {
        val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        var url = ApiConstants.API_PATH + "shop/" + shopId + "/update/"
        val addressId = addresslist[0].id
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopInfoModifyActivity", "返回資料 resStr：" + resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("ShopInfoModifyActivity", "返回資料 ret_val：" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopPicUpdate(url, addressId,postImg)
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

    private fun processImageB(): File? {
        val drawable = binding.ivShoppicB.drawable as BitmapDrawable
        val bmp = drawable.bitmap
        val bmpCompress = getResizedBitmap(bmp, 1440)
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
                            binding!!.ivShoppicB.setImageBitmap(bitmap)

                            isSelectImage = true
                            val file = processImageB()
                            doShopPicUpdate(file!!)

                        } else {

                            isSelectImage = false
                        }
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding!!.ivShoppicB.setImageBitmap(bitmap)
                                binding.tvShoppicBAdd.setText(R.string.modify_newbg)

                            }
                            val file = processImageB()
                            doShopPicUpdate(file!!)
                            isSelectImage = true

                        } else {

                            isSelectImage = false
                        }
                    }
                }

            } catch (e: Exception) {

                isSelectImage = false
                e.printStackTrace()
                //handle exception
            }

        }
        if (resultCode == RESULT_OK && requestCode == pickImage_s) {
            imageUri = data?.data

            try {
                imageUri?.let {
                    if (Build.VERSION.SDK_INT <= 28) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
                        if (bitmap != null) {
                            binding!!.ivShopImg.setImageBitmap(bitmap)
                            binding!!.ivShopImgEdit.visibility = View.INVISIBLE
                            isSelectImage_s = true
                            val file = processImage()
                            doShopIconUpdate(file!!)

                        } else {
                            binding!!.ivShopImg.setImageDrawable(getDrawable(R.mipmap.ic_no_image))
                            isSelectImage_s = false
                        }
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding!!.ivShopImg.setImageBitmap(bitmap)
                                binding!!.ivShopImgEdit.visibility = View.VISIBLE
                                val file = processImage()
                                doShopIconUpdate(file!!)
                            }

                            isSelectImage_s = true

                        } else {
                            binding!!.ivShopImg.setImageDrawable(getDrawable(R.mipmap.ic_no_image))
                            isSelectImage_s = false
                        }
                    }
                }

            } catch (e: Exception) {

                isSelectImage_s = false
                e.printStackTrace()
                //handle exception
            }
        }
    }
}