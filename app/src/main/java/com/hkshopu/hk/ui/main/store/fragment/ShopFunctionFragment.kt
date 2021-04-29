package com.hkshopu.hk.ui.main.store.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventGetShopCatSuccess
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.FragmentShopfunctionBinding

import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.utils.rxjava.RxBus

class ShopFunctionFragment : Fragment(R.layout.fragment_shopfunction) {

    companion object {
        fun newInstance(): ShopFunctionFragment {
            val args = Bundle()
            val fragment = ShopFunctionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var binding: FragmentShopfunctionBinding? = null
    private var fragmentShopfunctionBinding: FragmentShopfunctionBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShopfunctionBinding.bind(view)
        fragmentShopfunctionBinding = binding


        initEvent()

    }
    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(activity!!, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventGetShopCatSuccess -> {
                        var shop_category_id_list = it.list
                        if (shop_category_id_list.size == 1) {
                            val itr = ApiConstants.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0)
                                if (key.equals(shop_category_id1)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        ApiConstants.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort1.text = shopCategoryBean.c_shop_category
                                    binding!!.tvStoresort1.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort1.visibility = View.VISIBLE
                                }
                            }
                        }
                        if (shop_category_id_list.size == 2) {
                            val itr = ApiConstants.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0)
                                var shop_category_id2: String = shop_category_id_list.get(1)
                                if (key.equals(shop_category_id1)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        ApiConstants.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort1.text = shopCategoryBean.c_shop_category
                                    binding!!.tvStoresort1.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort1.visibility = View.VISIBLE
                                } else if (key.equals(shop_category_id2)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        ApiConstants.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort2.text = shopCategoryBean.c_shop_category

                                    binding!!.tvStoresort2.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort2.visibility = View.VISIBLE
                                }
                            }
                        }
                        if (shop_category_id_list.size == 3) {
                            val itr = ApiConstants.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0)
                                var shop_category_id2: String = shop_category_id_list.get(1)
                                var shop_category_id3: String = shop_category_id_list.get(2)
                                if (key.equals(shop_category_id1)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        ApiConstants.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort1.text = shopCategoryBean.c_shop_category
                                    binding!!.tvStoresort1.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort1.visibility = View.VISIBLE
                                } else if (key.equals(shop_category_id2)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        ApiConstants.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort2.text = shopCategoryBean.c_shop_category

                                    binding!!.tvStoresort2.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort2.visibility = View.VISIBLE
                                } else if (key.equals(shop_category_id3)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        ApiConstants.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort3.text = shopCategoryBean.c_shop_category

                                    binding!!.tvStoresort3.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort3.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                }
            }, {
                it.printStackTrace()
            })
    }

}