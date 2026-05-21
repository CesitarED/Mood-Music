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
    @SerializedName("image") val image: List<ImageDto>
)

data class ArtistDto(
    @SerializedName("name") val name: String
)

data class ImageDto(
    @SerializedName("#text") val url: String,
    @SerializedName("size") val size: String
)
