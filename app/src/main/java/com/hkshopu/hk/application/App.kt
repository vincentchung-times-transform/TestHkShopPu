package com.hkshopu.hk.application

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.mallotec.reb.localeplugin.LocaleConstant
import com.mallotec.reb.localeplugin.LocalePlugin
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.hkshopu.hk.BuildConfig
import com.hkshopu.hk.R
import com.tencent.mmkv.MMKV

class App : Application(), LifecycleOwner {

    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalePlugin.init(this, LocaleConstant.RECREATE_CURRENT_ACTIVITY)
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        initMMKV()
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