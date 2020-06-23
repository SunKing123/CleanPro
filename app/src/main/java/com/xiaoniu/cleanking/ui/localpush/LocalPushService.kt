package com.xiaoniu.cleanking.ui.localpush

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.update.MmkvUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil

class LocalPushService : Service() {

    private var mBatteryPower = 50 //当前电量监控

    private var temp = 30 //点前电池温度

    private var isCharged = false //是否为充电状态
    private var batteryReceiver: BroadcastReceiver? = null
    private val mPushBinder = PushBinder(this)

    override fun onCreate() {
        super.onCreate()
        LogUtils.e("=========localPushService onCreate")
        checkChargeState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.e("=========localPushService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        LogUtils.e("=========localPushService onBind")
        return mPushBinder
    }

    fun showPopActivity(lastPressHomeTime: Long) {
        val lastAppPressHomeTime = MmkvUtil.getLong(SpCacheConfig.KEY_LAST_CLEAR_APP_PRESSED_HOME, 0L)
        if (lastAppPressHomeTime > 0) {
            val current = System.currentTimeMillis()
            val period: Long = current / 1000 - lastAppPressHomeTime / 1000
            if (period < 10 * 60) {
            //if (period < 10) {
                LogUtils.e("====LocalPushService 距离上次清理APP触发Home键过了" + period + "秒小于限制时间，直接返回")
                return
            }
        }
        showLocalPushAlertWindow(lastPressHomeTime)
    }


    private fun showLocalPushAlertWindow(homePressTime: Long) {

        //1.读取本地缓存的推送配置Config列表
        val map = PreferenceUtil.getLocalPushConfig()

        //2.判断【一键加速】功能是否满足推送条件
        val speedItem = map[LocalPushType.TYPE_SPEED_UP]
        if (speedItem != null) {
            if (LocalPushUtils.getInstance().allowPopSpeedUp(speedItem)) {
                LogUtils.e("===LocalPushService 允许弹出speed的window")
                // WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, speedItem);
                startPopActivity(baseContext, homePressTime, speedItem)
                return
            } else {
                LogUtils.e("===LocalPushService 不允许弹出speed的window")
            }
        } else {
            LogUtils.e("=====LocalPushService speedItem为空")
        }


        //3.判断【垃圾清理】功能是否满足推送条件
        val clearItem = map[LocalPushType.TYPE_NOW_CLEAR]
        if (clearItem != null && LocalPushUtils.getInstance().allowPopClear(clearItem)) {
            LogUtils.e("===LocalPushService 允许弹出clear的window")
            // WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, clearItem);
            startPopActivity(baseContext, homePressTime, clearItem)
            return
        }

        //4.判断【手机降温】功能是否满足推送条件
        val coolItem = map[LocalPushType.TYPE_PHONE_COOL]
        if (coolItem != null) {
            if (LocalPushUtils.getInstance().allowPopCool(coolItem)) {
                LogUtils.e("===LocalPushService 允许弹出cool的window")
                //  WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, coolItem);
                coolItem.localTemp = temp
                startPopActivity(baseContext, homePressTime, coolItem)
                return
            }
        }

        //5.判断【超强省电】功能是否满足推送条件
        val powerItem = map[LocalPushType.TYPE_SUPER_POWER]
        if (powerItem != null) {
            val mBatteryPower = getCurrentBattery()
            if (LocalPushUtils.getInstance().allowPopPowerSaving(powerItem, isCharged, mBatteryPower)) {
                LogUtils.e("===LocalPushService 允许弹出power的window")
                // WindowUtil.getInstance().showWindowWhenDelayTwoSecond(context, homePressTime, powerItem);
                powerItem.localPower = mBatteryPower
                startPopActivity(baseContext, homePressTime, powerItem)
            }
        }
    }


    /* private void showToastPopWindow(Context context, Long homePressTime, LocalPushConfigModel.Item item) {
        StatisticsUtils.customTrackEvent("local_push_window_custom", "推送弹窗满足推送时机弹窗创建时", "", "local_push_window");
        long current = System.currentTimeMillis();
        long period = current / 1000 - homePressTime / 1000;
        LocalPushWindow toast = new LocalPushWindow(context, item);
        if (period >= 3) {
            toast.show(1000 * 20);
        } else {
            new Handler().postDelayed(() -> {
                toast.show(1000 * 20);
            }, (3 - period) * 1000);
        }
    }*/
    private fun startPopActivity(context: Context, homePressTime: Long, item: LocalPushConfigModel.Item) {
        val screenIntent = Intent()
        screenIntent.setClassName(context.packageName, SchemeConstant.StartFromClassName.CLASS_LOCAL_PUSH_ACTIVITY)
        screenIntent.putExtra("config", item)
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        val current = System.currentTimeMillis()
        val period = current / 1000 - homePressTime / 1000
        //本来是延迟3秒后弹出，但是启动Activity时间较长，所以这里改成2秒(popup window里面延迟了一秒，所以这里先不延迟)
        context.startActivity(screenIntent)
    }


    //判断是否充电
    private fun checkChargeState() {
        try {
            var usb: Boolean //usb充电
            var ac: Boolean //交流电
            var wireless = false //无线充电
            var chargePlug = -1
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            if (batteryReceiver == null) {
                batteryReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {


                        //获取当前电量，如未获取具体数值，则默认为0
                        val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                        //获取最大电量，如未获取到具体数值，则默认为100
                        val batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                        mBatteryPower = batteryLevel * 100 / batteryScale
                        LogUtils.e("=====进入了OnReceive====${mBatteryPower}")
                        //获取当前电池温度
                        temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                        val i = temp / 10
                        temp = if (i > 0) i else 30 + NumberUtils.mathRandomInt(1, 3)
                    }
                }
            }
            //注册接收器以获取电量信息
            val powerIntent = registerReceiver(batteryReceiver, iFilter)
            powerIntent?.let {
                //----判断是否为充电状态-------------------------------
                chargePlug = powerIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                usb = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
                ac = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
                //无线充电---API>=17
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wireless = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
                }
                LogUtils.e("=====usp:${usb}======ac:${ac}=====wireless:${wireless}")
//            Logger.i(SystemUtils.getProcessName(this) + "zz--" + (usb ? "usb" : ac ? "ac" : wireless ? "wireless" : ""));
                isCharged = usb || ac || wireless
            }

        } catch (e: Exception) {
            e.printStackTrace()
            isCharged = false
        }


        //更新sp当前充电状态
        PreferenceUtil.getInstants().saveInt(SpCacheConfig.CHARGE_STATE, if (isCharged) 1 else 0)
    }

    //当前电量监控
    private fun getCurrentBattery(): Int {
        try {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBatteryPower = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            }
        } catch (e: Exception) {
            mBatteryPower = 50
            e.printStackTrace()
        }
        return mBatteryPower
    }


    class PushBinder(val service: LocalPushService) : Binder() {

    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let {
            unregisterReceiver(it)
        }
    }
}