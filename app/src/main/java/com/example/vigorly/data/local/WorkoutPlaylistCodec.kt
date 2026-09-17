package com.example.vigorly.data.local

import com.example.vigorly.data.model.WorkoutPlaylist
import org.json.JSONArray
import org.json.JSONObject

object WorkoutPlaylistCodec {

    fun encode(lists: List<WorkoutPlaylist>): String {
        val arr = JSONArray()
        lists.forEach { list ->
            arr.put(
                JSONObject()
                    .put("id", list.id)
                    .put("name", list.name)
                    .put("isAuto", list.isAuto)
                    .put("workoutIds", JSONArray(list.workoutIds))
            )
        }
        return arr.toString()
    }

    fun decode(raw: String?): List<WorkoutPlaylist> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val idsJson = obj.optJSONArray("workoutIds") ?: JSONArray()
                    val ids = buildList {
                        for (j in 0 until idsJson.length()) {
                            add(idsJson.getString(j))
                        }
                    }
                    add(
                        WorkoutPlaylist(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            workoutIds = ids,
                            isAuto = obj.optBoolean("isAuto", false)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun sanitizeUserLists(lists: List<WorkoutPlaylist>): List<WorkoutPlaylist> {
        return lists.filterNot { list ->
            list.isAuto || list.id.startsWith("auto_")
        }
    }

    fun uniqueName(
        desired: String,
        existing: List<WorkoutPlaylist>,
        excludeId: String? = null
    ): String {
        val used = existing
            .filter { it.id != excludeId }
            .map { it.name.trim().lowercase() }
            .toSet()
        val base = desired.trim().ifBlank { nextUntitled(used) }
        if (base.lowercase() !in used) return base
        var n = 2
        while ("$base $n".lowercase() in used) n++
        return "$base $n"
    }

    fun isNameTaken(
        desired: String,
        existing: List<WorkoutPlaylist>,
        excludeId: String? = null
    ): Boolean {
        val name = desired.trim()
        if (name.isEmpty()) return false
        return existing.any {
            it.id != excludeId && it.name.trim().equals(name, ignoreCase = true)
        }
    }

    private fun nextUntitled(used: Set<String>): String {
        var n = 1
        while ("lista $n" in used || "list $n" in used) n++
        return "Lista $n"
    }
}
