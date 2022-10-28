package com.karis.daraja.utils

sealed class DarajaStates<R>(
    data: R?
) {
    class InitialState<T>(data: T?) : DarajaStates<T>(data)
    class LoadingToken<T>(data: T?) : DarajaStates<T>(data)
    class TokenFetchedSuccess<T>(data: T) : DarajaStates<T>(data)
    class TokenFetchedError<T>(data: T) : DarajaStates<T>(data)
    class SendingOTPLoading<T>(data: T?) : DarajaStates<T>(data)
    class SendingOTPSuccess<T>(data: T) : DarajaStates<T>(data)
    class SendingOTPError<T>(data: T) : DarajaStates<T>(data)
}

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Resource<T>(data, throwable)
}
