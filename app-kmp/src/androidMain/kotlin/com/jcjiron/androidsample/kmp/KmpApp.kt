package com.jcjiron.androidsample.kmp

import android.app.Application
import com.jcjiron.androidsample.core.AppConfig
import com.jcjiron.androidsample.kmp.di.startAppKoin
import org.koin.android.ext.koin.androidContext

class KmpApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startAppKoin(AppConfig(baseUrl = BuildConfig.BASE_URL, isDebug = BuildConfig.DEBUG)) {
            androidContext(this@KmpApp)
        }
    }
}
