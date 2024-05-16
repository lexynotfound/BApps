package com.raihanardila.bapps.core.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("userId")
    val userId: String
)