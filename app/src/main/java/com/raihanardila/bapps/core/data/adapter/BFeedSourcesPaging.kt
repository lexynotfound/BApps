package com.raihanardila.bapps.core.data.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.core.data.remote.client.ApiService

class BFeedSourcesPaging(
    private val apiService: ApiService,
) : PagingSource<Int, StoriesBModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoriesBModel> {
        return try {
            val currentPage = params.key ?: 1
            val response = apiService.getStories(page = currentPage, size = params.loadSize)

            val data = response.body()?.listStory ?: emptyList()

            LoadResult.Page(
                data = data,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (data.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoriesBModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
