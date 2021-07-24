package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventPurchaseListToSpecificPage
import com.HKSHOPU.hk.component.EventRefreshShoppingCartItemCount
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.component.EventShopmenuToSpecificPage
import com.HKSHOPU.hk.data.bean.BuyerProfileBean
import com.HKSHOPU.hk.data.bean.ShoppingCartItemCountBean
import com.HKSHOPU.hk.databinding.FragmentBuyerprofileBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.*
import com.HKSHOPU.hk.ui.main.buyer.shoppingcart.activity.ShoppingCartEditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.HelpCenterActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopPreviewCommingSoonActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.HKSHOPU.hk.utils.extension.load
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
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
    var url_UserPeofile = ApiConstants.API_HOST + "user_detail/"+userId+"/profile/"
    var shoppingCartItemCount: ShoppingCartItemCountBean = ShoppingCartItemCountBean()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyerprofileBinding.bind(view)
        fragmentBuyerprofileBinding = binding

        if (userId!!.isEmpty()) {
            binding!!.tvProfiletitle.setText(R.string.guest)
            binding!!.progressBarBuyerProfile.visibility = View.GONE
            binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
        } else {
            getUserProfile(url_UserPeofile)
        }

//        initVM()
        initView()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
//        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
//            if (event.action == KeyEvent.ACTION_DOWN) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    //go to previous fragemnt
//                    //perform your fragment transaction here
//                    //pass data as arguments
//
//                    return@OnKeyListener true
//                }
//            }
//            false
//        })

        initEvent()
    }
    private fun initView(){
        binding!!.btnSetting.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerAccountSetupActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutIcCart.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(activity, ShoppingCartEditActivity::class.java)
                startActivity(intent)
            }
