package com.raihanardila.bapps.core.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raihanardila.bapps.core.data.local.repository.BStoriesRepository
import com.raihanardila.bapps.core.data.remote.response.bfeed.ResponseBMaps
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class BStoriesViewModel(
    private val repository: BStoriesRepository
) : ViewModel() {

    fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<ResponseBMaps> {
        val result = MutableLiveData<ResponseBMaps>()
        viewModelScope.launch {
            try {
                val response = repository.uploadImage(file, description, lat, lon)
                if (response.isSuccessful) {
                    result.postValue(response.body())
                } else {
                    result.postValue(
                        ResponseBMaps(
                            true,
                            "Upload failed: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                result.postValue(ResponseBMaps(true, "Upload failed: ${e.message}"))
            }
        }
        return result
    }

}