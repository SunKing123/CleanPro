package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.base.BaseActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * 暂时废弃===登录默认微信登录===LoginWeiChatActivity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public ArrayList<SnsPlatform> platforms = new ArrayList<>();
    private SHARE_MEDIA[] list = {SHARE_MEDIA.QQ,SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN};
    private ImageView QQ,WeiXin,Sina;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initVariable(Intent intent) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initPlatforms();
        QQ =  findViewById(R.id.iv_qq_login);
        WeiXin =  findViewById(R.id.iv_weixin_login);
        Sina =  findViewById(R.id.iv_sina_login);
        QQ.setOnClickListener(this);
        WeiXin.setOnClickListener(this);
        Sina.setOnClickListener(this);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {

    }

    private void initPlatforms(){
        platforms.clear();
        for (SHARE_MEDIA e :list) {
            if (!e.toString().equals(SHARE_MEDIA.GENERIC.toString())){
                platforms.add(e.toSnsPlatform());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_qq_login:
                UmEnter(0);
                break;
            case R.id.iv_weixin_login:
                UmEnter(2);
                break;
            case R.id.iv_sina_login:
                UmEnter(1);
                break;
        }
    }

    private void UmEnter(int pos) {
        final boolean isauth = UMShareAPI.get(this).isAuthorize(this,platforms.get(pos).mPlatform);
        if (isauth){
            UMShareAPI.get(this).deleteOauth(this,platforms.get(pos).mPlatform,authListener);
        }else {
            UMShareAPI.get(this).doOauthVerify(this,platforms.get(pos).mPlatform,authListener);
        }
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(LoginActivity.this,"成功了",Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(LoginActivity.this,"失败："+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(LoginActivity.this,"取消了",Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
