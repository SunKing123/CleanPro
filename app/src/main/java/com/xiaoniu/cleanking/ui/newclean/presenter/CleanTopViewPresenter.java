package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.strategy.ExternalInterface;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.mvp.BasePresenter;
import com.xiaoniu.cleanking.mvp.IBaseView;
import com.xiaoniu.cleanking.ui.main.activity.AgentWebViewActivity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.newclean.contact.CleanMainTopViewContact;
import com.xiaoniu.cleanking.ui.newclean.model.CleanMainTopViewModel;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.GlideUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList.DataBean.SwitchActiveLineDTOList;
import com.xiaoniu.common.utils.StatisticsUtils;

import java.util.List;

/**
 * Created by xinxiaolong on 2020/5/27.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanTopViewPresenter implements CleanMainTopViewContact.Presenter {

    CleanMainTopViewContact.View view;
    CleanMainTopViewContact.Model model;
    Activity context;
    //是否处于吸顶状态
    boolean mIsSuckTop;
    //是否点击过topView
    boolean isClickedTop;
    //当前的广告数据
    SwitchActiveLineDTOList currentAdvModel;
    int activeAdvIndex = -1;

    List<SwitchActiveLineDTOList> activeAdvList;

    public CleanTopViewPresenter(Activity context, CleanMainTopViewContact.View view, CleanMainTopViewContact.Model model) {
        this.context = context;
        this.view = view;
        this.model = model;
    }

    @Override
    public void onCreate() {
        //拉取互动广告数据
        model.getInteractionSwitch(new Common4Subscriber<InteractionSwitchList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InteractionSwitchList interactionSwitchList) {
                if (interactionSwitchList != null && interactionSwitchList.getData().size() > 0) {
                    activeAdvList = interactionSwitchList.getData().get(0).getSwitchActiveLineDTOList();
                    currentAdvModel = getNextAdvModel();
                    redrawView();
                }

            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    //取出下一个广告数据
    private SwitchActiveLineDTOList getNextAdvModel() {
        if (activeAdvList == null || activeAdvList.size() == 0) {
            return null;
        }
        activeAdvIndex = activeAdvIndex >= activeAdvList.size() - 1 ? 0 : activeAdvIndex+1;
        return activeAdvList.get(activeAdvIndex);
    }


    @Override
    public void onResume() {
        redrawView();
    }

    @Override
    public void onScroll(boolean isSuckTop) {
        if (mIsSuckTop != isSuckTop) {
            mIsSuckTop = isSuckTop;
            if (mIsSuckTop) {
                view.setVisibleFloatingView(View.GONE);
            }else {
                int visible=(!isClickedTop&&!isPermissionAllOpen())?View.VISIBLE:View.GONE;
                view.setVisibleFloatingView(visible);

            }
        }
    }

    //判断并重绘view
    private void redrawView() {
        if (mIsSuckTop) {
            view.setVisibleFloatingView(View.GONE);
            return;
        }
        if (isPermissionAllOpen()) {
            showAdvView();
            view.setVisibleFloatingView(View.GONE);
        } else {
            showPermissionView();
        }
    }

    //显示互动广告view
    private void showAdvView() {
        if (currentAdvModel == null) {
            view.setVisibleImage(View.GONE);
            return;
        }
        view.setVisibleImage(View.VISIBLE);
        view.loadAdvImage(currentAdvModel.getImgUrl());
    }

    //显示权限提醒view
    private void showPermissionView() {
        view.setVisibleImage(View.VISIBLE);
        view.loadPermissionTipImage();
        view.setVisibleFloatingView(isClickedTop ? View.GONE : View.VISIBLE);
    }

    //头部view点击
    @Override
    public void onTopViewClick() {
        isClickedTop = true;
        if (isPermissionAllOpen()) {
            onAdvClick();
        } else {
            onPermissionTipClick();
        }
    }

    //互动广告点击
    private void onAdvClick() {
        if (currentAdvModel == null) {
            return;
        }
        context.startActivity(new Intent(context, AgentWebViewActivity.class)
                .putExtra(ExtraConstant.WEB_URL, currentAdvModel.getLinkUrl()));

        currentAdvModel = getNextAdvModel();

        StatisticsUtils.trackClick("Interaction_ad_click", "用户在首页点击互动式广告按钮", "cold_splash_page", "home_page");
    }

    //权限提醒点击
    private void onPermissionTipClick() {
        PermissionIntegrate.getPermission().startWK(context);
        StatisticsUtils.trackClick("permission_icon_click", "首页权限图标点击", "cold_splash_page", "home_page");
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        model = null;
    }

    @Override
    public void attach(IBaseView view) {

    }

    @Override
    public void detach() {

    }

    //权限是否全部打开
    public boolean isPermissionAllOpen() {
        // 权限是否全部开启
        if (PermissionIntegrate.getPermission().getIsNecessary()) {
            return ExternalInterface.getInstance(context).isOpenNecessaryPermission(context);
        }
        return ExternalInterface.getInstance(context).isOpenAllPermission(context);
    }

}
