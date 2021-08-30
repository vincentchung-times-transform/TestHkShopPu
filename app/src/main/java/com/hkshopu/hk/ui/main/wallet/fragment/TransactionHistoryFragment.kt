package com.HKSHOPU.hk.ui.main.wallet.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.AddValueHistoryBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.wallet.adapter.StoredValueAdapter
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class TransactionHistoryFragment : Fragment() {

    companion object {
        fun newInstance(): TransactionHistoryFragment {
            val args = Bundle()
            val fragment = TransactionHistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var loading_background: ImageView
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    private val adapter = StoredValueAdapter()
    var list: ArrayList<AddValueHistoryBean> = arrayListOf()
    var walletId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_seller_sales, container, false)
        walletId = arguments!!.getString("walletId").toString()

        allProduct = v.find<RecyclerView>(R.id.recyclerview)
        progressBar = v.find<ProgressBar>(R.id.progressBar)
        loading_background = v.find<ImageView>(R.id.imgView_loading_background)


        val shopId= MMKV.mmkvWithID("http").getString("ShopId", "")
        val status = "Cancelled"
        //        待付款 - Pending Payment
//        待發貨 - Pending Delivery
//        待收貨 - Pending Good Receive
//        訂單已完成 - Completed
//        已取消 - Cancelled
//        退貨/退款 - Refunded
//        getSellerSaleList(shopId!!, status)
//        getSellerSaleList("131", status)

//        12e3b172-e020-4875-a233-14337aebf422
//        getWalletTransactionsRead("12e3b172-e020-4875-a233-14337aebf422")
//         getWalletTransactionsRead(walletId)
        initView()

        return v
    }

    override fun onResume() {
        super.onResume()
        getWalletTransactionsRead(walletId)
    }

    private fun initView(){
        initRecyclerView()
    }


    private fun initRecyclerView(){

        val layoutManager = LinearLayoutManager(requireActivity())
        allProduct.layoutManager = layoutManager

        allProduct.adapter = adapter

    }

    fun getWalletTransactionsRead(
        wallet_id: String,
    )
    {
        progressBar.visibility = View.VISIBLE
        loading_background.visibility = View.VISIBLE

        Log.d("getWalletTransactionsRead", "wallet_id: ${wallet_id}")
        val url = ApiConstants.API_SWAGGER+"wallet/transactions/${wallet_id}"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {

                var resStr: String? = ""
                var code: Int = 0
                try {
                    list.clear()

                    resStr = response.body()!!.string()
                    code = response.code()

                    val jsonArray = JSONArray(resStr)
                    Log.d("getWalletTransactionsRead", "返回資料 resStr：" + resStr)
                    Log.d("getWalletTransactionsRead", "返回資料 code：" + code.toString())

                    if (code == 200) {

                        if(jsonArray.length()>0){
                            for(i in 0..jsonArray.length()-1){
                                val addValueHistoryBean: AddValueHistoryBean =
                                    Gson().fromJson(jsonArray.get(i).toString(), AddValueHistoryBean::class.java)
                                list.add(addValueHistoryBean)
                            }
                        }
                        Log.d("getWalletTransactionsRead", "解析資料 list：" + list.toString())

                        requireActivity().runOnUiThread {
                            adapter.setData(list)
                            progressBar.visibility = View.GONE
                            loading_background.visibility = View.GONE
                        }

                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireActivity(), "Wallet History Get Failed", Toast.LENGTH_SHORT)
                                .show()

                            progressBar.visibility = View.GONE
                            loading_background.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletTransactionsRead", "JSONException: ${e.toString()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletTransactionsRead", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("getWalletTransactionsRead", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.Get_Data(
            url
        )
    }
}