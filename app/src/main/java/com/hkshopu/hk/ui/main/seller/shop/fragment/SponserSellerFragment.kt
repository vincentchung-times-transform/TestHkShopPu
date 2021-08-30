package com.HKSHOPU.hk.ui.main.seller.shop.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventChangeSponserTab
import com.HKSHOPU.hk.component.EventChangeSponserTabPosition
import com.HKSHOPU.hk.component.EventSaleListToSpecificPage
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.invisible
import com.tencent.mmkv.MMKV

class SponserSellerFragment : Fragment() {
    companion object {
        fun newInstance(): SponserSellerFragment {
            val args = Bundle()
            val fragment = SponserSellerFragment()
            fragment.arguments = args
            return fragment
        }
    }
    var sponserId = MMKV.mmkvWithID("http").getString("SponserId", "").toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_sponser_seller, container, false)
        val progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.invisible()
        val ivHonor = v.findViewById<ImageView>(R.id.iv_honor)
        ivHonor.setOnClickListener {
            if(sponserId.equals(CommonVariable.costlist.get(0).title)) {
                RxBus.getInstance().post(EventChangeSponserTab(sponserId))
            }else if(sponserId.equals(CommonVariable.costlist.get(1).title)) {
                RxBus.getInstance().post(EventChangeSponserTab(sponserId))
            }else{
                RxBus.getInstance().post(EventChangeSponserTabPosition(1))
            }
        }
        val ivGlory = v.findViewById<ImageView>(R.id.iv_glory)
        ivGlory.setOnClickListener{
            if(sponserId.equals(CommonVariable.costlist.get(2).title)) {
                RxBus.getInstance().post(EventChangeSponserTab(sponserId))
            }else{
                RxBus.getInstance().post(EventChangeSponserTabPosition(2))
            }
        }
        val ivExcellence = v.findViewById<ImageView>(R.id.iv_excellence)
        ivExcellence.setOnClickListener{
            if(sponserId.equals(CommonVariable.costlist.get(3).title)) {
                RxBus.getInstance().post(EventChangeSponserTab(sponserId))
            }else{
                RxBus.getInstance().post(EventChangeSponserTabPosition(3))
            }
        }
        v.findViewById<ImageView>(R.id.iv_knowmore).setOnClickListener{
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        return v
    }


}