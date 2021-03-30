package com.hkshopu.hk.ui.main.fragment

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventShopDesUpdated
import com.hkshopu.hk.utils.rxjava.RxBus



import org.jetbrains.anko.find
import java.util.regex.Pattern

class FBLoginPermitDialogFragment : DialogFragment(), View.OnClickListener {
    companion object {
        val TAG = FBLoginPermitDialogFragment::class.java.simpleName

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        fun newInstance(): FBLoginPermitDialogFragment {
            val f = FBLoginPermitDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            //args.putInt("num", num);
            f.arguments = args
            return f
        }
    }
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
        val v = inflater.inflate(R.layout.dialog_fragment_des_add, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)
        et_shopDes = v.find<EditText>(R.id.et_shopdes)
        et_shopDes!!.setSingleLine(false)
        et_shopDes!!.setSingleLine(false)
        et_shopDes!!.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        et_shopDes!!.setLines(3)
        et_shopDes!!.setMaxLines(3)
        v.findViewById<View>(R.id.btn_cancel_shopname).setOnClickListener(this)
        v.findViewById<View>(R.id.btn_confirm_shopname).setOnClickListener(this)
        val specialCharFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                val regexStr = "[`~!@#$%^&*()+=|{}':;'\",\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
                val pattern = Pattern.compile(regexStr)
                val matcher = pattern.matcher(source.toString())
                if (matcher.matches()) {
                    ""
                } else {
                    null
                }
            }
        et_shopDes!!.setFilters(arrayOf(specialCharFilter))
        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel_shopname -> dismiss()
            R.id.btn_confirm_shopname -> {
                var ShopDes = et_shopDes!!.text.toString()
                if(!ShopDes.isEmpty()){
                    RxBus.getInstance().post(EventShopDesUpdated(ShopDes))
                    dismiss()
                }
            }
        }
    }
}