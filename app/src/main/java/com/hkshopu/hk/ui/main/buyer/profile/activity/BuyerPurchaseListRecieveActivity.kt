package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventOrderCompelete
import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerOrderDetail_Adapter
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.PurchaseConfirmDialogFragment
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.UpComingDialogFragment
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.StoreDeleteApplyDialogFragment
import com.HKSHOPU.hk.utils.extension.load
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson

import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BuyerPurchaseListRecieveActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerorderdetailReceiveBinding


    private val adapter = BuyerOrderDetail_Adapter()
    var orderNumber =""
    var orderId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerorderdetailReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.isVisible = true
        initView()
        initClick()
        initEvent()
        orderId = intent.getBundleExtra("bundle")!!.getString("order_id").toString()
        doGetData(orderId!!)
    }

    private fun initView() {

    }


    private fun initClick() {
        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(this, ShopNotifyActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivContact.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("https://www.hkshopu.com/")
            startActivity(intent)
        }
        binding.ivRefund.setOnClickListener {
           UpComingDialogFragment().show(
               getSupportFragmentManager(),
                "MyCustomFragment"
            )
        }
        binding.ivOrderdone.setOnClickListener {
            PurchaseConfirmDialogFragment(orderId).show(
                getSupportFragmentManager(),
                "MyCustomFragment"
            )
        }

    }
    private fun initRecyclerView(){
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventOrderCompelete ->{
                        finish()
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun doGetData(order_id: String) {

        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        var url = ApiConstants.API_HOST + "user_detail/"+order_id +"/order_detail/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var myOrderBean: MyOrderBean = MyOrderBean()
                val list_product = ArrayList<OrderProductBean>()
                list_product.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerPurchaseList_recieveActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerPurchaseList_recieveActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonObject = json.getJSONObject("data")
                        val jsonArray_product: JSONArray = jsonObject.getJSONArray("productList")
                        val state = jsonObject.getString("status")
                        if(state.equals("Pending Good Receive")) {
                            myOrderBean =
                                Gson().fromJson(jsonObject.toString(), MyOrderBean::class.java)

                            for (i in 0 until jsonArray_product.length()) {
                                val jsonObject_product: JSONObject = jsonArray_product.getJSONObject(i)
                                val orderProductBean: OrderProductBean =
                                    Gson().fromJson(jsonObject_product.toString(), OrderProductBean::class.java)
                                list_product.add(orderProductBean)
                            }

                            runOnUiThread {
                                binding.tvStatus.setText(myOrderBean.buyer_message_title)
                                binding.tvReceive.setText(myOrderBean.buyer_message_content)

                                binding.tvLogistic.text = myOrderBean.shipment_info
                                binding.tvNumber.setText(myOrderBean.waybill_number)

                                binding.tvBuyername.text = myOrderBean.name_in_address
                                binding.tvBuyerphone.text = myOrderBean.phone
                                binding.tvBuyeraddress.text = myOrderBean.full_address

                                binding.ivStore.load(myOrderBean.shop_icon)
                                binding.tvStoreName.text = myOrderBean.shop_title
                                binding.tvAmount.setText("HKD$ ${myOrderBean.subtotal.toString()}")
                                binding.tvShippingFee.setText("HKD$ ${myOrderBean.shipment_price.toString()}")
                                val total_amount= myOrderBean.subtotal + myOrderBean.shipment_price
                                binding.tvTotal.setText("HKD$ ${total_amount.toString().toString()}")

                                binding.tvOrdernumber.text = myOrderBean.order_number
                                orderNumber = myOrderBean.order_number


                                try {
                                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                                    if(myOrderBean.payment_at.isNotEmpty()){
                                        val payment_at: Date = format.parse(myOrderBean.payment_at)
                                        var payment_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(payment_at)
                                        binding.tvPaytime.text = payment_at_result.toString()
                                    }

                                    if(myOrderBean.actual_post_at.isNotEmpty()){
                                        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                        val estimated_deliver_at: Date = format.parse(myOrderBean.actual_post_at)
                                        var estimated_deliver_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(estimated_deliver_at)
                                        binding.tvDeliveryTimeValue.setText(estimated_deliver_at_result.toString())
                                    }
                                    if(myOrderBean.estimated_deliver_at.isNotEmpty()){
                                        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                        val estimated_deliver_at: Date = format.parse(myOrderBean.estimated_deliver_at)
                                        var estimated_deliver_at_result = SimpleDateFormat("dd/MM/yyyy HH:mm").format(estimated_deliver_at)
                                        binding.tvDeliveryTimeValueEst.setText(estimated_deliver_at_result.toString())
                                    }


                                } catch (e: ParseException) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace()
                                }

                                initRecyclerView()
                            }
                            adapter.setData(list_product)

                        }
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
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