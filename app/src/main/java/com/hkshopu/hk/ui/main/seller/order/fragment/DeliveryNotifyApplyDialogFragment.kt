package com.HKSHOPU.hk.ui.main.seller.order.fragment

import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener

import com.paypal.pyplcheckout.sca.runOnUiThread
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class DeliveryNotifyApplyDialogFragment(var order_id:String, var shipment_number:String, var delivery_date:String, var attachment:String, var file: File, var drawable: Drawable, var attachment_selected: Boolean): DialogFragment(), View.OnClickListener {

    var shop_order_id:String = order_id
    var shipmentNumber:String = shipment_number
    var deliveryDate:String = delivery_date
    var attachmentName:String = attachment
    var attachmentDoc:File = file
    var attachmentDrawable: Drawable = drawable
    var attachmentSelected: Boolean = attachment_selected
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_deliverynotifyapply, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.dialog_fragment_background
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)
        val tv_shipment_number = v.findViewById<TextView>(R.id.tv_shipment_number)
        tv_shipment_number.setText(shipment_number)
        val tv_delivery_date = v.findViewById<TextView>(R.id.tv_delivery_date)
        tv_delivery_date.setText(deliveryDate)
        var tv_attachment_title = v.findViewById<TextView>(R.id.tv_attachment_title)
        val tv_attachment = v.findViewById<TextView>(R.id.tv_attachment)
        tv_attachment.setText(attachmentName)
        tv_attachment.setOnClickListener(this)

        if(attachmentSelected){
            tv_attachment_title.visibility = View.VISIBLE
            tv_attachment.visibility = View.VISIBLE
        }else{
            tv_attachment_title.visibility = View.GONE
            tv_attachment.visibility = View.GONE
        }

        v.findViewById<ImageView>(R.id.btn_forward).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
//        v.findViewById<ImageView>(R.id.iv_attachment).setImageDrawable(attachmentDrawable)

//        Imagin.with(imageWrapper, v.findViewById<ImageView>(R.id.iv_attachment))
//            // enable double tap to zoom functionality
//            .enableDoubleTapToZoom()
//            // enable pinch to zoom functionality
//            .enablePinchToZoom()
//            // add an event listener when the user does a single tap
//            .enableSingleTap(object : SingleTapHandler.OnSingleTapListener {
//                override fun onSingleTap() {
//                    Toast.makeText(v.findViewById<ImageView>(R.id.iv_attachment).context, picture.name, Toast.LENGTH_SHORT).show()
//                }
//            })
//            // this allows us to do an action when the user swipes the ImageView vertically and/or horizontally
//            .enableScroll(
//                allowScrollOutOfBoundsHorizontally = false,
//                allowScrollOutOfBoundsVertically = true,
//                scrollDistanceToCloseInPx = distanceToClose
//            ) {
//                onSwipedToCloseListener?.onSwipeToClose()
//            }

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> {
                dismiss()
            }
            R.id.btn_forward -> {
                Do_confirmOrderShipmentDetails(shop_order_id, shipment_number, deliveryDate, attachmentDoc!!)
            }

            R.id.tv_attachment ->{
                var manager = requireActivity().supportFragmentManager
                DeliveryNotifyApplyAttachmentZoomDialogFragment(drawable).show(
                    manager,
                    "MyCustomFragment"
                )
            }
        }
    }


    private fun Do_confirmOrderShipmentDetails(
        shop_order_id : String,
        waybill_number: String,
        estimated_deliver_at: String,
        delivery_pic : File
    ) {
        Log.d(
            "Do_confirmOrderShipmentDetails",
            "shop_order_id: ${shop_order_id} ; waybill_number: ${waybill_number} ; estimated_deliver_at: ${estimated_deliver_at} ; delivery_pic: ${delivery_pic}"
        )
        val url = ApiConstants.API_HOST + "shop_order/confirmOrderShipmentDetail/${shop_order_id}/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_confirmOrderShipmentDetails", "返回資料 resStr：" + resStr)
                    Log.d("Do_confirmOrderShipmentDetails", "返回資料 ret_val：" + ret_val)
                    runOnUiThread {
                        Toast.makeText(
                            requireActivity(),
                            ret_val.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (status == 0) {

                        runOnUiThread {
                            var manager = requireActivity().supportFragmentManager
                            DeliveryNotifyApplySuccessDialogFragment().show(
                                manager,
                                "MyCustomFragment"
                            )
                        }


//                        val intent = Intent(
//                            requireActivity(),
//                            FpsPayAuditActivity::class.java
//                        )
//                        startActivity(intent)
                    } else {
                    }
                } catch (e: JSONException) {
                    Log.d("Do_confirmOrderShipmentDetails", "JSONException：" + e.toString())
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(
                            requireActivity(),
                            "網路異常",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_confirmOrderShipmentDetails", "IOException：" + e.toString())
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(
                            requireActivity(),
                            "網路異常",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d(
                    "Do_confirmOrderShipmentDetails",
                    "onErrorResponse：" + ErrorResponse.toString()
                )
                com.paypal.pyplcheckout.sca.runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        web.Do_confirmOrderShipmentDetails(
            url,
            shop_order_id,
            waybill_number,
            estimated_deliver_at,
            delivery_pic
        )
    }

}