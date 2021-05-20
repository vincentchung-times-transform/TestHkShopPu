package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.component.EventProductSearch
import com.hkshopu.hk.data.bean.ResourceMerchant
import com.hkshopu.hk.databinding.ActivityMymechantsBinding
import com.hkshopu.hk.ui.main.product.activity.AddNewProductActivity
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import org.jetbrains.anko.singleLine

//import kotlinx.android.synthetic.main.activity_main.*

class MyMerchantsActivity : BaseActivity() {
    private lateinit var binding: ActivityMymechantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMymechantsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initFragment()
        initClick()
        initEditView()
    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceMerchant.pagerFragments[position]
            }

            override fun getItemCount(): Int {
                return ResourceMerchant.tabList.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceMerchant.tabList[position])

        }.attach()
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }

    fun initEditView() {
        binding.etSearchKeyword.singleLine = true
        binding.etSearchKeyword.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    RxBus.getInstance().post(EventProductSearch( binding.etSearchKeyword.text.toString()))

                    binding.etSearchKeyword.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.etSearchKeyword)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editMoreTimeInput = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                RxBus.getInstance().post(EventProductSearch( binding.etSearchKeyword.text.toString()))
            }
        }
        binding.etSearchKeyword.addTextChangedListener(textWatcher_editMoreTimeInput)



    }
    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvAddproduct.setOnClickListener {

            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }


}