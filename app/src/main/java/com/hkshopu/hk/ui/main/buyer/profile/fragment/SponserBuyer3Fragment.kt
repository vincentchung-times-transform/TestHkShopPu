package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

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
import com.HKSHOPU.hk.data.bean.PaymentBean
import com.HKSHOPU.hk.data.bean.ShopAddressListBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.SponserAdjustmentDialogFragment
import com.facebook.FacebookSdk
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class SponserBuyer3Fragment : Fragment() {
    companion object {
        fun newInstance(): SponserBuyer3Fragment {
            val args = Bundle()
            val fragment = SponserBuyer3Fragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var paymentSpinner: Spinner
    var selected_payment_id = ""
    var mutablelist_paymentBean : MutableList<PaymentBean> = mutableListOf()
    val url = ApiConstants.API_HOST+"sponsor/join/"
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var levelId = ""
    var sponserId = MMKV.mmkvWithID("http").getString("SponserBuyerId", "").toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_sponser_buyer_venus, container, false)
        levelId = CommonVariable.costlist.get(2).id.toString()
        Log.d("CommonVariable.costlist", "costlist: ${CommonVariable.costlist} \n ; " +
                "levelId: ${levelId}")
        val venus = v.findViewById<TextView>(R.id.tv_venus)
        venus.text = "HKD\$"+ CommonVariable.costlist.get(2).price.toString()+"/月"
        val donate_detail = v.findViewById<TextView>(R.id.tv_storedvalue_detail_number)
        donate_detail.text = "HK\$"+CommonVariable.costlist.get(2).price.toString()
        val donate_value = v.findViewById<TextView>(R.id.tv_price_value)
        donate_value.text = "HK\$"+ CommonVariable.costlist.get(2).price.toString()
        paymentSpinner = v.findViewById<Spinner>(R.id.container_payment_spinner)
        val payddetail = v.findViewById<RelativeLayout>(R.id.layout_paiddetail)
        val fps = v.findViewById<ImageView>(R.id.btn_generateOder)
        fps.setOnClickListener{
            if(sponserId.equals("N")  && sponserId.isNotEmpty()){
                StoreValueConfirmDialogFragment(userId,levelId).show(
                    requireActivity().getSupportFragmentManager(),
                    "MyCustomFragment"
                )
            }else{
                if (!sponserId.equals(CommonVariable.costlist.get(2).title)) {
                    SponserAdjustmentDialogFragment().show(
                        requireActivity().getSupportFragmentManager(),
                        "MyCustomFragment"
                    )
                }else{
                    StoreValueConfirmDialogFragment(userId,levelId).show(
                        requireActivity().getSupportFragmentManager(),
                        "MyCustomFragment"
                    )
                }
            }
        }
        if (sponserId.equals("N")){
            fps.setImageResource(R.mipmap.btn_fps)
            payddetail.visibility = View.VISIBLE
        }else{
            if (!sponserId.equals(CommonVariable.costlist.get(2).title)) {
                fps.setImageResource(R.mipmap.ic_fpsdisable)
                payddetail.visibility = View.GONE
            }else{
                fps.setImageResource(R.mipmap.ic_fpsdisable)
                payddetail.visibility = View.GONE
            }
        }

        doGetPaymentMethodList()
        return v
    }
    private fun doGetPaymentMethodList() {

        var url = ApiConstants.API_HOST + "payment/method"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doGetPaymentMethodList", "返回資料 resStr：" + resStr)
                    Log.d("doGetPaymentMethodList", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val paymentBean: PaymentBean =
                                Gson().fromJson(jsonObject.toString(), PaymentBean::class.java)

                            mutablelist_paymentBean.add(paymentBean)
                        }

                        val payment_list: MutableList<String> = ArrayList<String>()

                        for (i in 0 until mutablelist_paymentBean.size) {
                            payment_list.add(mutablelist_paymentBean.get(i).payment_desc.toString())
                        }

                        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                            FacebookSdk.getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            payment_list
                        )
                        activity!!.runOnUiThread {
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            paymentSpinner.setAdapter(adapter)
                            paymentSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long,
                                ) {
                                    selected_payment_id = mutablelist_paymentBean.get(position).id
                                    Toast.makeText(activity!!, """已選取"${mutablelist_paymentBean.get(position).payment_desc}"作為付款方式""", Toast.LENGTH_SHORT).show()

//                                    var item_id_list = arrayListOf<String>()
//                                    for(i in 0 until mAdapter_ShoppingCartItems.getDatas().size){
//                                        for(j in 0 until mAdapter_ShoppingCartItems.getDatas().get(i).productList.size){
//                                            item_id_list.add(mAdapter_ShoppingCartItems.getDatas().get(i).productList.get(j).product_spec.shopping_cart_item_id.toString())
//                                        }
//                                    }
//                                    var gson = Gson()
//                                    var item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

//                                    doUpdateShoppingCartitems(item_id_list_json,"","","", payment_id.toString(), mutablelist_paymentBean.get(position).payment_desc.toString())
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }
                        }

                    }else{
                        activity!!.runOnUiThread {
                            Toast.makeText(activity!! , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetPaymentMethodList_errorMessage", "JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(activity!! , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetPaymentMethodList_errorMessage", "IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(activity!! , "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetPaymentMethodList_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    Toast.makeText(activity!!, "網路異常請重新連接", Toast.LENGTH_SHORT).show()
                }
            }
        })
        web.Get_Data(url)
    }

}