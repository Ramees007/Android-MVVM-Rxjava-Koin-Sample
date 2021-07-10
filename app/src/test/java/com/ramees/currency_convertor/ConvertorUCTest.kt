package com.ramees.currency_convertor

import com.ramees.currency_convertor.data.ConversionItem
import com.ramees.currency_convertor.data.ConvertedItem
import com.ramees.currency_convertor.data.RateRepo
import com.ramees.currency_convertor.domain.ConverterUsecase
import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.After
import org.junit.Test

import java.io.IOException
import java.math.BigDecimal
import java.util.*

/**
 * Test for [ConverterUsecase]
 * */
class ConvertorUCTest {

    lateinit var disposable: Disposable



    @Test
    fun testConversionHandled() {

        val fakeRepo = object : RateRepo {
            override fun getConversionRates(): Observable<Resource<List<ConversionItem>>> {
                return Observable.just(Resource.Success(conversionList))
            }
        }

        val cuc = ConverterUsecase(fakeRepo)

        val testObs: TestObserver<Resource<List<ConvertedItem>>> = cuc.getConvertedList(
            "JPY",
            BigDecimal(25)
        ).test()
        disposable = testObs


        testObs.assertValues(Resource.Success(convertedList))
    }

    @Test
    fun testErrorPropogatedCorrectly(){

        val fakeRepo = object : RateRepo {
            override fun getConversionRates(): Observable<Resource<List<ConversionItem>>> {
                return Observable.just(Resource.Error(IOException("networkError")))
            }
        }

        val cuc = ConverterUsecase(fakeRepo)

        val testObs: TestObserver<Resource<List<ConvertedItem>>> = cuc.getConvertedList(
            "USD",
            BigDecimal(10)
        ).test()
        disposable = testObs
        testObs.assertValue{
            it is Resource.Error && it.error.message == "networkError"
        }

    }


    val conversionList = listOf(
        ConversionItem("BDT", BigDecimal(84.787655)),
        ConversionItem("USD", BigDecimal(1)),
        ConversionItem("KRW", BigDecimal(1112.689596)),
        ConversionItem("ZWL", BigDecimal(322.000305)),
        ConversionItem("JOD", BigDecimal(0.7088996)),
        ConversionItem("JPY", BigDecimal(109.374992))
    )

    val convertedList = listOf(
        ConvertedItem(
            Currency.getInstance("BDT"),
            BigDecimal(19.38003).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
        ),
        ConvertedItem(
            Currency.getInstance("USD"),
            BigDecimal(0.22857).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
        ),
        ConvertedItem(
            Currency.getInstance("KRW"),
            BigDecimal(254.32906).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
        ),
        ConvertedItem(
            Currency.getInstance("ZWL"),
            BigDecimal(73.60007).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
        ),
        ConvertedItem(
            Currency.getInstance("JOD"),
            BigDecimal(0.16205).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
        ),
        ConvertedItem(
            Currency.getInstance("JPY"),
            BigDecimal(25).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
        )


    )


    @After
    fun tearDown(){
        disposable.dispose()
    }
}