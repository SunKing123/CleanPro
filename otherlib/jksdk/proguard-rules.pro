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
-keep public class com.comm.jksdk.bean.**{*;}
-keep public class com.comm.jksdk.utils.**{*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}


#Midas变现SDK
-dontwarn com.xnad.sdk.**
-keep class com.xnad.sdk.**{*;}
-keep public class com.xnad.sdk.ad.**{*;}
#牛数埋点SDK混淆
-dontwarn com.xiaoniu.statistic.**
-keep class com.xiaoniu.statistic.**{*;}
-keep class com.bun.miitmdid.** {*;}
-keep class com.bun.miitmdid.core.** {*;}
#穿山甲广告SDK混淆
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class com.bytedance.** { *; }
-keep class com.ss.android.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}
#优量汇FileProvider
-keep class com.xnad.sdk.ad.provider.** {*;}
#mintegral sdk
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mintegral.** {*; }
-keep interface com.mintegral.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mintegral.**
-keep class **.R$* { public static final int mintegral*; }
-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }
-keep interface androidx.** { *; }
-keep class androidx.** { *; }
-keep public class * extends androidx.** { *; }
# topmob广告广告SDK混淆
-keep class com.meishu.sdk.** { *; }
#tuia广告SDK
-ignorewarnings
-dontwarn com.lechuan.midunovel.**
-keep class com.lechuan.midunovel.** { *; }
#InMoBiSDK
-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** { *; }
-dontwarn com.inmobi.**
-dontwarn com.squareup.picasso.**
#skip the Picasso library classes
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**
#skip AVID classes
-keep class com.integralads.avid.library.** {*;}
# skip OAID classes
-keep class com.bun.miitmdid.core.** {*;}
-dontwarn com.bun.**
#skip IAB classes
-keep class com.iab.** {*;}
-dontwarn com.iab.**
#以下两⾏仅针对海外流量
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{public *;}
#快手SDK
-keep class com.kwad.sdk.** { *;}
-keep class com.ksad.download.** { *;}
-keep class com.kwai.filedownloader.** { *;}