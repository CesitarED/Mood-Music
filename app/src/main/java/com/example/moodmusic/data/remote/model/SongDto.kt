package com.example.moodmusic.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * DTOs para la API de Last.fm.
 * Mapeamos exactamente #text para capturar las URLs que envía la API.
 */
data class LastFmResponse(
    @SerializedName("tracks") val tracks: TopTracks? = null
)

data class TopTracks(
    @SerializedName("track") val track: List<TrackDto>? = null
)

data class TrackInfoResponse(
    @SerializedName("track") val track: TrackDto? = null
)

data class ArtistInfoResponse(
    @SerializedName("artist") val artist: ArtistDto? = null
)

data class TrackDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("artist") val artist: ArtistDto? = null,
    @SerializedName("image") val image: List<ImageDto>? = null,
    @SerializedName("album") val album: AlbumDto? = null
)

data class ArtistDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: List<ImageDto>? = null
)

data class AlbumDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("image") val image: List<ImageDto>? = null
)

data class ImageDto(
    @SerializedName("#text") val url: String? = null,
    @SerializedName("size") val size: String? = null
)
