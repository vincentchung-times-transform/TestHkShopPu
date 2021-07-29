package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

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
import com.HKSHOPU.hk.component.EventGenerateOeder
import com.HKSHOPU.hk.component.EventOrderCompelete
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class PurchaseConfirmDialogFragment(var orderNumber:String): DialogFragment(), View.OnClickListener {

    var signal : Boolean = false
    var order_number = orderNumber
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_purchaseconfirm, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.dialog_fragment_background2
            ), 0
        )
        Log.d("ProductConfirmDialogFragment", "orderNumber：" + order_number)
        dialog!!.window!!.setBackgroundDrawable(inset)
        val number = v.findViewById<TextView>(R.id.tv_ordernumber)
        number.text = order_number
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
                Do_OrderComplete(order_number)

            }
        }
    }

    private fun Do_OrderComplete(
        order_id: String
    ) {
        Log.d("Do_OrderComplete", "order_id: ${order_id}")
        val userId = MMKV.mmkvWithID("http").getString("UserId", "")
        val url = ApiConstants.API_HOST + "user_detail/order_completed/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_OrderComplete", "返回資料 resStr：" + resStr)
                    Log.d("Do_OrderComplete", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        RxBus.getInstance().post(EventOrderCompelete())
                        dismiss()
                    } else {
                        runOnUiThread {
                            Toast.makeText(requireActivity(), ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_OrderComplete", "JSONException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_OrderComplete", "IOException：" + e.toString())
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_OrderComplete", "onErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_OrderComplete(
            url,
            userId,
            order_id
        )
    }

}