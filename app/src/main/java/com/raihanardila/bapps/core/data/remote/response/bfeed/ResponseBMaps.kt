package com.raihanardila.bapps.core.data.remote.response.bfeed

import com.google.gson.annotations.SerializedName

data class ResponseBMaps(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)
