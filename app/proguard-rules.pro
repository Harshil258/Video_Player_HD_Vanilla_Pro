# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE


-keep public class com.google.ads.** {*;}
-keep public class com.google.android.gms.** {*;}
-keep class sun.misc.Unsafe.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-keep public class com.harshil258.adplacer.utils.Logger.** { *; }
-keep public class com.harshil258.adplacer.app.** { *; }
-keep public class com.harshil258.adplacer.models.** { *; }
-keep public class com.harshil258.adplacer.interfaces.** { *; }
-keep public class com.harshil258.adplacer.utils.** {*;}
-keep public class com.harshil258.adplacer.adViews.** {*;}
-keep public class com.harshil258.adplacer.models.** {*;}
-keep public class com.harshil258.adplacer.utils.** {*;}
-keep public class com.harshil258.adplacer.interfaces.** {*;}
-dontwarn java.lang.invoke.StringConcatFactory


-keep public class com.google.ads.** {*;}
-keep public class com.google.android.gms.** {*;}
-keep class com.vanillavideoplayer.commonvanillaads.** {*;}
-keep class com.vanillavideoplayer.commonvanillaads.model.AdDetail.** {
    *;
}
-keep class com.vanillavideoplayer.commonvanillaads.model.AdDetail.** {
    public <fields>;
}
-keep class com.vanillavideoplayer.commonvanillaads.z.Logger.** { *; }

-keep class com.vanillavideoplayer.commonvanillaads.finalcls.** { *; }
-keepclassmembers class com.vanillavideoplayer.commonvanillaads.finalcls.** { *; }
-keepclassmembers class com.vanillavideoplayer.commonvanillaads.models** { <fields>; }
-keep class sun.misc.Unsafe.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }

# Keep all classes and members in the androidx.media3 package
-keep class androidx.media3.** { *; }

# Keep all classes and members in the com.google.android.exoplayer2 package
-keep class com.google.android.exoplayer2.** { *; }

# Keep all classes and members in the com.google.android.exoplayer2.ui package
-keep class com.google.android.exoplayer2.ui.** { *; }

# Keep all classes and members in the com.google.android.exoplayer2.ext package
-keep class com.google.android.exoplayer2.ext.** { *; }

-keep class com.onesignal.** { *; }
-keepattributes *Annotation*
-dontwarn com.onesignal.**

-keep class com.vanillavideoplayer.hd.videoplayer.pro.VanillaNotificationExtenderService {
    *;
}