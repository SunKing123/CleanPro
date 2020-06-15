package com.xiaoniu.cleanking.ui.localpush;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.text.HtmlCompat;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.utils.StatisticsUtils;


public class PopPushActivity extends AppCompatActivity {

    String urlSchema;

    private Handler mHandle = new Handler();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            finish();
        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(android.R.color.transparent), true);
        } else {
            StatusBarCompat.setStatusBarColor(this, getResources().getColor(android.R.color.transparent), false);
        }

        ActivityCollector.addActivity(this, PopPushActivity.class);

        StatisticsUtils.customTrackEvent("local_push_window_custom", "推送弹窗满足推送时机弹窗创建时", "", "local_push_window");

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
        setContentView(R.layout.activity_pop_layout);

        LocalPushConfigModel.Item item = (LocalPushConfigModel.Item) getIntent().getSerializableExtra("config");


        AppCompatImageView icon = findViewById(R.id.logo);
        if (!TextUtils.isEmpty(item.getIconUrl())) {
            GlideUtils.loadRoundImage(this, item.getIconUrl(), icon, 20);
        }

        AppCompatTextView title = findViewById(R.id.title);

        AppCompatTextView content = findViewById(R.id.content);
        content.setText(item.getContent());

        AppCompatButton button = findViewById(R.id.button);
        switch (item.getOnlyCode()) {
            case LocalPushType.TYPE_NOW_CLEAR:
                urlSchema = SchemeConstant.LocalPushScheme.SCHEME_NOWCLEANACTIVITY;
                long size = PreferenceUtil.getLastScanRubbishSize();
                button.setText("立即清理");
                title.setText(HtmlCompat.fromHtml(item.getTitle().replace("#", "<font color='#FF4545'><b>" + size + "MB</b></font>"), HtmlCompat.FROM_HTML_MODE_COMPACT));
                break;
            case LocalPushType.TYPE_SPEED_UP:
                urlSchema = SchemeConstant.LocalPushScheme.SCHEME_PHONEACCESSACTIVITY;
                int random = NumberUtils.mathRandomInt(70, 85);
                title.setText(HtmlCompat.fromHtml(item.getTitle().replace("#", "<font color='#FF4545'><b>" + random + "%</b></font>"), HtmlCompat.FROM_HTML_MODE_COMPACT));
                button.setText("一键加速");
                break;
            case LocalPushType.TYPE_PHONE_COOL:
                urlSchema = SchemeConstant.LocalPushScheme.SCHEME_PHONECOOLINGACTIVITY;
                title.setText(HtmlCompat.fromHtml(item.getTitle().replace("#", "<font color='#FF4545'><b>" + item.getLocalTemp() + "°</b></font>"), HtmlCompat.FROM_HTML_MODE_COMPACT));
                button.setText("一键降温");
                break;
            case LocalPushType.TYPE_SUPER_POWER:
                title.setText(HtmlCompat.fromHtml(item.getTitle().replace("#", "<font color='#FF4545'><b>" + item.getLocalPower() + "%</b></font>"), HtmlCompat.FROM_HTML_MODE_COMPACT));
                button.setText("立即省电");
                urlSchema = SchemeConstant.LocalPushScheme.SCHEME_PHONESUPERPOWERACTIVITY;
                break;
            default:
                break;
        }

        //更新上次弹框的时间
        LocalPushUtils.getInstance().updateLastPopTime(item.getOnlyCode());
        //更新当天弹框的次数
        LocalPushUtils.getInstance().autoIncrementDayLimit(item.getOnlyCode());

        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!TextUtils.isEmpty(urlSchema)) {
                    StatisticsUtils.trackClick("local_push_window_click", "本地推送弹窗点击", "", "local_push_window");
                    SchemeProxy.openScheme(PopPushActivity.this, urlSchema);
                    finish();
                }
            }
            return true;
        });
        mHandle.postDelayed(this::finish, 5000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
