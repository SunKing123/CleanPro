package com.hellogeek.permission.statusbarcompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


/**
 * After Lollipop use system method.
 * Created by qiu on 8/27/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class StatusBarCompatLollipop {

    /**
     * return statusBar's Height in pixels
     */
    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

    /**
     * set StatusBarColor
     * <p>
     * 1. set Flags to call setStatusBarColor
     * 2. call setSystemUiVisibility to clear translucentStatusBar's Flag.
     * 3. set FitsSystemWindows to false
     */
    static void setStatusBarColor(Activity activity, int statusColor, boolean isNight) {
        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusColor);
        if (isNight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    /**
     * 设置 StatusBar渐变色背景
     * <p>
     * 1. set Flags to call setStatusBarColor
     * 2. call setSystemUiVisibility to clear translucentStatusBar's Flag.
     * 3. set FitsSystemWindows to false
     */
    static void setStatusBarGradientColor(Activity activity, int drawableResource, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView instanceof StatusBarView) {
            mChildView.setBackgroundResource(drawableResource);
        } else {
            mChildView.setPadding(0, getStatusBarHeight(activity), 0, 0);
            mChildView = new StatusBarView(activity);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
            mChildView.setLayoutParams(params);
            mChildView.setBackgroundResource(drawableResource);
            mContentView.addView(mChildView);
        }
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    /**
     * translucentStatusBar(full-screen)
     * <p>
     * 1. set Flags to full-screen
     * 2. set FitsSystemWindows to false
     *
     * @param hideStatusBarBackground hide statusBar's shadow
     */
    static void translucentStatusBarForImage(Activity activity, boolean hideStatusBarBackground, boolean isNight) {
        Window window = activity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (isNight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mContentView.getChildAt(mContentView.getChildCount() - 1) instanceof StatusBarView) {
            mContentView.removeView(mContentView.getChildAt(mContentView.getChildCount() - 1));
            mChildView.setPadding(0, 0, 0, 0);
            if (mContentView.getChildAt(0) != null) {
                mContentView.getChildAt(0).setPadding(0, 0, 0, 0);
            }
        }
//        if (mChildView != null) {
//            ViewCompat.setFitsSystemWindows(mChildView, false);
//            ViewCompat.requestApplyInsets(mChildView);
//        }
    }

    /**
     * translucentStatusBar(full-screen)
     * <p>
     * 1. set Flags to full-screen
     * 2. set FitsSystemWindows to false
     *
     * @param hideStatusBarBackground hide statusBar's shadow
     */
    static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mContentView.getChildAt(mContentView.getChildCount() - 1) instanceof StatusBarView)
            mContentView.removeView(mContentView.getChildAt(mContentView.getChildCount() - 1));
        else
            mChildView.setPadding(mChildView.getPaddingLeft(), mChildView.getPaddingTop() + getStatusBarHeight(activity), mChildView.getPaddingRight(), mChildView.getPaddingBottom());//添加statusbar高度
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    /**
     * @param activity
     * @param hideStatusBarBackground statusbar是否背景透明
     * @param bgView                  需要沉浸的view
     */
    static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground, View bgView, boolean isNight) {
        Window window = activity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (isNight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        if (mContentView.getChildAt(mContentView.getChildCount() - 1) instanceof StatusBarView)
            mContentView.removeView(mContentView.getChildAt(mContentView.getChildCount() - 1));
        else
            bgView.setPadding(bgView.getPaddingLeft(), bgView.getPaddingTop() + getStatusBarHeight(activity), bgView.getPaddingRight(), bgView.getPaddingBottom());//添加statusbar高度
        if (bgView != null) {
            ViewCompat.setFitsSystemWindows(bgView, false);
            ViewCompat.requestApplyInsets(bgView);
        }
    }

    /**
     * translucentStatusBar for fragment
     *
     * @param fragment
     * @param hideStatusBarBackground
     */
    static void translucentStatusBarForFragment(Fragment fragment, boolean hideStatusBarBackground) {

        Window window = fragment.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup contentView = fragment.getActivity().findViewById(android.R.id.content);
        if (contentView != null) {
            View rootView;
            rootView = contentView.getChildAt(contentView.getChildCount() - 1);
            if (rootView instanceof StatusBarView) {
                contentView.removeView(rootView);
                contentView.getChildAt(0).setPadding(0, 0, 0, 0);
            }
        }

        View view = fragment.getView();
        if (view != null) {
//            if (view instanceof ScrollView) {
//                ViewGroup viewGroup = (ViewGroup) view;
//                View childAt = viewGroup.getChildAt(0);
//                if (childAt != null) {
//                    childAt.setPadding(childAt.getPaddingLeft(), childAt.getPaddingTop() + getStatusBarHeight(fragment.getContext()), childAt.getPaddingRight(), childAt.getPaddingBottom());
//                }
//            } else {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(fragment.getContext()), view.getPaddingRight(), view.getPaddingBottom());
//            }
        }
    }

    /**
     * @param fragment
     * @param hideStatusBarBackground
     * @param bgView
     */
    static void translucentStatusBarForFragment(Fragment fragment, boolean hideStatusBarBackground, View bgView) {

        Window window = fragment.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup contentView = fragment.getActivity().findViewById(android.R.id.content);
        if (contentView != null) {
            View rootView;
            rootView = contentView.getChildAt(contentView.getChildCount() - 1);
            if (rootView instanceof StatusBarView) {
                contentView.removeView(rootView);
                contentView.getChildAt(0).setPadding(0, 0, 0, 0);
            }
        }

//        View rootView = fragment.getView();
//        if (rootView != null) {
//            if (rootView instanceof ScrollView) {
//                ViewGroup viewGroup = (ViewGroup) rootView;
//                View childAt = viewGroup.getChildAt(0);
//                if (childAt != null) {
//                    childAt.setPadding(childAt.getPaddingLeft(), childAt.getPaddingTop() + getStatusBarHeight(fragment.getContext()), childAt.getPaddingRight(), childAt.getPaddingBottom());
//                }
//            } else {
//                rootView.setPadding(rootView.getPaddingLeft(), rootView.getPaddingTop() + getStatusBarHeight(fragment.getContext()), rootView.getPaddingRight(), rootView.getPaddingBottom());
//            }
//        }
        bgView.setPadding(bgView.getPaddingLeft(), bgView.getPaddingTop() + getStatusBarHeight(fragment.getContext()), bgView.getPaddingRight(), bgView.getPaddingBottom());
    }

//    /**
//     * compat for CollapsingToolbarLayout
//     * <p>
//     * 1. change to full-screen mode(like translucentStatusBar).
//     * 2. cancel CollapsingToolbarLayout's WindowInsets, let it layout as normal(now setStatusBarScrimColor is useless).
//     * 3. set View's FitsSystemWindow to false.
//     * 4. add Toolbar's height, let it layout from top, then add paddingTop to layout normal.
//     * 5. change statusBarColor by AppBarLayout's offset.
//     * 6. add Listener to change statusBarColor
//     */
//    static void setStatusBarColorForCollapsingToolbar(Activity activity, final AppBarLayout appBarLayout, final CollapsingToolbarLayout collapsingToolbarLayout,
//                                                      Toolbar toolbar, final int statusColor) {
//        final Window window = activity.getWindow();
//
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//
//        ViewCompat.setOnApplyWindowInsetsListener(collapsingToolbarLayout, new OnApplyWindowInsetsListener() {
//            @Override
//            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
//                return insets;
//            }
//        });
//
//        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
//        View mChildView = mContentView.getChildAt(0);
//        if (mChildView != null) {
//            ViewCompat.setFitsSystemWindows(mChildView, false);
//            ViewCompat.requestApplyInsets(mChildView);
//        }
//
//        ((View) appBarLayout.getParent()).setFitsSystemWindows(false);
//        appBarLayout.setFitsSystemWindows(false);
//
//        toolbar.setFitsSystemWindows(false);
//        if (toolbar.getTag() == null) {
//            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
//            int statusBarHeight = getStatusBarHeight(activity);
//            lp.height += statusBarHeight;
//            toolbar.setLayoutParams(lp);
//            toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() + statusBarHeight, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
//            toolbar.setTag(true);
//        }
//
//        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
//        if (behavior != null && behavior instanceof AppBarLayout.Behavior) {
//            int verticalOffset = ((AppBarLayout.Behavior) behavior).getTopAndBottomOffset();
//            if (Math.abs(verticalOffset) > appBarLayout.getHeight() - collapsingToolbarLayout.getScrimVisibleHeightTrigger()) {
//                window.setStatusBarColor(statusColor);
//            } else {
//                window.setStatusBarColor(Color.TRANSPARENT);
//            }
//        } else {
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }
//
//        collapsingToolbarLayout.setFitsSystemWindows(false);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (Math.abs(verticalOffset) > appBarLayout.getHeight() - collapsingToolbarLayout.getScrimVisibleHeightTrigger()) {
//                    if (window.getStatusBarColor() == Color.TRANSPARENT) {
//                        ValueAnimator animator = ValueAnimator.ofArgb(Color.TRANSPARENT, statusColor)
//                                .setDuration(collapsingToolbarLayout.getScrimAnimationDuration());
//                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                window.setStatusBarColor((Integer) valueAnimator.getAnimatedValue());
//                            }
//                        });
//                        animator.start();
//                    }
//                } else {
//                    if (window.getStatusBarColor() == statusColor) {
//                        ValueAnimator animator = ValueAnimator.ofArgb(statusColor, Color.TRANSPARENT)
//                                .setDuration(collapsingToolbarLayout.getScrimAnimationDuration());
//                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                window.setStatusBarColor((Integer) valueAnimator.getAnimatedValue());
//                            }
//                        });
//                        animator.start();
//                    }
//                }
//            }
//        });
//        collapsingToolbarLayout.getChildAt(0).setFitsSystemWindows(false);
//        collapsingToolbarLayout.setStatusBarScrimColor(statusColor);
//    }
}

