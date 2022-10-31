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
import com.github.daraja.services.STKPushService
import com.github.daraja.utils.DarajaStkPushState
import com.github.daraja.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DarajaDriver(private val consumerKey: String, private val consumerSecret: String) :
    IDarajaDriver {

    override fun performStkPush(stkPushRequest: STKPushRequest) = flow {
        val firstSTKPushService = getInstance()
        val darajaStkPushState = DarajaStkPushState()

        emit(
            Resource.Loading(
                darajaStkPushState
            )
        )

        when (val getAccessTokenResult = getAccessToken(firstSTKPushService)) {
            is Resource.Error -> {
                emit(
                    Resource.Error(
                        data = darajaStkPushState.copy(
                            error = getAccessTokenResult.error
                        ),
                        throwable = getAccessTokenResult.error!!
                    )
                )
            }
            is Resource.Success -> {
                emit(
                    Resource.Loading(
                        darajaStkPushState.copy(
                            accessToken = getAccessTokenResult.data!!.accessToken
                        )
                    )
                )

                when (val sendOtpResult =
                    sendOtp(
                        firstSTKPushService = firstSTKPushService,
                        stkPushRequest = stkPushRequest,
                        token = getAccessTokenResult.data.accessToken
                    )
                ) {
                    is Resource.Error -> {
                        emit(
                            Resource.Error(
                                data = darajaStkPushState.copy(
                                    error = sendOtpResult.error
                                ),
                                throwable = sendOtpResult.error!!
                            )
                        )
                    }
                    is Resource.Success -> {
                        emit(
                            Resource.Success(
                                darajaStkPushState.copy(
                                    otpResult = sendOtpResult.data
                                )
                            )
                        )
                    }
                    else -> {}
                }
            }
            else -> {}
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun getAccessToken(firstSTKPushService: STKPushService): Resource<AccessTokenResponse> {
        return try {
            val keys = "$consumerKey:$consumerSecret"
            val authToken = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)

            val response = firstSTKPushService.accessToken(authToken)
            Resource.Success(response)
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun sendOtp(
        token: String,
        firstSTKPushService: STKPushService,
        stkPushRequest: STKPushRequest
    ): Resource<STKPushResponse> {
        return try {
            val response = firstSTKPushService.sendPush(stkPushRequest, "Bearer $token")
            Resource.Success(response)
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    private fun getInstance(): STKPushService {
        val loggingInterceptor = provideLoggingInterceptor()
        val okHttpClient = provideOkHttpClient(httpLoggingInterceptor = loggingInterceptor)
        val retrofit = provideRetrofit(okHttpClient = okHttpClient)
        return provideMpesaService(retrofit)
    }
}
