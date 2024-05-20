package com.raihanardila.bapps.core.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raihanardila.bapps.core.data.adapter.BFeedSourcesPaging
import com.raihanardila.bapps.core.data.local.repository.BMainRepository
import com.raihanardila.bapps.core.data.model.StoriesBModel
import kotlinx.coroutines.flow.Flow

class BMainViewModel(
    private val repository: BMainRepository
) : ViewModel() {

    val storiesFlow: Flow<PagingData<StoriesBModel>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { BFeedSourcesPaging(repository.apiService) }
    ).flow.cachedIn(viewModelScope)

    suspend fun getStoriesMap(location: Int) = repository.getStoriesMap(location)
}
