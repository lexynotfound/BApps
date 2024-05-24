package com.raihanardila.bapps.utils

import com.raihanardila.bapps.core.data.model.StoriesBModel

object Dummys {
    fun generateResponse(): List<StoriesBModel> {
        return (0..100).map { i ->
            StoriesBModel(
                id = i.toString(),
                name = "name $i",
                description = "description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/$i",
                createdAt = "createdAt $i",
                lat = 40.7434,
                lon = 74.0080
            )
        }
    }
}
