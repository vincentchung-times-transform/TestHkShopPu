package com.HKSHOPU.hk.ui.main.sponsor

import android.content.Intent
import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.ui.main.homepage.fragment.*
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopSponserEditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity

//import kotlinx.android.synthetic.main.activity_main.*

class ToSponserEditActivity : BaseActivity() {
    private lateinit var binding: ActivityTosponsereditBinding
    var index:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTosponsereditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        index = intent.getBundleExtra("bundle")!!.getString("index","").toString()
        initVM()
        initClick()

    }
    private fun initVM() {
    }
    private fun initClick() {

        binding.ivGohomepage.setOnClickListener {
            val intent = Intent(this@ToSponserEditActivity, ShopmenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        binding.ivGoSponserEdit.setOnClickListener {
            val intent = Intent(this@ToSponserEditActivity, ShopSponserEditActivity::class.java)
            var bundle = Bundle()
            bundle.putString("index",index)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
//            Log.d("GoShopActivity", "ivGoShop Clicked")
            finish()
        }
    }
}