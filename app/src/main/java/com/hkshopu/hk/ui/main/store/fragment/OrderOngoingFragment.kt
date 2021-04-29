package com.hkshopu.hk.ui.main.store.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hkshopu.hk.R

class OrderOngoingFragment : Fragment() {

    companion object {
        fun newInstance(): OrderOngoingFragment {
            val args = Bundle()
            val fragment = OrderOngoingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orderongoing, container, false)
    }

}