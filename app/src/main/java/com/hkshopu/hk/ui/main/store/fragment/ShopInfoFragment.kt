package com.hkshopu.hk.ui.main.store.fragment


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.hkshopu.hk.R
import com.hkshopu.hk.component.CommonVariable
import com.hkshopu.hk.component.EventGetShopCatSuccess
import com.hkshopu.hk.data.bean.ShopAddressBean
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.databinding.FragmentShopinfoBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.activity.AddNewProductActivity
import com.hkshopu.hk.ui.main.store.activity.*
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopInfoFragment : Fragment(R.layout.fragment_shopinfo) {

    companion object {
        fun newInstance(): ShopInfoFragment {
            val args = Bundle()
            val fragment = ShopInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var binding: FragmentShopinfoBinding? = null
    private var fragmentShopInfoBinding: FragmentShopinfoBinding? = null
    private val pickCoverImage = 100
    private val pickImage = 200
    private var imageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shopId = arguments!!.getInt("shop_id", 0)
        MMKV.mmkvWithID("http").putInt(
            "ShopId",
            shopId
        )


        var url = ApiConstants.API_HOST + "/shop/" + shopId + "/show/"
        binding = FragmentShopinfoBinding.bind(view)
        fragmentShopInfoBinding = binding
        initView()
        getShopInfo(url)
        getView()!!.isFocusableInTouchMode = true
        getView()!!.requestFocus()
        getView()!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //go to previous fragemnt
                    //perform your fragment transaction here
                    //pass data as arguments
                    getActivity()!!.supportFragmentManager.beginTransaction().remove(this@ShopInfoFragment).commit()
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
                return ResourceStore.pagerFragments[position]
            }

            override fun getItemCount(): Int {
                return ResourceStore.tabList.size
            }

        }

        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceStore.tabList[position])
        }.attach()
        binding!!.mviewPager.setUserInputEnabled(false);

//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }


    fun initVM() {

    }

    @SuppressLint("CheckResult")
    fun initEvent() {

    }

    fun initClick() {

        binding!!.ivBack.setOnClickListener {
            getActivity()!!.supportFragmentManager.beginTransaction().remove(this@ShopInfoFragment).commit()
        }

        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(activity, ShopNotifyActivity::class.java)
            startActivity(intent)
        }

        binding!!.ivShopImg.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


        binding!!.layoutShoptitle.setOnClickListener {
            val intent = Intent(activity, ShopInfoModifyActivity::class.java)
            startActivity(intent)
        }

        binding!!.layoutShopRate.setOnClickListener {
            val intent = Intent(activity, ShopEvaluationActivity::class.java)
            startActivity(intent)
        }

        binding!!.layoutMerchants.setOnClickListener {
            val intent = Intent(activity, MyMerchantsActivity::class.java)
            startActivity(intent)
        }
        binding!!.layoutLikes.setOnClickListener {
            val intent = Intent(activity, ShopAttentionActivity::class.java)
            startActivity(intent)
        }
        binding!!.layoutIncome.setOnClickListener {
            val intent = Intent(activity, ShopIncomeActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentShopInfoBinding = null
        super.onDestroyView()
    }

    private fun getShopInfo(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopInfoBean>()
                list.clear()
                val shop_category_id_list = ArrayList<String>()
                shop_category_id_list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopInfoFragment", "返回資料 resStr：" + resStr)
                    Log.d("ShopInfoFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已找到商店資料!")) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("ShopInfoFragment", "返回資料 Object：" + jsonObject.toString())
                        val shopInfoBean: ShopInfoBean =
                            Gson().fromJson(jsonObject.toString(), ShopInfoBean::class.java)
                        list.add(shopInfoBean)
//                        val bank_account: JSONArray = jsonObject.getJSONArray("shop_bank_account")
//                        for (i in 0 until bank_account.length()) {
//                            val account = bank_account.get(i)
//                            val shopBankAccountBean: ShopBankAccountBean =
//                                Gson().fromJson(account.toString(), ShopBankAccountBean::class.java)
//                            CommonVariable.bankaccountlist.add(shopBankAccountBean)
//                        }
                        val shopaddress: JSONArray = jsonObject.getJSONArray("shop_address")
                        if (shopaddress.length() > 0) {
                            for (i in 0 until shopaddress.length()) {
                                val address = shopaddress.get(i)
                                val shopAddressBean: ShopAddressBean =
                                    Gson().fromJson(address.toString(), ShopAddressBean::class.java)
                                CommonVariable.addresslist.add(shopAddressBean)

                            }
                        }
                        val translations: JSONArray = jsonObject.getJSONArray("shop_category_id")

                        for (i in 0 until translations.length()) {
                            val shop_category_id = translations.get(i)
                            if (!shop_category_id.toString().equals("0")) {
                                shop_category_id_list.add(shop_category_id.toString())
                                Log.d(
                                    "ShopInfoFragment",
                                    "返回資料 shop_category_id：" + shop_category_id.toString()
                                )
                            }
                        }


                        RxBus.getInstance().post(EventGetShopCatSuccess(shop_category_id_list))
                        activity!!.runOnUiThread {
                            binding!!.tvShoptitle.text = list[0].shop_title
                            binding!!.myProduct.text = list[0].product_count.toString()
                            binding!!.tvRating.text = list[0].rating.toString()
                            binding!!.myLikes.text = list[0].follower.toString()
                            binding!!.myIncome.text = list[0].income.toString()
                            binding!!.ivShopImg.loadNovelCover(list[0].shop_icon)
                            MMKV.mmkvWithID("http").putString("shoptitle", list[0].shop_title)
                                .putString("description",list[0].long_description)
                            list[0].email_on ?. let {
                                if (list[0].email_on.equals("Y")) {
                                    MMKV.mmkvWithID("http").putString("email_on", list[0].email_on)
                                        .putString("shop_email", list[0].shop_email)
                                }
                                null // finally returns null
                            } ?: let {

                            }
                        }


                    }
//                        initRecyclerView()

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickCoverImage) {
            imageUri = data?.data

        } else if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding!!.ivShopImg.setImageURI(imageUri)
        }
    }

}