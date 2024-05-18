package com.raihanardila.bapps.core.data.remote.response.bfeed

import com.google.gson.annotations.SerializedName
import com.raihanardila.bapps.core.data.model.StoriesBModel

data class ResponseBFeed(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStory: List<StoriesBModel>,
    @SerializedName("message")
    val message: String
)
