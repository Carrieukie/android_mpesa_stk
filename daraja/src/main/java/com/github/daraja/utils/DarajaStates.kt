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
package com.github.daraja.utils

sealed class DarajaStates<R>(
    val data: R?
) {
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
