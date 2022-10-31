package com.github.daraja.model.response


import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("errorCode")
    val errorCode: String,
    @field:SerializedName("errorMessage")
    val errorMessage: String,
    @field:SerializedName("requestId")
    val requestId: String
)