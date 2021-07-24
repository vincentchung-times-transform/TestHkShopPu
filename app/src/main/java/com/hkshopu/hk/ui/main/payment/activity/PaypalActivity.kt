package com.HKSHOPU.hk.ui.main.payment.activity


import android.os.Bundle
import android.util.Log

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.*
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PaymentButtonEligibilityStatus


//import kotlinx.android.synthetic.main.activity_main.*

class PaypalActivity : BaseActivity() {
    private val tag = javaClass.simpleName

    private lateinit var binding: ActivityPaypalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaypalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.payPalButton.onEligibilityStatusChanged = { buttonEligibilityStatus: PaymentButtonEligibilityStatus ->
            Log.v(tag, "OnEligibilityStatusChanged")
            Log.d(tag, "Button eligibility status: $buttonEligibilityStatus")
        }
        initVM()
        initClick()
    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.payPalButton.setup(
            createOrder = CreateOrder { createOrderActions ->
                Log.v(tag, "CreateOrder")
                createOrderActions.create(
                    Order.Builder()
                        .appContext(
                            AppContext(
                               userAction = UserAction.PAY_NOW

                            )
                        )
                        .intent(OrderIntent.CAPTURE)
                        .purchaseUnitList(
                            listOf(
                                PurchaseUnit.Builder()
                                    .amount(
                                        Amount.Builder()
                                            .value("0.1")
                                            .currencyCode(CurrencyCode.HKD)
                                            .build()
                                    )
                                    .build()
                            )
                        )
                        .build()
                        .also { Log.d(tag, "Order: $it") }
                )

            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.d(tag, "CaptureOrderResult: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.d(tag, "Buyer canceled the PayPal experience.")
            },
            onError = OnError { errorInfo ->
                Log.d(tag, "Error: $errorInfo")
            }

        )


        binding.titleBack.setOnClickListener {

            finish()
        }

    }

}