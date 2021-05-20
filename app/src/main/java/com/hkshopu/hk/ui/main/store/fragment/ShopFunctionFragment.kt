package com.hkshopu.hk.ui.main.store.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hkshopu.hk.R
import com.hkshopu.hk.component.*
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.FragmentShopfunctionBinding

import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.activity.*
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

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

    private fun initClick() {
        binding!!.tvMoreList.setOnClickListener {
            val intent = Intent(activity, MyMerchantsActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvMoreDelivery.setOnClickListener {

            val intent = Intent(activity, LogisticListActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvMoreBankaccount.setOnClickListener {

            val intent = Intent(activity, BankListActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvMoreStoresort.setOnClickListener {

            var bundle = Bundle()
            bundle.putBoolean("toShopFunction",true)
            val intent = Intent(activity, ShopCategoryActivity::class.java)
            intent.putExtra("bundle",bundle)
            activity!!.startActivity(intent)

        }
        binding!!.tvMoreAd.setOnClickListener {
            val intent = Intent(activity, AdvertisementActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvMoreAcntset.setOnClickListener {
            val intent = Intent(activity, AccountSetupActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvMoreShoppreview.setOnClickListener {
            val intent = Intent(activity, ShopPreviewActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvMoreHelp.setOnClickListener {
            val intent = Intent(activity, HelpCenterActivity::class.java)
            activity!!.startActivity(intent)

        }
    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        var list: ArrayList<ShopCategoryBean> = arrayListOf()
        list.clear()
        var category_id_list: ArrayList<String> = arrayListOf()
        RxBus.getInstance().toMainThreadObservable(activity!!, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventGetShopCatSuccess -> {
                        var shop_category_id_list = it.list
                        if (shop_category_id_list.size == 1) {
                            val itr = CommonVariable.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0)
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
                                }
                            }
                        }
                        if (shop_category_id_list.size == 2) {
                            val itr = CommonVariable.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0)
                                var shop_category_id2: String = shop_category_id_list.get(1)
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
                                }
                            }
                        }
                        if (shop_category_id_list.size == 3) {
                            val itr = CommonVariable.ShopCategory.keys.iterator()
                            while (itr.hasNext()) {
                                val key = itr.next()
                                var shop_category_id1: String = shop_category_id_list.get(0)
                                var shop_category_id2: String = shop_category_id_list.get(1)
                                var shop_category_id3: String = shop_category_id_list.get(2)
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
                                }
                            }
                        }
                    }
                    is EventChangeShopCategory -> {
                        list = it.list
                        var shop_category_id1: Int = 0
                        var shop_category_id2: Int = 0
                        var shop_category_id3: Int = 0
                        if (list.size == 1) {
                            shop_category_id1 = list[0].id
                            var storesort1 = list[0].c_shop_category
                            var storesort1_color = "#" + list[0].shop_category_background_color
                            binding!!.tvStoresort1.text = storesort1
                            binding!!.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )

                            binding!!.tvStoresort1.visibility = View.VISIBLE
                            category_id_list.add(shop_category_id1.toString())
                        } else if (list.size == 2) {
                            shop_category_id1 = list[0].id
                            shop_category_id2 = list[1].id
                            var storesort1 = list[0].c_shop_category
                            var storesort2 = list[1].c_shop_category
                            var storesort1_color = "#" + list[0].shop_category_background_color
                            var storesort2_color = "#" + list[1].shop_category_background_color
                            binding!!.tvStoresort1.text = storesort1
                            binding!!.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding!!.tvStoresort1.visibility = View.VISIBLE
                            binding!!.tvStoresort2.text = storesort2
                            binding!!.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding!!.tvStoresort2.visibility = View.VISIBLE
                            category_id_list.add(shop_category_id1.toString())
                            category_id_list.add(shop_category_id2.toString())
                        } else {
                            shop_category_id1 = list[0].id
                            shop_category_id2 = list[1].id
                            shop_category_id3 = list[2].id
                            var storesort1 = list[0].c_shop_category
                            var storesort2 = list[1].c_shop_category
                            var storesort3 = list[2].c_shop_category
                            var storesort1_color = "#" + list[0].shop_category_background_color
                            var storesort2_color = "#" + list[1].shop_category_background_color
                            var storesort3_color = "#" + list[2].shop_category_background_color
                            binding!!.tvStoresort1.text = storesort1
                            binding!!.tvStoresort1.setBackgroundColor(
                                Color.parseColor(
                                    storesort1_color
                                )
                            )
                            binding!!.tvStoresort1.visibility = View.VISIBLE
                            binding!!.tvStoresort2.text = storesort2
                            binding!!.tvStoresort2.setBackgroundColor(
                                Color.parseColor(
                                    storesort2_color
                                )
                            )
                            binding!!.tvStoresort2.visibility = View.VISIBLE
                            binding!!.tvStoresort3.text = storesort3
                            binding!!.tvStoresort3.setBackgroundColor(
                                Color.parseColor(
                                    storesort3_color
                                )
                            )
                            binding!!.tvStoresort3.visibility = View.VISIBLE
                            category_id_list.add(shop_category_id1.toString())
                            category_id_list.add(shop_category_id2.toString())
                            category_id_list.add(shop_category_id3.toString())
                        }
                        doShopCategoryUpdate(category_id_list)
                    }

                    is EventGetBankAccountSuccess -> {

                        bankaccountlist = it.list

                    }

                }
            }, {
                it.printStackTrace()
            })
    }

    private fun doShopCategoryUpdate(list: ArrayList<String>) {
        val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        var url = ApiConstants.API_PATH +"shop/"+ shopId + "/updateSelectedShopCategory/"
        Log.d("ShopFunctionFragment", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopFunctionFragment", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    Log.d("ShopFunctionFragment", "返回資料 ret_val：" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {
                        activity!!.runOnUiThread {
                            Toast.makeText(activity!!, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        activity!!.runOnUiThread {

                            Toast.makeText(activity!!, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ShopCategoryUpdate(url, list)
    }
}