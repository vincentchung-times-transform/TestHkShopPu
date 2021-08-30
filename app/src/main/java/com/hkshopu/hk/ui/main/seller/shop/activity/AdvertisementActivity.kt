package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityAdvertisementBinding
import com.HKSHOPU.hk.ui.main.advertisement.activity.MyAdvertisementActivity

//import kotlinx.android.synthetic.main.activity_main.*

class AdvertisementActivity : BaseActivity() {
    private lateinit var binding: ActivityAdvertisementBinding
    var shop_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shop_id = intent.getBundleExtra("bundle")!!.getString("shopId").toString()

        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.ivMyad.setOnClickListener {
//            val url = "http://www.hkshopu.com/"
//            val i = Intent(Intent.ACTION_VIEW)
//            i.data = Uri.parse(url)
//            startActivity(i)
            val intent = Intent(this, MyAdvertisementActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId", shop_id)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
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
        binding.btnKnowMore.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        binding.btnKnowMore.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

    }


}