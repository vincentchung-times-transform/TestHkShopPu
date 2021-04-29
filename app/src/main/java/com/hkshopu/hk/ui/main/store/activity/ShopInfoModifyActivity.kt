package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityShopinfomodifyBinding

import com.hkshopu.hk.ui.user.vm.AuthVModel


class ShopInfoModifyActivity : BaseActivity() {
    private lateinit var binding: ActivityShopinfomodifyBinding

    private val VM = AuthVModel()
    private val pickImage = 100
    private val pickImage_s = 200
    private var imageUri: Uri? = null
    private var imageUri_s: Uri? = null
    private var isSelectImage = false
    private var isSelectImage_s = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopinfomodifyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initVM()
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

        binding.tvShopName.setOnClickListener {
            val intent = Intent(this, ShopNameEditActivity::class.java)
            startActivity(intent)
        }

        binding.ivChevronShopPhone.setOnClickListener{
            val intent = Intent(this, PhoneEditActivity::class.java)
            startActivity(intent)
        }

        binding.ivChevronUserEmail.setOnClickListener{
            val intent = Intent(this, EmailAdd1Activity::class.java)
            startActivity(intent)
        }

        binding.ivChevronUserSocialaccount.setOnClickListener{
            val intent = Intent(this, SocialAccountSetActivity::class.java)
            startActivity(intent)
        }

//        binding.ivChevronPwchange.setOnClickListener{
//
//        }

        binding.tvSave.setOnClickListener {

        }

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
                            binding!!.ivShoppicB.setImageBitmap(bitmap)

                            isSelectImage = true


                        } else {

                            isSelectImage = false
                        }
                    }else{
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding!!.ivShoppicB.setImageBitmap(bitmap)
                                binding.tvShoppicBAdd.setText(R.string.modify_newbg)
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

        }else if(resultCode == RESULT_OK && requestCode == pickImage_s){
            imageUri_s = data?.data

            try {
                imageUri_s?.let {
                    if(Build.VERSION.SDK_INT <= 28) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri_s)
                        if (bitmap != null) {
                            binding!!.ivShopImg.setImageBitmap(bitmap)

                            isSelectImage_s = true


                        } else {

                            isSelectImage_s = false
                        }
                    }else{
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri_s!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        if (bitmap != null) {
                            runOnUiThread {
                                binding!!.ivShopImg.setImageBitmap(bitmap)

                            }

                            isSelectImage_s = true

                        }else{

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