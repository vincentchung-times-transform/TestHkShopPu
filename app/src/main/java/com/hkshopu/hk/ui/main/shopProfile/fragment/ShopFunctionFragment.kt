package com.HKSHOPU.hk.ui.main.shopProfile.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopBankAccountBean
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.databinding.FragmentShopfunctionBinding

import com.HKSHOPU.hk.ui.main.shopProfile.activity.*
import com.HKSHOPU.hk.utils.rxjava.RxBus

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
    private var bankaccountlist = ArrayList<ShopBankAccountBean>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShopfunctionBinding.bind(view)
        fragmentShopfunctionBinding = binding



        initClick()
        initEvent()

    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentManager!!.beginTransaction().remove((this as Fragment?)!!)
            .commitAllowingStateLoss()

    }

    private fun initClick() {
        binding!!.layoutList.setOnClickListener {
            val intent = Intent(activity, MySalesActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.layoutDelivery.setOnClickListener {

            val intent = Intent(activity, LogisticListActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.layoutBankaccount.setOnClickListener {

            val intent = Intent(activity, BankListActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.layoutStoresort.setOnClickListener {

            var bundle = Bundle()
            bundle.putBoolean("toShopFunction",true)

            val intent = Intent(activity, ShopCategoryForEditShopActivity::class.java)
            intent.putExtra("bundle",bundle)
            activity!!.startActivity(intent)

        }
        binding!!.layoutAd.setOnClickListener {
            val intent = Intent(activity, AdvertisementActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.layoutAcntset.setOnClickListener {

            val intent = Intent(activity, AccountSetupActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.layoutShoppreview.setOnClickListener {
            val intent = Intent(activity, ShopPreviewActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.layoutHelp.setOnClickListener {
            val intent = Intent(activity, HelpCenterActivity::class.java)
            activity!!.startActivity(intent)
        }
    }


    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(activity!!, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventGetShopCatSuccess -> {
                        var shop_category_id_list = it.list

                        if (shop_category_id_list.size == 1) {
                            val itr = CommonVariable.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0).toString()
                                if (key.equals(shop_category_id1)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        CommonVariable.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort1.text = shopCategoryBean.c_shop_category
                                    binding!!.tvStoresort1.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort1.visibility = View.VISIBLE
                                    binding!!.cardViewStoreSort01.visibility = View.VISIBLE
                                }
                            }
                        }
                        if (shop_category_id_list.size == 2) {
                            val itr = CommonVariable.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0).toString()
                                var shop_category_id2: String = shop_category_id_list.get(1).toString()
                                if (key.equals(shop_category_id1)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        CommonVariable.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort1.text = shopCategoryBean.c_shop_category
                                    binding!!.tvStoresort1.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort1.visibility = View.VISIBLE
                                    binding!!.cardViewStoreSort01.visibility = View.VISIBLE
                                } else if (key.equals(shop_category_id2)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        CommonVariable.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort2.text = shopCategoryBean.c_shop_category

                                    binding!!.tvStoresort2.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort2.visibility = View.VISIBLE
                                    binding!!.cardViewStoreSort02.visibility = View.VISIBLE
                                }
                            }
                        }
                        if (shop_category_id_list.size == 3) {
                            val itr = CommonVariable.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0).toString()
                                var shop_category_id2: String = shop_category_id_list.get(1).toString()
                                var shop_category_id3: String = shop_category_id_list.get(2).toString()
                                if (key.equals(shop_category_id1)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        CommonVariable.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort1.text = shopCategoryBean.c_shop_category
                                    binding!!.tvStoresort1.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort1.visibility = View.VISIBLE
                                    binding!!.cardViewStoreSort01.visibility = View.VISIBLE
                                } else if (key.equals(shop_category_id2)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        CommonVariable.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort2.text = shopCategoryBean.c_shop_category

                                    binding!!.tvStoresort2.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort2.visibility = View.VISIBLE
                                    binding!!.cardViewStoreSort02.visibility = View.VISIBLE
                                } else if (key.equals(shop_category_id3)) {
                                    val shopCategoryBean: ShopCategoryBean =
                                        CommonVariable.ShopCategory[key]!!
                                    var storesort1_color =
                                        "#" + shopCategoryBean.shop_category_background_color
                                    binding!!.tvStoresort3.text = shopCategoryBean.c_shop_category

                                    binding!!.tvStoresort3.setBackgroundColor(
                                        Color.parseColor(
                                            storesort1_color
                                        )
                                    )
                                    binding!!.tvStoresort3.visibility = View.VISIBLE
                                    binding!!.cardViewStoreSort03.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                    is EventChangeShopCategory -> {

                        var category_list: ArrayList<ShopCategoryBean> = arrayListOf()
                        var category_id_list: ArrayList<String> = arrayListOf()

                        category_list = it.list

                        var shop_category_id1: String = ""
                        var shop_category_id2: String = ""
                        var shop_category_id3: String = ""
                        if (category_list.size == 1) {
                            shop_category_id1 = category_list[0].id.toString()
                            var storesort1 = category_list[0].c_shop_category
                            var storesort1_color = "#" + category_list[0].shop_category_background_color
                            binding!!.tvStoresort1.text = storesort1
                            binding!!.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )

                            binding!!.tvStoresort1.visibility = View.VISIBLE
                            binding!!.cardViewStoreSort01.visibility = View.VISIBLE
                            binding!!.tvStoresort2.visibility = View.INVISIBLE
                            binding!!.cardViewStoreSort02.visibility = View.INVISIBLE
                            binding!!.tvStoresort3.visibility = View.INVISIBLE
                            binding!!.cardViewStoreSort03.visibility = View.INVISIBLE
                            category_id_list.add(shop_category_id1.toString())
                        } else if (category_list.size == 2) {
                            shop_category_id1 = category_list[0].id
                            shop_category_id2 = category_list[1].id
                            var storesort1 = category_list[0].c_shop_category
                            var storesort2 = category_list[1].c_shop_category
                            var storesort1_color = "#" + category_list[0].shop_category_background_color
                            var storesort2_color = "#" + category_list[1].shop_category_background_color
                            binding!!.tvStoresort1.text = storesort1
                            binding!!.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding!!.tvStoresort1.visibility = View.VISIBLE
                            binding!!.cardViewStoreSort01.visibility = View.VISIBLE
                            binding!!.tvStoresort2.text = storesort2
                            binding!!.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding!!.tvStoresort2.visibility = View.VISIBLE
                            binding!!.cardViewStoreSort02.visibility = View.VISIBLE
                            category_id_list.add(shop_category_id1.toString())
                            category_id_list.add(shop_category_id2.toString())
                            binding!!.tvStoresort3.visibility = View.INVISIBLE
                            binding!!.cardViewStoreSort03.visibility = View.INVISIBLE
                        } else {
                            shop_category_id1 = category_list[0].id
                            shop_category_id2 = category_list[1].id
                            shop_category_id3 = category_list[2].id
                            var storesort1 = category_list[0].c_shop_category
                            var storesort2 = category_list[1].c_shop_category
                            var storesort3 = category_list[2].c_shop_category
                            var storesort1_color = "#" + category_list[0].shop_category_background_color
                            var storesort2_color = "#" + category_list[1].shop_category_background_color
                            var storesort3_color = "#" + category_list[2].shop_category_background_color
                            binding!!.tvStoresort1.text = storesort1
                            binding!!.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding!!.tvStoresort1.visibility = View.VISIBLE
                            binding!!.cardViewStoreSort01.visibility = View.VISIBLE

                            binding!!.tvStoresort2.text = storesort2
                            binding!!.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding!!.tvStoresort2.visibility = View.VISIBLE
                            binding!!.cardViewStoreSort02.visibility = View.VISIBLE
                            binding!!.tvStoresort3.text = storesort3
                            binding!!.tvStoresort3.setBackgroundColor(
                                Color.parseColor(
                                    storesort3_color
                                )
                            )
                            binding!!.tvStoresort3.visibility = View.VISIBLE
                            binding!!.cardViewStoreSort03.visibility = View.VISIBLE

                            category_id_list.add(shop_category_id1.toString())
                            category_id_list.add(shop_category_id2.toString())
                            category_id_list.add(shop_category_id3.toString())
                        }


                    }

                    is EventGetBankAccountSuccess -> {

                        bankaccountlist = it.list

                    }

                    is EventLogout ->{
                        this.onDestroy()
                    }

                }
            }, {
                it.printStackTrace()
            })
    }


}