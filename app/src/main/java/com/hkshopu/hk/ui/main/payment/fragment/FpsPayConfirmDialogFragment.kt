package com.HKSHOPU.hk.ui.main.payment.fragment

import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventAddShopSuccess
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity

import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class FpsPayConfirmDialogFragment(var account:String, var contact_type:String, var phone_or_email:String,var date:String, var jsonTutList: String, var fpsPayAccount_id:String, var transferTime: String): DialogFragment(), View.OnClickListener {


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
        account.text = transferAccount

        if (contactType.equals("phone")){
            v.findViewById<TextView>(R.id.tv_contactType_title).setText(context!!.getText(R.string.transfer_phone))
        }else{
            v.findViewById<TextView>(R.id.tv_contactType_title).setText(context!!.getText(R.string.transfer_email))
        }
        val phone = v.findViewById<TextView>(R.id.tv_phone)
        phone.text = transferPhoneOrEmail
        val date = v.findViewById<TextView>(R.id.tv_date)
        date.text = transferDate
        v.findViewById<ImageView>(R.id.btn_forward).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)

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
        Log.d("Do_confirmFPSOrderTransaction", "order_id: ${order_id} ; user_payment_account_id: ${user_payment_account_id} ; target_delivery_date_time: ${target_delivery_date_time}")
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
                        val intent = Intent(activity, FpsPayAuditActivity::class.java)
                        requireActivity().startActivity(intent)
                        startActivity(intent)
                        requireActivity().finish()
                        dismiss()

                    } else {
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

}