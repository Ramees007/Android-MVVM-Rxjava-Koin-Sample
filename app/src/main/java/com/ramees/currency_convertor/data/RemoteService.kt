package com.ramees.currency_convertor.data

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteService {
    @GET("list")
    fun getCurrencies(@Query("access_key") accessKey : String): Observable<CurrencyResponseItem>

    @GET("live")
    fun getConversions(@Query("access_key") accessKey : String): Observable<ConversionResponseItem>
}