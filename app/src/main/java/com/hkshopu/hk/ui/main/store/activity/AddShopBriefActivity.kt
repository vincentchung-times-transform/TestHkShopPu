package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.provider.MediaStore
import android.view.View

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.ActivityAddshopbriefBinding


import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV.mmkvWithID


class AddShopBriefActivity : BaseActivity() {
    private lateinit var binding: ActivityAddshopbriefBinding

    private val VM = AuthVModel()
    private val pickImage = 200
    private var imageUri: Uri? = null
    private var isSelectImage = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddshopbriefBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initClick()


    }
    private fun initView(){
        val phoneShow:Boolean = mmkvWithID("http").getBoolean("PhoneShow",false)
        val phone:String? = mmkvWithID("http").getString("phone","")
        if(phoneShow){
            binding.tvAddshopbriefContact.visibility = View.VISIBLE
            binding.ivAddshopbriefContact1.visibility = View.VISIBLE
            binding.tvAddshopbriefPhone.text = phone
        }
        val emailShow:Boolean = mmkvWithID("http").getBoolean("EmailShow",false)
        val email:String? = mmkvWithID("http").getString("email","")
        if(emailShow){
            binding.tvAddshopbriefContact.visibility = View.VISIBLE
            binding.ivAddshopbriefEmail.visibility = View.VISIBLE
            binding.tvAddshopbriefEmail.text = email
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
                            binding.ivNoimage.visibility = View.INVISIBLE
                            binding!!.ivShopimage.setImageBitmap(bitmap)

                            isSelectImage = true


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
    
}