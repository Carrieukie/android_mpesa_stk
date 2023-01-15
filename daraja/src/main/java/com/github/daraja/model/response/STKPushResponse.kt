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
package com.github.daraja.model.response

import com.google.gson.annotations.SerializedName

data class STKPushResponse(

    @field:SerializedName("MerchantRequestID")
    val merchantRequestID: String? = null,

    @field:SerializedName("ResponseCode")
    val responseCode: String? = null,

    @field:SerializedName("CustomerMessage")
    val customerMessage: String? = null,

    @field:SerializedName("CheckoutRequestID")
    val checkoutRequestID: String? = null,

    @field:SerializedName("ResponseDescription")
    val responseDescription: String? = null,

    @SerializedName("requestId")
    var requestId: String? = null,

    @SerializedName("errorCode")
    var code: String? = null,

    @SerializedName("errorMessage")
    var message: String? = null
)
