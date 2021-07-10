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
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.*

import com.paypal.android.sdk.payments.*
import org.json.JSONException
import java.math.BigDecimal

//import kotlinx.android.synthetic.main.activity_main.*

class FpsPayActivity : BaseActivity() {

    private lateinit var binding: ActivityFpspayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpspayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBackClick.setOnClickListener {

            finish()
        }
        binding.ivGotransfer.setOnClickListener {
            val intent = Intent()

            intent.setClass(this@FpsPayActivity, FpsPayAccountActivity::class.java)

            startActivity(intent)
        }

        binding.ivCopyText.setOnClickListener {
            copyText()
        }

    }
    fun copyText() {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;
        myClip = ClipData.newPlainText("text", binding.tvPhone.text.toString());
        myClipboard?.setPrimaryClip(myClip);

        Toast.makeText(this, "複製電話成功", Toast.LENGTH_SHORT).show();
    }
    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}