package com.xiaoniu.cleanking.ui.localpush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.text.HtmlCompat;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.scheme.Constant.SchemeConstant;
import com.xiaoniu.cleanking.scheme.SchemeProxy;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.lang.reflect.Field;


public class LocalPushWindow {

    private Toast mToast;
    private Handler mHandler = new Handler();
    private boolean canceled = true;
    String urlSchema;

    public LocalPushWindow(Context context, LocalPushConfigModel.Item item) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //自定义布局
        @SuppressLint("InflateParams")
        View mView = inflater.inflate(R.layout.dialog_local_push_layout, null);

        AppCompatImageView icon = mView.findViewById(R.id.logo);
        if (!TextUtils.isEmpty(item.getIconUrl())) {
            GlideUtils.loadRoundImage(context, item.getIconUrl(), icon, 20);
        }

        AppCompatTextView title = mView.findViewById(R.id.title);

        AppCompatTextView content = mView.findViewById(R.id.content);
        content.setText(item.getContent());

        AppCompatButton button = mView.findViewById(R.id.button);
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

        //自定义toast文本
        mView.findViewById(R.id.button).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(urlSchema)) {
                StatisticsUtils.trackClick("local_push_window_click", "本地推送弹窗点击", "", "local_push_window");
                hide();
                SchemeProxy.openScheme(context, urlSchema);
            }
        });
        if (mToast == null) {
            mToast = new Toast(context);
        }
        //设置toast居中显示
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(mView);

        try {
            Object mTN;
            mTN = getField(mToast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    //显示与隐藏动画
                    //  params.windowAnimations = R.style.ClickToast;
                    //Toast可点击
                    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                    //设置viewgroup宽高
                    // params.width = WindowManager.LayoutParams.MATCH_PARENT; //设置Toast宽度为屏幕宽度
                    //  params.height = WindowManager.LayoutParams.WRAP_CONTENT; //设置高度
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 自定义居中显示toast
     */
    public void show() {
        mToast.show();
    }

    public Toast getToast() {
        return mToast;
    }

    /**
     * 自定义时长、居中显示toast
     *
     * @param duration 单位毫秒ms
     */
    public void show(int duration) {
        TimeCount mTimeCount = new TimeCount(duration, 1000);
        if (canceled) {
            mTimeCount.start();
            canceled = false;
            showUntilCancel();
        }
    }

    /**
     * 隐藏toast
     */
    private void hide() {
        if (mToast != null) {
            mToast.cancel();
        }
        canceled = true;
    }

    private void showUntilCancel() {
        if (canceled) { //如果已经取消显示，就直接return
            return;
        }
        mToast.show();
        mHandler.postDelayed(this::showUntilCancel, Toast.LENGTH_LONG);
    }

    /**
     * 自定义计时器
     */
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); //millisInFuture总计时长，countDownInterval时间间隔(一般为1000ms)
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            hide();
        }
    }


    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
