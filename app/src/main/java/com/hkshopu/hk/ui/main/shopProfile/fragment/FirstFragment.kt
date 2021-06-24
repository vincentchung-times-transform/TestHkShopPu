package com.HKSHOPU.hk.ui.main.shopProfile.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.FragmentFirstBinding

class FirstFragment : Fragment((R.layout.fragment_first)) {

    companion object {
        fun newInstance(): FirstFragment {
            val args = Bundle()
            val fragment = FirstFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var binding: FragmentFirstBinding? = null
    private var fragmentFirstBinding: FragmentFirstBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFirstBinding.bind(view)
        fragmentFirstBinding = binding
        initView()
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // you can execute the logic here
                if (isEnabled) {

                } else {
                    activity?.onBackPressed()
                }
            }
        })

    }

    private fun initView() {
        binding!!.btnLearnMore.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

}