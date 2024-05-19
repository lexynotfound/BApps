package com.raihanardila.bapps.core.data.local.repository

import com.raihanardila.bapps.core.data.remote.client.ApiService

class BMainRepository(
    val apiService: ApiService
) {

    suspend fun getStoriesMap(location: Int) = apiService.getStoriesMap(location)

    suspend fun getStories(page: Int, size: Int) = apiService.getStories(page, size)
}
