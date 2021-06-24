package com.HKSHOPU.hk.ui.main.shopProfile.fragment

import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshShopList
import com.HKSHOPU.hk.ui.main.shopProfile.activity.HelpCenterActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus

class StoreDeleteDialogFragment(): DialogFragment(), View.OnClickListener {


    var signal : Boolean = false

//    companion object {
//        val TAG = StoreOrNotDialogFragment::class.java.simpleName
//
//        /**
//         * Create a new instance of MyDialogFragment, providing "num"
//         * as an argument.
//         */
//        fun newInstance(): StoreOrNotDialogFragment {
//            val f = StoreOrNotDialogFragment()
//
//            // Supply num input as an argument.
//            val args = Bundle()
//            //args.putInt("num", num);
//            f.arguments = args
//            return f
//        }
//    }
    var et_shopDes:EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_storedelete, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_confirm).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_tohelp).setOnClickListener(this)

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_confirm -> {
                RxBus.getInstance().post(EventRefreshShopList())
                dismiss()
            }
            R.id.btn_tohelp -> {

                val intent = Intent(activity, HelpCenterActivity::class.java)
                activity!!.startActivity(intent)
            }
        }
    }

}