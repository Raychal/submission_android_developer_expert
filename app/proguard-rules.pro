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

-keep class com.raychal.core.navigation.** { *; }

-keep class com.raychal.core.domain.model.** { *; }
-keep class com.raychal.core.data.remote.response.** { *; }

-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature
-keepnames class kotlinx.serialization.** { *; }

-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**
-keep,includedescriptorclasses class net.zetetic.database.** { *; }
-keep,includedescriptorclasses interface net.zetetic.database.** { *; }

-keepattributes Signature, *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.Gson { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * implements com.google.gson.Gson
-keepclasseswithmembers class * {
    <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-dontwarn retrofit2.**

-keepnames class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.Entity

-keep class androidx.media3.common.MediaItem { *; }
-keep class androidx.media3.common.Player { *; }
-keep class androidx.media3.exoplayer.ExoPlayer { *; }
-keep class androidx.media3.ui.** { *; }
-keep class androidx.media3.decoder.** { *; }
-dontwarn androidx.media3.**

-keep class androidx.paging.PagingSource { *; }
-keep class * extends androidx.paging.PagingSource

-keep class coil.request.ImageRequest { *; }
-keep class coil.compose.** { *; }
-dontwarn coil.**

-dontwarn kotlinx.coroutines.**
-dontwarn okhttp3.**
-dontwarn okio.**

-keep public class * extends android.app.Activity
-keep public class * extends androidx.core.app.ComponentActivity

-keep class com.raychal.favorite.ui.** { *; }
-keep class com.raychal.favorite.ui.** { *; }

-keepattributes Signature, EnclosingMethod, InnerClasses, *Annotation*

-keep class com.google.android.play.core.splitcompat.SplitCompat { *; }

-keep public class * extends androidx.lifecycle.ViewModel

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.appcompat.app.AppCompatActivity