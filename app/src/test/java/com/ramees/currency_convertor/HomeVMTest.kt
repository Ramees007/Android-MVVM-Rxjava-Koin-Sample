package com.ramees.currency_convertor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ramees.currency_convertor.data.*
import com.ramees.currency_convertor.domain.ConverterUsecase
import com.ramees.currency_convertor.domain.GetCurrencyUsecase
import com.ramees.currency_convertor.ui.HomeVM
import com.ramees.currency_convertor.util.Resource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.IOException
import java.math.BigDecimal
import java.util.*

/**
 * Test for [HomeVM]
 * */
class HomeVMTest {
    @get:Rule
    public val rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun enterAmountTestWithSuccessReponse() {
        val vm = HomeVM(
            ConverterUsecase(fakeRateRepoWithData), GetCurrencyUsecase(fakeCurrencyRepoWithData),
            fakePref, Schedulers.trampoline(), Schedulers.trampoline()
        )
        vm.enterAmount("2")
        vm.selectCurrency("AED")
        Assert.assertEquals(
            vm.convertedLiveDate.getOrAwaitValue(), Resource.Success(
                listOf(
                    ConvertedItem(Currency.getInstance("INR"), "40.00"),
                    ConvertedItem(Currency.getInstance("USD"), "0.50"),
                    ConvertedItem(Currency.getInstance("EUR"), "0.40"),
                    ConvertedItem(Currency.getInstance("AED"), "2.00")
                )
            )
        )

    }

    @Test
    fun enterAmountWithErrorResponse() {
        val vm = HomeVM(
            ConverterUsecase(fakeRateRepoWithError), GetCurrencyUsecase(fakeCurrencyRepoWithData),
            fakePref, Schedulers.trampoline(), Schedulers.trampoline()
        )
        vm.enterAmount("2")
        vm.selectCurrency("AED")
        val data = vm.convertedLiveDate.getOrAwaitValue()
        assert(data is Resource.Error && data.error is IOException && data.error.message == "ioError")

    }

    @Test
    fun getCurrencyTestWithData(){
        val vm = HomeVM(
            ConverterUsecase(fakeRateRepoWithData), GetCurrencyUsecase(fakeCurrencyRepoWithData),
            fakePref, Schedulers.trampoline(), Schedulers.trampoline()
        )
        vm.getCurrencies()
        Assert.assertEquals(vm.currencyLD.getOrAwaitValue(),
            Resource.Success(listOf(CurrencyItem("INR","India")))
        )
    }

    @Test
    fun getCurrencyTestWithError(){
        val vm = HomeVM(
            ConverterUsecase(fakeRateRepoWithData), GetCurrencyUsecase(fakeCurrencyRepoWithError),
            fakePref, Schedulers.trampoline(), Schedulers.trampoline()
        )
        vm.getCurrencies()
        val data = vm.currencyLD.getOrAwaitValue()
        assert(data is Resource.Error && data.error is IOException && data.error.message == "Network Error")

    }


    val fakeCurrencyRepoWithData = object : CurrencyRepo {
        override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
            return Observable.just(Resource.Success(listOf(CurrencyItem("INR", "India"))))
        }
    }

    val fakeCurrencyRepoWithError = object : CurrencyRepo {
        override fun getCurrencies(): Observable<Resource<List<CurrencyItem>>> {
            return Observable.just(Resource.Error(IOException("Network Error")))
        }
    }

    val fakeRateRepoWithData = object : RateRepo {
        override fun getConversionRates(): Observable<Resource<List<ConversionItem>>> {
            return Observable.just(
                Resource.Success(
                    listOf(
                        ConversionItem("INR", BigDecimal(80)),
                        ConversionItem("USD", BigDecimal(1)),
                        ConversionItem("EUR", BigDecimal(0.8)),
                        ConversionItem("AED", BigDecimal(4)),
                    )
                )
            )
        }
    }

    val fakeRateRepoWithError = object : RateRepo {
        override fun getConversionRates(): Observable<Resource<List<ConversionItem>>> {
            return Observable.just(Resource.Error(IOException("ioError")))
        }
    }

    val fakePref = object : Pref {
        override var selectedCurrencyCode: String = "USD"
            get() = "USD"
            set(value) {
                field = value
            }
    }



}