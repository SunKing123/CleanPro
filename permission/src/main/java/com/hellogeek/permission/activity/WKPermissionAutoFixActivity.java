package com.hellogeek.permission.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.R;
import com.hellogeek.permission.R2;
import com.hellogeek.permission.base.BaseActivity;
import com.hellogeek.permission.bean.ASBase;
import com.hellogeek.permission.bean.CustomSharedPreferences;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.server.AccessibilityServiceMonitor;
import com.hellogeek.permission.server.interfaces.IAccessibilityServiceMonitor;
import com.hellogeek.permission.statusbarcompat.StatusBarCompat;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.ExternalInterface;
import com.hellogeek.permission.strategy.IGetManfactureExample;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.strategy.ServiceEvent;
import com.hellogeek.permission.usagerecord.UsageBuider;
import com.hellogeek.permission.usagerecord.UsageRecordHelper;
import com.hellogeek.permission.usagerecord.UsageRecordType;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.Constant;
import com.hellogeek.permission.util.DialogUtil;
import com.hellogeek.permission.util.PermissionConvertUtils;
import com.hellogeek.permission.util.PhoneRomUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static com.hellogeek.permission.util.Constant.PACKAGE_USAGE_STATS;
import static com.hellogeek.permission.util.Constant.PROVIDER_BACKSTAGEPOPUP;
import static com.hellogeek.permission.util.Constant.PROVIDER_LOCKDISPLAY;
import static com.hellogeek.permission.util.Constant.PROVIDER_NECESSARY_PERMISSIONALLOPEN;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTICEOFTAKEOVER;
import static com.hellogeek.permission.util.Constant.PROVIDER_NOTIFICATIONBAR;
import static com.hellogeek.permission.util.Constant.PROVIDER_PERMISSIONALLOPEN;
import static com.hellogeek.permission.util.Constant.PROVIDER_REPLACEACLLPAGE;
import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;
import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;
import static com.hellogeek.permission.util.Constant.PROVIDER_SYSTEMSETTING;


