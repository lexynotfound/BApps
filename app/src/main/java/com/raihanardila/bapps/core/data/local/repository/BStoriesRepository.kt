package com.raihanardila.bapps.core.data.local.repository

import com.raihanardila.bapps.core.data.remote.client.ApiService
import com.raihanardila.bapps.core.data.remote.response.bfeed.ResponseBMaps
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class BStoriesRepository(
    private val apiService: ApiService
) {
    suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Response<ResponseBMaps> {
        return apiService.uploadImage(file, description, lat, lon)
    }
}