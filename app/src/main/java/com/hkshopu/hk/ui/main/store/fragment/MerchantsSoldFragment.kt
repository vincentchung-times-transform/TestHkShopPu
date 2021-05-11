package com.hkshopu.hk.ui.main.store.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hkshopu.hk.R

class MerchantsSoldFragment : Fragment() {

    companion object {
        fun newInstance(): MerchantsSoldFragment {
            val args = Bundle()
            val fragment = MerchantsSoldFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

}