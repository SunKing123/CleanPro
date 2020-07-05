# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\tie\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

-keep class com.google.gson.** {*;}
#-keep class com.google.**{*;}
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.baidu.mobstat.** { *; }
-keep class com.baidu.bottom.** { *; }

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.dubai.fa.model.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.** {
    <fields>;
    <methods>;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-dontwarn com.google.gson.**

#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*

 #不混淆R类
-keep public class com.honglu.calftrader.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 埋点
-dontwarn com.xiaoniu.statistic.**
-keep class com.xiaoniu.statistic.**{*;}
# 保持哪些类不被混淆
-keep public class * implements java.io.Serializable {
	public *;
}
-keep class com.xiaoniu.cleanking.widget.** { *; } #自定义控件不参与混淆
-keep class com.xiaoniu.cleanking.ui.main.presenter.** { *; } #自定义控件不参与混淆
-keep class com.xiaoniu.cleanking.base.** { *; } #base不参与混淆
-keep public class * extends installment.loan.base.BaseEntity  #Bean类不参与混淆
-keep class com.xiaoniu.cleanking.ui.main.bean.**{ *; }  #Bean类不参与混淆
-keep class com.xiaoniu.cleanking.ui.repayment.bean.**{ *; }  #Bean类不参与混淆
-keep class com.xiaoniu.cleanking.ui.usercenter.bean.**{ *; }  #Bean类不参与混淆
-keep class com.xiaoniu.cleanking.callback.** { *; }
-keep class android.content.pm.** { *; }
-keep public class * extends android.view
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

###bugly混淆
#-dontwarn com.tencent.bugly.**
#-keep public class com.tencent.bugly.**{*;}


-keep class com.xiaoniu.cleanking.ui.main.bean.**{ *; }  #Bean类不参与混淆
-keep class com.xiaoniu.cleanking.ui.keeplive.**{ *; }  #包活相关类不参与混淆
#########Okhttputils混淆
#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}


#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}


#okio
-dontwarn okio.**
-keep class okio.**{*;}

#常用参数类
-keep class com.xiaoniu.cleanking.utils.** { *; }
-keep class com.xiaoniu.cleanking.api.** { *; }
-keep class com.xiaoniu.cleanking.app.UrlConstans { *; }
#常用参数类end


#如果引用了v4或者v7包
#-dontwarn android.support.**
#-keep class android.support.** { *; }
#-keep interface android.support.** { *; }
#
#-libraryjars <java.home>\lib\rt.jar
#-dontwarn org.apache.commons.**
-dontwarn android.support.v4.**

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep public class * extends android.support.v4.**
-keep class android.support.v4.view.**{ *;}
-keep class android.support.v4.content.**{ *;}

-keep class android.support.design.widget.** { *; }
-keep class android.support.design.** { *; }

-keep public class * extends android.support.v4.app.FragmentActivity

# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
# Butterknife end

#Retrofit
-dontwarn okio.**
-dontwarn javax.annotation.**

#webview
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

################引用的第三方jar包library#########################
##-libraryjars libs/httpmime-4.1.3.jar
#-keep class org.apache.http.entity.mime.** { *; }
#-keep public interface org.apache.http.entity.mime.** {
#   *;
#}

#-dontwarn org.apache.http.**
#-keep class org.apache.http.** { *;}


# Luban
-keep class top.zibin.luban.**{*;}
-dontwarn top.zibin.luban.*

# RxJava
-dontwarn sun.misc.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
      public static ** test();
  }
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#udesk
-keep class udesk.** {*;}
-keep class cn.udesk.**{*; }
#七牛
-keep class okhttp3.** {*;}
-keep class okio.** {*;}
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings
#smack
-keep class org.jxmpp.** {*;}
-keep class de.measite.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.xmlpull.** {*;}
-dontwarn org.xbill.**
-keep class org.xbill.** {*;}

#agora
-keep class io.agora.**{*;}

