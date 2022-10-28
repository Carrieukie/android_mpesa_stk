package com.karis.daraja.model.response

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(

    @field:SerializedName("access_token")
    var accessToken: String,

    @field:SerializedName("expires_in")
    val expiresIn: String
)
