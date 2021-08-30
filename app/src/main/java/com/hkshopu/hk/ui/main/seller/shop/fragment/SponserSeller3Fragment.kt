package com.HKSHOPU.hk.ui.main.seller.shop.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.StoreValueConfirmDialogFragment
import com.HKSHOPU.hk.ui.main.sponsor.ToSponserEditActivity
import com.HKSHOPU.hk.ui.main.wallet.activity.MyWalletActivity
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SponserSeller3Fragment : Fragment() {
    companion object {
        fun newInstance(): SponserSeller3Fragment {
            val args = Bundle()
            val fragment = SponserSeller3Fragment()
            fragment.arguments = args
            return fragment
        }
    }

    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
    var levelId = ""
    var sponserId = MMKV.mmkvWithID("http").getString("SponserId", "").toString()
    val walletId = MMKV.mmkvWithID("http").getString("walletId","").toString()

    lateinit var progressBar: ProgressBar
    lateinit var loadingBg: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_sponser_seller_glory, container, false)
        progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        loadingBg = v.findViewById<ImageView>(R.id.imgView_loading_background)

        levelId = CommonVariable.costlist.get(2).id.toString()
        val glory = v.findViewById<TextView>(R.id.tv_glory)
        glory.text = "HKD\$" + CommonVariable.costlist.get(2).price.toString() + "/月"
        var balance = MMKV.mmkvWithID("http").getInt("walletbalance", 0)
        val mywallet_detail = v.findViewById<TextView>(R.id.tv_mywallet_detail_number)
        mywallet_detail.text = "HK\$" + balance
        val walletbalance = v.findViewById<TextView>(R.id.tv_wallet_left)
        walletbalance.text = "\$"+balance.toString()
        val balance_reduce = balance - CommonVariable.costlist.get(2).price
        var walletleft = v.findViewById<TextView>(R.id.tv_mywallet_left_value)
        val donate_value = v.findViewById<TextView>(R.id.tv_donate_value)
        donate_value.text = "-HK\$" + CommonVariable.costlist.get(2).price.toString()
        val donate_value_bottom = v.findViewById<TextView>(R.id.tv_donatevalue_bottom)
        donate_value_bottom.text = "$" + CommonVariable.costlist.get(2).price.toString() + "/月"
        val donate = v.findViewById<TextView>(R.id.tv_donatebutton)
        val warning = v.findViewById<ImageView>(R.id.iv_warning)
        val payddetail = v.findViewById<RelativeLayout>(R.id.layout_paiddetail)

        if(sponserId.equals("N") && sponserId.isNotEmpty()){
            payddetail.visibility = View.VISIBLE
            donate.setBackgroundResource(R.drawable.customborder_onboard_turquise_8dp)

            if (balance_reduce > 0) {
                walletleft.text = "HK\$" + balance_reduce.toString()
            } else {
                walletleft.setText(R.string.Insufficientbalance)
                walletleft.setTextColor(resources.getColor(R.color.bright_red, null))
                warning.visibility = View.VISIBLE
                donate.setText(R.string.storevalue)
                donate.setBackgroundResource(R.drawable.customborder_8dp_fc4d3f)
            }

        }else{
            if (!sponserId.equals(CommonVariable.costlist.get(2).title)) {
                payddetail.visibility = View.GONE
                donate.setBackgroundResource(R.drawable.customborder_8dp_8e8e93)
            }else{
                payddetail.visibility = View.GONE
                donate.setBackgroundResource(R.drawable.customborder_8dp_8e8e93)
            }
        }

        donate.setOnClickListener {
            if(sponserId.equals("N") && sponserId.isNotEmpty()){
                if (donate.text.equals("我要贊助")) {

                    val url = ApiConstants.API_SWAGGER + "wallet/history/add/"
                    val action = "SR"
                    do_WalletReduce(
                        url,
                        walletId,
                        shopId,
                        CommonVariable.costlist.get(2).price,
                        action,
                        "榮耀贊助商"
                    )
                } else {
                    val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
                    val intent = Intent(activity, MyWalletActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("shopId", shopId)
                    intent.putExtra("bundle", bundle)
                    activity!!.startActivity(intent)
                }
            }else{
                if (!sponserId.equals(CommonVariable.costlist.get(2).title)) {
                    SponserAdjustmentDialogFragment().show(
                        requireActivity().getSupportFragmentManager(),
                        "MyCustomFragment"
                    )
                }else{
                    SponserAdjustmentDialogFragment().show(
                        requireActivity().getSupportFragmentManager(),
                        "MyCustomFragment"
                    )
                }
            }
        }
        return v
    }
    private fun do_WalletReduce(url: String, wallet_id: String, shop_id: String,change:Int,action: String, desc: String) {
        progressBar.visibility = View.VISIBLE
        loadingBg.visibility = View.VISIBLE
        Log.d("do_WalletReduce", "wallet_id: ${wallet_id} \n" +
                "shop_id: ${shop_id} \n" +
                "change: ${change} \n" +
                "action: ${action} \n" +
                "desc: ${desc}")
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var code = 0
                try {
                    resStr = response.body()!!.string()
                    code = response.code()
                    Log.d("do_WalletReduce", "返回資料 resStr：" + resStr)
                    if (code == 201) {
                        do_JoinSponser(shopId, levelId)
                    }
                } catch (e: JSONException) {
                    Log.d("do_WalletReduce", "JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("do_WalletReduce", "IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("do_WalletReduce", "onErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.do_wallet(
            url,wallet_id, shop_id,change,action, desc
        )
    }
    private fun do_JoinSponser(shop_id: String, sponsor_level_id: String) {
        Log.d("Do_JoinSponser", "shop_id: ${shop_id} ; sponsor_level_id: ${sponsor_level_id}")
        val url = ApiConstants.API_HOST + "sponsor/join/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_JoinSponser", "返回資料 resStr：" + resStr)
                    Log.d("Do_JoinSponser", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        requireActivity().runOnUiThread {
                            progressBar.visibility = View.GONE
                            loadingBg.visibility = View.GONE
                        }

                        val intent = Intent(requireActivity(), ToSponserEditActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("index", "2")
                        intent.putExtra("bundle", bundle)
                        startActivity(intent)
                    } else {
                        activity!!.runOnUiThread {
                            Toast.makeText(
                                requireActivity(),
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_JoinSponser", "JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_JoinSponser", "IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_JoinSponser", "onErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_JoinSponser(
            url, shop_id, sponsor_level_id
        )
    }


    private fun Do_walletHistoryDetailPartialUpdate(
        order_id: String,
    ) {
        Log.d("Do_walletHistoryDetailPartialUpdate", "order_id: ${order_id}")
        val url = ApiConstants.API_SWAGGER+"wallet/history/detail/${order_id}"

        // create your json here
        val jsonObject = JSONObject()
        try {
            jsonObject.put("status", 1)
            Log.d("doConvertAddValueToOrder", "jsonObject: ${jsonObject.toString()}")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var code: Int = 0
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    code = response.code()

                    Log.d("getWalletCreate", "返回資料 resStr：" + resStr)
                    Log.d("getWalletCreate", "返回資料 code：" + code.toString())

                    if (code == 200) {

                    }else{
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_walletHistoryDetailPartialUpdate", "JSONException：" + e.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_walletHistoryDetailPartialUpdate", "IOException：" + e.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_walletHistoryDetailPartialUpdate", "onErrorResponse：" + ErrorResponse.toString())
                requireActivity().runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_walletHistoryDetailPartialUpdate(
            url,
            jsonObject
        )
    }

}