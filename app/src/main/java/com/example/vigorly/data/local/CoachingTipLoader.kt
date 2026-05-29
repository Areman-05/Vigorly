package com.example.vigorly.data.local

import android.content.Context
import com.example.vigorly.data.model.CoachingTip
import org.json.JSONObject

object CoachingTipLoader {
    fun parseLine(line: String): CoachingTip? {
        if (line.isBlank()) return null
        val json = JSONObject(line)
        return CoachingTip(id = json.getString("id"), text = json.getString("text"))
    }

    fun load(context: Context): List<CoachingTip> =
        context.assets.open("coaching/tips.jsonl").bufferedReader().useLines { lines ->
            lines.mapNotNull { parseLine(it) }.toList()
        }
}
