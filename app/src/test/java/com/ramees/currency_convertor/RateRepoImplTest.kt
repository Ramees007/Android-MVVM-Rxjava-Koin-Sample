package com.ramees.currency_convertor

import com.ramees.currency_convertor.data.*
import com.ramees.currency_convertor.util.DATA_REFRESH_TIME_SEC
import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.After
import org.junit.Test
import java.io.IOException
import java.math.BigDecimal

/**
 * Test for [RateRepoImpl]
 * */
class RateRepoImplTest {
    lateinit var disposable: Disposable


    @Test
    fun localStorageValidCacheHandledCorrectly() {
        val repo = RateRepoImpl(fakeLocalDataStoreValidCache,fakeRemoteDataSourceWithData)

        val testObs: TestObserver<Resource<List<ConversionItem>>> = repo.getConversionRates().test()
        disposable = testObs

        testObs.assertValues(Resource.Success(validConvDataCached.conversions) as  Resource<List<ConversionItem>>)

    }

    @Test
    fun localStorageinvalidCacheDiscarded() {
        val repo = RateRepoImpl(fakeLocalDataStoreInvalidCache,fakeRemoteDataSourceWithData)

        val testObs: TestObserver<Resource<List<ConversionItem>>> = repo.getConversionRates().test()
        disposable = testObs

        testObs.assertValues(Resource.Success(convDataNw.conversions) as  Resource<List<ConversionItem>>)

    }

    @Test
    fun localStorageWithoutCacheCallsApi() {
        val repo = RateRepoImpl(fakeLocalDataStoreNoCache,fakeRemoteDataSourceWithData)

        val testObs: TestObserver<Resource<List<ConversionItem>>> = repo.getConversionRates().test()
        disposable = testObs

        testObs.assertValues(Resource.Success(convDataNw.conversions) as  Resource<List<ConversionItem>>)

    }

    @Test
    fun remoteDataErrorHandled() {
        val repo = RateRepoImpl(fakeLocalDataStoreNoCache,fakeRemoteDataSourceWithError)

        val testObs: TestObserver<Resource<List<ConversionItem>>> = repo.getConversionRates().test()
        disposable = testObs

        testObs.assertValue {
            it is Resource.Error && it.error is IOException && it.error.message == "networkError"
        }

    }

    @After
    fun tearDown(){
        disposable.dispose()
    }




    val fakeRemoteDataSourceWithData = object : RemoteDataSource {
        override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
            return Observable.just(Resource.Success(emptyList()))
        }

        override fun getConversions(): Observable<Resource<ConversionData>> {
            return Observable.just(Resource.Success(convDataNw))
        }
    }

    val fakeRemoteDataSourceWithError = object : RemoteDataSource {
        override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
            return Observable.just(Resource.Error(IOException("networkError")))
        }

        override fun getConversions(): Observable<Resource<ConversionData>> {
            return Observable.just(Resource.Error(IOException("networkError")))
        }
    }



    val fakeLocalDataStoreNoCache: LocalDataStore = object : LocalDataStore {
        override fun getCurrencies(): Observable<List<CurrencyItem>> {
            return Observable.just(emptyList())
        }

        override fun getConversions(): Observable<ConversionData> {
            return Observable.just(ConversionData.createInvalid())
        }

        override fun saveCurrencies(currencies: List<CurrencyItem>) {

        }

        override fun saveConversionData(conversionData: ConversionData) {

        }
    }

    val fakeLocalDataStoreValidCache: LocalDataStore = object : LocalDataStore {
        override fun getCurrencies(): Observable<List<CurrencyItem>> {
            return Observable.just(emptyList())
        }

        override fun getConversions(): Observable<ConversionData> {
            return Observable.just(validConvDataCached)
        }

        override fun saveCurrencies(currencies: List<CurrencyItem>) {

        }

        override fun saveConversionData(conversionData: ConversionData) {

        }
    }

    val fakeLocalDataStoreInvalidCache: LocalDataStore = object : LocalDataStore {
        override fun getCurrencies(): Observable<List<CurrencyItem>> {
            return Observable.just(emptyList())
        }

        override fun getConversions(): Observable<ConversionData> {
            return Observable.just(invalidConvDataCached)
        }

        override fun saveCurrencies(currencies: List<CurrencyItem>) {

        }

        override fun saveConversionData(conversionData: ConversionData) {

        }
    }


    val validConvDataCached = ConversionData(
        (System.currentTimeMillis() / 1000) - 10000,
        listOf(
            ConversionItem("AED", BigDecimal(4)),
            ConversionItem("INR", BigDecimal(83)),
            ConversionItem("EUR", BigDecimal(0.75)),
            ConversionItem("USD", BigDecimal(1))
        )
    )

    val invalidConvDataCached = ConversionData(
        (System.currentTimeMillis() / 1000) - (DATA_REFRESH_TIME_SEC +1000),
        listOf(
            ConversionItem("AED", BigDecimal(4)),
            ConversionItem("INR", BigDecimal(83)),
            ConversionItem("EUR", BigDecimal(0.75)),
            ConversionItem("USD", BigDecimal(1))
        )
    )

    val convDataNw = ConversionData(
        (System.currentTimeMillis() / 1000) - 10000,
        listOf(
            ConversionItem("AED", BigDecimal(4)),
            ConversionItem("INR", BigDecimal(80)),
            ConversionItem("EUR", BigDecimal(0.75)),
            ConversionItem("USD", BigDecimal(1))
        )
    )


}