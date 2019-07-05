package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.base.SimpleActivity;
import com.xiaoniu.cleanking.ui.main.bean.HardwareInfo;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Administrator
 */
@Route(path = RouteConstants.HARDWARE_INFO_ACTIVITY)
public class HardwareInfoActivity extends SimpleActivity {

    @BindView(R.id.tv_title)
    TextView mTextTitle;
    @BindView(R.id.text_battery_status)
    TextView mTextBatteryStatus;
    @BindView(R.id.text_battery_level)
    TextView mTextBatteryLevel;
    @BindView(R.id.text_bluetooth_status)
    TextView mTextBluetoothStatus;
    @BindView(R.id.text_gps_status)
    TextView mTextGpsStatus;
    @BindView(R.id.text_wifi_status)
    TextView mTextWifiStatus;
    @BindView(R.id.text_cpu_type)
    TextView mTextCpuType;
    @BindView(R.id.text_cpu_cores)
    TextView mTextCpuCores;
    @BindView(R.id.text_cpu_load)
    TextView mTextCpuLoad;
    @BindView(R.id.text_bluetooth_close)
    TextView mTextBluetoothClose;
    @BindView(R.id.text_gps_close)
    TextView mTextGpsClose;
    @BindView(R.id.text_wifi_close)
    TextView mTextWifiClose;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hardware_info;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_4690FD), false);
        }

        HardwareInfo hardwareInfo = (HardwareInfo) getIntent().getSerializableExtra("content");

        mTextTitle.setText(hardwareInfo.getSize() + "个导致发热的部件");

        initData(hardwareInfo);
    }

    private void initData(HardwareInfo hardwareInfo) {
        //充电状态
        mTextBatteryStatus.setText("充电状态:  " + (hardwareInfo.isCharge() ? "充电中" : "未充电"));
        //电量剩余
        mTextBatteryLevel.setText("电量剩余:  " + hardwareInfo.getBatteryLevel());
        //蓝牙模块
        mTextBluetoothStatus.setText(hardwareInfo.isBluetoothOpen() ? "已开启" : "已关闭");
        mTextBluetoothStatus.setTextColor(hardwareInfo.isBluetoothOpen() ? getResources().getColor(R.color.color_333333) : getResources().getColor(R.color.color_CCCCCC));
        mTextBluetoothClose.setVisibility(hardwareInfo.isBluetoothOpen() ? View.VISIBLE : View.GONE);
        //GPS开关
        mTextGpsStatus.setText(hardwareInfo.isGPSOpen() ? "已开启" : "已关闭");
        mTextGpsStatus.setTextColor(hardwareInfo.isGPSOpen() ? getResources().getColor(R.color.color_333333) : getResources().getColor(R.color.color_CCCCCC));
        mTextGpsClose.setVisibility(hardwareInfo.isGPSOpen() ? View.VISIBLE : View.GONE);
        //WIFI开关
        mTextWifiStatus.setText(hardwareInfo.isWiFiOpen() ? "已开启" : "已关闭");
        mTextWifiStatus.setTextColor(hardwareInfo.isWiFiOpen() ? getResources().getColor(R.color.color_333333) : getResources().getColor(R.color.color_CCCCCC));
        mTextWifiClose.setVisibility(hardwareInfo.isWiFiOpen() ? View.VISIBLE : View.GONE);
        //CPU型号
        mTextCpuType.setText("CPU型号:  " + hardwareInfo.getCPUType());
        //CPU核心数
        mTextCpuCores.setText("CPU核心数:  " + hardwareInfo.getCPUCore());
        //CPU负载
        mTextCpuLoad.setText("CPU负载:  " + hardwareInfo.getCPULoad());
    }

    @OnClick({R.id.img_back})
    public void onBackPress(View view) {
        finish();
    }

    @OnClick({R.id.text_bluetooth_close, R.id.text_gps_close, R.id.text_wifi_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_bluetooth_close:
                break;
            case R.id.text_gps_close:
                break;
            case R.id.text_wifi_close:
                break;
        }
    }
}
