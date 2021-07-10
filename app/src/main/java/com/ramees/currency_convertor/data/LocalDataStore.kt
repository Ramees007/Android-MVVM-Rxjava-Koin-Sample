package com.ramees.currency_convertor.data

import com.ramees.currency_convertor.util.FileUtil
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.rxjava3.core.Observable
import java.lang.reflect.Type

interface LocalDataStore {
    fun getCurrencies(): Observable<List<CurrencyItem>>
    fun getConversions(): Observable<ConversionData>
    fun saveCurrencies(currencies: List<CurrencyItem>)
    fun saveConversionData(conversionData: ConversionData)
}

class FileDataStore(moshi: Moshi, private val fileUtil: FileUtil) : LocalDataStore {

    companion object {
        const val CURRENCIES_SAVED_FILE = "currency_cache.txt"
        const val CONVERSION_SAVED_FILE = "conversions_cache.txt"
    }

    private val currencyListType: Type = Types.newParameterizedType(
        List::class.java,
        CurrencyItem::class.java
    )
    private val currencyListAdapter: JsonAdapter<List<CurrencyItem>> = moshi.adapter(currencyListType)

    private val conversionDataAdapter: JsonAdapter<ConversionData> = moshi.adapter(ConversionData::class.java)

    override fun getCurrencies(): Observable<List<CurrencyItem>> {
        return Observable.fromCallable {
            val readStr = fileUtil.readFromInternalFile(CURRENCIES_SAVED_FILE)
            if (readStr.isBlank()) return@fromCallable emptyList()

            return@fromCallable currencyListAdapter.fromJson(readStr)?:emptyList()
        }.onErrorResumeNext {
            Observable.just(emptyList())
        }
    }

    override fun getConversions(): Observable<ConversionData> {
        return Observable.fromCallable {
            val readStr = fileUtil.readFromInternalFile(CONVERSION_SAVED_FILE)
            if (readStr.isBlank()) return@fromCallable ConversionData.createInvalid()
            return@fromCallable conversionDataAdapter.fromJson(readStr)?:ConversionData.createInvalid()
        }.onErrorResumeNext {
            Observable.just(ConversionData.createInvalid())
        }

    }

    override fun saveCurrencies(currencies: List<CurrencyItem>) {
        val currencyStr = currencyListAdapter.nullSafe().toJson(currencies) ?: return
        fileUtil.replaceInInternalFile(currencyStr, CURRENCIES_SAVED_FILE)
    }

    override fun saveConversionData(conversionData: ConversionData) {
        val conversionDataStr = conversionDataAdapter.nullSafe().toJson(conversionData) ?: return
        fileUtil.replaceInInternalFile(conversionDataStr, CONVERSION_SAVED_FILE)
    }


}