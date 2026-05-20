package com.example.moodmusic.data.remote.api

import com.example.moodmusic.data.remote.model.LastFmResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("2.0/?method=tag.gettoptracks&format=json")
    suspend fun getSongsByTag(
        @Query("tag") tag: String,
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 20
    ): LastFmResponse

    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com/"
    }
}
