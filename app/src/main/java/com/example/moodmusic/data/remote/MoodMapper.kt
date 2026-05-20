package com.example.moodmusic.data.remote

object MoodMapper {
    /**
     * Mapea el mood seleccionado por el usuario a tags compatibles con Last.fm
     */
    fun mapMoodToTag(mood: String): String {
        return when (mood.lowercase()) {
            "eufórico" -> "party"
            "feliz" -> "happy"
            "motivado" -> "workout"
            "inspirado" -> "inspirational"
            "en paz" -> "chillout"
            "reflexivo" -> "reflective"
            "triste" -> "sad"
            "enojado" -> "rock"
            else -> "pop"
        }
    }
}
