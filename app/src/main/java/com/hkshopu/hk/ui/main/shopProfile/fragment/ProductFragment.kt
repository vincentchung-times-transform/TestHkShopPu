package com.HKSHOPU.hk.ui.main.shopProfile.fragment


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.tiper.MaterialSpinner


class ProductFragment : Fragment(R.layout.fragment_product) {
    companion object {
        fun newInstance() : ProductFragment {
            val args = Bundle()
            val fragment = ProductFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val listener by lazy {
        object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
                Log.v("MaterialSpinner", "onItemSelected parent=${parent.id}, position=$position")
                parent.focusSearch(View.FOCUS_UP)?.requestFocus()
            }

            override fun onNothingSelected(parent: MaterialSpinner) {
                Log.v("MaterialSpinner", "onNothingSelected parent=${parent.id}")
            }
        }
    }


    fun initView() {
        initClick()


    }

    fun initClick() {

    }

    fun initData() {

    }


}