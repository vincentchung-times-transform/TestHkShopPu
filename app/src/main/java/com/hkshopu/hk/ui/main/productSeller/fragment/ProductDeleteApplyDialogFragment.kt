package com.HKSHOPU.hk.ui.main.productSeller.fragment

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshShopInfo
import com.HKSHOPU.hk.component.EventRefreshShopList
import com.HKSHOPU.hk.component.EventdeleverFragmentAfterUpdateStatus
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProductDeleteApplyDialogFragment(var product_id: String, var keyword: String): DialogFragment(), View.OnClickListener {


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

    lateinit var progressBar_product_delete_apply: ProgressBar
    lateinit var iv_loading_background_product_delete_apply: ImageView

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

        progressBar_product_delete_apply = v.findViewById<ProgressBar>(R.id.progressBar_product_delete_apply)
        iv_loading_background_product_delete_apply = v.findViewById<ImageView>(R.id.iv_loading_background_product_delete_apply)
        progressBar_product_delete_apply.visibility = View.GONE
        iv_loading_background_product_delete_apply.visibility = View.GONE

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

    private fun Do_ProductDelete(product_id: String) {

        progressBar_product_delete_apply.visibility = View.VISIBLE
        iv_loading_background_product_delete_apply.visibility = View.VISIBLE

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
                            RxBus.getInstance().post(EventdeleverFragmentAfterUpdateStatus())
                            RxBus.getInstance().post(EventRefreshShopInfo())
                            RxBus.getInstance().post(EventRefreshShopList())

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


        progressBar_product_delete_apply.visibility = View.GONE
        iv_loading_background_product_delete_apply.visibility = View.GONE
    }

}