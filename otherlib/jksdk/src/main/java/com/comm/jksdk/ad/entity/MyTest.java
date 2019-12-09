package com.comm.jksdk.ad.entity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.DownloadStatusController;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTDislikeDialogAbstract;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.ad.entity
 * @ClassName: MyTest
 * @Description: java类作用描述
 * @Author: fanhailong
 * @CreateDate: 2019/12/2 13:22
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/2 13:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MyTest implements Serializable, TTFeedAd {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void setVideoAdListener(VideoAdListener videoAdListener) {

    }

    @Override
    public double getVideoDuration() {
        return 0;
    }

    @Nullable
    @Override
    public TTImage getVideoCoverImage() {
        return null;
    }

    @Override
    public Bitmap getAdLogo() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getButtonText() {
        return null;
    }

    @Override
    public int getAppScore() {
        return 0;
    }

    @Override
    public int getAppCommentNum() {
        return 0;
    }

    @Override
    public int getAppSize() {
        return 0;
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public TTImage getIcon() {
        return null;
    }

    @Override
    public List<TTImage> getImageList() {
        return null;
    }

    @Override
    public int getInteractionType() {
        return 0;
    }

    @Override
    public int getImageMode() {
        return 0;
    }

    @Override
    public List<FilterWord> getFilterWords() {
        return null;
    }

    @Override
    public TTAdDislike getDislikeDialog(Activity activity) {
        return null;
    }

    @Override
    public TTAdDislike getDislikeDialog(TTDislikeDialogAbstract ttDislikeDialogAbstract) {
        return null;
    }

    @Override
    public DownloadStatusController getDownloadStatusController() {
        return null;
    }

    @Override
    public void registerViewForInteraction(@NonNull ViewGroup viewGroup, @NonNull View view, AdInteractionListener adInteractionListener) {

    }

    @Override
    public void registerViewForInteraction(@NonNull ViewGroup viewGroup, @NonNull List<View> list, @Nullable List<View> list1, AdInteractionListener adInteractionListener) {

    }

    @Override
    public void registerViewForInteraction(@NonNull ViewGroup viewGroup, @NonNull List<View> list, @Nullable List<View> list1, @Nullable View view, AdInteractionListener adInteractionListener) {

    }

    @Override
    public void setDownloadListener(TTAppDownloadListener ttAppDownloadListener) {

    }

    @Override
    public void setActivityForDownloadApp(@NonNull Activity activity) {

    }

    @Override
    public View getAdView() {
        return null;
    }

    @Override
    public Map<String, Object> getMediaExtraInfo() {
        return null;
    }
}
