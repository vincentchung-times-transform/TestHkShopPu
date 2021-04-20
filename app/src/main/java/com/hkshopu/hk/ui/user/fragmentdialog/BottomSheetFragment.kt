package com.hkshopu.hk.ui.user.fragmentdialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.DialogFragmentNotificationBinding
import com.hkshopu.hk.databinding.FragmentBottomSheetDialogBinding

class BottomSheeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDialogBinding? = null
    private val binding get() = _binding!!


    var listener: OnDialogButtonFragmentListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        return view


    }

    interface OnDialogButtonFragmentListener {

        fun onSelectDialog(select: String)
    }
}