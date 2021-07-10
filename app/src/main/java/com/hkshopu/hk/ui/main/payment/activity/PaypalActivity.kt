package com.HKSHOPU.hk.ui.main.payment.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.*
import com.paypal.android.sdk.payments.*
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
import org.json.JSONException
import java.math.BigDecimal

//import kotlinx.android.synthetic.main.activity_main.*

class PaypalActivity : BaseActivity() {

    companion object {
        private val TAG = "paymentExample"
        /**
         * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
         * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
         * from https://developer.paypal.com
         * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
         * without communicating to PayPal's servers.
         */
        private val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK

        // note that these credentials will differ between live & sandbox environments.
        private val CONFIG_CLIENT_ID = "credentials from developer.paypal.com"
//        private val CONFIG_CLIENT_ID ="AdBCLHocOrbf94O5WAIkLVi3OAjuwWseJfwNtX6uHSm96tV5gqB_e1g4uBvfvS6TlQeAs9mjT90b-Ok3"
//        private val CONFIG_CLIENT_ID_LIVE= "ATQnt-r5Gm45zeZaEkjaDG9qG3jpM2IDrzNqhXUzNJc1-0USna3hg5gt6lm73M5wyvbTQCG_1iD1KoZv"
        private val CONFIG_CLIENT_SECRET ="EFg3J2mb7i__gh7xOjd836JiS_WwXOb6No0mJiSc-bt_SyjIE0resRHWkHDYVuWdsNnWGJN7R3M7_7qX"
        private val REQUEST_CODE_PAYMENT = 1


        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

    }
    private lateinit var binding: ActivityPaypalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaypalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()
        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)
    }
    fun onBuyPressed(pressed: View) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        val thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE)

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        val intent = Intent(this@PaypalActivity, PaymentActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy)

        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }
    private fun getThingToBuy(paymentIntent: String): PayPalPayment {
        return PayPalPayment(
            BigDecimal("1.75"), "HKD", "sample item",
            paymentIntent)
    }
    private fun initVM() {

    }
    private fun initClick() {

        binding.payPalButton.setup(

            createOrder = CreateOrder { createOrderActions ->
                val order = Order(
                    intent = OrderIntent.CAPTURE,
                    appContext = AppContext(
                        userAction = UserAction.PAY_NOW
                    ),
                    purchaseUnitList = listOf(
                        PurchaseUnit(
                            amount = Amount(
                                currencyCode = CurrencyCode.HKD,
                                value = "10.00"
                            )
                        )
                    )
                )

                createOrderActions.create(order)

            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.d("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.d("OnCancel", "Buyer canceled the PayPal experience.")
            },
            onError = OnError { errorInfo ->
                Log.d("OnError", "Error: $errorInfo")
            }

        )


        binding.titleBack.setOnClickListener {

            finish()
        }

    }
    protected fun displayResultText(result: String) {
//        var resultView: TextView = findViewById(R.id.txtResult)
//        resultView.text = "Result : " + result
        Toast.makeText(
            applicationContext,
            result, Toast.LENGTH_LONG).show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4))
                        Log.i(TAG, confirm.payment.toJSONObject().toString(4))
                        /**
                         * TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        displayResultText("PaymentConfirmation info received from PayPal")


                    } catch (e: JSONException) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    TAG,
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
            }
        }
    }

    public override fun onDestroy() {
        // Stop service when done
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }


}