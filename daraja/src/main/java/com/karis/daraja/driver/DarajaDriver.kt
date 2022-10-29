/*
 * Copyright 2022 Eric Kariuki Kimani.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.karis.daraja.driver

import android.util.Base64
import com.karis.daraja.di.DependenciesModule.provideLoggingInterceptor
import com.karis.daraja.di.DependenciesModule.provideMpesaService
import com.karis.daraja.di.DependenciesModule.provideOkHttpClient
import com.karis.daraja.di.DependenciesModule.provideRetrofit
import com.karis.daraja.model.requests.STKPushRequest
import com.karis.daraja.services.STKPushService
import com.karis.daraja.utils.DarajaStates
import com.karis.daraja.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DarajaDriver(private val consumerKey: String, private val consumerSecret: String) : IDarajaDriver {

    private lateinit var token: String

    override fun performStkPush(stkPushRequest: STKPushRequest) = flow {
        val firstSTKPushService = getInstance()

        emit(DarajaStates.LoadingToken(null))

        try {
            val keys = "$consumerKey:$consumerSecret"
            val authToken = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)

            val response = firstSTKPushService.accessToken(authToken)
            emit(DarajaStates.TokenFetchedSuccess(Resource.Success(response)))
            token = response.accessToken
        } catch (e: Exception) {
            emit(DarajaStates.TokenFetchedError(Resource.Error(throwable = e, data = null)))
        }

        emit(DarajaStates.SendingOTPLoading(null))

        try {
            val response = firstSTKPushService.sendPush(stkPushRequest, "Bearer $token")
            emit(DarajaStates.SendingOTPSuccess(Resource.Success(data = response)))
        } catch (e: Exception) {
            emit(DarajaStates.SendingOTPError(Resource.Error(e)))
        }
    }.flowOn(Dispatchers.IO)

    private fun getInstance(): STKPushService {
        val loggingInterceptor = provideLoggingInterceptor()
        val okHttpClient = provideOkHttpClient(httpLoggingInterceptor = loggingInterceptor)
        val retrofit = provideRetrofit(okHttpClient = okHttpClient)
        return provideMpesaService(retrofit)
    }
}
