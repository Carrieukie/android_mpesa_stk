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
package com.github.daraja.driver

import android.util.Base64
import com.github.daraja.di.DependenciesModule.provideLoggingInterceptor
import com.github.daraja.di.DependenciesModule.provideMpesaService
import com.github.daraja.di.DependenciesModule.provideOkHttpClient
import com.github.daraja.di.DependenciesModule.provideRetrofit
import com.github.daraja.model.requests.STKPushRequest
import com.github.daraja.model.response.AccessTokenResponse
import com.github.daraja.model.response.STKPushResponse
import com.github.daraja.services.DarajaService
import com.github.daraja.utils.Resource
import com.github.daraja.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DarajaDriver(private val consumerKey: String, private val consumerSecret: String) :
    IDarajaDriver {

    private val ioDispatcher = Dispatchers.IO

    override fun performStkPush(stkPushRequest: STKPushRequest) = flow {
        val firstSTKPushService = getInstance()
        val darajaStkPushState = DarajaStkPushState()

        emit(
            Resource.Loading(
                darajaStkPushState
            )
        )

        when (val accessTokenResult = getAccessToken(firstSTKPushService)) {
            is Resource.Error -> {
                emit(
                    Resource.Error(
                        errorMessage = accessTokenResult.errorMessage,
                        throwable = accessTokenResult.error
                    )
                )
            }
            is Resource.Success -> {
                emit(
                    Resource.Loading(
                        accessTokenResult.data?.let {
                            darajaStkPushState.copy(
                                accessToken = it.accessToken
                            )
                        }
                    )
                )

                when (
                    val sendOtpResult =
                        accessTokenResult.data?.let {
                            sendOtp(
                                firstDarajaService = firstSTKPushService,
                                stkPushRequest = stkPushRequest,
                                token = it.accessToken
                            )
                        }
                ) {
                    is Resource.Error -> {
                        emit(
                            Resource.Error(
                                errorMessage = sendOtpResult.errorMessage,
                                throwable = sendOtpResult.error
                            )
                        )
                    }
                    is Resource.Success -> {
                        emit(
                            Resource.Success(
                                darajaStkPushState.copy(otpResult = sendOtpResult.data)
                            )
                        )
                    }
                    else -> {}
                }
            }
            else -> {}
        }
    }.flowOn(ioDispatcher)

    override suspend fun getAccessToken(firstDarajaService: DarajaService): Resource<AccessTokenResponse> {
        return safeApiCall(ioDispatcher) {
            val keys = "$consumerKey:$consumerSecret"
            val authToken = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)

            val response = firstDarajaService.accessToken(authToken)
            response
        }
    }

    override suspend fun sendOtp(
        token: String,
        firstDarajaService: DarajaService,
        stkPushRequest: STKPushRequest
    ): Resource<STKPushResponse> {
        return safeApiCall(ioDispatcher) {
            val response = firstDarajaService.sendPush(stkPushRequest, "Bearer $token")
            response
        }
    }

    private fun getInstance(): DarajaService {
        val loggingInterceptor = provideLoggingInterceptor()
        val okHttpClient = provideOkHttpClient(httpLoggingInterceptor = loggingInterceptor)
        val retrofit = provideRetrofit(okHttpClient = okHttpClient)
        return provideMpesaService(retrofit)
    }
}
