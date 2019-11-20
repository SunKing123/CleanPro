package com.xiaoniu.common.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Locale;
import java.util.UUID;

/**
 * @author zhengzhihao
 * @date 2019/11/8 09
 * @mail：zhengzhihao@hellogeek.com
 */
public class DeviceUtil {


    public static String getAndroidId(Context context){
        String m_szAndroidID = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return m_szAndroidID;
    }

    public static String getDeviceId(Context context){
        TelephonyManager TelephonyMgr = (TelephonyManager)context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(TelephonyMgr != null){
            String szImei = TelephonyMgr.getDeviceId();
            return szImei;
        }
        return null;
    }

    // xiaomi 即使连接 wifi 也不能获取到 mac
    public static String getWLANMAC(Context ctx){
        WifiManager wm = (WifiManager)ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wm != null){
            WifiInfo connectionInfo = wm.getConnectionInfo();
            if(connectionInfo != null){
                String m_szWLANMAC = connectionInfo.getMacAddress();
                return m_szWLANMAC;
            }
        }
        return null;
    }

    public static String getWLANMACShell(){
        String macSerial = null;
        String str = "";
        try{
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for(; null != str; ){
                str = input.readLine();
                if(str != null){
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
            return macSerial;
        } catch(IOException ex){
//            ex.printStackTrace();
            return null;
        }
    }

    public static String getBTMAC(){
        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = null;
        if(m_BluetoothAdapter != null){
            m_szBTMAC = m_BluetoothAdapter.getAddress();
        }
        return m_szBTMAC;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }


    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机设备名
     *
     * @return  手机设备名
     */
    public static String getSystemDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机主板名
     *
     * @return  主板名
     */
    public static String getDeviceBoand() {
        return Build.BOARD;
    }


    /**
     * 获取手机厂商名
     *
     * @return  手机厂商名
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }


    public static String getPseudoID(){
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + //主板：The name of the underlying board, like "goldfish".
                Build.BRAND.length() % 10 + //系统定制商：The consumer-visible brand with which the product/hardware will be associated, if any.
                Build.CPU_ABI.length() % 10 + //cpu指令集：The name of the instruction set (CPU type + ABI convention) of native code.
                Build.DEVICE.length() % 10 + //设备参数：The name of the industrial design.
//                                Build.DISPLAY.length() % 10 + //显示屏参数：A build ID string meant for displaying to the user
//                                Build.HOST.length() % 10 +
                Build.ID.length() % 10 + //修订版本列表：Either a changelist number, or a label like "M4-rc20".
                Build.MANUFACTURER.length() % 10 + //硬件制造商：The manufacturer of the product/hardware.
                Build.MODEL.length() % 10 + //版本即最终用户可见的名称：The end-user-visible name for the end product.
                Build.PRODUCT.length() % 10 + //整个产品的名称：The name of the overall product.
                Build.TAGS.length() % 10 + //描述build的标签,如未签名，debug等等。：Comma-separated tags describing the build, like "unsigned,debug".
                Build.TYPE.length() % 10 + //build的类型：The type of build, like "user" or "eng".
                Build.USER.length() % 10; //13 位 //

        return MD5Utils.encryption(m_szDevIDShort);
    }

    public static String getBuildInfo(){
        return "BOARD: " + Build.BOARD
                + "\nBRAND: " + Build.BRAND
                + "\nCPU_ABI: " + Build.CPU_ABI
                + "\nDEVICE: " + Build.DEVICE
                + "\nDISPLAY: " + Build.DISPLAY
                + "\nHOST: " + Build.HOST
                + "\nID: " + Build.ID
                + "\nMANUFACTURER: " + Build.MANUFACTURER
                + "\nMODEL: " + Build.MODEL
                + "\nPRODUCT: " + Build.PRODUCT
                + "\nTAGS: " + Build.TAGS
                + "\nTYPE: " + Build.TYPE
                + "\nSERIAL: " + Build.SERIAL
                + "\nUSER: " + Build.USER;
    }

    public static String getDeviceId2(Context context){
        TelephonyManager tm = (TelephonyManager)context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = tm.getDeviceId();
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidId) || TextUtils.isEmpty(tmDevice))
            return "androidId 为空";
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32));
        return deviceUuid.toString();
    }
}
