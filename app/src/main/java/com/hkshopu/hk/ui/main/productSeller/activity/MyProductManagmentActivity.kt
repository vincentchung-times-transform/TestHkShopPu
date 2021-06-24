package com.HKSHOPU.hk.ui.main.productSeller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ResourceMerchant
import com.HKSHOPU.hk.databinding.ActivityMymechantsBinding
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.*


//import kotlinx.android.synthetic.main.activity_main.*

class MyProductManagmentActivity : BaseActivity() {
    private lateinit var binding: ActivityMymechantsBinding
    var delete_mode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMymechantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarMyProducts.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundMyProducts.visibility = View.VISIBLE


        //清掉 MMKV.mmkvWithID("addPro").clear() MMKV.mmkvWithID("editPro").clear()
        MMKV.mmkvWithID("addPro").clear()
        MMKV.mmkvWithID("editPro").clear()


        initFragment()
        initClick()
        initEditView()
        initEvent()


        binding.progressBarMyProducts.visibility = View.GONE
        binding.imgViewLoadingBackgroundMyProducts.visibility = View.GONE
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

        binding!!.layoutProductDelete.setOnClickListener {


            if( delete_mode.equals(false)){
                delete_mode = true
                RxBus.getInstance().post(EventProductDelete( true))
                binding!!.ivProductDelete.setImageResource(R.mipmap.ic_trash_can_colorful)
            }else{
                delete_mode = false
                RxBus.getInstance().post(EventProductDelete( false))
                binding!!.ivProductDelete.setImageResource(R.mipmap.ic_shopdelete)
            }

//            adapter.updateData(cancel)
        }


        binding.ivBack.setOnClickListener {

//            RxBus.getInstance().post(EventRefreshShopInfo())

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

    override fun onBackPressed() {

//        RxBus.getInstance().post(EventRefreshShopInfo())

        finish()

    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        var index: Int

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventTransferToFragmentAfterUpdate -> {

                        index = it.index

                        binding!!.mviewPager.setCurrentItem(index, false)

                    }
                    is EventLoadingStatus-> {
                        var boolean = it.boolean

                        if(boolean){
                            binding.progressBarMyProducts.visibility = View.VISIBLE
                            binding.imgViewLoadingBackgroundMyProducts.visibility = View.VISIBLE
                        }else{
                            binding.progressBarMyProducts.visibility = View.GONE
                            binding.imgViewLoadingBackgroundMyProducts.visibility = View.GONE
                        }

                    }

                }
            }, {
                it.printStackTrace()
            })

    }


}