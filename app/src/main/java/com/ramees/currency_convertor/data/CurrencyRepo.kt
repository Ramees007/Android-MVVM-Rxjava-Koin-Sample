package com.ramees.currency_convertor.data

import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable

interface CurrencyRepo {
    fun getCurrencies(): Observable<Resource<List<CurrencyItem>>>
}


class CurrencyRepoImpl(
    private val localDataStore: LocalDataStore,
    private val remoteDataSource: RemoteDataSource
) : CurrencyRepo {
    override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
        return localDataStore.getCurrencies().flatMap {
            if (it.isEmpty()) {
                remoteDataSource.getCurrencies().doAfterNext { result ->
                   if (result is Resource.Success) localDataStore.saveCurrencies(result.data)
                }
            } else {
                Observable.just(Resource.Success(it))
            }
        }
    }
}

