package com.example.vigorly

import com.example.vigorly.data.local.FavoritesCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesCodecTest {
    @Test
    fun encode_decode_roundTrip() {
        val ids = setOf("w1", "w2", "w3")
        val encoded = FavoritesCodec.encode(ids)
        assertEquals(ids, FavoritesCodec.decode(encoded))
    }

    @Test
    fun decode_nullOrBlank_returnsEmpty() {
        assertTrue(FavoritesCodec.decode(null).isEmpty())
        assertTrue(FavoritesCodec.decode("").isEmpty())
    }
}
