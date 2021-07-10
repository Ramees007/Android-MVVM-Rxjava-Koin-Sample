package com.ramees.currency_convertor.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.ramees.currency_convertor.R
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Disposable.addTo(cd: CompositeDisposable) {
    cd.add(this)
}

fun Throwable.errorForUser(context: Context): String {
    return when (this) {
        is HttpException -> {
            context.getString(R.string.server_error)
        }
        is IOException -> {
            context.getString(R.string.network_error)
        }
        else -> context.getString(R.string.unexpected_error)
    }
}

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}
