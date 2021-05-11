package com.hkshopu.hk.ui.main.store.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.component.EventProductSearch
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ProductInfoBean
import com.hkshopu.hk.data.bean.ResourceMerchant
import com.hkshopu.hk.databinding.ActivityMymechantsBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Type

//import kotlinx.android.synthetic.main.activity_main.*


var mutableList_pics = mutableListOf<ItemPics>()

class MyMerchantsActivity : BaseActivity() {
    private lateinit var binding: ActivityMymechantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMymechantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initFragment()
        initVM()
        initClick()

    }

    fun initView() {

        initEditView()

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
    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvAddproduct.setOnClickListener {

//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
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