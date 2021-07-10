package com.ramees.currency_convertor.data

import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable

interface RemoteDataSource {
    fun getCurrencies(): Observable<Resource<List<CurrencyItem>>>
    fun getConversions(): Observable<Resource<ConversionData>>
}

class RetrofitDataSource(private val service: RemoteService) : RemoteDataSource {

    companion object {
        const val ACCESS_KEY = "73b962bed17d852f84a293c1bf54b0f1"
    }

    override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
        return service.getCurrencies(ACCESS_KEY).retry(3).map { response ->
            if (response == null || !response.success) throw IllegalStateException("API returned error")
            Resource.Success(response.currencies.entries.map {
                CurrencyItem(it.key, it.value)
            }) as Resource<List<CurrencyItem>>
        }.onErrorResumeNext {
            Observable.just(Resource.Error(it))
        }
    }

    override fun getConversions(): Observable<Resource<ConversionData>> {
        return service.getConversions(ACCESS_KEY).retry(3).map { response ->
            if (response == null || !response.success) throw IllegalStateException("API returned error")
            Resource.Success(ConversionData(
                response.timestamp,
                response.quotes.map { ConversionItem(it.key.takeLast(3), it.value) })) as Resource<ConversionData>
        }.onErrorResumeNext {
            Observable.just(Resource.Error(it))
        }
    }
}