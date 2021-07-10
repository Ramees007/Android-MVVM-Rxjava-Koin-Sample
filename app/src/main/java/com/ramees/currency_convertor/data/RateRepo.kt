package com.ramees.currency_convertor.data

import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable


interface RateRepo {
    fun getConversionRates(): Observable<Resource<List<ConversionItem>>>
}

class RateRepoImpl(
    private val localDataStore: LocalDataStore,
    private val remoteDataSource: RemoteDataSource
) : RateRepo {
    override fun getConversionRates(): Observable<Resource<List<ConversionItem>>> {
        return localDataStore.getConversions().flatMap { localConvData ->
            if (localConvData.isValid()) {
                Observable.just(Resource.Success(localConvData.conversions))
            } else {
                remoteDataSource.getConversions()
                    .doAfterNext { result ->
                        if (result is Resource.Success) localDataStore.saveConversionData(result.data)
                    }.map {
                        if (it is Resource.Success) Resource.Success(it.data.conversions)
                        else Resource.Error((it as Resource.Error).error)
                    }
            }
        }

    }

}


