#极光推送
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#小米
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.push.**{*;}

#华为
-keep class com.huawei.hms.**{*;}
-dontwarn com.huawei.**
-keep public class * extends android.app.Activity
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}

#OPPO
-dontwarn com.coloros.mcsdk.**
-keep class com.coloros.mcsdk.** { *; }

#VIVO
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }

#MEIZU
-dontwarn com.meizu.**
-keep class com.meizu.**{*;}