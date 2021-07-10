package com.ramees.currency_convertor.domain

import com.ramees.currency_convertor.data.CurrencyItem
import com.ramees.currency_convertor.data.CurrencyRepo
import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable

class GetCurrencyUsecase(private val currencyRepo: CurrencyRepo) {

    fun getCurrencies() : Observable<Resource<List<CurrencyItem>>>{
        return currencyRepo.getCurrencies()
    }
}