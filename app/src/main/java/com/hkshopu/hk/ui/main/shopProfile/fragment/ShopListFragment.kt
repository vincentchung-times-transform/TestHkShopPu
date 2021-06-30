package com.HKSHOPU.hk.ui.main.shopProfile.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.data.bean.ShopListBean
import com.HKSHOPU.hk.databinding.FragmentShoplistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.activity.AddShopActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ShopInfoAdapter
import com.HKSHOPU.hk.ui.user.activity.LoginActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList


class ShopListFragment : Fragment(R.layout.fragment_shoplist) {

    companion object {
        fun newInstance(): ShopListFragment {
            val args = Bundle()
            val fragment = ShopListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var binding: FragmentShoplistBinding? = null
    private var fragmentShopListBinding: FragmentShoplistBinding? = null
    private val adapter = ShopInfoAdapter()
    val userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    private var url = ApiConstants.API_HOST + "/user/" + userId + "/shop/"
    var url_forShopCategory = ApiConstants.API_HOST + "/shop_category/index/"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentShoplistBinding.bind(view)
        fragmentShopListBinding = binding

        binding!!.containerLogin.visibility = View.GONE
        binding!!.containerAddNewShop.visibility = View.GONE

        RxBus.getInstance().post(EventFinishLoadingShopmenu())


        initView()
        initVM()
        initEvent()
        initClick()


    }
    override fun onDestroy() {
        super.onDestroy()
        fragmentManager!!.beginTransaction().remove((this as Fragment?)!!)
            .commitAllowingStateLoss()

    }

    fun initView() {
        Log.d("ShopListFragment", "UserId：" + userId)
        if (userId.isNullOrEmpty()) {
            binding!!.containerLogin.visibility = View.VISIBLE
            binding!!.layoutShopTitle.visibility = View.INVISIBLE
        } else {

            RxBus.getInstance().post(EventStartLoadingShopmenu())

            getShopList(url)
        }
    }

    private fun initRecyclerView() {

        val layoutManager = LinearLayoutManager(activity!!)
        binding!!.recyclerview.layoutManager = layoutManager
        binding!!.recyclerview.adapter = adapter
        adapter.itemClick = {
            val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
            val newFragment: ShopInfoFragment = ShopInfoFragment.newInstance()
            val args = Bundle()
            args.putString("shop_id", it)
            newFragment.arguments = args
            ft.replace(R.id.layout_shopInfo, newFragment, "ShopInfoFragment")
            ft.commit()
        }
        adapter.deleteClick = {
            StoreDeleteApplyDialogFragment(it).show(
                fragmentManager!!,
                "MyCustomFragment"
            )
        }
    }

    fun initVM() {

    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(activity!!, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventAddShopSuccess -> {
                        getShopList(url)
                    }

                    is EventRefreshShopList -> {
                        getShopList(url)
                    }
                    is EventLogout -> {
                        onDestroy()
                    }

                }
            }, {
                it.printStackTrace()
            })
    }

    fun initClick() {

        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(activity, ShopNotifyActivity::class.java)
            activity!!.startActivity(intent)
        }

        binding!!.tvAddonlineshop.setOnClickListener {
            getShopCategory(url_forShopCategory)
            val intent = Intent(activity, AddShopActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvAddonlineshopForShopList.setOnClickListener {

            getShopCategory(url_forShopCategory)
            val intent = Intent(activity, AddShopActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvAddonlineshopLogin.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            activity!!.startActivity(intent)
            activity!!.finish()
        }
        var cancel = false
        binding!!.layoutShopdelete.setOnClickListener {

            if(cancel){
                binding!!.ivShopdelete.setImageResource(R.mipmap.ic_shopdelete)
                cancel = false
            }else{
                binding!!.ivShopdelete.setImageResource(R.mipmap.ic_trash_can_colorful)
                cancel = true
            }

            adapter.updateData(cancel)
        }

    }

    private fun getShopList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopListBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopListFragment", "返回資料 resStr：" + resStr)
                    Log.d("ShopListFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        val translations: JSONArray = json.getJSONArray("data")

                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            Log.d("ShopListFragment", "返回資料 Object：" + jsonObject.toString())
                            val shopListBean: ShopListBean =
                                Gson().fromJson(jsonObject.toString(), ShopListBean::class.java)
                            list.add(shopListBean)
                        }
                        adapter.setData(list)

                        activity!!.runOnUiThread {
                            binding!!.containerAddNewShop.visibility = View.GONE
                            initRecyclerView()
                            binding!!.containerShopList.visibility = View.VISIBLE
                            binding!!.layoutShopTitle.visibility = View.VISIBLE
                            binding!!.tvAddonlineshopForShopList.visibility = View.VISIBLE
                        }


                    } else {

                        activity!!.runOnUiThread {
                            binding!!.containerAddNewShop.visibility = View.VISIBLE
                            binding!!.layoutShopTitle.visibility = View.GONE
                            binding!!.tvAddonlineshopForShopList.visibility = View.GONE
                            binding!!.containerShopList.visibility = View.GONE
                        }

                    }
                    activity!!.runOnUiThread {
                        RxBus.getInstance().post(EventFinishLoadingShopmenu())
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

    private fun Do_ShopDelete(shop_id: String) {
        Log.d("ShopListFragment", "送資料 shop_id：" + shop_id)
        var url = ApiConstants.API_HOST + "/shop/" + shop_id + "/delete/"
        Log.d("ShopListFragment", "送資料URL URL：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopListBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopListFragment", "返回資料 resStr：" + resStr)
                    Log.d("ShopListFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {


                        activity!!.runOnUiThread {

                            StoreDeleteDialogFragment().show(
                                fragmentManager!!,
                                "MyCustomFragment"
                            )
                        }

                    } else {
                        val data = json.getJSONObject("data")
                        val order_count = data.getInt("order_count")
                        activity!!.runOnUiThread {
                            StoreDeleteDenyDialogFragment(order_count).show(
                                fragmentManager!!,
                                "MyCustomFragment"
                            )
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
        web.Delete_Data(url)
    }


    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentShopListBinding = null
        super.onDestroyView()
    }

    private fun getShopCategory(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getShopCategory", "返回資料 resStr：" + resStr)
                    Log.d("getShopCategory", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商店清單!")) {

                        CommonVariable.shopCategoryListForAdd.clear()
                        CommonVariable.ShopCategory.clear()

                        val translations: JSONArray = json.getJSONArray("shop_category_list")
                        Log.d("getShopCategory", "返回資料 List：" + translations.toString())
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopCategoryBean: ShopCategoryBean =
                                Gson().fromJson(jsonObject.toString(), ShopCategoryBean::class.java)


                            CommonVariable.shopCategoryListForAdd.add(shopCategoryBean)
                            CommonVariable.ShopCategory.put(shopCategoryBean.id.toString(), shopCategoryBean)
                        }

                    }


//                    Log.d("RechargeActivity", "返回值：" + rtnCode)

//                    Log.d("ComicReadActivity", "返回值：" + imgUrl)
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



}