package com.example.vigorly.auth

import android.app.Activity
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import org.json.JSONObject

data class GoogleUserInfo(
    val id: String,
    val email: String,
    val displayName: String?,
    val profilePictureUri: String?
)

class GoogleSignInNotConfiguredException : Exception()
class GoogleSignInCancelledException : Exception()
class GoogleSignInNoAccountException : Exception()
class GoogleSignInConfigException(message: String?) : Exception(message)

class GoogleSignInHelper(
    private val activity: Activity
) {

    private val credentialManager = CredentialManager.create(activity)

    suspend fun signIn(webClientId: String): Result<GoogleUserInfo> {
        if (webClientId.isBlank() || webClientId == PLACEHOLDER_CLIENT_ID) {
            return Result.failure(GoogleSignInNotConfiguredException())
        }

        val strategies = listOf(
            { buildSignInWithGoogleRequest(webClientId) },
            { buildGoogleIdRequest(webClientId, authorizedOnly = true) },
            { buildGoogleIdRequest(webClientId, authorizedOnly = false) }
        )

        var lastError: Throwable? = null
        for (strategy in strategies) {
            val result = requestCredential(strategy())
            if (result.isSuccess) return result
            val error = result.exceptionOrNull() ?: continue
            lastError = error
            if (error is GoogleSignInCancelledException) {
                return Result.failure(error)
            }
            Log.w(TAG, "Google sign-in attempt failed: ${error.message}", error)
        }

        return Result.failure(lastError ?: IllegalStateException("Google sign-in failed"))
    }

    private suspend fun requestCredential(request: GetCredentialRequest): Result<GoogleUserInfo> {
        return try {
            val response = credentialManager.getCredential(
                request = request,
                context = activity
            )
            parseCredential(response.credential)
        } catch (_: GetCredentialCancellationException) {
            Result.failure(GoogleSignInCancelledException())
        } catch (_: NoCredentialException) {
            Result.failure(GoogleSignInNoAccountException())
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException type=${e.type} message=${e.errorMessage}", e)
            val message = e.errorMessage?.toString().orEmpty()
            if (message.contains("28433", ignoreCase = true) ||
                message.contains("developer", ignoreCase = true) ||
                message.contains("10:", ignoreCase = true)
            ) {
                Result.failure(GoogleSignInConfigException(message))
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Google sign-in error", e)
            Result.failure(e)
        }
    }

    private fun parseCredential(credential: androidx.credentials.Credential): Result<GoogleUserInfo> {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val email = extractEmailFromIdToken(googleCredential.idToken)?.trim().orEmpty()
            if (email.isBlank()) {
                return Result.failure(IllegalStateException("Google account email missing"))
            }
            return Result.success(
                GoogleUserInfo(
                    id = googleCredential.id,
                    email = email,
                    displayName = googleCredential.displayName,
                    profilePictureUri = googleCredential.profilePictureUri?.toString()
                )
            )
        }
        return Result.failure(IllegalStateException("Unexpected credential type: ${credential.type}"))
    }

    private fun buildSignInWithGoogleRequest(webClientId: String): GetCredentialRequest {
        val option = GetSignInWithGoogleOption.Builder(webClientId).build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    private fun buildGoogleIdRequest(
        webClientId: String,
        authorizedOnly: Boolean
    ): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(authorizedOnly)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    companion object {
        private const val TAG = "GoogleSignIn"
        const val PLACEHOLDER_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"

        private fun extractEmailFromIdToken(idToken: String): String? {
            val payloadSegment = idToken.split(".").getOrNull(1) ?: return null
            return runCatching {
                val payload = String(
                    Base64.decode(payloadSegment, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
                )
                JSONObject(payload).optString("email").takeIf { it.isNotBlank() }
            }.getOrNull()
        }
    }
}
