package com.HKSHOPU.hk.ui.main.payment.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.databinding.*

import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.main.wallet.activity.MyWalletActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV

//import kotlinx.android.synthetic.main.activity_main.*

class FpsPayAuditActivity : BaseActivity() {

    private lateinit var binding: ActivityFpspayauditBinding
    var mode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpspayauditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle = intent.getBundleExtra("bundle")
        mode = bundle!!.getString("mode").toString()
        Log.d("FpsPayAuditActivity", "mode: ${mode}")

        initView()
        initClick()
    }

    private fun initView() {

        when(mode){
            "shopping"->{
                binding.tv1.setText(getText(R.string.audit_title))
                binding.ivGopurchaselist.setText(getText(R.string.purchaselist))
            }
            "addValue"->{
                binding.tv1.setText(getText(R.string.audit_add_value_title))
                binding.ivGopurchaselist.setText(getText(R.string.back_to_wallet))
            }
            "sponsor"->{
                binding.tv1.setText(getText(R.string.audit_title))
                binding.ivGopurchaselist.setText(getText(R.string.purchaselist))
            }
        }

    }

    private fun initClick() {
        binding.ivBackClick.setOnClickListener {
            finish()
        }

        binding.ivGopurchaselist.setOnClickListener {

            when(mode){
                "shopping"->{
                    MMKV.mmkvWithID("myOderList").putString("myOderList", "PurchaseListFragment")

                    RxBus.getInstance().post(EventShopmenuToSpecificPage(1))
                    RxBus.getInstance().post(EventRefreshUserInfo())
                    RxBus.getInstance().post(EventRefreshShoppingCartItemCount())

                    val intent = Intent(this, ShopmenuActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                "addValue"->{
                    val intent = Intent(this, MyWalletActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "sponsor"->{
                    MMKV.mmkvWithID("myOderList").putString("myOderList", "PurchaseListFragment")

                    RxBus.getInstance().post(EventShopmenuToSpecificPage(1))
                    RxBus.getInstance().post(EventRefreshUserInfo())
                    RxBus.getInstance().post(EventRefreshShoppingCartItemCount())

                    val intent = Intent(this, ShopmenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        binding.ivGohomepage.setOnClickListener {
            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)
            RxBus.getInstance().post(EventShopmenuToSpecificPage(0))
            finish()
        }
    }

    public override fun onDestroy() {
        // Stop service when done
        super.onDestroy()
    }

}