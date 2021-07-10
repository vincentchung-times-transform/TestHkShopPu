package com.HKSHOPU.hk.ui.main.payment.activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.*
import com.paypal.android.sdk.payments.*
import org.jetbrains.anko.textColor
import org.json.JSONException
import java.math.BigDecimal

//import kotlinx.android.synthetic.main.activity_main.*

class FpsPayAccountActivity : BaseActivity() {

    private lateinit var binding: ActivityFpspayAccountBinding
    var ReceiptTime = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpspayAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClick()

    }

    private fun initView() {

        binding.etReceiptTime.doAfterTextChanged {
            ReceiptTime = binding.etReceiptTime.text.toString()
            if(ReceiptTime.isNotEmpty()){
                binding.tvSendTransferinfo.textColor = getColor(R.color.white)
                binding.tvSendTransferinfo.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            }
        }

    }

    private fun initClick() {

        binding.ivBackClick.setOnClickListener {

            finish()
        }

        binding.tvSendTransferinfo.setOnClickListener {

        }

    }

    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}