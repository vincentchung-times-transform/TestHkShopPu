package com.HKSHOPU.hk.ui.main.seller.shop.activity

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

import com.HKSHOPU.hk.Base.BaseActivity

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopAddressBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.databinding.ActivityShopinfomodifyBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener

import com.HKSHOPU.hk.ui.onboard.vm.AuthVModel
import com.HKSHOPU.hk.utils.extension.load
import com.HKSHOPU.hk.utils.rxjava.RxBus
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
    var shopInfoBean = ShopInfoBean()
    var addresslist = ArrayList<ShopAddressBean>()
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/show/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopinfomodifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getShopInfo(url)
        initEvent()
        initClick()
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

        binding.layoutShopName.setOnClickListener {
            val addressId = addresslist[0].id
            val shopName = shopInfoBean.shop_title
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("shop_name",shopName)
            val intent = Intent(this, ShopNameEditActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.layoutShopBrief.setOnClickListener {
            val addressId = addresslist[0].id
            val shopIcon = shopInfoBean.shop_icon
            val shopPic = shopInfoBean.shop_pic
            val shopDes = shopInfoBean.shop_description
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("shop_icon",shopIcon)
            bundle.putString("shop_pic",shopPic)
            bundle.putString("shop_des",shopDes)
            val intent = Intent(this, AddShopBriefActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.layoutShopPhone.setOnClickListener {
            val addressId = addresslist[0].id
            val phone_old = shopInfoBean.shop_phone
            val phone_is_show = shopInfoBean.shop_is_phone_show
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("phone_old",phone_old)
            bundle.putString("phone_is_show",phone_is_show)
            val intent = Intent(this, PhoneEditActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.layoutUserEmail.setOnClickListener {
            val addressId = addresslist[0].id
            val email_old = shopInfoBean.shop_email
            val email_on = shopInfoBean.email_on
            var bundle = Bundle()
            bundle.putString("address_id",addressId)
            bundle.putString("email_old",email_old)
            bundle.putString("email_on",email_on)
            val intent = Intent(this, EmailAddBeforeIdentifyingActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        binding.ivChevronUserSocialaccount.setOnClickListener {
            val addressId = addresslist[0].id
            val facebook_on = shopInfoBean.facebook_on
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
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                addresslist.clear()
                val shop_category_id_list = ArrayList<String>()
                shop_category_id_list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("ShopInfoFragment", "返回資料 resStr：" + resStr)
                    Log.d("ShopInfoFragment", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("ShopInfoFragment", "返回資料 Object：" + jsonObject.toString())
                        shopInfoBean =
                            Gson().fromJson(jsonObject.toString(), ShopInfoBean::class.java)


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
                            binding!!.tvShopName.text = shopInfoBean.shop_title
                            val shop_is_phone_show = shopInfoBean.shop_is_phone_show

//                            if(shop_is_phone_show.equals("Y")) {
//                                binding.tvShopPhone.text = list[0].shop_phone
//                            }else{
//                                binding.tvShopPhone.text = list[0].shop_phone
//                            }
                            binding.tvShopPhone.text = shopInfoBean.shop_phone

                            shopInfoBean.email_on ?. let {
                                if (shopInfoBean.email_on.equals("Y")) {

                                }
                                null // finally returns null
                            } ?: let {

                            }
                            val email_old = shopInfoBean.shop_email

                            shopInfoBean.email_on ?. let {
//                                if (list[0].email_on.equals("Y")) {
//                                }
                                binding.tvUserEmail.text = email_old
                                null // finally returns null
                            } ?: let {

                            }

                            binding.tvShopBrief.text = shopInfoBean.shop_description
                            binding!!.ivShopImg.load(shopInfoBean.shop_icon)
                            binding!!.ivShoppicB.load(shopInfoBean.shop_pic)
                            if(shopInfoBean.shop_pic.length > 0) {
                                binding.tvShoppicBAdd.setText(R.string.modify_newbg)
                            }

                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getShopInfo_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getShopInfo_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getShopInfo_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun doShopIconUpdate(postImg: File) {
        runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
            binding.imgViewLoadingBackground.visibility = View.VISIBLE
        }
        val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        var url = ApiConstants.API_PATH + "shop/" + shopId + "/update/"
        val addressId = addresslist[0].id
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("ShopInfoModifyActivity", "返回資料 resStr：" + resStr)

                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doShopIconUpdate_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doShopIconUpdate_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doShopIconUpdate_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Do_ShopIconUpdate(url, addressId,postImg)
    }

    private fun doShopPicUpdate(postImg: File) {
        runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
            binding.imgViewLoadingBackground.visibility = View.VISIBLE
        }
        val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        var url = ApiConstants.API_PATH + "shop/" + shopId + "/update/"
        val addressId = addresslist[0].id
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doShopPicUpdate", "返回資料 resStr：" + resStr)
                    Log.d("doShopPicUpdate", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ShopInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doShopPicUpdate_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doShopPicUpdate_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doShopIconUpdate_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
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
                Log.d("onActivityResult_errorMessage", "Exception: ${e.toString()}")
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
                Log.d("onActivityResult_errorMessage", "Exception: ${e.toString()}")
                isSelectImage_s = false
                e.printStackTrace()
                //handle exception
            }
        }
    }
}