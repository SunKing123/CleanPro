package com.xiaoniu.cleanking.ui.usercenter.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.receiver.HomeKeyEventBroadCastReceiver;
import com.xiaoniu.cleanking.ui.usercenter.service.FloatingImageDisplayService;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.ToastUtils;

import butterknife.BindView;

/**
 * 权限列表页面
 */
public class PermissionActivity extends SimpleActivity {

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.line_permiht)
    LinearLayout line_permiht;
    @BindView(R.id.line_xfc)
    LinearLayout line_xfc;
    @BindView(R.id.line_dingwei)
    LinearLayout line_dingwei;

    @Override
    public int getLayoutId() {
        return R.layout.activity_permision;
    }


    @Override
    public void initView() {
        register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        line_permiht.setOnClickListener(v -> {
            Uri packageURI = Uri.parse("package:" + getPackageName());
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(intent);
        });
        line_xfc.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(PermissionActivity.this)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
                ToastUtils.showShort("当前无权限，请授权");
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivity(intent);
                FloatingImageDisplayService.imageRes = new int[]{R.mipmap.icon_per2, R.mipmap.icon_per3, R.mipmap.icon_per4};
                FloatingImageDisplayService.imageWidth = new int[]{275, 275, 275};
                FloatingImageDisplayService.imageHeight = new int[]{186, 186, 206};
                Intent intent1 = new Intent(this, FloatingImageDisplayService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent1);
                } else {
                    startService(intent1);
                }
//                startService(new Intent(PermissionActivity.this, FloatingImageDisplayService.class));
            }
        });
        line_dingwei.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
//            if (Build.VERSION.SDK_INT >= 23 &&!Settings.canDrawOverlays(PermissionActivity.this)) {
//                Toast.makeText(PermissionActivity.this, "当前无权限，请授权", Toast.LENGTH_SHORT);
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//                startActivity(intent);
//                FloatingImageDisplayService.imageRes = new int[]{R.mipmap.icon_ht1, R.mipmap.icon_ht2, R.mipmap.icon_ht3, R.mipmap.icon_ht4};
//                FloatingImageDisplayService.imageWidth = new int[]{275, 275, 275, 275};
//                FloatingImageDisplayService.imageHeight = new int[]{186, 186, 206, 186};
//                startService(new Intent(PermissionActivity.this, FloatingImageDisplayService.class));
//            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(PermissionActivity.this, FloatingImageDisplayService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterScreenActionReceiver(this);
    }

    private boolean isRegisterReceiver = false;
    private HomeKeyEventBroadCastReceiver homeKeyEventBroadCastReceiver = new HomeKeyEventBroadCastReceiver();

    /**
     * 广播注册
     *
     * @param mContext 上下文对象
     */
    public void register(Context mContext) {
        if (!isRegisterReceiver) {
            isRegisterReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mContext.registerReceiver(homeKeyEventBroadCastReceiver, filter);
        }
    }

    /**
     * 广播注销
     *
     * @param mContext 上下文对象
     */
    public void unRegisterScreenActionReceiver(Context mContext) {
        if (isRegisterReceiver) {
            isRegisterReceiver = false;
            mContext.unregisterReceiver(homeKeyEventBroadCastReceiver);
        }
    }

}
