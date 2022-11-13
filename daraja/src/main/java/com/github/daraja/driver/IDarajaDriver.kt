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

import com.github.daraja.model.requests.STKPushRequest
import com.github.daraja.model.response.AccessTokenResponse
import com.github.daraja.model.response.STKPushResponse
import com.github.daraja.services.STKPushService
import com.github.daraja.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IDarajaDriver {
    fun performStkPush(stkPushRequest: STKPushRequest): Flow<Resource<DarajaStkPushState>>

    suspend fun getAccessToken(firstSTKPushService: STKPushService): Resource<AccessTokenResponse>

    suspend fun sendOtp(
        token: String,
        firstSTKPushService: STKPushService,
        stkPushRequest: STKPushRequest
    ): Resource<STKPushResponse>
}
