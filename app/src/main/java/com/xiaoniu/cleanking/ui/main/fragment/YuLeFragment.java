package com.xiaoniu.cleanking.ui.main.fragment;

import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.just.agentweb.AgentWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.databinding.FragmentYuleBinding;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

/**
 * Created by zhaoyingtao
 * Date: 2020/6/30
 * Describe:娱乐刮刮乐页面==加载h5页面
 */
public class YuLeFragment extends SimpleFragment {
    FragmentYuleBinding mBinding;
    public static YuLeFragment getInstance() {
        return new YuLeFragment();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_yule;
    }

    AgentWeb mAgentWeb;

    @Override
    protected void initView() {
        mBinding = DataBindingUtil.bind(getView());
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mBinding.webFragment, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("https://www.baidu.com/");
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //放在initView中无效
            StatusBarCompat.translucentStatusBarForImage(getActivity(), true, true);
        }
    }
    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }
}
