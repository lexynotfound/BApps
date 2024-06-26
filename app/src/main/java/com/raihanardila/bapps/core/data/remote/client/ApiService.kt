package com.raihanardila.bapps.core.data.remote.client

import com.raihanardila.bapps.core.data.model.AuthModel
import com.raihanardila.bapps.core.data.remote.response.auth.LoginResponse
import com.raihanardila.bapps.core.data.remote.response.auth.RegisterResponse
import com.raihanardila.bapps.core.data.remote.response.bfeed.ResponseBFeed
import com.raihanardila.bapps.core.data.remote.response.bfeed.ResponseBMaps
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("v1/stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): Response<ResponseBFeed>

    @GET("v1/stories")
    suspend fun getStoriesMap(
        @Query("location") location: Int = 1
    ): Response<ResponseBFeed>

    @POST("v1/login")
    fun login(
        @Body user: AuthModel
    ): Call<LoginResponse>


    @POST("v1/register")
    fun register(
        @Body info: AuthModel
    ): Call<RegisterResponse>

    @Multipart
    @POST("v1/stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Response<ResponseBMaps>

}