package com.HKSHOPU.hk.ui.main.seller.order.fragment

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
import androidx.fragment.app.FragmentTransaction
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayAuditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity

import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class DeliveryNotifyApplySuccessDialogFragment(): DialogFragment(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
        setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_deliverynotifyapplysuccess, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.custom_unit_transparent
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)
        val btn_dismiss = v.findViewById<ImageView>(R.id.btn_dismiss)
        btn_dismiss.setOnClickListener(this)
        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_dismiss->{

                MMKV.mmkvWithID("mySaleList").putString("mySaleList", "SaleListFragment")

                RxBus.getInstance().post(EventShopmenuToSpecificPage(2))
                RxBus.getInstance().post(EventRefreshShopFucition())
                RxBus.getInstance().post(EventRefreshShoppingCartItemCount())

                val intent = Intent(requireActivity(), ShopmenuActivity::class.java)
                startActivity(intent)
                dismiss()

            }
        }
    }


}