/**
 * @ProjectName: clean
 * @Package: com.hellogeek.permission.activity
 * @ClassName: WKPermissionAutoFixActivity
 * @Description: 悟空权限自适应修复
 * @Author: LiDing
 * @CreateDate: 2020/5/13 10:02
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/13 10:02
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class WKPermissionAutoFixActivity extends BaseActivity implements IAccessibilityServiceMonitor {
    public static final String TAG = PermissionAutoFixActivity.class.getSimpleName();

    @BindView(R2.id.rl_normal)
    RelativeLayout rlNormal;

    @BindView(R2.id.rl_loading)
    RelativeLayout rlLoading;

    @BindView(R2.id.rl_success)
    RelativeLayout rlSuccess;

    @BindView(R2.id.iv_gear)
    ImageView ivGear;

    @BindView(R2.id.iv_loading)
    ImageView ivLoading;

    @BindView(R2.id.tvFix)
    TextView tvFix;

    private final int ACCESSIBILITY_SETTINGS = 1000;
    private Boolean isOneRepair = true;
    @BindView(R2.id.listFixHint)
    RecyclerView listFixHint;

    BaseQuickAdapter mAdapter;
    @BindView(R2.id.tv_pb_text)
    TextView tvPbText;

    @BindView(R2.id.tv_risk_num)
    TextView tvRiskNum;
    Integer risksNumber = 4;

    private static final int mStartAFMGuide = 1000;
    private AutoFixAction autoFixAction;
    private AccessibilityServiceMonitor mService;
    public static final String PAGE = "authority_repair_page";
    private String sourcePage;

    @Override
    protected void initParams(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Constant.inExecution = false;
        isFixing = false;
        if (PermissionIntegrate.getPermission().getPermissionCallBack() != null) {
            PermissionIntegrate.getPermission().getPermissionCallBack().permissionSetSuccess(isAllOpen);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBack = false;
    }

    private boolean isBack = false;


    @Override
    protected int getLayoutResID() {
        return R.layout.activity_wk_permission_auto_fix;
    }

    @Override
    protected void initContentView() {
        sourcePage = PermissionIntegrate.getPermission().getPermissionSourcePage();
        Constant.inExecution = true;
        StatusBarCompat.translucentStatusBarForImage(this, true, false);
        EventBus.getDefault().register(this);
        listFixHint.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listFixHint.setAdapter(mAdapter = new BaseQuickAdapter<ASBase, BaseViewHolder>(R.layout.wk_per_item_auto_fix_hint) {
            @Override
            protected void convert(BaseViewHolder helper, ASBase item) {
                if (item == null) {
                    return;
                }
                helper.setText(R.id.hintText, PermissionConvertUtils.getTitleStr(item.permission));
                helper.setText(R.id.hintDesc, "需要开启" + item.permission.getName() + "权限");

                helper.getView(R.id.hintIcon).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                // int defaultColor = PermissionIntegrate.getPermission().getPermissionDefaultColor() != 0 ? PermissionIntegrate.getPermission().getPermissionDefaultColor() : getResources().getColor(R.color.permission_title);
                // int openColor = PermissionIntegrate.getPermission().getPermissionOpenColor() != 0 ? PermissionIntegrate.getPermission().getPermissionOpenColor() : Color.parseColor("#FF3B8E");
                // helper.setTextColor(R.id.hintText, defaultColor);

                ImageView hintIcon = helper.getView(R.id.hintIcon);
                hintIcon.setImageResource(PermissionConvertUtils.getRes(item.permission));
                ImageView allowIcon = helper.getView(R.id.allowIcon);
                Button openBtn = helper.getView(R.id.btn_open);
                if (item.isAllow) {
                    risksNumber--;
                }
                tvRiskNum.setText(risksNumber + "");

                if (isOneRepair || item.isAllow) {
                    allowIcon.setVisibility(View.VISIBLE);
                    openBtn.setVisibility(View.GONE);
                    allowIcon.setImageResource(item.isAllow ? R.mipmap.permission_icon : R.mipmap.not_permission_icon);
                } else {
                    allowIcon.setVisibility(View.GONE);
                    openBtn.setVisibility(View.VISIBLE);
                    openBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 请求指定权限
                            Toast.makeText(WKPermissionAutoFixActivity.this, "请开启，" + item.permission.getName() + "权限！", Toast.LENGTH_LONG).show();
                            request(item.permission);
                        }
                    });
                }

                if (risksNumber == 0) {
                    rlLoading.setVisibility(View.GONE);
                    rlSuccess.setVisibility(View.VISIBLE);
                    tvFix.setText(getString(R.string.use_now));
                }

            }
        });

        new UsageBuider().setUsageType(UsageRecordType.TYPE_VIEW_PAGE.getValue())
                .setPage(PAGE)
                .setSourcePage(sourcePage)
                .setEventCode("authority_repair_page_view_page")
                .setEventName("权限一键修复页面浏览")
                .send();

    }

    private List<ASBase> base;
    private boolean isAllOpen = true;
    private int successNum = 0;

    private void setPermissionList() {
        base = new ArrayList<>();
        if (autoFixAction != null) {
            ArrayList<Permission> list = autoFixAction.getPermissionList();
            for (Permission permission : list) {
                ASBase asBase = new ASBase();
                asBase.permission = permission;
                asBase.isNecessary = isNecessary(permission);
                asBase.isAllow = AccessibilitUtil.isOpenPermission(this, permission);
                isAllOpen = isAllOpen & asBase.isAllow;
                if (!asBase.isAllow) {
                    successNum++;
                }
                base.add(asBase);
            }
            if (base != null) {
                risksNumber = base.size();
                mAdapter.setNewData(base);
            }
        }
    }


    private boolean isNecessary(Permission permission) {
        switch (permission) {
            case NOTIFICATIONBAR:
                return false;
            case REPLACEACLLPAGE:
                return PhoneRomUtils.isHuawei() ? true : false;
            case BACKSTAGEPOPUP:
                return (PhoneRomUtils.isVivo() || PhoneRomUtils.isXiaoMi()) ? true : false;
            case SYSTEMSETTING:
                return true;

            case SELFSTARTING:
                return (PhoneRomUtils.isOppo()) ? true : false;

            case LOCKDISPALY:
                return (PhoneRomUtils.isVivo() || PhoneRomUtils.isXiaoMi()) ? true : false;
            case SUSPENDEDTOAST:
                return true;
            case NOTICEOFTAKEOVER:
                return false;
        }
        return false;
    }

    @Override
    protected void initData() {
        autoFixAction = IGetManfactureExample.getManfactureExample(this);
        setPermissionList();
        if (CustomSharedPreferences.getBooleanValue(this, CustomSharedPreferences.isPermissionShow)) {
            openSuccessExhibition(isAllOpen, true);
        }
    }

    @Override
    protected boolean onGoBack() {
        return false;
    }

    @OnClick({R2.id.tvFix})  // , R2.id.ivClose, R2.id.tv_add_qq
    public void onclickView(View view) {
        int id = view.getId();
        if (isAllOpen) {
            finish();
        }
        if (id == R.id.tvFix) {
            if (autoFixAction == null) {
                return;
            }
            boolean isAllopen = false;
            if (PermissionIntegrate.getPermission().getIsNecessary()) {
                isAllopen = !ExternalInterface.getInstance(this).isOpenNecessaryPermission(this);
            } else {
                isAllopen = !ExternalInterface.getInstance(this).isOpenAllPermission(this);
            }


            if (isAllopen) {
                if (!AccessibilitUtil.isAccessibilitySettingsOn(this, AccessibilityServiceMonitor.class.getCanonicalName())) {
                    handler.sendEmptyMessage(1);
                    AccessibilitUtil.showSettingsUIV2(this, ACCESSIBILITY_SETTINGS);
                    isFixing = true;
                } else {
                    if (AccessibilityServiceMonitor.getInstance() != null) {
                        startAnimation();
                        if (mService == null) {
                            mService = AccessibilityServiceMonitor.getInstance();
                            mService.setAccessibilityEvent(this);
                        }

                        if (autoFixAction == null) {
                            autoFixAction = IGetManfactureExample.getManfactureExample(this);
                        }
                        if (autoFixAction != null) {
                            autoFixAction.configAccessbility(mService);
                            request(null);
                            isFixing = true;
                        }
                    } else {
                        AccessibilitUtil.showSettingsUIV2(this, ACCESSIBILITY_SETTINGS);
                        handler.sendEmptyMessage(1);
                        isFixing = true;
                    }
                }
            }
            new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                    .setPage(PAGE)
                    .setSourcePage(sourcePage)
                    .setEventCode("once_repair_click")
                    .setEventName("权限一键修复按钮点击")
                    .setExtra("number_name_of_authority", UsageRecordHelper.getPermissionClickJson(base))
                    .send();
        } else if (id == R.id.ivClose) {
            if (isFixing) {
                new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                        .setPage(PAGE)
                        .setSourcePage(sourcePage)
                        .setEventCode("close_click")
                        .setEventName("关闭按钮点击")
                        .setExtra("authorization_result", UsageRecordHelper.getPermissionResultJson(base))
                        .send();
            }
            finish();
        } else if (id == R.id.tv_add_qq) {
            Log.i(TAG, "click add QQ");
            isFixing = false;
            if (PermissionIntegrate.getPermission().getPermissionAddQQCallback() != null) {
                new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                        .setPage(PAGE)
                        .setSourcePage(sourcePage)
                        .setEventCode("qq_feedback_click")
                        .setEventName("加QQ群点击")
                        .send();
                PermissionIntegrate.getPermission().getPermissionAddQQCallback().addQQCallback();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (isBack) {
            super.onBackPressed();
            if (isFixing) {
                new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                        .setPage(PAGE)
                        .setSourcePage(sourcePage)
                        .setEventCode("back_click")
                        .setEventName("物理返回按钮点击")
                        .setExtra("authorization_result", UsageRecordHelper.getPermissionResultJson(base))
                        .send();
            }
        }
    }

    private boolean isFixing;

    private String getPermissionProvider(Permission permission) {
        switch (permission) {
            case NOTIFICATIONBAR:
                return PROVIDER_NOTIFICATIONBAR;
            case REPLACEACLLPAGE:
                return PROVIDER_REPLACEACLLPAGE;
            case BACKSTAGEPOPUP:
                return PROVIDER_BACKSTAGEPOPUP;
            case SYSTEMSETTING:
                return PROVIDER_SYSTEMSETTING;

            case SELFSTARTING:
                return PROVIDER_SELFSTARTING;

            case LOCKDISPALY:
                return PROVIDER_LOCKDISPLAY;
            case SUSPENDEDTOAST:
                return PROVIDER_SUSPENDEDTOAST;
            case NOTICEOFTAKEOVER:
                return PROVIDER_NOTICEOFTAKEOVER;
        }
        return "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        CustomSharedPreferences.setValue(this, CustomSharedPreferences.isActivity, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBack = true;
//        EventBus.getDefault().post(new RemoveEvent());
        if (mService != null && isFixing && autoFixAction != null) {
            if (CustomSharedPreferences.getBooleanValue(this, CustomSharedPreferences.isManual) && !AccessibilitUtil.isOpenPermission(this, permission)) {
                DialogUtil.showChangeCallToolsDialog(this, PermissionConvertUtils.getTitleStr(permission), new DialogUtil.CallToolsDialogDismissListener() {
                    @Override
                    public void open() {
                        PermissionProvider.save(WKPermissionAutoFixActivity.this, getPermissionProvider(permission), true);
                        setIsOpen(new PathEvent(permission, true));
                        request(null);
                    }

                    @Override
                    public void dismiss() {
                        PermissionProvider.save(WKPermissionAutoFixActivity.this, getPermissionProvider(permission), false);
                        request(null);
                    }
                });
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        request(null);
                    }
                }, 1000);
            }
        }
    }

    Random rand = new Random();

    /**
     * 请求权限
     *
     * @param requestPermission 需要请求的权限
     */
    private void request(Permission requestPermission) {

        if (!CustomSharedPreferences.getBooleanValue(this, CustomSharedPreferences.isPermissionShow)) {
            CustomSharedPreferences.setValue(this, CustomSharedPreferences.isPermissionShow, true);
        }
        if (base == null) {
            return;
        }
        if (tvPbText != null) {
            if (getAllowCount() == base.size()) {
                tvPbText.setText(100 + "");
            } else {
                tvPbText.setText((getAllowCount() * (100 / base.size())) + "");
            }
        }
        boolean isAllDone = true;

        if (PermissionIntegrate.getPermission().getIsNecessary()) {
            for (ASBase asBase : base) {
                if (asBase.isNecessary) {
                    isAllDone = isAllDone && asBase.isAllow;
                }
            }
        } else {
            for (ASBase asBase : base) {
                isAllDone = isAllDone && asBase.isAllow;
            }
        }

        if (isAllDone) {
            isAllOpen = isAllDone;
            if (PermissionIntegrate.getPermission().getIsNecessary()) {
                PermissionProvider.save(this, PROVIDER_NECESSARY_PERMISSIONALLOPEN, true);
            } else {
                PermissionProvider.save(this, PROVIDER_PERMISSIONALLOPEN, true);
            }
            new UsageBuider().setUsageType(UsageRecordType.TYPE_CUSTOM.getValue())
                    .setPage(PAGE)
                    .setSourcePage(sourcePage)
                    .setEventCode("one_click_repair_complete")
                    .setEventName("“一键修复完成”结果上报")
                    .setExtra("authorization_result", UsageRecordHelper.getPermissionResultJson(base))
                    .send();

            rlLoading.setVisibility(View.GONE);
            rlSuccess.setVisibility(View.VISIBLE);
            tvFix.setText(getString(R.string.use_now));

        } else {
            boolean isExecute = false;
            for (ASBase asBase : base) {
                if (null != requestPermission) {
                    if (!asBase.isAllow && requestPermission.getName().equals(asBase.permission.getName())) {
                        permission = asBase.permission;
                        isExecute = true;
                        autoFixAction.permissionAction(asBase.permission);
                        break;
                    }
                    continue;
                }
                if (!asBase.isAllow && asBase.executeNumber == 0) {
                    permission = asBase.permission;
                    asBase.executeNumber++;
                    isExecute = true;
                    autoFixAction.permissionAction(asBase.permission);
                    break;
                }
            }
            if (!isExecute) {
                openSuccessExhibition(isAllDone, false);
            }
        }
    }

    // 一键修复完成
    private void openSuccessExhibition(boolean isAllDone, boolean isFirst) {
        if (ivGear.getAnimation() != null) {
            ivGear.clearAnimation();
        }
        if (ivLoading.getAnimation() != null) {
            ivLoading.clearAnimation();
        }
        if (isAllDone) {

        }
        for (ASBase asBase : base) {
            asBase.executeNumber = 0;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setService(ServiceEvent event) {
        mService = event.getAccessibilityServiceMonitor();
        mService.setAccessibilityEvent(this);
        startAnimation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setIsOpen(PathEvent event) {
        if (event.getIsOpen()) {
            for (ASBase asBase : base) {
                if (event.getPermission() == asBase.permission) {
                    asBase.isAllow = true;
                    UsageRecordHelper.recordItemData(new UsageBuider().setUsageType(UsageRecordType.TYPE_CUSTOM.getValue()).setPage(PAGE), event.getIsAuto(), asBase);
                }
            }
            risksNumber = base.size();
            mAdapter.notifyDataSetChanged();
        }
        if (event.getIsBack()) {
            onResume();
        }
    }

    private int getAllowCount() {
        int allowCount = 0;
        for (ASBase sASBase : base) {
            if (sASBase.isAllow) {
                allowCount++;
            }
        }
        return allowCount;
    }

    private Permission permission;

    private void startAnimation() {
        rlNormal.setVisibility(View.GONE);
        rlLoading.setVisibility(View.VISIBLE);

        Animation animation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(3000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        ivGear.startAnimation(animation);

        Animation animation2 = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation2.setDuration(2000);
        animation2.setRepeatCount(Animation.INFINITE);
        animation2.setInterpolator(new LinearInterpolator());
        ivLoading.startAnimation(animation2);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    startActivity(new Intent(WKPermissionAutoFixActivity.this, ASMGuideActivity.class));
                    break;
                default:
            }

        }
    };


    @SuppressLint("NewApi")
    @Override
    public void onEvent(AccessibilityEvent event) {
        if (mService != null && autoFixAction != null) {
            autoFixAction.permissionHandlerEvent(mService, event);
            try {
                if (mService.getRootInActiveWindow() != null) {
                    mService.getRootInActiveWindow().recycle();
                } else {
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isOpen = false;
        Permission requestPermission = null;
        // 判断是否有无障碍权限
        if (AccessibilitUtil.isAccessibilitySettingsOn(this, AccessibilityServiceMonitor.class.getCanonicalName())) {
            return;
        }
        if (requestCode == ACCESSIBILITY_SETTINGS) {
            // 显示单独权限开启按钮
            isOneRepair = false;
            requestPermission = Permission.SUSPENDEDTOAST;
        } else if (requestCode == Permission.SUSPENDEDTOAST.getRequestCode()) {
            // 判断是否具备悬浮窗权限
            isOpen = AccessibilitUtil.isOpenPermission(this, Permission.SUSPENDEDTOAST);
            if (isOpen) {
                PermissionProvider.save(this, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 1));
            }
            requestPermission = Permission.SELFSTARTING;
        } else if (requestCode == Permission.SELFSTARTING.getRequestCode()) {
            // 判断是否具备自启动权限
            isOpen = AccessibilitUtil.isOpenPermission(this, Permission.SELFSTARTING);
            if (isOpen) {
                PermissionProvider.save(this, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true, 1));
            }
            requestPermission = Permission.NOTIFICATIONBAR;
        } else if (requestCode == Permission.NOTIFICATIONBAR.getRequestCode()) {
            // 判断是否具备通知管理权限
            isOpen = AccessibilitUtil.isOpenPermission(this, Permission.NOTIFICATIONBAR);
            if (isOpen) {
                PermissionProvider.save(this, PROVIDER_NOTIFICATIONBAR, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true, 1));
            }

            requestPermission = Permission.PACKAGEUSAGESTATS;
        } else if (requestCode == Permission.PACKAGEUSAGESTATS.getRequestCode()) {
            // 判断是否具备查看应用使用情况权限
            isOpen = AccessibilitUtil.isOpenPermission(this, Permission.PACKAGEUSAGESTATS);
            if (isOpen) {
                PermissionProvider.save(this, PACKAGE_USAGE_STATS, true);
                EventBus.getDefault().post(new PathEvent(Permission.PACKAGEUSAGESTATS, true, 1));
            }

        }
        risksNumber = mAdapter.getItemCount();
        mAdapter.notifyDataSetChanged();
        if (!isOpen && requestCode != ACCESSIBILITY_SETTINGS) {
            return;
        }
        // 修改权限执行次数
//        permission = asBase.permission;
//        asBase.executeNumber++;

        Toast.makeText(this, "请开启，" + requestPermission.getName() + "权限！", Toast.LENGTH_LONG).show();
        request(requestPermission);
    }

}