//            var bundle = Bundle()
//            bundle.putBoolean("toShopFunction", false)
//            val intent = Intent(requireActivity(), GoShopActivity::class.java)
//            requireActivity().startActivity(intent)
        }
        binding!!.ivPencil.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerInfoModifyActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutProfileRate.setOnClickListener{
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerEvaluationActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutPurchaselist.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                val newFragment: PurchaseListFragment = PurchaseListFragment.newInstance()
                val args = Bundle()
//            args.putString("shop_id", it)
                newFragment.arguments = args
                ft.replace(R.id.layout_buyerprofile, newFragment, "PurchaseListFragment")
                ft.commit()
            }
        }
        binding!!.layoutPendingPay.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                val newFragment: PurchaseListFragment = PurchaseListFragment.newInstance()
                val args = Bundle()
                args.putString("page_id", "0")
                newFragment.arguments = args
                ft.replace(R.id.layout_buyerprofile, newFragment, "PurchaseListFragment")
                ft.commit()
                RxBus.getInstance().post(EventPurchaseListToSpecificPage(0))
            }
        }

        binding!!.layoutPendingDelivery.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                val newFragment: PurchaseListFragment = PurchaseListFragment.newInstance()
                val args = Bundle()
                args.putString("page_id", "1")
                newFragment.arguments = args
                ft.replace(R.id.layout_buyerprofile, newFragment, "PurchaseListFragment")
                ft.commit()
                RxBus.getInstance().post(EventPurchaseListToSpecificPage(1))
            }
        }
        binding!!.layoutPendingReceive.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                val newFragment: PurchaseListFragment = PurchaseListFragment.newInstance()
                val args = Bundle()
                args.putString("page_id", "2")
                newFragment.arguments = args
                ft.replace(R.id.layout_buyerprofile, newFragment, "PurchaseListFragment")
                ft.commit()
                RxBus.getInstance().post(EventPurchaseListToSpecificPage(2))
            }
        }
        binding!!.layoutEvaluate.setOnClickListener {
            val intent = Intent(activity, ShopPreviewCommingSoonActivity::class.java)
            activity!!.startActivity(intent)
        }
        binding!!.layoutCollects.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerLikedListActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutFavorites.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerFollowListActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutPath.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerBrowsedListActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutMyaddress.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerAddressListActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutMyaccount.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), BuyerAccountSetupActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.layoutHelpcenter.setOnClickListener {
            if(userId.isNullOrEmpty()){
                Log.d("btnAddToShoppingCart", "UserID為空值")
                LoginFirstDialogFragment().show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                val intent = Intent(requireActivity(), HelpCenterActivity::class.java)
                requireActivity().startActivity(intent)
            }
        }
        binding!!.icNotification.setOnClickListener {
            val intent = Intent(requireActivity(), ShopNotifyActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

    private fun getUserProfile(url: String) {
        binding!!.progressBarBuyerProfile.visibility = View.VISIBLE
        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.VISIBLE

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerProfileBean>()
                list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getUserProfile", "返回資料 resStr：" + resStr)
                    Log.d("getUserProfile", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")

                        val buyerProfileBean: BuyerProfileBean =
                            Gson().fromJson(jsonObject.toString(), BuyerProfileBean::class.java)

                        list.add(buyerProfileBean)

                        requireActivity().runOnUiThread {
                            binding!!.ivShopImg.load(list[0].pic)
                            binding!!.tvProfiletitle.text = list[0].name
                            binding!!.ratingBar.setRating(list[0].user_rating.toFloat())
                            binding!!.tvRating.text = list[0].user_rating.toString()

                            GetShoppingCartItemCountForBuyerProfile(userId!!)
                        }

                    }else{
                        runOnUiThread {
                            binding!!.progressBarBuyerProfile.visibility = View.GONE
                            binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getUserProfile_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getUserProfile_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getUserProfile_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding!!.progressBarBuyerProfile.visibility = View.GONE
                    binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun getUserLikedCount(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getUserLikedCount", "返回資料 resStr：" + resStr)
                    Log.d("getUserLikedCount", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val likedCount = json.get("data")
                        requireActivity().runOnUiThread {
                            binding!!.myCollect.text = likedCount.toString()
                            var url_UserFollwedCount = ApiConstants.API_HOST + "user_detail/"+userId+"/followed_count/"
                            getUserFollwedCount(url_UserFollwedCount)
                        }
                    }else{
                        runOnUiThread {
                            binding!!.progressBarBuyerProfile.visibility = View.GONE
                            binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getUserLikedCount", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getUserLikedCount", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getUserLikedCount", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding!!.progressBarBuyerProfile.visibility = View.GONE
                    binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                }
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
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getUserFollwedCount", "返回資料 resStr：" + resStr)
                    Log.d("getUserFollwedCount", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val followCount = json.get("data")

                        requireActivity().runOnUiThread {
                            binding!!.myFavorites.text = followCount.toString()
                            var url_UserBrowseCount = ApiConstants.API_HOST + "user_detail/"+userId+"/browsed_count/"
                            getUserBrowseCount(url_UserBrowseCount)
                        }
                    }else{
                        runOnUiThread {
                            binding!!.progressBarBuyerProfile.visibility = View.GONE
                            binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("getUserFollwedCount_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getUserFollwedCount_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getUserFollwedCount_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding!!.progressBarBuyerProfile.visibility = View.GONE
                    binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                }
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
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getUserBrowseCount", "返回資料 resStr：" + resStr)
                    Log.d("getUserBrowseCount", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val browseCount = json.get("data")

                        requireActivity().runOnUiThread {
                            binding!!.myPath.text = browseCount.toString()
                            binding!!.progressBarBuyerProfile.visibility = View.GONE
                            binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                        }

                    }else{
                        runOnUiThread {
                            binding!!.progressBarBuyerProfile.visibility = View.GONE
                            binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("getUserBrowseCount_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getUserBrowseCount_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getUserBrowseCount_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding!!.progressBarBuyerProfile.visibility = View.GONE
                    binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun  GetShoppingCartItemCountForBuyerProfile (user_id: String) {
        Log.d("GetShoppingCartItemCountForBuyerProfile", "user_id: ${user_id.toString()}")
        val url = ApiConstants.API_HOST+"shopping_cart/${user_id}/count/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("GetShoppingCartItemCountForBuyerProfile", "返回資料 resStr：" + resStr)
                    Log.d("GetShoppingCartItemCountForBuyerProfile", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals( "已取得商品清單!")) {

                        val jsonObject: JSONObject = json.getJSONObject("data")

                        Log.d(
                            "GetShoppingCartItemCountForBuyerProfile",
                            "返回資料 jsonObject：" + jsonObject.toString()
                        )

                        shoppingCartItemCount = Gson().fromJson(
                            jsonObject.toString(),
                            ShoppingCartItemCountBean::class.java
                        )

                        runOnUiThread {
                            binding!!.tvCartItemCount.setText(shoppingCartItemCount.cartCount.toString())

                            if(shoppingCartItemCount.cartCount > 0){
                                binding!!.tvCartItemCount.visibility = View.VISIBLE
                            }else{
                                binding!!.tvCartItemCount.visibility = View.GONE
                            }
                        }

                        var url_UserLikedCount = ApiConstants.API_HOST + "user_detail/"+userId+"/liked_count/"
                        getUserLikedCount(url_UserLikedCount)
                    }

                } catch (e: JSONException) {

                    Log.d("GetShoppingCartItemCountForBuyerProfile_errorMessage", "GetShoppingCartItemCountForBuyer: JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()

                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("GetShoppingCartItemCountForBuyerProfile_errorMessage", "GetShoppingCartItemCountForBuyer: IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()

                        binding!!.progressBarBuyerProfile.visibility = View.GONE
                        binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("GetShoppingCartItemCountForBuyerProfile_errorMessage", "GetShoppingCartItemCountForBuyer: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()

                    binding!!.progressBarBuyerProfile.visibility = View.GONE
                    binding!!.imgViewLoadingBackgroundBuyerProfile.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }


    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshUserInfo -> {
                        getUserProfile(url_UserPeofile)

                        Thread(Runnable {

                            try{
                                Thread.sleep(1000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }


                            runOnUiThread {
                                var myOderList = MMKV.mmkvWithID("myOderList").getString("myOderList", "")
                                Log.d("PurchaseListFragment_myOderList", "myOderList: ${myOderList.toString()}")
                                if(myOderList.equals("PurchaseListFragment")){
                                    val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                                    val newFragment: PurchaseListFragment = PurchaseListFragment.newInstance()
                                    val args = Bundle()
                                    args.putString("page_id", "0")
                                    newFragment.arguments = args
                                    ft.replace(R.id.layout_buyerprofile, newFragment, "PurchaseListFragment")
                                    ft.commit()
                                }
                                RxBus.getInstance().post(EventPurchaseListToSpecificPage(0))
                                MMKV.mmkvWithID("myOderList").putString("myOderList", "")
                            }
                        }).start()

                    }
                }
            }, {
                it.printStackTrace()
            })
    }

}