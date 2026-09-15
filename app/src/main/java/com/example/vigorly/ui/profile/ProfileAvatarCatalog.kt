package com.example.vigorly.ui.profile

import androidx.compose.ui.graphics.Color

object ProfileAvatarCatalog {
    const val PREFIX = "preset:"
    const val DEFAULT_ID = "ember"

    enum class Motif {
        Rings,
        Hex,
        Wave,
        Leaf,
        Spark,
        Diamond,
        Arc,
        Orbit,
        Bloom,
        Crest,
        Grid,
        Shard
    }

    data class Preset(
        val id: String,
        val motif: Motif,
        val gradientStart: Color,
        val gradientMid: Color,
        val gradientEnd: Color,
        val accent: Color
    )

    private val presets = listOf(
        Preset(
            id = "ember",
            motif = Motif.Spark,
            gradientStart = Color(0xFFFF8A5C),
            gradientMid = Color(0xFFFF2D55),
            gradientEnd = Color(0xFF8B0A2E),
            accent = Color(0xFFFFD0A8)
        ),
        Preset(
            id = "midnight",
            motif = Motif.Orbit,
            gradientStart = Color(0xFF6C7BFF),
            gradientMid = Color(0xFF3B1F6E),
            gradientEnd = Color(0xFF12081F),
            accent = Color(0xFFC9B8FF)
        ),
        Preset(
            id = "ocean",
            motif = Motif.Wave,
            gradientStart = Color(0xFF5CE1E6),
            gradientMid = Color(0xFF1B7FBF),
            gradientEnd = Color(0xFF0A2F4A),
            accent = Color(0xFFB8F0FF)
        ),
        Preset(
            id = "forest",
            motif = Motif.Leaf,
            gradientStart = Color(0xFFA8E063),
            gradientMid = Color(0xFF2F8F5B),
            gradientEnd = Color(0xFF0F3B2A),
            accent = Color(0xFFD6FFB0)
        ),
        Preset(
            id = "magma",
            motif = Motif.Rings,
            gradientStart = Color(0xFFFFD166),
            gradientMid = Color(0xFFFF6B35),
            gradientEnd = Color(0xFF7A1020),
            accent = Color(0xFFFFE6A8)
        ),
        Preset(
            id = "violet",
            motif = Motif.Hex,
            gradientStart = Color(0xFFE0AAFF),
            gradientMid = Color(0xFF8B5CF6),
            gradientEnd = Color(0xFF2E1065),
            accent = Color(0xFFF3E8FF)
        ),
        Preset(
            id = "citrus",
            motif = Motif.Arc,
            gradientStart = Color(0xFFFFF1A8),
            gradientMid = Color(0xFFFFB703),
            gradientEnd = Color(0xFFE85D04),
            accent = Color(0xFFFFF6D1)
        ),
        Preset(
            id = "arctic",
            motif = Motif.Diamond,
            gradientStart = Color(0xFFE8F4FF),
            gradientMid = Color(0xFF7EB6FF),
            gradientEnd = Color(0xFF254E8F),
            accent = Color(0xFFF5FBFF)
        ),
        Preset(
            id = "rose",
            motif = Motif.Bloom,
            gradientStart = Color(0xFFFFB3C6),
            gradientMid = Color(0xFFFF4D6D),
            gradientEnd = Color(0xFF6A0530),
            accent = Color(0xFFFFD6E0)
        ),
        Preset(
            id = "gold",
            motif = Motif.Crest,
            gradientStart = Color(0xFFFFF0B3),
            gradientMid = Color(0xFFE6A800),
            gradientEnd = Color(0xFF6B3F00),
            accent = Color(0xFFFFF7D6)
        ),
        Preset(
            id = "neon",
            motif = Motif.Grid,
            gradientStart = Color(0xFFB8FF66),
            gradientMid = Color(0xFF00C2A8),
            gradientEnd = Color(0xFF0B3D4A),
            accent = Color(0xFFE8FFC9)
        ),
        Preset(
            id = "slate",
            motif = Motif.Shard,
            gradientStart = Color(0xFFC9CDD6),
            gradientMid = Color(0xFF5B6472),
            gradientEnd = Color(0xFF1A1D24),
            accent = Color(0xFFE8EBF0)
        )
    )

    fun all(): List<Preset> = presets

    fun find(id: String?): Preset? = presets.find { it.id == id }

    fun encode(id: String): String = "$PREFIX$id"

    fun presetId(avatarUrl: String?): String? {
        if (!isPreset(avatarUrl)) return null
        return avatarUrl!!.removePrefix(PREFIX)
    }

    fun isPreset(avatarUrl: String?): Boolean = avatarUrl?.startsWith(PREFIX) == true

    fun isRemoteUrl(avatarUrl: String?): Boolean =
        !avatarUrl.isNullOrBlank() && !isPreset(avatarUrl)

    fun resolve(avatarUrl: String?): Preset {
        val id = presetId(avatarUrl)
        if (id != null) {
            find(id)?.let { return it }
            when (id) {
                "spark", "flame" -> find("ember")
                "runner" -> find("citrus")
                "iron" -> find("violet")
                "flow" -> find("forest")
                "wave" -> find("ocean")
                "heart" -> find("rose")
                "elite" -> find("gold")
                else -> null
            }?.let { return it }
        }
        return find(DEFAULT_ID)!!
    }
}
