package com.example.moodmusic.data.remote.model

import com.google.gson.annotations.SerializedName

data class LastFmResponse(
    @SerializedName("tracks") val tracks: TopTracks
)

data class TopTracks(
    @SerializedName("track") val track: List<TrackDto>
)

data class TrackDto(
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artist: ArtistDto,
    @SerializedName("image") val image: List<ImageDto> = emptyList()
)

data class ArtistDto(
    @SerializedName("name") val name: String
)

data class ImageDto(
    @SerializedName("#text") val url: String,
    @SerializedName("size") val size: String
)

data class ArtistInfoResponse(
    @SerializedName("artist") val artist: ArtistInfoDto?
)

data class ArtistInfoDto(
    @SerializedName("image") val image: List<ImageDto> = emptyList()
)

data class ItunesSearchResponse(
    @SerializedName("results") val results: List<ItunesSongDto> = emptyList()
)

data class ItunesSongDto(
    @SerializedName("artworkUrl100") val artworkUrl100: String? = null
)

data class CancionUi(
    val name: String,
    val artistName: String,
    val imageUrl: String?
)
