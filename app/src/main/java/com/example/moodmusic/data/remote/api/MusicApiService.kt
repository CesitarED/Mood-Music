package com.example.moodmusic.data.remote.api

import com.example.moodmusic.data.remote.model.LastFmResponse
import com.example.moodmusic.data.remote.model.ArtistInfoResponse
import com.example.moodmusic.data.remote.model.ItunesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("2.0/?method=tag.gettoptracks&format=json")
    suspend fun getSongsByTag(
        @Query("tag") tag: String,
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 20
    ): LastFmResponse

    @GET("2.0/?method=artist.getinfo&format=json")
    suspend fun getArtistInfo(
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String
    ): ArtistInfoResponse

    @GET("https://itunes.apple.com/search")
    suspend fun searchItunesSong(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 1
    ): ItunesSearchResponse

    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com/"
    }
}
