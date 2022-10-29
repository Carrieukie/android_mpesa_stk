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
package com.github.daraja.model.requests

import com.google.gson.annotations.SerializedName

data class STKPushRequest(

    @field:SerializedName("BusinessShortCode")
    val businessShortCode: String,

    @field:SerializedName("Password")
    val password: String,

    @field:SerializedName("Timestamp")
    val timestamp: String,

    @field:SerializedName("TransactionType")
    val transactionType: String,

    @field:SerializedName("Amount")
    val amount: String,

    @field:SerializedName("PartyA")
    val partyA: String,

    @field:SerializedName("PartyB")
    val partyB: String,

    @field:SerializedName("PhoneNumber")
    val phoneNumber: String,

    @field:SerializedName("CallBackURL")
    val callBackURL: String,

    @field:SerializedName("AccountReference")
    val accountReference: String,

    @field:SerializedName("TransactionDesc")
    val transactionDesc: String

)
