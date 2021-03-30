package com.hkshopu.hk.ui.main.activity


import android.annotation.SuppressLint
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.application.App
import com.hkshopu.hk.component.EventShopCatSelected
import com.hkshopu.hk.component.EventShopDesUpdated
import com.hkshopu.hk.component.EventShopNameUpdated
import com.hkshopu.hk.databinding.ActivityAddshopBinding
import com.hkshopu.hk.ui.main.fragment.ShopInfoFragment
import com.hkshopu.hk.ui.user.activity.EmailVerifyActivity
import com.hkshopu.hk.ui.user.activity.LoginActivity
import com.hkshopu.hk.ui.user.activity.UserIofoActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable


class AddShopActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityAddshopBinding

    private val VM = ShopVModel()
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
        VM.shopnamecheck(this,shopName)



    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.shopnameLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data!!.equals("商店名稱未重複!")) {
                        binding.ivStep2.setImageResource(R.mipmap.ic_step2_check)
                        binding.ivStep2Check.visibility = View.VISIBLE
                    }else{
                        val text1: String = it.data.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1)
                        KeyboardUtil.showKeyboard(binding.etShopname)
                    }

                    finish()
                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })

        VM.addnewshopLiveData.observe(this, {
            when (it?.status) {
                Status.Success -> {
                    if (it.data!!.equals("商店新增成功!")) {

                    }else{
                        val text1: String = it.data.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1)
                    }

                    finish()
                }

            }
            })

    }

    private fun initView() {
        binding.ivAddnewshop.isClickable = false
        initEvent()
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(App.instance, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventShopCatSelected -> {
                        binding.ivStep3.setImageResource(R.mipmap.ic_step3_on)
                        binding.ivStep3Check.visibility = View.VISIBLE
                        if(binding.ivStep3Check.visibility == View.VISIBLE && binding.ivStep2Check.visibility == View.VISIBLE && binding.ivStep1Check.visibility == View.VISIBLE){
                            binding.ivAddnewshop.setImageResource(R.mipmap.btn_addnewshop_en)
                            binding.ivAddnewshop.isClickable = true
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
        binding.ivAddnewshop.setOnClickListener {
            binding.ivStep2.setImageResource(R.mipmap.ic_step2_check)
            VM.adddnewshop(this,shopName)
        }
        binding.tvMoreStoresort.setOnClickListener {
            val intent = Intent(this, ShopCategoryActivity::class.java)
            startActivity(intent)
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