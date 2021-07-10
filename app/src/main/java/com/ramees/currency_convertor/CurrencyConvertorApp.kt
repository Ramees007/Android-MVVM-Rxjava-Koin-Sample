package com.ramees.currency_convertor

import android.app.Application
import android.util.Log
import com.ramees.currency_convertor.di.module
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyConvertorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CurrencyConvertorApp)
            modules(listOf(module))
        }

        RxJavaPlugins.setErrorHandler {
            Log.e(CurrencyConvertorApp::class.simpleName,it.message?:"RxJavaPlugins undelivered exception - no message")
        }
    }
}