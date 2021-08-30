package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

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
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.invisible
import com.tencent.mmkv.MMKV

class SponserBuyerFragment : Fragment() {
    companion object {
        fun newInstance(): SponserBuyerFragment {
            val args = Bundle()
            val fragment = SponserBuyerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var sponserId = MMKV.mmkvWithID("http").getString("SponserBuyerId", "").toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_sponser_buyer, container, false)
        val progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.invisible()
        val ivPluto = v.findViewById<ImageView>(R.id.iv_pluto)
        ivPluto.setOnClickListener {
            if (sponserId.equals(CommonVariable.costlist.get(0).title)) {
                RxBus.getInstance().post(EventChangeSponserTab(sponserId))
            } else if (sponserId.equals(CommonVariable.costlist.get(1).title)) {
                RxBus.getInstance().post(EventChangeSponserBuyerTab(sponserId))
            } else {
                RxBus.getInstance().post(EventChangeSponserBuyerTabPosition(1))
            }
        }
        val ivVenus = v.findViewById<ImageView>(R.id.iv_venus)
        ivVenus.setOnClickListener {
            if (sponserId.equals(CommonVariable.costlist.get(2).title)) {
                RxBus.getInstance().post(EventChangeSponserBuyerTab(sponserId))
            } else {
                RxBus.getInstance().post(EventChangeSponserBuyerTabPosition(2))
            }
        }
        val ivMercury = v.findViewById<ImageView>(R.id.iv_mercury)
        ivMercury.setOnClickListener {
            if (sponserId.equals(CommonVariable.costlist.get(3).title)) {
                RxBus.getInstance().post(EventChangeSponserBuyerTab(sponserId))
            } else {
                RxBus.getInstance().post(EventChangeSponserBuyerTabPosition(3))
            }
        }
        v.findViewById<ImageView>(R.id.iv_knowmore).setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        return v
    }


}