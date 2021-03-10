package com.hkshopu.hk.ui.user.fragmentdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hkshopu.hk.databinding.ActivityAddshopBinding.inflate
import com.hkshopu.hk.databinding.ActivityLaunchBinding.inflate

import com.hkshopu.hk.databinding.DialogFragmentNotificationBinding

class NotificationDialogFragment: DialogFragment() {
    private var _binding: DialogFragmentNotificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogFragmentNotificationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

        initView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initView() {
        initClick()

    }
    private fun initClick() {
        binding.dontAllow.setOnClickListener {
            dismiss()
        }
        binding.allow.setOnClickListener {

        }

    }

}


