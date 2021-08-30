package com.HKSHOPU.hk.ui.main.seller.order.fragment

import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R

import com.otaliastudios.zoom.ZoomImageView


class DeliveryNotifyApplyAttachmentZoomDialogFragment(var drawable: Drawable): DialogFragment(), View.OnClickListener {

    var attachmentDrawable: Drawable = drawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_deliverynotifyapplyattatchmentzoom, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.custom_unit_transparent
            ), 0
        )
        dialog!!.window!!.setBackgroundDrawable(inset)
        val btn_dismiss = v.findViewById<ImageView>(R.id.btn_dismiss)
        val iv_attachment = v.findViewById<ZoomImageView>(R.id.iv_attachment)

        btn_dismiss.setOnClickListener(this)
        iv_attachment.setImageDrawable(attachmentDrawable)

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
            R.id.btn_dismiss -> {
                dismiss()
            }
        }
    }
}