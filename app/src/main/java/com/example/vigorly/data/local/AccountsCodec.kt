package com.example.vigorly.data.local

import com.example.vigorly.data.model.UserAccount
import com.example.vigorly.util.PasswordHasher
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
                    .put("passwordHash", account.passwordHash)
                    .put("passwordSalt", account.passwordSalt)
                    .put("username", account.username)
                    .put("birthDate", account.birthDate)
                    .put("createdAtMillis", account.createdAtMillis)
                    .put("authProvider", account.authProvider)
                    .apply { account.googleId?.let { put("googleId", it) } }
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
                if (json.has("passwordHash")) {
                    add(
                        UserAccount(
                            id = json.getString("id"),
                            email = json.getString("email"),
                            passwordHash = json.getString("passwordHash"),
                            passwordSalt = json.optString("passwordSalt"),
                            username = json.getString("username"),
                            birthDate = json.getString("birthDate"),
                            createdAtMillis = json.optLong("createdAtMillis", System.currentTimeMillis()),
                            authProvider = json.optString("authProvider", "email"),
                            googleId = json.optString("googleId").takeIf { it.isNotBlank() }
                        )
                    )
                } else {
                    val legacyPassword = json.getString("password")
                    val (salt, hash) = PasswordHasher.legacyHash(legacyPassword)
                    add(
                        UserAccount(
                            id = json.getString("id"),
                            email = json.getString("email"),
                            passwordHash = hash,
                            passwordSalt = salt,
                            username = json.getString("username"),
                            birthDate = json.getString("birthDate"),
                            createdAtMillis = json.optLong("createdAtMillis", System.currentTimeMillis()),
                            authProvider = json.optString("authProvider", "email"),
                            googleId = json.optString("googleId").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }
        }
    }
}
