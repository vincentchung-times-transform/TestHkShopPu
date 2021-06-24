package com.HKSHOPU.hk.ui.main.productSeller.fragment

import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.shopProfile.activity.HelpCenterActivity

class SpecificationInfoDialogFragment() : DialogFragment(), View.OnClickListener {
    companion object {
        val TAG = SpecificationInfoDialogFragment::class.java.simpleName

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        fun newInstance(): SpecificationInfoDialogFragment {
            val f = SpecificationInfoDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            //args.putInt("num", num);
            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_spec_info, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

//        v.findViewById<View>(R.id.btn_to_help_center).setOnClickListener(this)
        v.findViewById<View>(R.id.btn_keep_going).setOnClickListener(this)
        v.findViewById<View>(R.id.btn_to_help_center).setOnClickListener(this)
        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_keep_going -> {
                dismiss()
            }
            R.id.btn_to_help_center -> {
                val intent = Intent(view.context, HelpCenterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}