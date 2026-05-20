package com.example.moodmusic.data.remote

object MoodMapper {
    /**
     * Mapea el mood seleccionado por el usuario a tags compatibles con Last.fm
     */
    fun mapMoodToTag(mood: String): String {
        return when (mood.lowercase()) {
            "eufórico" -> "party, energetic"
            "feliz" -> "happy, upbeat, pop"
            "motivado" -> "workout, motivation"
            "inspirado" -> "inspirational"
            "en paz" -> "calm, ambient, chillout"
            "reflexivo" -> "reflective, piano"
            "triste" -> "sad, melancholy"
            "enojado" -> "heavy metal, rock"
            else -> "pop"
        }
    }
}
