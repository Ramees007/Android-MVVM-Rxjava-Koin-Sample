package com.ramees.currency_convertor.di

import com.ramees.currency_convertor.data.*
import com.ramees.currency_convertor.domain.ConverterUsecase
import com.ramees.currency_convertor.domain.GetCurrencyUsecase
import com.ramees.currency_convertor.ui.HomeVM
import com.ramees.currency_convertor.util.BigDecimalAdapter
import com.ramees.currency_convertor.util.FileUtil
import com.squareup.moshi.Moshi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


val module = module {


    single<LocalDataStore> { FileDataStore(get(), get()) }


    single<Pref> { PrefStore(get()) }


    single<RemoteService> { currencyLayerApi(get()) }

    single { provideOkHttpClient() }

    single { provideRetrofit(get(), get()) }

    single { provideMoshi() }


    viewModel { HomeVM(get(), get(), get(),get(named(IO_SCHED_KEY)),get(named(MAIN_SCHED_KEY))) }


    factory { FileUtil(get()) }

    factory<CurrencyRepo> { CurrencyRepoImpl(get(), get()) }

    factory<RateRepo> { RateRepoImpl(get(), get()) }

    factory<RemoteDataSource> { RetrofitDataSource(get()) }

    factory { ConverterUsecase(get()) }

    factory { GetCurrencyUsecase(get()) }


    single<Scheduler>(named(IO_SCHED_KEY)) {
        Schedulers.io()
    }

    single<Scheduler>(named(MAIN_SCHED_KEY)) {
        AndroidSchedulers.mainThread()
    }


}


const val IO_SCHED_KEY = "io"
const val MAIN_SCHED_KEY = "main"


private fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
    return Retrofit.Builder()
        .baseUrl("http://api.currencylayer.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
}

private fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .build()
}

private fun provideMoshi() = Moshi.Builder().add(BigDecimalAdapter).build()

private fun currencyLayerApi(retrofit: Retrofit) = retrofit.create(RemoteService::class.java)