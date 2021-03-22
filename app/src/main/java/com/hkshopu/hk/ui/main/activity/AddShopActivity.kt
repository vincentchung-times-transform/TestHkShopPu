package com.hkshopu.hk.ui.main.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityAddshopBinding
import com.hkshopu.hk.ui.main.fragment.ShopInfoFragment
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable


class AddShopActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityAddshopBinding

    private val VM = AuthVModel()
    private val pickImage = 100
    private var imageUri: Uri? = null
    var shopName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initEditText()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {
        shopName = binding.etShopname.text.toString()

        binding.ivStep2.setImageResource(R.mipmap.ic_step2_check)
        binding.ivStep2Check.visibility = View.VISIBLE

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {

    }

    private fun initView() {

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
        binding.ivAddnewshop.setOnClickListener {
            binding.ivStep2.setImageResource(R.mipmap.ic_step2_check)
        }
        binding.tvStoresortMore.setOnClickListener {

        }

    }


    private fun initEditText() {
        binding.etShopname.addTextChangedListener(this)
//        password1.addTextChangedListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding!!.ivShopImg.setImageURI(imageUri)
            binding.ivStep1Check.visibility = View.VISIBLE
            binding.ivStep1.setImageResource(R.mipmap.ic_step1_check)
            binding.ivStep2.setImageResource(R.mipmap.ic_step2_on)
        }
    }
}