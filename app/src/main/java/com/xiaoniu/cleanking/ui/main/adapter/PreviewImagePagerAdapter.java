package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.ui.main.bean.Image;

import java.util.List;

/**
 * Created by SonnyJack on 2018/7/18 18:43.
 */
public class PreviewImagePagerAdapter extends PagerAdapter implements OnOutsidePhotoTapListener, OnPhotoTapListener {
    private List<FileEntity> mDataList;

    public void deleteData(List<Image> tempList) {
        mDataList.removeAll(tempList);
        notifyDataSetChanged();
    }

    public PreviewImagePagerAdapter(Context context, List<FileEntity> dataList) {
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return null == mDataList ? 0 : mDataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setMinimumScale(0.5f);
        if (mDataList.size() > 0) {
            FileEntity image = mDataList.get(position);
            if (null != image) {
                loadArtworkImage(photoView, image.getPath(), 0, 0);

            }
            photoView.setOnOutsidePhotoTapListener(this);
            photoView.setOnPhotoTapListener(this);
            container.addView(photoView);
        }
        return photoView;
    }

    @Override
    public void onOutsidePhotoTap(ImageView imageView) {
        finishActivity();
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        finishActivity();
    }

    private void finishActivity() {
    }


    /**
     * 加载原图
     */
    public  void loadArtworkImage(ImageView image, String url, int errorHolder, int placeHolder) {
        if (image == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        final Context context = image.getContext();
        if (isValidContextForGlide(context) == false) {
            return;
        }
        Glide.with(image)
                .load(url)
                .into(image);

    }
    public  boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
