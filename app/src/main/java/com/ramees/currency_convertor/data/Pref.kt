package com.ramees.currency_convertor.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Pref {
    var selectedCurrencyCode: String
}

class PrefStore(private val context: Context) : Pref {

    private val prefs : SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    override var selectedCurrencyCode by StringPreference(
        prefs, SELECTED_CUR_SYMBOL,
        Currency.getInstance(Locale.getDefault()).currencyCode
    )


    companion object {
        const val PREFS_NAME = "currency_convertor_pref"
        const val SELECTED_CUR_SYMBOL = "sel_cur_symbol"
    }
}

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.getString(name, defaultValue) ?: ""
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        preferences.edit().putString(name, value).apply()
    }
}