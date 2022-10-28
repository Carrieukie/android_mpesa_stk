package com.karis.daraja.model.response

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
