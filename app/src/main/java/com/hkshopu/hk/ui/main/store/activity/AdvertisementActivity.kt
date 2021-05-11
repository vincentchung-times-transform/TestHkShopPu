package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.ActivityAdvertisementBinding
import com.hkshopu.hk.databinding.ActivityShopattentionBinding

//import kotlinx.android.synthetic.main.activity_main.*

class AdvertisementActivity : BaseActivity() {
    private lateinit var binding: ActivityAdvertisementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivMyad.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.ivDiscount.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.ivActivity.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

    }


}