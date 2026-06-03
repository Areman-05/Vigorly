package com.example.vigorly.ui.profile

import androidx.compose.ui.graphics.Color

object ProfileAvatarCatalog {
    const val PREFIX = "preset:"
    const val DEFAULT_ID = "spark"

    data class Preset(
        val id: String,
        val iconName: String,
        val gradientStart: Color,
        val gradientEnd: Color
    )

    private val presets = listOf(
        Preset("spark", "bolt", Color(0xFFFF8A50), Color(0xFFE85D04)),
        Preset("flame", "local_fire_department", Color(0xFFFF6B6B), Color(0xFFC92A2A)),
        Preset("runner", "directions_run", Color(0xFF4DABF7), Color(0xFF1864AB)),
        Preset("iron", "fitness_center", Color(0xFF9775FA), Color(0xFF5F3DC4)),
        Preset("flow", "self_improvement", Color(0xFF63E6BE), Color(0xFF087F5B)),
        Preset("wave", "pool", Color(0xFF66D9E8), Color(0xFF0B7285)),
        Preset("heart", "favorite", Color(0xFFFF8787), Color(0xFFC2255C)),
        Preset("elite", "workspace_premium", Color(0xFFFFD43B), Color(0xFFE67700))
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
        presetId(avatarUrl)?.let { find(it) }?.let { return it }
        return find(DEFAULT_ID)!!
    }
}
