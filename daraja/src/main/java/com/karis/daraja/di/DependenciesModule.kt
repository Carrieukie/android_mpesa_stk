package com.karis.daraja.di

import com.karis.daraja.BuildConfig
import com.karis.daraja.services.STKPushService
import com.karis.daraja.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DependenciesModule {

    internal fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(getHttpLoggingLevel())
    }

    internal fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(Constants.CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
        return builder.build()
    }

    internal fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build()
    }

    private fun getHttpLoggingLevel() = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }

    internal fun provideMpesaService(retrofit: Retrofit): STKPushService {
        return retrofit.create(STKPushService::class.java)
    }
}
