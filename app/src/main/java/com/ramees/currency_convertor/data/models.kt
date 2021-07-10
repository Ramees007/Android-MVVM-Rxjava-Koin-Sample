package com.ramees.currency_convertor.data

import com.ramees.currency_convertor.util.DATA_REFRESH_TIME_SEC
import com.squareup.moshi.JsonClass
import java.math.BigDecimal
import java.util.*

@JsonClass(generateAdapter = true)
data class CurrencyResponseItem(val success: Boolean, val currencies: Map<String, String>)

@JsonClass(generateAdapter = true)
data class ConversionResponseItem(
    val success: Boolean,
    val timestamp: Long,
    val source: String,
    val quotes: Map<String, BigDecimal>
)

@JsonClass(generateAdapter = true)
data class CurrencyItem(val currencyCode: String, val country: String)

@JsonClass(generateAdapter = true)
data class ConversionData(
    val timeStamp: Long,
    val conversions: List<ConversionItem>
) {
    companion object {
        private const val INVALID_TIME: Long = -1
        fun createInvalid() = ConversionData(
            INVALID_TIME,
            emptyList()
        )
    }


    fun isValid() = conversions.isNotEmpty() &&
            timeStamp > (System.currentTimeMillis()/1000 - DATA_REFRESH_TIME_SEC)
}

@JsonClass(generateAdapter = true)
data class ConversionItem(val destCurrencyCode: String, val factorWrtUSD: BigDecimal)

data class ConvertedItem(val currency: Currency,  val amount: String)





