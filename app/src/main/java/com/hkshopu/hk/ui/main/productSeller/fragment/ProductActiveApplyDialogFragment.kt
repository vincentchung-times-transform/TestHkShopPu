package com.hkshopu.hk.ui.main.productSeller.fragment

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus

class ProductActiveApplyDialogFragment(var product_id:Int, var keyword: String,  var fragment: Fragment): DialogFragment(), View.OnClickListener {


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
    var product_Id = product_id
    var VM = ShopVModel()

    lateinit var progressBar_product_active_apply: ProgressBar
    lateinit var iv_loading_background_product_active_apply: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_productactiveapply, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_confirm).setOnClickListener(this)

        progressBar_product_active_apply = v.findViewById<ProgressBar>(R.id.progressBar_product_active_apply)
        iv_loading_background_product_active_apply = v.findViewById<ImageView>(R.id.iv_loading_background_product_active_apply)
        progressBar_product_active_apply.visibility = View.GONE
        iv_loading_background_product_active_apply.visibility = View.GONE

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_confirm -> {

                progressBar_product_active_apply.visibility = View.VISIBLE
                iv_loading_background_product_active_apply.visibility = View.VISIBLE

                VM.updateProductStatus(fragment, product_id, "active")
                RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus())


                progressBar_product_active_apply.visibility = View.GONE
                iv_loading_background_product_active_apply.visibility = View.GONE

                dismiss()

            }
        }
    }


} 