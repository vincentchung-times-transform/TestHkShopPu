package com.HKSHOPU.hk.ui.main.productSeller.fragment

import android.content.Intent
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
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventMyStoreFragmentRefresh
import com.HKSHOPU.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.HKSHOPU.hk.ui.main.productSeller.activity.EditProductActivity
import com.HKSHOPU.hk.ui.main.productSeller.activity.ProductDetailForSalerActivity
import com.HKSHOPU.hk.ui.user.vm.ShopVModel
import com.HKSHOPU.hk.utils.rxjava.RxBus

class EditProductRemindDialogFragment(var activity: ProductDetailForSalerActivity, var product_id: String): DialogFragment(), View.OnClickListener {


    var signal : Boolean = false
    var VM = ShopVModel()

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
    lateinit var progressBar_edit_product_reminder: ProgressBar
    lateinit var iv_loading_background_edit_product_reminder: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_edit_product_remind, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_cancel_remind).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_edit_keep_goning).setOnClickListener(this)
        progressBar_edit_product_reminder = v.findViewById<ProgressBar>(R.id.progressBar_edit_product_reminder)
        iv_loading_background_edit_product_reminder = v.findViewById<ImageView>(R.id.iv_loading_background_edit_product_reminder)
        progressBar_edit_product_reminder.visibility = View.GONE
        iv_loading_background_edit_product_reminder.visibility = View.GONE

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel_remind -> {
                dismiss()
            }
            R.id.btn_edit_keep_goning->{

                progressBar_edit_product_reminder.visibility = View.VISIBLE
                iv_loading_background_edit_product_reminder.visibility = View.VISIBLE


                var currentActivity : ProductDetailForSalerActivity = activity

                VM.updateProductStatus(currentActivity, product_id, "draft")

                RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus())
                RxBus.getInstance().post(EventMyStoreFragmentRefresh())

                progressBar_edit_product_reminder.visibility = View.GONE
                iv_loading_background_edit_product_reminder.visibility = View.GONE

                val intent = Intent(currentActivity, EditProductActivity::class.java)
                startActivity(intent)

                currentActivity.finish()

                dismiss()

            }
        }
    }

}