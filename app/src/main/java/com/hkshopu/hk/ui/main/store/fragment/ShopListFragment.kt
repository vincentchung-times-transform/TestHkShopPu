package com.hkshopu.hk.ui.main.store.fragment


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
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventAddShopSuccess
import com.hkshopu.hk.data.bean.ShopListBean
import com.hkshopu.hk.databinding.FragmentShoplistBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.activity.AddShopActivity
import com.hkshopu.hk.ui.main.store.adapter.ShopInfoAdapter
import com.hkshopu.hk.ui.user.activity.LoginActivity
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList


class ShopListFragment : Fragment(R.layout.fragment_shoplist){

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
    val userId = MMKV.mmkvWithID("http").getInt("UserId", 0);
    private var url = ApiConstants.API_HOST+"/user/"+userId+"/shop/"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShoplistBinding.bind(view)
        fragmentShopListBinding = binding
        initView()
        initVM()
        initEvent()
        initClick()

    }

    fun initView() {
        Log.d("ShopListFragment", "UserId：" + userId)
        if(userId == 0){
            binding!!.container0.visibility = View.VISIBLE
        } else {
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
            args.putInt("shop_id", it)
            newFragment.arguments = args
            ft.replace(R.id.layout_shopInfo, newFragment, "ShopInfoFragment")
            ft.commit()
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

                }
            }, {
                it.printStackTrace()
            })
    }

    fun initClick() {

        binding!!.tvAddonlineshop.setOnClickListener {
            val intent = Intent(activity, AddShopActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvAddonlineshop2.setOnClickListener {
            val intent = Intent(activity, AddShopActivity::class.java)
            activity!!.startActivity(intent)

        }

        binding!!.tvAddonlineshopLogin.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            activity!!.startActivity(intent)
            activity!!.finish()
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
                    if (ret_val.equals("已取得您的商店清單!")) {

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
                            initRecyclerView()
                            binding!!.container2.visibility = View.VISIBLE
                            binding!!.tvAddonlineshop2.visibility = View.VISIBLE

                        }


                    } else {
                        activity!!.runOnUiThread {
                            binding!!.container1.visibility = View.VISIBLE
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


    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentShopListBinding = null
        super.onDestroyView()
    }


}