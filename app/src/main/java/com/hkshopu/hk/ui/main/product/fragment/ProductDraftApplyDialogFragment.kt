package com.hkshopu.hk.ui.main.product.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventProductSearch
import com.hkshopu.hk.component.EventRefreshShopList
import com.hkshopu.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.hkshopu.hk.data.bean.ShopListBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.activity.HelpCenterActivity
import com.hkshopu.hk.ui.main.store.activity.ShopmenuActivity
import com.hkshopu.hk.ui.main.store.fragment.StoreDeleteDenyDialogFragment
import com.hkshopu.hk.ui.main.store.fragment.StoreDeleteDialogFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProductDraftApplyDialogFragment(var product_id:Int, var keyword: String, var fragment: Fragment): DialogFragment(), View.OnClickListener {


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_productdraftapply, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_confirm).setOnClickListener(this)

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_confirm -> {
                VM.updateProductStatus(fragment, product_id, "draft")
                RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus("action"))
                dismiss()
            }
        }
    }


}