package com.github.daraja.drivertwo

import android.util.Base64
import com.github.daraja.di.DependenciesModule.provideLoggingInterceptor
import com.github.daraja.di.DependenciesModule.provideMpesaService
import com.github.daraja.di.DependenciesModule.provideOkHttpClient
import com.github.daraja.di.DependenciesModule.provideRetrofit
import com.github.daraja.model.requests.STKPushRequest
import com.github.daraja.services.STKPushService
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

class DarajaDriverTwoPointO(private val consumerKey: String, private val consumerSecret: String) :
    IDriverTwoPoint0 {

    private val _darajaState = MutableStateFlow(DarajaState())
    val darajaState: StateFlow<DarajaState> = _darajaState

    private val ioDispatcher = Dispatchers.IO
    private val firstSTKPushService = getInstance()
    private var bearerToken: String? = null

    override fun performStkPush(stkPushRequest: STKPushRequest) {
        runCatching {
            intent {
                if (bearerToken == null) {
                    getAccessToken().collect { accessTokenResponse ->
                        when (accessTokenResponse) {
                            is Resource.Error -> {
                                reduce {
                                    _darajaState.value.copy(
                                        isLoading = false,
                                        message = accessTokenResponse.errorMessage
                                            ?: accessTokenResponse.error?.message
                                            ?: "Something went wrong"
                                    )
                                }
                            }
                            is Resource.Loading -> {
                                reduce {
                                    _darajaState.value.copy(
                                        isLoading = false,
                                        message = "Authenticating"
                                    )
                                }
                            }
                            is Resource.Success -> {
                                reduce {
                                    _darajaState.value.copy(
                                        isLoading = false,
                                        message = "Successfully Authenticated"
                                    )
                                }
                                bearerToken = accessTokenResponse.data?.accessToken ?: "makosha!!"
                            }
                        }
                    }
                }

                sendOtp(
                    token = bearerToken!!,
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
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Success -> {
                            reduce {
                                _darajaState.value.copy(
                                    message = "Otp sent successfully"
                                )
                            }
                        }
                    }
                }
            }
        }.onFailure { exception: Throwable ->
            _darajaState.value = _darajaState.value.copy(
                message = exception.localizedMessage ?: exception.message ?: "Makosha!",
                isLoading = false
            )
        }
    }

    private suspend fun getAccessToken() = flow {
        emit(Resource.Loading(null))

        val response = safeApiCall(ioDispatcher) {
            val keys = "$consumerKey:$consumerSecret"
            val authToken = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
            val response = firstSTKPushService.accessToken(authToken)
            response
        }

        emit(response)
    }.flowOn(ioDispatcher)

    private suspend fun sendOtp(
        token: String,
        stkPushRequest: STKPushRequest
    ) = flow {
        emit(Resource.Loading(null))

        val response = safeApiCall(ioDispatcher) {
            val response = firstSTKPushService.sendPush(stkPushRequest, "Bearer $token")
            response
        }

        emit(response)
    }.flowOn(ioDispatcher)

    private fun getInstance(): STKPushService {
        val loggingInterceptor = provideLoggingInterceptor()
        val okHttpClient = provideOkHttpClient(httpLoggingInterceptor = loggingInterceptor)
        val retrofit = provideRetrofit(okHttpClient = okHttpClient)
        return provideMpesaService(retrofit)
    }

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
