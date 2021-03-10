package com.hkshopu.hk.ui.main.activity


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.ActivityAddshopBinding
import com.hkshopu.hk.ui.main.fragment.ShopInfoFragment
import com.hkshopu.hk.ui.user.vm.AuthVModel



class AddShopActivity : BaseActivity(), TextWatcher, ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityAddshopBinding
    lateinit var manager: FragmentManager
    var showTab : Int = 1

    private val VM = AuthVModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {

    }

    private fun initView() {
        binding.titleBackAddshop.setOnClickListener {

            finish()
        }
        initFragment()
        initEditText()
        binding.mviewPager.currentItem = showTab
    }
    private val fragments = mutableListOf<Fragment>()
    private fun initFragment() {
        manager = supportFragmentManager
        if (fragments.isNotEmpty())return
        val ShopInfoFragment = ShopInfoFragment.newInstance()

        fragments.add(ShopInfoFragment)
        binding.mviewPager.adapter = object : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
            override fun getItem(position: Int) =  fragments[position]
            override fun getCount() = fragments.size
        }
        binding.mviewPager.setPagingEnabled(false)
        binding.mviewPager.addOnPageChangeListener(this)
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }

    private fun initEditText() {
//        editEmail.addTextChangedListener(this)
//        password1.addTextChangedListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

}