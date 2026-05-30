package com.example.vigorly.data.local

import com.example.vigorly.data.model.UserAccount
import org.json.JSONArray
import org.json.JSONObject

object AccountsCodec {
    fun encode(accounts: List<UserAccount>): String {
        val array = JSONArray()
        accounts.forEach { account ->
            array.put(
                JSONObject()
                    .put("id", account.id)
                    .put("email", account.email)
                    .put("password", account.password)
                    .put("username", account.username)
                    .put("birthDate", account.birthDate)
            )
        }
        return array.toString()
    }

    fun decode(raw: String?): List<UserAccount> {
        if (raw.isNullOrBlank()) return emptyList()
        val array = JSONArray(raw)
        return buildList {
            for (i in 0 until array.length()) {
                val json = array.getJSONObject(i)
                add(
                    UserAccount(
                        id = json.getString("id"),
                        email = json.getString("email"),
                        password = json.getString("password"),
                        username = json.getString("username"),
                        birthDate = json.getString("birthDate")
                    )
                )
            }
        }
    }
}
