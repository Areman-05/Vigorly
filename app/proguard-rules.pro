# Modelos y codecs usados con reflexión JSON / DataStore
-keep class com.example.vigorly.data.model.** { *; }

# Google Sign-In / Credentials
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }
-dontwarn com.google.android.gms.**
