package com.karis.daraja.services

import com.karis.daraja.model.requests.STKPushRequest
import com.karis.daraja.model.response.AccessTokenResponse
import com.karis.daraja.model.response.STKPushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface STKPushService {

    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun sendPush(
        @Body stkPushRequest: STKPushRequest,
        @Header("Authorization") auth: String
    ): STKPushResponse

    @GET("oauth/v1/generate?grant_type=client_credentials")
    suspend fun accessToken(
        @Header("Authorization") auth: String
    ): AccessTokenResponse
}
