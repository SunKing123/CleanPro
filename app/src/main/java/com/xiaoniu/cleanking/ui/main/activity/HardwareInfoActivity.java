package com.xiaoniu.cleanking.ui.main.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppApplication;
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

    @BindView(R.id.layout_title_bar)
    RelativeLayout mLayoutTitleBar;
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

    public static final int REQUEST_CODE_BLUETOOTH = 1001;
    public static final int REQUEST_CODE_LOCATION = 1002;
    public static final int REQUEST_CODE_WIFI = 1003;

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
        if (TextUtils.isEmpty(hardwareInfo.getCPULoad())) {
            mTextCpuLoad.setText("CPU负载:  0%");
        } else {
            mTextCpuLoad.setText("CPU负载:  " + hardwareInfo.getCPULoad());
        }
    }

    @OnClick({R.id.img_back})
    public void onBackPress(View view) {
        finish();
    }

    @OnClick({R.id.text_bluetooth_close, R.id.text_gps_close, R.id.text_wifi_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_bluetooth_close:
                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), REQUEST_CODE_BLUETOOTH);
                break;
            case R.id.text_gps_close:
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_LOCATION);
                break;
            case R.id.text_wifi_close:
                //关闭wifi
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_CODE_WIFI);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (!isBluetoothOpen()) {
                mTextBluetoothStatus.setText("已关闭");
                mTextBluetoothStatus.setTextColor(getResources().getColor(R.color.color_CCCCCC));
                mTextBluetoothClose.setVisibility(View.GONE);
            }
        } else if (requestCode == REQUEST_CODE_LOCATION) {
            if (!isGPSOPen()) {
                mTextGpsStatus.setText("已关闭");
                mTextGpsStatus.setTextColor(getResources().getColor(R.color.color_CCCCCC));
                mTextGpsClose.setVisibility(View.GONE);
            }
        } else if (requestCode == REQUEST_CODE_WIFI) {
            if (!isWifiOpen()) {
                mTextWifiStatus.setText("已关闭");
                mTextWifiStatus.setTextColor(getResources().getColor(R.color.color_CCCCCC));
                mTextWifiClose.setVisibility(View.GONE);
            }
        }
    }

    /**
     * gps是否开启
     *
     * @return
     */
    public boolean isGPSOPen() {
        LocationManager locationManager
                = (LocationManager) AppApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 是否wifi开启
     *
     * @return
     */
    private boolean isWifiOpen() {
        WifiManager wifiManager = (WifiManager) AppApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return wifiManager.isWifiEnabled();
    }

    /**
     * 是否蓝牙开启
     *
     * @return
     */
    private boolean isBluetoothOpen() {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            return blueAdapter.isEnabled();
        }
        return false;
    }
}
