package com.HKSHOPU.hk.ui.main.shopProfile.fragment


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopAddressBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.databinding.FragmentShopinfoBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.activity.*
import com.HKSHOPU.hk.ui.main.productSeller.activity.AddNewProductActivity
import com.HKSHOPU.hk.ui.main.productSeller.activity.MyProductManagmentActivity
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
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
        binding = FragmentShopinfoBinding.bind(view)
        fragmentShopInfoBinding = binding

        binding!!.progressBarShopInfo.visibility = View.VISIBLE
        binding!!.ivLoadingBackgroundShopInfo.visibility = View.VISIBLE

        val shopId = arguments!!.getString("shop_id", "").toString()
        MMKV.mmkvWithID("http").putString(
            "ShopId",
            shopId
        )

        var url = ApiConstants.API_HOST + "shop/" + shopId + "/show/"

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
                    onDestroy()

                    return@OnKeyListener true
                }
            }
            false
        })

        initEvent()


    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentManager!!.beginTransaction().remove((this as Fragment?)!!)
            .commitAllowingStateLoss()

    }

    fun initView() {

        binding!!.ivAddmerchant.visibility = View.GONE

        initClick()
        initVM()
        initEvent()
        initFragment()

    }
    class MyFragmentAdapter(
        items: MutableList<Fragment>,
        context: Fragment
    ) : FragmentStateAdapter(context) {

        private val items: MutableList<Fragment> = items

        private val pageIds= items.map { it.hashCode().toLong() }

        override fun getItemCount(): Int = items.size

        override fun createFragment(position: Int): Fragment {

            return items[position]
        }


        override fun getItemId(position: Int): Long {
            return items[position].hashCode().toLong() // make sure notifyDataSetChanged() works
        }

        override fun containsItem(itemId: Long): Boolean {
            return pageIds.contains(itemId)
        }
    }

    private fun initFragment() {

        binding!!.mviewPager.adapter = MyFragmentAdapter(ResourceStore.pagerFragments, this)
        binding!!.mviewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (childFragmentManager.fragments.size > position) {
                    val fragment = childFragmentManager.fragments.get(position)
                    fragment.view?.let {
                        updatePagerHeightForChild(it,  binding!!.mviewPager)
                    }
                }
            }

            fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
                view.post {
                    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        view.width, View.MeasureSpec.EXACTLY
                    )
                    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        0, View.MeasureSpec.UNSPECIFIED
                    )
                    view.measure(widthMeasureSpec, heightMeasureSpec)
                    if (pager.layoutParams.height != view.measuredHeight) {
                        pager.layoutParams = (pager.layoutParams).also {
                            it.height = view.measuredHeight
                        }
                    }
                }
            }
        })

        binding!!.mviewPager.setUserInputEnabled(false)

        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceStore.tabList[position])
        }.attach()

        binding!!.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0->{
                        binding!!.ivAddmerchant.visibility = View.GONE
                    }
                    1->{
//                        binding!!.ivAddmerchant.visibility = View.VISIBLE
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))



    }


    fun initVM() {

    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        var boolean = false

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshShopInfo -> {

                        Thread(Runnable {

                            val shopId = arguments!!.getString("shop_id", "").toString()
                            var url = ApiConstants.API_HOST + "/shop/" + shopId + "/show/"

                            getShopInfo(url)


                        }).start()

                    }

                    is EventAddProductButtonVisibility -> {
                        boolean = it.boolean

                        if(boolean){
                            binding!!.ivAddmerchant.visibility = View.VISIBLE

                        }else{
                            binding!!.ivAddmerchant.visibility = View.GONE
                        }


                    }

                    is EventLogout ->{

                        this.onDestroy()
                    }

                }
            }, {
                it.printStackTrace()
            })

    }

    fun initClick() {


        binding!!.ivAddmerchant.setOnClickListener {
            val intent = Intent(activity, AddNewProductActivity::class.java)
            activity!!.startActivity(intent)
        }

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
            val intent = Intent(activity, MyProductManagmentActivity::class.java)
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
                var list = ArrayList<ShopInfoBean>()
                list.clear()
                var shop_category_id_list = ArrayList<String>()

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
                            val shop_category_id = translations.get(i).toString()
                            if (!shop_category_id.equals(0)) {
                                shop_category_id_list.add(shop_category_id)

                            }
                        }
                        Log.d(
                            "shop_category_id_list",
                            "返回資料 shop_category_id_list：" + shop_category_id_list.toString()
                        )

                        CommonVariable.shopCategorySelectedListForEdit = list[0].shop_category_id
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


                            if(list[0].long_description == null){
                                RxBus.getInstance().post(EventAddShopBriefSuccess(""))
                            }else{
                                RxBus.getInstance().post(EventAddShopBriefSuccess(list[0].long_description))
                            }


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

                    activity!!.runOnUiThread {

                        binding!!.progressBarShopInfo.visibility = View.GONE
                        binding!!.ivLoadingBackgroundShopInfo.visibility = View.GONE

                    }


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