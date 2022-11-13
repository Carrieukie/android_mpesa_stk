package com.github.daraja.utils

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(errorMessage: String? = null, throwable: Throwable? = null, data: T? = null) :
        Resource<T>(data, throwable, errorMessage)
}
