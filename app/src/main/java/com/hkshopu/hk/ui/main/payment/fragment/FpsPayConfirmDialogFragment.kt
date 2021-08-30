package com.HKSHOPU.hk.ui.main.payment.fragment

import a.a.a.a.a
import a.a.a.a.a.e
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventGenerateAddValueOeder
import com.HKSHOPU.hk.component.EventGenerateOeder
import com.HKSHOPU.hk.data.bean.FpsAccountBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson

import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class FpsPayConfirmDialogFragment(var activity: Activity,var account:String, var contact_type:String, var phone_or_email:String,var date:String, var jsonTutList: String, var fpsPayAccount_id:String, var transferTime: String, var mode: String, var order_number_for_add_value:String): DialogFragment(), View.OnClickListener {

    lateinit var progressBar: ProgressBar
    lateinit var btn_forward: ImageView
    lateinit var btn_cancel: ImageView

    var transferAccount = account
    var contactType = contact_type
    var transferPhoneOrEmail = phone_or_email
    var transferDate = date
    var jsonTutList_order = jsonTutList
    var selectedFpsPayAccount_id = fpsPayAccount_id
    var TransferTime_db = transferTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_fpspayconfirm, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)
        val account = v.findViewById<TextView>(R.id.tv_account)
        progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        btn_forward = v.findViewById<ImageView>(R.id.btn_forward)
        btn_cancel = v.findViewById<ImageView>(R.id.btn_cancel)

        account.text = transferAccount
        btn_forward.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)

        if (contactType.equals("phone")){
            v.findViewById<TextView>(R.id.tv_contactType_title).setText(context!!.getText(R.string.transfer_phone))
        }else{
            v.findViewById<TextView>(R.id.tv_contactType_title).setText(context!!.getText(R.string.transfer_email))
        }
        val phone = v.findViewById<TextView>(R.id.tv_phone)
        phone.text = transferPhoneOrEmail
        val date = v.findViewById<TextView>(R.id.tv_date)
        date.text = transferDate


        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> {
                dismiss()
            }
            R.id.btn_forward -> {
                Do_confirmFPSOrderTransaction(jsonTutList_order, selectedFpsPayAccount_id, TransferTime_db)
            }
        }
    }


    private fun Do_confirmFPSOrderTransaction(
        order_id: String,
        user_payment_account_id: String,
        target_delivery_date_time: String,
    ) {
        progressBar.visibility = View.VISIBLE
        btn_cancel.isClickable = false
        btn_forward.isClickable = false

        Log.d("Do_confirmFPSOrderTransaction", "order_id: ${order_id} ; user_payment_account_id: ${user_payment_account_id} ; target_delivery_date_time: ${target_delivery_date_time} ; mode: ${mode}")
        val url = ApiConstants.API_HOST + "payment/fps/confirmFPSOrderTransaction/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_confirmFPSOrderTransaction", "返回資料 resStr：" + resStr)
                    Log.d("Do_confirmFPSOrderTransaction", "返回資料 ret_val：" + ret_val)
                    runOnUiThread {
                        Toast.makeText(requireActivity(), ret_val.toString(), Toast.LENGTH_SHORT).show()
                    }
                    if (status == 0) {
                        when(mode){
                            "shopping"->{

                                runOnUiThread {
                                    progressBar.visibility = View.GONE
                                    btn_cancel.isClickable = true
                                    btn_forward.isClickable = true
                                }

                                val intent = Intent(activity, FpsPayAuditActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("mode", mode)
                                intent.putExtra("bundle", bundle)
                                activity.startActivity(intent)
                                startActivity(intent)
                                activity.finish()
                                dismiss()
                            }
                            "addValue"->{
                            Do_walletHistoryDetailPartialUpdate(order_number_for_add_value)
                            }
                            "sponsor"->{
                                runOnUiThread {
                                    progressBar.visibility = View.GONE
                                    btn_cancel.isClickable = true
                                    btn_forward.isClickable = true
                                }

                                val intent = Intent(activity, FpsPayAuditActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("mode", mode)
                                intent.putExtra("bundle", bundle)
                                activity.startActivity(intent)
                                startActivity(intent)
                                activity.finish()
                                dismiss()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_confirmFPSOrderTransaction", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_confirmFPSOrderTransaction", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_confirmFPSOrderTransaction", "onErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_confirmFPSOrderTransaction(
            url,
            order_id,
            user_payment_account_id,
            target_delivery_date_time,
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
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            btn_cancel.isClickable = true
                            btn_forward.isClickable = true
                        }


                        val intent = Intent(activity, FpsPayAuditActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("mode", mode)
                        intent.putExtra("bundle", bundle)
                        activity.startActivity(intent)
                        startActivity(intent)
                        activity.finish()
                        dismiss()


                    }else{
                        runOnUiThread {
                            Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_walletHistoryDetailPartialUpdate", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_walletHistoryDetailPartialUpdate", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_walletHistoryDetailPartialUpdate", "onErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
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