#freso
-keep class com.facebook.imagepipeline.** {*; }
-keep class com.facebook.animated.gif.** {*; }
-keep class com.facebook.drawee.** {*; }
-keep class com.facebook.drawee.backends.pipeline.** {*; }
-keep class bolts.** {*; }
-keep class me.relex.photodraweeview.** {*; }

-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

#Android M 权限
-keep class rx.** {*;}
-keep class com.tbruyelle.rxpermissions.** {*;}

-keep class de.hdodenhof.circleimageview.** {*;}

#umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep class com.umeng.** { *; }
-keep public interface com.umeng.** {
   *;
}
-dontwarn com.umeng.*

#参数加密
-keep class org.bouncycastle.**{*;}
-keep class org.apache.commons.codec.**{*;}


#evnetbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#TONGDUN
-dontwarn android.os.**
-dontwarn com.android.internal.**
-keep class cn.tongdun.android.**{*;}

#友盟分享
-dontusemixedcaseclassnames
    -dontshrink
    -dontoptimize
    -dontwarn com.google.android.maps.**
    -dontwarn android.webkit.WebView
    -dontwarn com.umeng.**
    -dontwarn com.tencent.weibo.sdk.**
    -dontwarn com.facebook.**
    -keep public class javax.**
    -keep public class android.webkit.**
    -dontwarn android.support.v4.**
    -keep enum com.facebook.**
    -keepattributes Exceptions,InnerClasses,Signature
    -keepattributes *Annotation*
    -keepattributes SourceFile,LineNumberTable

    -keep public interface com.facebook.**
    -keep public interface com.tencent.**
    -keep public interface com.umeng.socialize.**
    -keep public interface com.umeng.socialize.sensor.**
    -keep public interface com.umeng.scrshot.**
    -keep class com.android.dingtalk.share.ddsharemodule.** { *; }
    -keep public class com.umeng.socialize.* {*;}


    -keep class com.facebook.**
    -keep class com.facebook.** { *; }
    -keep class com.umeng.scrshot.**
    -keep public class com.tencent.** {*;}
    -keep class com.umeng.socialize.sensor.**
    -keep class com.umeng.socialize.handler.**
    -keep class com.umeng.socialize.handler.*
    -keep class com.umeng.weixin.handler.**
    -keep class com.umeng.weixin.handler.*
    -keep class com.umeng.qq.handler.**
    -keep class com.umeng.qq.handler.*
    -keep class UMMoreHandler{*;}
    -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
    -keep class com.tencent.mm.sdk.modelmsg.** implements   com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
    -keep class im.yixin.sdk.api.YXMessage {*;}
    -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
    -keep class com.tencent.mm.sdk.** {
     *;
    }
    -keep class com.tencent.mm.opensdk.** {
   *;
    }
    -dontwarn twitter4j.**
    -keep class twitter4j.** { *; }

    -keep class com.tencent.** {*;}
    -dontwarn com.tencent.**
    -keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
    }
    -keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
        }
    -keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }

    -keep class com.tencent.open.TDialog$*
    -keep class com.tencent.open.TDialog$* {*;}
    -keep class com.tencent.open.PKDialog
    -keep class com.tencent.open.PKDialog {*;}
    -keep class com.tencent.open.PKDialog$*
    -keep class com.tencent.open.PKDialog$* {*;}

    -keep class com.sina.** {*;}
    -dontwarn com.sina.**
    -keep class  com.alipay.share.sdk.** {
       *;
    }
    -keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
    }

    -keep class com.linkedin.** { *; }
    -keepattributes Signature
#JPush
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.push.** { *; }

 -ignorewarning
 -keepattributes *Annotation*
 -keepattributes Exceptions
 -keepattributes InnerClasses
 -keepattributes Signature
 # hmscore-support: remote transport
 -keep class * extends com.huawei.hms.core.aidl.IMessageEntity { *; }
 # hmscore-support: remote transport
 -keepclasseswithmembers class * implements com.huawei.hms.support.api.transport.DatagramTransport {      <init>(...);    }
 # manifest: provider for updates
 -keep public class com.huawei.hms.update.provider.UpdateProvider { public *; protected *; }

