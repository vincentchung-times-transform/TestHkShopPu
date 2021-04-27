package com.hkshopu.hk.ui.main.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.hkshopu.hk.R

class FirstFragment : Fragment() {

    companion object {
        fun newInstance(): FirstFragment {
            val args = Bundle()
            val fragment = FirstFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_first, container, false)


        v.findViewById<ImageView>(R.id.btn_learn_more).setOnClickListener{
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


        return v
    }

}