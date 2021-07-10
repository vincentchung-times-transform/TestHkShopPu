package com.HKSHOPU.hk.ui.main.buyer.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.FragmentBuyerprofileBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.activity.BuyerAccountSetupActivity
import com.HKSHOPU.hk.ui.main.buyer.activity.BuyerAddressListActivity
import com.HKSHOPU.hk.ui.main.buyer.activity.BuyerInfoModifyActivity
import com.HKSHOPU.hk.ui.main.shopProfile.activity.HelpCenterActivity
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class BuyerProfileFragment : Fragment((R.layout.fragment_buyerprofile)) {
    companion object {
        fun newInstance(): BuyerProfileFragment {
            val args = Bundle()
            val fragment = BuyerProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private var binding: FragmentBuyerprofileBinding? = null
    private var fragmentBuyerprofileBinding: FragmentBuyerprofileBinding? = null
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyerprofileBinding.bind(view)
        fragmentBuyerprofileBinding = binding

        binding!!.progressBar5.isVisible = true

        var url_homeAd = ApiConstants.API_HOST + "shop/advertisement/"
//        getHomeAd(url_homeAd)
        var url = ApiConstants.API_HOST + "shop_category/index/"
//        getShopCategory(url)

//        getRecommendedStores(userId.toString())

        if (userId!!.isEmpty()) {

        } else {
            var url_UserLikedCount = ApiConstants.API_HOST + "user_detail/"+userId+"/liked_count/"
            getUserLikedCount(url_UserLikedCount)
        }


//        initVM()
        initView()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //go to previous fragemnt
                    //perform your fragment transaction here
                    //pass data as arguments

                    return@OnKeyListener true
                }
            }
            false
        })

    }
    private fun initView(){
        binding!!.ivPencil.setOnClickListener {
            val intent = Intent(requireActivity(), BuyerInfoModifyActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding!!.layoutCollects.setOnClickListener {

        }
        binding!!.layoutFavorites.setOnClickListener {

        }
        binding!!.layoutPath.setOnClickListener {

        }
        binding!!.layoutMyaddress.setOnClickListener {
            val intent = Intent(requireActivity(), BuyerAddressListActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding!!.layoutMyaccount.setOnClickListener {
            val intent = Intent(requireActivity(), BuyerAccountSetupActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding!!.layoutHelpcenter.setOnClickListener {
            val intent = Intent(requireActivity(), HelpCenterActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

    private fun getUserLikedCount(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BuyerProfileFragment", "返回資料 resStr：" + resStr)
                    Log.d("BuyerProfileFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val likedCount = json.get("data")

                        requireActivity().runOnUiThread {
                            binding!!.myCollect.text = likedCount.toString()
                            var url_UserFollwedCount = ApiConstants.API_HOST + "user_detail/"+userId+"/followed_count/"
                            getUserFollwedCount(url_UserFollwedCount)
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
        web.Get_Data(url)
    }
    private fun getUserFollwedCount(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BuyerProfileFragment", "返回資料 resStr：" + resStr)
                    Log.d("BuyerProfileFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val followCount = json.get("data")

                        requireActivity().runOnUiThread {
                            binding!!.myFavorites.text = followCount.toString()
                            var url_UserBrowseCount = ApiConstants.API_HOST + "user_detail/"+userId+"/browsed_count/"
                            getUserBrowseCount(url_UserBrowseCount)
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
        web.Get_Data(url)
    }

    private fun getUserBrowseCount(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BuyerProfileFragment", "返回資料 resStr：" + resStr)
                    Log.d("BuyerProfileFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val browseCount = json.get("data")

                        requireActivity().runOnUiThread {
                            binding!!.myPath.text = browseCount.toString()
                           binding!!.progressBar5.isVisible = false
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
        web.Get_Data(url)
    }
}