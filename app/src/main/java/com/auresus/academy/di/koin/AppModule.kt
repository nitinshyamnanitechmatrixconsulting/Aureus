package com.auresus.academy.di.koin

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.auresus.academy.AureusApplication
import com.auresus.academy.model.local.preference.PreferenceConstants
import com.auresus.academy.model.local.preference.PreferenceHelper
import com.auresus.academy.model.remote.ApiConstant
import com.auresus.academy.model.remote.ApiServices
import com.auresus.academy.model.remote.ReflectionUtil
import com.auresus.academy.utils.validations.ValidationHelper
import com.auresus.academy.utils.validations.Validator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/** Created by Sahil Bharti on 5/4/19.
 *
 *  Copyright (c) 2019 Sahil Inc. All rights reserved.
*/
val appModule = module {

    /** PROVIDE GSON SINGLETON */
    single<Gson> {
        val builder = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        builder.setLenient().create()
    }

    /** PROVIDE RETROFIT SINGLETON */
    single {
        var loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        var httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(loggingInterceptor)
        httpClient.connectTimeout(ApiConstant.API_TIME_OUT, TimeUnit.MILLISECONDS)
        httpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
        var okHttpClient = httpClient.build()

        Retrofit.Builder()
            .baseUrl(ApiConstant.PROD_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().disableHtmlEscaping().create()))
            .build()
    }

    /*** Provide API Service Singleton*/
    single {
        (get<Retrofit>()).create<ApiServices>(ApiServices::class.java)
    }

    /** Provide Preference Helper singleton Object
     * private val preferenceHelper : PreferenceHelper by inject() */

    single {
        PreferenceHelper(
            (androidApplication() as AureusApplication).getSharedPreferences(
                PreferenceConstants.PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
        )
    }

    /** Provide validation Helper classses for validation
     * you can inject it in KoinComponent class  using
     * private val validationHelper : ValidationHelper by inject() */
    single {
        ValidationHelper()
    }

    single {
        Validator(get())
    }

    /**Provide ReflectionUtil class Singleton object
     * you can use it any KoinComponent class  below is declaration
     *  private val reflectionUtil: ReflectionUtil by inject()*/
    single { ReflectionUtil(get()) }


}