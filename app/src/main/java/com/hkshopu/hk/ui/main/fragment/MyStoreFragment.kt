package com.hkshopu.hk.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.hkshopu.hk.R
import com.hkshopu.hk.ui.main.activity.AddNewProductActivity
import com.hkshopu.hk.ui.main.activity.AddShopActivity
import com.hkshopu.hk.ui.main.activity.AddShopBriefActivity
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.find

class MyStoreFragment : Fragment() {

    companion object {
        fun newInstance(): MyStoreFragment {
            val args = Bundle()
            val fragment = MyStoreFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_mystore, container, false)


        val btn_addShopBrief = v.find<ImageButton>(R.id.iv_addshopbrief)
        btn_addShopBrief.setOnClickListener {
            val intent = Intent(activity, AddShopBriefActivity::class.java)
            activity!!.startActivity(intent)
        }
        val btn_addNewProduct = v.find<RelativeLayout>(R.id.layout_add_product)
        btn_addNewProduct.setOnClickListener {
            val intent = Intent(activity, AddNewProductActivity::class.java)
            activity!!.startActivity(intent)
        }

        return v
    }

}