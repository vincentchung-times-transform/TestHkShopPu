package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson

import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.component.CommonVariable
import com.hkshopu.hk.component.EventAddShopBriefSuccess
import com.hkshopu.hk.component.EventGetShopCatSuccess
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityAddshopbriefBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.user.activity.BuildAccountActivity


import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.extension.load
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKV.mmkvWithID
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class AddShopBriefActivity : BaseActivity() {
    private lateinit var binding: ActivityAddshopbriefBinding

    private val VM = AuthVModel()
    private val pickImage = 200
    private var imageUri: Uri? = null
    private var isSelectImage = false
    private var address_id:String=""
    private lateinit var description:String
    val shopId = mmkvWithID("http").getInt("ShopId",0)
    val shoptitle = mmkvWithID("http").getString("shoptitle","")
    var url = ApiConstants.API_HOST+"shop/"+shopId+"/get_simple_info_of_specific_shop/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddshopbriefBinding.inflate(layoutInflater)
        setContentView(binding.root)
        address_id = CommonVariable.addresslist[0].id
//        shop_Icon = intent.getBundleExtra("bundle")!!.getString("shop_icon","")
//        shop_Pic = intent.getBundleExtra("bundle")!!.getString("shop_pic","")
//        shop_Des = intent.getBundleExtra("bundle")!!.getString("shop_des","")
        initView()
        initVM()
        initClick()
        getShopBrief(url)


    }
    private fun initView(){
        binding.tvAddshopbriefName.text = shoptitle
        binding.etAddshopbrief.doAfterTextChanged {
            description =  binding.etAddshopbrief.text.toString()
        }
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

    private fun initClick() {
        binding.layoutAddshopbrief.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        binding.titleBackAddshopbrief.setOnClickListener {

            finish()
        }
        binding.layoutShopbg.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        binding.ivAddshopbriefShare.setOnClickListener {

        }

        binding.ivAddshopbriefSave.setOnClickListener {
            doShopDesUpdate(description)
        }

    }
    private fun getShopBrief(url: String) {
        Log.d("AddShopBriefActivity", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    val list = ArrayList<ShopAddressBriefBean>()
                    list.clear()
                    val infolist = ArrayList<ShopBriefBean>()
                    infolist.clear()
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopBriefActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopBriefActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status =  json.get("status")
                    if (status==0) {
                        val jsonObject: JSONObject = json.getJSONObject("data")
                        val shopBriefBean: ShopBriefBean =
                            Gson().fromJson(jsonObject.toString(), ShopBriefBean::class.java)
                            infolist.add(shopBriefBean)

                            runOnUiThread {

                                    binding.ivAddshopbriefPic.load(infolist[0].shop_icon)
                                    binding.ivShopimage.load(infolist[0].background_pic)
                                    binding.etAddshopbrief.setText(infolist[0].long_description)

                                if(infolist[0].shop_email.length > 0) {
                                    binding.tvAddshopbriefContact.visibility = View.VISIBLE
                                    binding.ivAddshopbriefEmail.visibility = View.VISIBLE
                                    binding.tvAddshopbriefEmail.visibility = View.VISIBLE
                                    binding.tvAddshopbriefEmail.text = infolist[0].shop_email
                                }


                                if(infolist[0].phone.length > 0) {

                                    binding.tvAddshopbriefContact.visibility = View.VISIBLE
                                    binding.ivAddshopbriefContact1.visibility = View.VISIBLE
                                    binding.tvAddshopbriefPhone.text = infolist[0].phone
                                    binding.tvAddshopbriefPhone.visibility = View.VISIBLE
                                }

                                val shopaddress: JSONArray = jsonObject.getJSONArray("shop_address")
                                if (shopaddress.length() > 0) {
                                    for (i in 0 until shopaddress.length()) {
                                        val address = shopaddress.get(i)
                                        if(address.toString().length > 0) {
                                            val shopAddressBriefBean: ShopAddressBriefBean =
                                                Gson().fromJson(address.toString(), ShopAddressBriefBean::class.java)
                                            list.add(shopAddressBriefBean)
                                            if(list[i].area.length > 0) {
                                                val address_brief =
                                                    list[i].area + list[i].district + list[i].road + list[i].number + list[i].other + list[i].floor + list[i].room
                                                binding.ivAddshopbriefAddress.visibility = View.VISIBLE
                                                binding.tvAddshopbriefAddress.visibility = View.VISIBLE
                                                binding.tvAddshopbriefAddress.text = address_brief

                                            }
                                        }

                                    }
                                    binding.tvAddshopbriefContact.visibility = View.VISIBLE
                                }

                            }
                        }else{

                        runOnUiThread {

                            Toast.makeText(this@AddShopBriefActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Get_Data(url)
    }
    private fun processImage(): File? {
        val drawable = binding.ivShopimage.drawable as BitmapDrawable
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
                    if(Build.VERSION.SDK_INT <= 28) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
                        if (bitmap != null) {
                            binding.ivNoimage.visibility = View.INVISIBLE
                            binding!!.ivShopimage.setImageBitmap(bitmap)

                            isSelectImage = true
                            val file = processImage()
                            doShopBgUpdate(file!!)

                        } else {

                            isSelectImage = false
                        }
                    }else{
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding.ivNoimage.visibility = View.INVISIBLE
                                binding!!.ivShopimage.setImageBitmap(bitmap)
                                val file = processImage()
                                doShopBgUpdate(file!!)
                            }

                            isSelectImage = true

                        }else{

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
    }



    private fun doShopBgUpdate(postImg: File) {
        val shopId = mmkvWithID("http").getInt("ShopId", 0)
        var url = ApiConstants.API_PATH + "shop/" + shopId + "/update/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopBriefActivity", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@AddShopBriefActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@AddShopBriefActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopBgUpdate(url, address_id,postImg)
    }
    private fun doShopDesUpdate(description: String) {
        val shopId = mmkvWithID("http").getInt("ShopId",0)
        var url = ApiConstants.API_PATH+"shop/"+shopId+"/update/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopBriefActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopBriefActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        RxBus.getInstance().post(EventAddShopBriefSuccess(description))
                        finish()
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@AddShopBriefActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopDesUpdate(url, address_id,description)
    }


}