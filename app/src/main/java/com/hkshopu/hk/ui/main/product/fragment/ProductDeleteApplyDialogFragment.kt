package com.hkshopu.hk.ui.main.product.fragment

import a.a.a.a.a.e
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
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
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
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProductDeleteApplyDialogFragment(var product_id:Int, var keyword: String): DialogFragment(), View.OnClickListener {


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
    var productId = product_id
    var keywordProStatus = keyword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_productdeleteapply, container, false)
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

                Do_ProductDelete(productId)

            }
        }
    }

    private fun Do_ProductDelete(product_id: Int) {

        var url = ApiConstants.API_HOST + "product/" + product_id + "/delete_product/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
//                val list = ArrayList<ShopListBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ProductListFragment", "返回資料 resStr：" + resStr)
                    Log.d("ProductListFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        if(ret_val == "刪除商品成功!"){
                            activity!!.runOnUiThread {

                                Toast.makeText(context, ret_val.toString(), Toast.LENGTH_SHORT).show()

                            }
                            RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus(keywordProStatus))
                            dismiss()
                        } else {
                            activity!!.runOnUiThread {
                                Toast.makeText(context, ret_val.toString(), Toast.LENGTH_SHORT).show()

                            }
                            dismiss()
                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Delete_Product(url)
    }

}