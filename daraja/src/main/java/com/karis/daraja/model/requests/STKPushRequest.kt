package com.karis.daraja.model.requests

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
