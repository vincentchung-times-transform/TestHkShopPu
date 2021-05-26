package com.hkshopu.hk.ui.main.product.fragment

import android.app.Activity
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
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.ui.main.store.activity.ShopmenuActivity
import com.tencent.mmkv.MMKV

class StoreOrNotDialogStoreProductsFragment(var activity: BaseActivity): DialogFragment(), View.OnClickListener {


    var signal : Boolean = false
    var product_edit_session = false
    var product_add_session = false
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
        val v = inflater.inflate(R.layout.dialog_fragment_store_or_not, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_cancel_add_prodcut).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_discard).setOnClickListener(this)

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel_add_prodcut -> dismiss()
            R.id.btn_discard -> {

                var activity: BaseActivity  = activity

                MMKV.mmkvWithID("addPro").clear()
                MMKV.mmkvWithID("editPro").clear()

                activity.finish()

            }
        }
    }

}