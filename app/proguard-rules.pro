# Depuración de crashes en release
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Modelos y codecs usados con reflexión JSON / DataStore
-keep class com.example.vigorly.data.model.** { *; }
-keepclassmembers class com.example.vigorly.data.local.** { *; }

# App: ViewModels, navegación y utilidades de serialización
-keep class com.example.vigorly.presentation.** { *; }
-keep class com.example.vigorly.navigation.** { *; }
-keep class com.example.vigorly.util.** { *; }
-keep class com.example.vigorly.data.MilestoneCatalog { *; }

# Kotlin / corrutinas
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontwarn kotlin.**
-keepclassmembers class kotlin.Metadata { public <methods>; }
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    public <methods>;
}
-keepclassmembernames class kotlinx.coroutines.CoroutineExceptionHandler {
    public <methods>;
}

# Jetpack Compose / Navigation
-dontwarn androidx.compose.**
-keep class androidx.compose.runtime.** { *; }
-keepnames class androidx.navigation.** { *; }

# Lifecycle / ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# DataStore
-keep class androidx.datastore.*.** { *; }

# Coil (avatares remotos)
-keep class coil.** { *; }
-dontwarn coil.**

# Google Sign-In / Credentials
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }
-dontwarn com.google.android.gms.**
