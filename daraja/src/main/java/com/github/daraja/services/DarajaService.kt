/*
 * Copyright 2022 Android M-pesa STK
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
package com.github.daraja.services

import com.github.daraja.model.requests.STKPushRequest
import com.github.daraja.model.response.AccessTokenResponse
import com.github.daraja.model.response.STKPushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DarajaService {

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