#例外高德地图定位包
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#start支付宝支付
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}
#end 支付宝支付

#-----聚信立start-----
-keep class com.juxinli.normandy.process.ProcessStatus{
    *;
}
-keep class com.juxinli.normandy.retrofitclient.bean.requestbean.TaskBean{*;}
-keep class com.juxinli.normandy.retrofitclient.bean.requestbean.*{*;}
-keep class com.juxinli.normandy.retrofitclient.bean.responsebean.*{*;}
-keep class **.R$* { *; }
-keep class com.juxinli.normandy.logger.*{*;}
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.google.**
-dontwarn com.google.**
# RxBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.hwangjr.rxbus.annotation.Subscribe public *;
    @com.hwangjr.rxbus.annotation.Produce public *;
}
# novate
-keep class retrofit2.** {*;}
-dontwarn okhttp3**
-keep  class okhttp3.**{*;}
-dontwarn okio**
-keep  class okio.**{*;}
-keep  class rx.**{*;}
-keep class com.tamic.novate.Novate.** {*;}
#Timber
-dontwarn org.jetbrains.annotations.**

-keep class com.juxinli.normandy.NormandySDK {
    public <methods>;
    public static <methods>;
}

-keep class com.juxinli.normandy.NormandySDK$* {
    *;
}
#枚举
-keepclassmembers enum * {
    **[] $VALUES;
    public *;
}
#----聚信立end-----

#----oss start---
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**
#----oss end---

#----小牛统计 start---
-dontwarn com.xiaoniu.statistic.**
-keep class com.xiaoniu.statistic.**{ *;}
#----小牛统计 end---

#---ARouter start---
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider
#---ARouter end---

#brvah
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}


#########################geek push混淆开始########################
#极客
-dontwarn com.geek.push.**
-keep class com.geek.push.**{*;}

#极光推送
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jpush.android.**
-keep class cn.jpush.android.** { *; }

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
-dontwarn com.meizu.cloud.**
-keep class com.meizu.cloud.** { *; }

# gson sdk 混淆开始
-dontwarn com.google.gson.**
-keep public class com.google.gson.**{*;}
# gson sdk 混淆结束

# AgentWeb 混淆开始
-keepclassmembers class com.just.agentweb.sample.common.AndroidInterface{ *; }
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**
# AgentWeb 混淆结束

#穿山甲 begin
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
#穿山甲 end


-keep class com.xiaoniu.cleanking.ui.lifecyler.**{ *; }  #包活相关类不参与混淆
-keep class com.xiaoniu.cleanking.bean.**{ *; }  #Bean类不参与混淆
-keep class com.xiaoniu.cleanking.app.**{ *; }  #app类不参与混淆
-keep class com.xiaoniu.cleanking.jsbridge.**{ *; }  #jsbridge类不参与混淆
-keep class com.xiaoniu.cleanking.room.**{ *; }  #room类不参与混淆

#########################common lib混淆开始########################
-ignorewarning
-keep class com.xiaoniu.common.base.** { *; }
-keep class com.xiaoniu.common.http.** { *; }
#-keep class com.xiaoniu.common.widget.** { *; }
-keep public class * extends com.xiaoniu.common.base.IAppLifecyclesImpl
#mdid获取sdk
-keep class com.bun.miitmdid.core.** {*;}

#########################广告sdk混淆开始########################
-keep public class com.comm.jksdk.**{*;}
-keep public class com.comm.jksdk.bean.**{*;}
-keep public class com.comm.jksdk.utils.**{*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}


#########################jsbridage#######################
-keep public class com.geek.webpage.**{*;}
-keep public class com.apkfuns.jsbridge.**{*;}
-keep class * extends com.apkfuns.jsbridge.module.JsModule{*;}
-keep class com.xiaoniu.asmhelp.bean.** { *; }


-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions

-keepattributes InnerClasses
-keepattributes Signature
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

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

#数美sdk
-keep class com.ishumei.** { *; }