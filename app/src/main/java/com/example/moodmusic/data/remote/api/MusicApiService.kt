package com.example.moodmusic.data.remote.api

import com.example.moodmusic.data.remote.model.ArtistInfoResponse
import com.example.moodmusic.data.remote.model.LastFmResponse
import com.example.moodmusic.data.remote.model.TrackInfoResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MusicApiService {
    @Headers("User-Agent: MoodMusic/1.0 (contact: MoodMusicApp)")
    @GET("2.0/?method=tag.gettoptracks&format=json")
    suspend fun getSongsByTag(
        @Query("tag") tag: String,
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 20,
        @Query("autocorrect") autocorrect: Int = 1
    ): LastFmResponse

    @Headers("User-Agent: MoodMusic/1.0 (contact: MoodMusicApp)")
    @GET("2.0/?method=track.getInfo&format=json")
    suspend fun getTrackInfo(
        @Query("track") trackName: String,
        @Query("artist") artistName: String,
        @Query("api_key") apiKey: String,
        @Query("autocorrect") autocorrect: Int = 1
    ): TrackInfoResponse

    @Headers("User-Agent: MoodMusic/1.0 (contact: MoodMusicApp)")
    @GET("2.0/?method=artist.getInfo&format=json")
    suspend fun getArtistInfo(
        @Query("artist") artistName: String,
        @Query("api_key") apiKey: String,
        @Query("autocorrect") autocorrect: Int = 1
    ): ArtistInfoResponse

    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com/"
    }
}
