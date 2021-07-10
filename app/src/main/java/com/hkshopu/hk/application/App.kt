package com.HKSHOPU.hk.application

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.HKSHOPU.hk.BuildConfig
import com.mallotec.reb.localeplugin.LocaleConstant
import com.mallotec.reb.localeplugin.LocalePlugin
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction
import com.tencent.mmkv.MMKV

class App : Application(), LifecycleOwner {

    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalePlugin.init(this, LocaleConstant.RECREATE_CURRENT_ACTIVITY)
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        initMMKV()
        val config = CheckoutConfig(
            application = this,
            clientId = "AdBCLHocOrbf94O5WAIkLVi3OAjuwWseJfwNtX6uHSm96tV5gqB_e1g4uBvfvS6TlQeAs9mjT90b-Ok3",
            environment = Environment.SANDBOX,
            returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay",
            currencyCode = CurrencyCode.HKD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
    }

    companion object {
        lateinit var instance: App
            private set
    }

    val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
    private fun initMMKV() {
        MMKV.initialize(this)
    }
}