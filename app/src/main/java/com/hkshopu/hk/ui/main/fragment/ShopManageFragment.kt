package com.hkshopu.hk.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.hkshopu.hk.Base.BaseFragment
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.databinding.FragmentShopinfoBinding
import com.hkshopu.hk.databinding.FragmentShopmanageBinding
import com.hkshopu.hk.ui.main.activity.AddShopActivity
import com.hkshopu.hk.ui.main.adapter.ShopInfoAdapter
import com.hkshopu.hk.ui.user.activity.LoginActivity
import com.hkshopu.hk.utils.extension.SwipeToDeleteCallback


class ShopManageFragment : Fragment(R.layout.fragment_shopmanage) {
    private var binding: FragmentShopmanageBinding? = null

    private var fragmentShopmanageBinding: FragmentShopmanageBinding? = null

    private val adapter = ShopInfoAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShopmanageBinding.bind(view)
        fragmentShopmanageBinding = binding
        initView()
    }
    fun initView() {
        initVM()
        initClick()
        val layoutManager = LinearLayoutManager(context)

        binding!!.recyclerView.layoutManager = layoutManager
        binding!!.recyclerView.isNestedScrollingEnabled = false
        binding!!.recyclerView.itemAnimator = DefaultItemAnimator()
        (binding!!.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding!!.recyclerView.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding!!.recyclerView.adapter as ShopInfoAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding!!.recyclerView)
    }
    private fun initVM() {
        val list = ArrayList<ShopInfoBean>()
        val shopInfoBean = ShopInfoBean("1","","Test")
        list.add(shopInfoBean)
        adapter.setData(list)
    }

    fun initClick() {
        binding!!.layoutAddShop.setOnClickListener {

//            val intent = Intent(context, AddShopActivity::class.java)
//            context?.startActivity(intent)

        }
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentShopmanageBinding = null
        super.onDestroyView()
    }


}