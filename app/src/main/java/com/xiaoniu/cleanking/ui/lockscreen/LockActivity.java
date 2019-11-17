package com.xiaoniu.cleanking.ui.lockscreen;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.ViewUtils;
import com.xiaoniu.cleanking.widget.lockview.TouchToUnLockView;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 锁屏信息流广告页面
 */
public class LockActivity extends AppCompatActivity implements View.OnClickListener {
    private TouchToUnLockView mUnlockView;
    private ImageView lockDial;
    private ImageView lockCamera;
    private LockExitDialog lockExitDialog;
    private ImageView batteryIcon;
    private TextView mLockTime, mLockDate;
    private SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    private RxPermissions rxPermissions;
    //    private FrameLayout videoContainer;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLockerWindow(getWindow());
        setContentView(R.layout.activity_lock);
        initView();
    }

    private void initView() {
        mLockTime = ViewUtils.get(this, R.id.lock_time_txt);
        mLockDate = ViewUtils.get(this, R.id.lock_date_txt);
        mUnlockView = ViewUtils.get(this, R.id.lock_unlock_view);
        mUnlockView.setOnTouchToUnlockListener(new TouchToUnLockView.OnTouchToUnlockListener() {
            @Override
            public void onTouchLockArea() {
            }

            @Override
            public void onSlidePercent(float percent) {

            }

            @Override
            public void onSlideToUnlock() {
                finish();
            }

            @Override
            public void onSlideAbort() {
            }
        });
        registerLockerReceiver();
//
      /*  rxPermissions = new RxPermissions(this);
        lockExitDialog = new LockExitDialog(this);
        lockDial = ViewUtils.get(this, R.id.lock_dial);
        lockCamera = ViewUtils.get(this, R.id.lock_camera);
        lockSetting = ViewUtils.get(this, R.id.lock_settings);
        batteryIcon = ViewUtils.get(this, R.id.lock_battery_icon);

//        videoContainer = ViewUtils.get(this, R.id.lock_video_container);

        lockCamera.setOnClickListener(this);
        lockDial.setOnClickListener(this);
        lockSetting.setOnClickListener(this);

        batteryIcon.setImageResource(PowerUtil.isCharging(null) ? R.drawable.lock_battery_charging : R.drawable.lock_battery_normal);*/

      /*  VideoFlowListFragment videoFlowListFragment = new VideoFlowListFragment();
        videoFlowListFragment.setCategroyId(-1, true, "锁屏通知页", 0);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lock_video_container, videoFlowListFragment)
                .commitAllowingStateLoss();*/
//        checkLockState();
    }

    private String content = null;

   /* private void checkLockState() {
        if (rxPermissions == null) {
            return;
        }
        if (rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE) && rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            executorService.execute(() -> {
                content = FileUtils.readTextFile();
                runOnUiThread(() -> {
                    if (!TextUtils.isEmpty(content) && content.contains("com.geek.lw")) {
                        finish();
                    }
                });
                FileUtils.writeTextFile(getPackageName());
            });
        } else {
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe((isGranted) -> {
                if (isGranted) {
                    executorService.execute(() -> {
                        content = FileUtils.readTextFile();
                        runOnUiThread(() -> {
                            if (!TextUtils.isEmpty(content) && content.contains("com.geek.qukan.video")) {
                                finish();
                            }
                        });
                        FileUtils.writeTextFile(getPackageName());
                    });
                } else {
                    LogUtils.i("permission dined");
                }
            });
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mUnlockView.startAnim();
        updateTimeUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUnlockView.stopAnim();

    }

    private void updateTimeUI() {
        mLockTime.setText(DateUtils.getHourString(this, System.currentTimeMillis()));
        mLockDate.setText(monthFormat.format(GregorianCalendar.getInstance().getTime()) + " " + weekFormat.format(GregorianCalendar.getInstance().getTime()));
    }

    private void setLockerWindow(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT > 18) {
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.setAttributes(lp);
        window.getDecorView().setSystemUiVisibility(0x0);

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
         /*     case R.id.lock_dial:
                emergencyDial();
                break;
            case R.id.lock_camera:
                openCamera();
                break;
          case R.id.lock_settings:
                if (lockExitDialog != null && !lockExitDialog.isShowing() && !isFinishing()) {
                    lockExitDialog.show();
                }
                break;*/
        }
    }

    protected UIChangingReceiver mUIChangingReceiver;

    public void registerLockerReceiver() {
        if (mUIChangingReceiver != null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        mUIChangingReceiver = new UIChangingReceiver();
        registerReceiver(mUIChangingReceiver, filter);
    }

    public void unregisterLockerReceiver() {
        if (mUIChangingReceiver == null) {
            return;
        }
        unregisterReceiver(mUIChangingReceiver);
        mUIChangingReceiver = null;
    }


    private class UIChangingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (!TextUtils.isEmpty(action)) {
                    if (action.equals(Intent.ACTION_TIME_TICK)) {
                        updateTimeUI();
                    }
                    /*else if (action.equals(Intent.ACTION_POWER_CONNECTED) || action.equals(Intent.ACTION_BATTERY_CHANGED)
                            || action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                        batteryIcon.setImageResource(PowerUtil.isCharging(intent) ? R.drawable.lock_battery_charging : R.drawable.lock_battery_normal);
                    }*/
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLockerReceiver();
//
     /*   executorService.execute(() -> {
            FileUtils.writeTextFile("stop");
        });*/
    }

    private void emergencyDial() {
        try {
            Intent intent = new Intent();
            intent.setAction("com.android.phone.EmergencyDialer.DIAL");
            startActivity(intent);
        } catch (Exception e) {
            LogUtils.e("dial error:" + e.getMessage());
        }
    }

    private void openCamera() {
        try {
            if (!rxPermissions.isGranted(Manifest.permission.CAMERA) || !rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe((isGranted) -> {
                    if (isGranted) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivity(intent);
                    } else {
                        ToastUtils.showShort("相机权限拒绝");
                    }
                });
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        } catch (Exception e) {
            LogUtils.e("open camera error:" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
