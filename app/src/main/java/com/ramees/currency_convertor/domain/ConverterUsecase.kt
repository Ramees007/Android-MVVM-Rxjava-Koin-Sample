package com.ramees.currency_convertor.domain

import com.ramees.currency_convertor.data.ConvertedItem
import com.ramees.currency_convertor.data.RateRepo
import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


class ConverterUsecase(
    private val rateRepo: RateRepo
) {

    fun getConvertedList(srcCurrency: String, amount: BigDecimal): Observable<Resource<List<ConvertedItem>>> {

        return rateRepo.getConversionRates().map { result ->
            if (result is Resource.Error)  Resource.Error(result.error) as Resource<List<ConvertedItem>>
            else {
                val conversionList = (result as Resource.Success).data
                val srcConvFactorWrtUSD =
                    conversionList.find { it.destCurrencyCode == srcCurrency }?.factorWrtUSD
                        ?: return@map Resource.Success(emptyList<ConvertedItem>())

                Resource.Success(conversionList.map { conversion ->
                    ConvertedItem(
                        Currency.getInstance(conversion.destCurrencyCode),
                        getRoundedAmount(
                            amount,
                            srcConvFactorWrtUSD,
                            conversion.factorWrtUSD
                        ).toString()
                    )
                })
                }
            }
        }

    

    private fun getRoundedAmount(
        inputAmt: BigDecimal,
        srcFactor: BigDecimal,
        destFactor: BigDecimal
    ): BigDecimal {
        return (inputAmt * destFactor.divide(srcFactor, 20, RoundingMode.HALF_UP))
            .setScale(2, BigDecimal.ROUND_HALF_EVEN)
    }


}