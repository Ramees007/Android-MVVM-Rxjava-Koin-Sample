package com.ramees.currency_convertor

import com.ramees.currency_convertor.data.CurrencyItem
import com.ramees.currency_convertor.data.CurrencyRepo
import com.ramees.currency_convertor.domain.GetCurrencyUsecase
import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.After
import org.junit.Test
import java.io.IOException

/**
 * Test for [GetCurrencyUsecase]
 * */
class GetCurrencyUCTest {

    lateinit var disposable: Disposable

    val currencies = listOf<CurrencyItem>(CurrencyItem("INR","India"),
        CurrencyItem("USD","United States of America"))




    @Test
    fun testCurrencyAcceptedCorrectly(){

        val fakeCurRepo = object : CurrencyRepo{
            override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
                return Observable.just(Resource.Success(currencies))
            }
        }

        val gc = GetCurrencyUsecase(fakeCurRepo)

        val testObs : TestObserver<Resource<List<CurrencyItem>>> = gc.getCurrencies().test()
        disposable = testObs

        testObs.assertValues(Resource.Success(currencies))

    }

    @Test
    fun testCurrencyErrorPropogatedCorrectly(){

        val fakeCurRepo = object : CurrencyRepo{
            override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
                return Observable.just(Resource.Error(IOException("networkError")))
            }
        }

        val gc = GetCurrencyUsecase(fakeCurRepo)

        val testObs : TestObserver<Resource<List<CurrencyItem>>> = gc.getCurrencies().test()
        disposable = testObs

        testObs.assertValue{
            it is Resource.Error && it.error is IOException && it.error.message == "networkError"
        }

    }

    @After
    fun tearDown(){
        disposable.dispose()
    }
}