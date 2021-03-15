package com.hkshopu.hk.ui.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventShopNameUpdated
import com.hkshopu.hk.databinding.ActivityEmailverifyBinding
import com.hkshopu.hk.databinding.FragmentSecondBinding
import com.hkshopu.hk.databinding.FragmentShopinfoBinding
import com.hkshopu.hk.ui.main.activity.MyAddressActivity
import com.hkshopu.hk.ui.user.activity.TermsOfServiceActivity
import com.hkshopu.hk.ui.user.activity.UserIofoActivity
import com.hkshopu.hk.utils.rxjava.RxBus

class SecondFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(): SecondFragment {
            val args = Bundle()
            val fragment = SecondFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val view = ""

    private var binding: FragmentSecondBinding? = null
    private var fragmentShopInfoBinding: FragmentSecondBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        view.findViewById<TextView>(R.id.txtView_more_myaddress).setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.txtView_more_myaddress -> {
                val intent = Intent(this.context, MyAddressActivity::class.java)
                startActivity(intent)

            }
        }
    }
}