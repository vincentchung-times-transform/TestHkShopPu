package com.HKSHOPU.hk.ui.main.seller.order.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ResourceOrder
import com.HKSHOPU.hk.databinding.FragmentSaleslistBinding
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus


class SalesListFragment(manager: FragmentManager) : Fragment(R.layout.fragment_saleslist) {

    companion object {
        fun newInstance(manager: FragmentManager): SalesListFragment {
//            val args = Bundle()
            val fragment = SalesListFragment(manager)
//            fragment.arguments = args
            return fragment
        }
    }
    var manager = manager
    var shop_id=""
    private var binding: FragmentSaleslistBinding? = null
    private var fragmentSaleslistBinding: FragmentSaleslistBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shop_id = requireArguments().getString("shop_id", "")
        binding = FragmentSaleslistBinding.bind(view)
        fragmentSaleslistBinding = binding

        initView()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //go to previous fragemnt
                    //perform your fragment transaction here
                    //pass data as arguments
                    manager.beginTransaction().remove(this@SalesListFragment).commit()
                    return@OnKeyListener true
                }
            }
            false
        })
    }

    fun initView() {
        initClick()
        initVM()
        initEvent()
        initFragment()
    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return ResourceOrder.pagerFragments_saleslist[position]
            }
            override fun getItemCount(): Int {
                return ResourceOrder.tabList_saleslist.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceOrder.tabList_saleslist[position])
        }.attach()
        binding!!.mviewPager.setUserInputEnabled(false);
        binding!!.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        if(shop_id.isNotEmpty()) {
            requireActivity().runOnUiThread {
                binding!!.mviewPager.setCurrentItem(shop_id.toInt(), false)
            }
        }
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    fun initVM() {
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshShopInfo -> {
                    }
                    is EventSaleListToSpecificPage -> {
                        var index = it.index
                        Log.d("EventSaleListToSpecificPage_indexInspect", "index: ${index}")
                        binding!!.mviewPager.setCurrentItem(index, false)
                    }
                }
            }, {
                it.printStackTrace()
            })
    }
    fun initClick() {
        binding!!.ivBack.setOnClickListener {
            manager.beginTransaction().remove(this@SalesListFragment).commit()
        }
        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(activity, ShopNotifyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentSaleslistBinding = null
        super.onDestroyView()
    }

}