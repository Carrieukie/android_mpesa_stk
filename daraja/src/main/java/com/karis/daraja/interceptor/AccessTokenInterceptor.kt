package com.karis.daraja.interceptor

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.Throws

class AccessTokenInterceptor(private val consumerKey: String, private val consumerSecret: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val keys = "$consumerKey:$consumerSecret"
        val request = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
            )
            .build()
        return chain.proceed(request)
    }
}
