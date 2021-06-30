package com.HKSHOPU.hk.ui.main.shopProfile.fragment

import android.app.Activity
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
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventFinishLoadingShopmenu
import com.HKSHOPU.hk.component.EventStartLoadingShopmenu
import com.HKSHOPU.hk.data.bean.ShopListBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class StoreDeleteApplyDialogFragment(var shop_Id: String ): DialogFragment(), View.OnClickListener {


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
    var shop_id = shop_Id
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_storedeleteapply, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_forward).setOnClickListener(this)

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_forward -> {

                RxBus.getInstance().post(EventStartLoadingShopmenu())
                view.isEnabled = false
                Do_ShopDelete(shop_id, view)

            }
        }
    }

    private fun Do_ShopDelete(shop_id: String, view: View) {
        Log.d("ShopListFragment", "送資料 shop_id：" + shop_id)
        var url = ApiConstants.API_HOST + "/shop/" + shop_id + "/delete/"
        Log.d("ShopListFragment", "送資料URL URL：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
           
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopListFragment", "返回資料 resStr：" + resStr)
                    Log.d("ShopListFragment", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        Log.d("Do_ShopDelete", "ret_val: ${ret_val.toString()}")
                        activity!!.runOnUiThread {
                            RxBus.getInstance().post(EventFinishLoadingShopmenu())
                            view.isEnabled = true
                        }

                        activity!!.runOnUiThread {

                            StoreDeleteDialogFragment().show(
                                fragmentManager!!,
                                "MyCustomFragment"
                            )
                            dismiss()
                        }


                    } else if (status == -2){
                        val data = json.getJSONObject("data")
                        val order_count = data.getInt("order_count")

                        Log.d("Do_ShopDelete", "ret_val: ${ret_val.toString()}")
                        activity!!.runOnUiThread {
                            RxBus.getInstance().post(EventFinishLoadingShopmenu())
                            view.isEnabled = true
                        }

                        activity!!.runOnUiThread {
                            StoreDeleteDenyDialogFragment(order_count).show(
                                fragmentManager!!,
                                "MyCustomFragment"
                            )
                            dismiss()
                        }

                    }else{

                        Log.d("Do_ShopDelete", "ret_val: ${ret_val.toString()}")
                        activity!!.runOnUiThread {
                            RxBus.getInstance().post(EventFinishLoadingShopmenu())
                            view.isEnabled = true
                        }

                        activity!!.runOnUiThread {
                            Toast.makeText(activity!!, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()

                } catch (e: JSONException) {
                    Log.d("Do_ShopDelete", "JSONException: ${e.toString()}")
                    activity!!.runOnUiThread {
                        RxBus.getInstance().post(EventFinishLoadingShopmenu())
                        view.isEnabled = true
                    }

                } catch (e: IOException) {
                    e.printStackTrace()

                    Log.d("Do_ShopDelete", "IOException: ${e.toString()}")
                    activity!!.runOnUiThread {
                        RxBus.getInstance().post(EventFinishLoadingShopmenu())
                        view.isEnabled = true
                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_ShopDelete", "ErrorResponse: ${ErrorResponse.toString()}")
                activity!!.runOnUiThread {
                    RxBus.getInstance().post(EventFinishLoadingShopmenu())
                    view.isEnabled = true
                }
            }
        })
        web.Delete_Data(url)
    }

}