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
import com.github.daraja.services.DarajaService
import com.github.daraja.utils.Resource
import com.github.daraja.utils.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class DarajaDriver(private val consumerKey: String, private val consumerSecret: String) :
    IDriverTwo {

    // an observable state holder observable of Daraja state
    // private to this class
    private val _darajaState = MutableStateFlow(DarajaState())

    // an immutable state holder observable of Daraja state publicly exposed to its collectors
    val darajaState: StateFlow<DarajaState> = _darajaState

    // Dispatcher for offloading blocking IO tasks to a shared pool of threads.
    private val ioDispatcher = Dispatchers.IO

    // Instance of daraja service
    private val darajaService = getInstance()

    // bearer token used to make api calls to Daraja
    private var bearerToken: String? = null

    /**
     * Authenticates with daraja backend and initiates an stk push on customer device
     * @param stkPushRequest: an object that contains parameters required to initiate an stk push
     * @see <a href="https://developer.safaricom.co.ke/APIs/MpesaExpressSimulate">MpesaExpressSimulate</a>
     *
     * @return unit
     */
    override fun performStkPush(stkPushRequest: STKPushRequest) {
        intent {
            // authenticate if bearer token  is null
            if (bearerToken == null) {
                getAccessToken().collect { accessTokenResponse ->
                    when (accessTokenResponse) {
                        is Resource.Error -> {
                            reduce {
                                _darajaState.value.copy(
                                    message = accessTokenResponse.errorMessage
                                        ?: accessTokenResponse.error?.message
                                        ?: "Something went wrong",
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Loading -> {
                            reduce {
                                _darajaState.value.copy(
                                    isLoading = true,
                                    message = "Authenticating daraja"
                                )
                            }
                        }
                        is Resource.Success -> {
                            reduce {
                                _darajaState.value.copy(
                                    message = "Successfully Authenticated",
                                    isLoading = false
                                )
                            }
                            bearerToken = accessTokenResponse.data?.accessToken
                        }
                    }
                }
            }
            bearerToken?.let {
                sendOtp(
                    token = it,
                    stkPushRequest = stkPushRequest
                ).collect { sendOtpResponse ->
                    when (sendOtpResponse) {
                        is Resource.Error -> {
                            reduce {
                                _darajaState.value.copy(
                                    message = sendOtpResponse.errorMessage
                                        ?: sendOtpResponse.error?.localizedMessage
                                        ?: "Makosha!",
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Loading -> {
                            reduce {
                                _darajaState.value.copy(
                                    message = "Sending Otp request",
                                    isLoading = true
                                )
                            }
                        }
                        is Resource.Success -> {
                            reduce {
                                _darajaState.value.copy(
                                    message = sendOtpResponse.data?.customerMessage ?: "Request sent successfully",
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Combines consumer key and secret to get a BASIC token which is used to make an api call
     * that returns to us a bearer token that gives you a time bound access token to call allowed APIs.
     * @see <a href="https://developer.safaricom.co.ke/APIs/Authorization">Safaricom Daraja</a>
     *
     * @return a flow builder Resource<out AccessTokenResponse>
     */
    private suspend fun getAccessToken() = flow {
        emit(Resource.Loading(null))

        val response = safeApiCall(ioDispatcher) {
            val keys = "$consumerKey:$consumerSecret"
            val authToken = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
            val response = darajaService.accessToken(authToken)
            response
        }

        emit(response)
    }.flowOn(ioDispatcher)

    /**
     * Makes an api call that initiates an STK push on behalf of the customer
     * @param token : the bearer token that is got as a result of the first authorization api call
     * @param stkPushRequest: an object that contains parameters required to initiate an stk push
     * @see <a href="https://developer.safaricom.co.ke/APIs/MpesaExpressSimulate">MpesaExpressSimulate</a>
     *
     */
    private suspend fun sendOtp(token: String, stkPushRequest: STKPushRequest) = flow {
        emit(Resource.Loading(null))

        val response = safeApiCall(ioDispatcher) {
            val response = darajaService.sendPush(stkPushRequest, "Bearer $token")
            response
        }

        emit(response)
    }.flowOn(ioDispatcher)

    /**
     * Returns an instance of Daraja Service which is a retrofit implementation of the API endpoints
     * defined by the service interface.
     *
     * @return DarajaService
     */
    private fun getInstance(): DarajaService {
        val loggingInterceptor = provideLoggingInterceptor()
        val okHttpClient = provideOkHttpClient(httpLoggingInterceptor = loggingInterceptor)
        val retrofit = provideRetrofit(okHttpClient = okHttpClient)
        return provideMpesaService(retrofit)
    }

    /**
     * Runs suspend function in a single couroutine context to prevent race conditions
     * @param transform: Any suspend function that returns Unit
     *
     *
     * @return Unit
     */
    private fun intent(transform: suspend () -> Unit) {
        CoroutineScope(ioDispatcher).launch(SINGLE_THREAD) {
            transform()
        }
    }

    /**
     * This reducer reduces state in a single thread context to avoid race conditions
     * on the State when more than one threads are changing it
     */
    private suspend fun reduce(reducer: DarajaState.() -> DarajaState) {
        withContext(SINGLE_THREAD) {
            _darajaState.value = _darajaState.value.reducer()
        }
    }

    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        private val SINGLE_THREAD = newSingleThreadContext("mvi")
    }
}
