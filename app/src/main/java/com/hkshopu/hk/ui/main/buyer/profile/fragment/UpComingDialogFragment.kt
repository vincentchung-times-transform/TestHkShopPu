package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.net.Uri
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


class UpComingDialogFragment(): DialogFragment(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_upcoming, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.dialog_fragment_background2
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)
        v.findViewById<ImageView>(R.id.btn_know_more).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_return).setOnClickListener(this)

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_know_more -> {
                val url = "http://www.hkshopu.com/"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                requireActivity().startActivity(i)
            }
            R.id.btn_return -> {
                dismiss()
            }
        }
    }
}