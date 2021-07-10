package com.ramees.currency_convertor.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramees.currency_convertor.data.ConvertedItem
import com.ramees.currency_convertor.data.CurrencyItem
import com.ramees.currency_convertor.data.Pref
import com.ramees.currency_convertor.domain.ConverterUsecase
import com.ramees.currency_convertor.domain.GetCurrencyUsecase
import com.ramees.currency_convertor.util.Resource
import com.ramees.currency_convertor.util.addTo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit


class HomeVM(
    private val convertorUsecase: ConverterUsecase,
    private val getCurrencyUsecase: GetCurrencyUsecase,
    private val pref: Pref,
    private val ioScheduler: Scheduler,
    private val mainScheduler: Scheduler
) : ViewModel() {
    private var amount: BigDecimal = BigDecimal.ZERO

    //TODO Needs to be moved to background thread as it is IO
    private var currency: String = pref.selectedCurrencyCode

    private val _convertedLD = MutableLiveData<Resource<List<ConvertedItem>>>()
    val convertedLiveDate = _convertedLD

    private val _currencyLD = MutableLiveData<Resource<List<CurrencyItem>>>()
    val currencyLD = _currencyLD

    private val _currencySelectionLD = MutableLiveData<Currency>()
    val currencySelectionLD = _currencySelectionLD

    private val compositeDisposable = CompositeDisposable()

    private var amountObservable: Subject<BigDecimal> = BehaviorSubject.createDefault(amount)
    private var currencyObservable: Subject<String> = BehaviorSubject.createDefault(currency)

    init {
        _currencySelectionLD.value = Currency.getInstance(currency)
        setUpObservable()
    }


    fun enterAmount(amt: String) {
        amount = amt.toBigDecimalOrNull() ?: BigDecimal.ZERO
        amountObservable.onNext(amount)
    }

    fun selectCurrency(currencyCode: String) {
        if (currencyCode == currency) return
        pref.selectedCurrencyCode = currencyCode
        currency = currencyCode
        _currencySelectionLD.value = Currency.getInstance(currency)
        currencyObservable.onNext(currencyCode)
    }

    fun getCurrencies() {

        getCurrencyUsecase.getCurrencies().subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .doOnSubscribe {
                _currencyLD.value = Resource.Loading
            }
            .subscribe({
                _currencyLD.value = it as Resource<List<CurrencyItem>>
            }, {
                _currencyLD.value = Resource.Error(it)
            })
            .addTo(compositeDisposable)
    }


    private fun setUpObservable() {
        val delayedAmountSubject = amountObservable.debounce(
            300,
            TimeUnit.MILLISECONDS, ioScheduler
        ).doOnNext {
            _convertedLD.postValue(Resource.Loading)
        }

        currencyObservable.doOnNext {
            _convertedLD.postValue(Resource.Loading)
        }

        Observable.combineLatest(currencyObservable,
            delayedAmountSubject, { t1, t2 ->
                t1 to t2
            }).flatMap { pair ->
            if (pair.second == BigDecimal.ZERO) {
                Observable.just(Resource.Success(emptyList()))
            } else {
                convertorUsecase.getConvertedList(pair.first, pair.second).onErrorResumeNext {
                    Observable.just(Resource.Error(it))
                }
            }
        }.subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe({
                _convertedLD.value = it
            }, {
                _convertedLD.value = Resource.Error(it)
            }).addTo(compositeDisposable)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}




