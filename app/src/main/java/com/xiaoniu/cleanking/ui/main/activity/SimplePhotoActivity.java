package com.xiaoniu.cleanking.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import java.util.ArrayList;

/**
 * Created by lang.chen on 2019/7/6
 */
public class SimplePhotoActivity extends TakePhotoActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LubanOptions option = new LubanOptions.Builder().setMaxHeight(1080).setMaxWidth(1080).setMaxSize(50 *1024).create();
        CompressConfig  config=CompressConfig.ofLuban(option);
        TakePhoto takePhoto=getTakePhoto();
        takePhoto.onEnableCompress(config,true);
        takePhoto.onPickFromGallery();
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }


    @Override
    public void takeCancel() {
        super.takeCancel();
        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        finish();
    }


    private void showImg(ArrayList<TImage> images) {
        if(null!=images && images.size()>0){
            Intent intent = new Intent();
            intent.putExtra("img_path", images.get(0).getOriginalPath());
            setResult(0X11,intent);
        }
        finish();
    }
}
