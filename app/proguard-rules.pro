# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#---------------------------------------------------------------------------
# Project Specific Rules
#---------------------------------------------------------------------------

# Menjaga class yang digunakan untuk navigasi antar modul (Dynamic Feature)
-keep class com.raychal.core.navigation.** { *; }

# Menjaga Model agar SerializedName tetap bekerja (Sangat Penting untuk Retrofit/Gson)
-keep class com.raychal.core.domain.model.** { *; }
-keep class com.raychal.core.data.remote.response.** { *; }

# Jika Anda menggunakan Kotlin Serialization (terlihat di plugins toml)
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature
-keepnames class kotlinx.serialization.** { *; }

#---------------------------------------------------------------------------
# SQLCipher & SQLite (Database Encryption)
#---------------------------------------------------------------------------
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**
-keep class net.zetetic.database.** { *; }

#---------------------------------------------------------------------------
# Gson
#---------------------------------------------------------------------------
-keepattributes Signature, *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.Gson
-keepclasseswithmembers class * {
    <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

#---------------------------------------------------------------------------
# Retrofit 2
#---------------------------------------------------------------------------
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-dontwarn retrofit2.**

#---------------------------------------------------------------------------
# Koin (Dependency Injection)
#---------------------------------------------------------------------------
# Koin butuh nama class tetap untuk pencarian definisi (pencarian via reified type)
-keepnames class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}
# Menjaga ViewModel agar Koin bisa meng-instantiate
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

#---------------------------------------------------------------------------
# Room Database
#---------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.Entity

#---------------------------------------------------------------------------
# Media3 / ExoPlayer (Video Player)
#---------------------------------------------------------------------------
-keep class androidx.media3.common.** { *; }
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.ui.** { *; }
-keep class androidx.media3.datasource.** { *; }
-keep class androidx.media3.decoder.** { *; }
-keep class androidx.media3.extractor.** { *; }
-dontwarn androidx.media3.**

#---------------------------------------------------------------------------
# Paging 3
#---------------------------------------------------------------------------
-keep class androidx.paging.PagingSource { *; }
-keep class * extends androidx.paging.PagingSource

#---------------------------------------------------------------------------
# Coil (Image Loading)
#---------------------------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**

#---------------------------------------------------------------------------
# Coroutines & OkHttp
#---------------------------------------------------------------------------
-dontwarn kotlinx.coroutines.**
-dontwarn okhttp3.**
-dontwarn okio.**

# --- DYNAMIC FEATURE RULES ---
-keep public class * extends android.app.Activity
-keep public class * extends androidx.core.app.ComponentActivity

-keep class com.raychal.favorite.ui.** { *; }
-keep class com.raychal.favorite.ui.** { *; }

# Tambahkan ini untuk mendukung refleksi (Class.forName)
-keepattributes Signature, EnclosingMethod, InnerClasses, *Annotation*

-keep class com.google.android.play.core.splitcompat.** { *; }
-keep class com.google.android.play.core.splitinstall.** { *; }

-keep public class * extends androidx.lifecycle.ViewModel