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

import android.util.Base64
import com.github.daraja.model.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

val timestamp: String
    get() = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

fun sanitizePhoneNumber(phone: String): String {
    if (phone == "") {
        return ""
    }
    if (phone.length < 11 && phone.startsWith("0")) {
        return phone.replaceFirst("^0".toRegex(), "254")
    }
    return if (phone.length == 13 && phone.startsWith("+")) {
        phone.replaceFirst("^+".toRegex(), "")
    } else phone
}

fun getPassword(businessShortCode: String, passkey: String, timestamp: String): String {
    val str = businessShortCode + passkey + timestamp
    // encode the password to Base64
    return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
}

internal suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): Resource<T> {
    return withContext(dispatcher) {
        try {
            Resource.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            when (throwable) {
                is IOException -> Resource.Error()
                is HttpException -> {
                    val stringErrorBody = errorBodyAsString(throwable)
                    if (stringErrorBody != null) {
                        val errorResponse = convertStringErrorResponseToJsonObject(stringErrorBody)
                        Resource.Error(
                            errorMessage = errorResponse?.errorMessage,
                            throwable = throwable
                        )
                    } else {
                        Resource.Error(null, null)
                    }
                }
                else -> {
                    Resource.Error(null, null)
                }
            }
        }
    }
}

private fun convertStringErrorResponseToJsonObject(jsonString: String): ErrorResponse? {
    val gson = Gson()
    return gson.fromJson(jsonString, ErrorResponse::class.java)
}

private fun errorBodyAsString(throwable: HttpException): String? {
    val reader = throwable.response()?.errorBody()?.charStream()
    return reader?.use { it.readText() }
}
