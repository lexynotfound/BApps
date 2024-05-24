package com.raihanardila.bapps.core.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.raihanardila.bapps.core.data.adapter.BFeedSourcesPaging
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.core.data.remote.client.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class BMainRepository(
    private val apiService: ApiService
) {

    fun getStoriesMap(token: String): LiveData<List<StoriesBModel>> = liveData(Dispatchers.IO) {
        try {
            val response = apiService.getStoriesMap()
            if (response.isSuccessful) {
                val stories = response.body()?.listStory
                stories?.let {
                    emit(it)
                }
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getStories(pageSize: Int, prefetchDistance: Int): Flow<PagingData<StoriesBModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BFeedSourcesPaging(apiService, initialPage = 1) }
        ).flow
    }
}
