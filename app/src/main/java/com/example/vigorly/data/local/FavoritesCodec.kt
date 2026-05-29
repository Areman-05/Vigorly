package com.example.vigorly.data.local

object FavoritesCodec {
    private const val SEP = ","

    fun encode(ids: Set<String>): String = ids.joinToString(SEP)

    fun decode(raw: String?): Set<String> {
        if (raw.isNullOrBlank()) return emptySet()
        return raw.split(SEP).filter { it.isNotBlank() }.toSet()
    }
}
