package com.raihanardila.bapps.core.data.local.repository

import com.raihanardila.bapps.core.data.model.AuthModel
import com.raihanardila.bapps.core.data.remote.client.ApiService
import com.raihanardila.bapps.core.data.remote.response.auth.LoginResponse
import com.raihanardila.bapps.core.data.remote.response.auth.RegisterResponse
import retrofit2.Call
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService
) {

    fun login(authModel: AuthModel): Call<LoginResponse> {
        return apiService.login(authModel)
    }

    fun register(authModel: AuthModel): Call<RegisterResponse> {
        return apiService.register(authModel)
    }
}