package com.xiaoniu.cleanking.ui.main.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.common.utils.DeviceUtils;


/**
 * 构建配置参见
 * {@link PromptDialog.Builder}
 */
public class PromptDialog implements DialogInterface {

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    private Dialog mDialog;

    private PromptDialog(final Builder builder) {
        mDialog = new Dialog(builder.context, builder.themeStyle == 0 ? R.style.BaseDialogTheme : builder.themeStyle);
        mDialog.setContentView(R.layout.dialog_prompt_layout);
        mDialog.setCancelable(builder.isCancelable);
        mDialog.setCanceledOnTouchOutside(builder.isCancelTouchOutSide);

        if (builder != null) {
            builder.titleTextColor = R.color.color_262626;
        }

        WindowManager.LayoutParams attributes = mDialog.getWindow().getAttributes();
        if (0 < builder.width) {
            attributes.width = builder.width;
        } else {
            attributes.width = (int) (DeviceUtils.getScreenWidth() * 0.85f);
        }

        if (0 < builder.height)
            attributes.height = builder.height;

        if (0 != builder.animStyle) {
            mDialog.getWindow().setWindowAnimations(builder.animStyle);
        }

        if (builder.dimAmountAlpha >= 0) {
            mDialog.getWindow().setDimAmount(builder.dimAmountAlpha);
        }

        if (Gravity.NO_GRAVITY != builder.gravity) {
            mDialog.getWindow().setGravity(builder.gravity);
        } else {
            mDialog.getWindow().setGravity(Gravity.CENTER);
        }

        LinearLayout titleLy = (LinearLayout) mDialog.findViewById(R.id.title_ly);
        titleLy.setVisibility(builder.isNoTitle ? View.GONE : View.VISIBLE);
        TextView promptTitle = (TextView) mDialog.findViewById(R.id.tv_prompt_title);
        promptTitle.setText(builder.title);
        if (0 != builder.titleTextColor) {
            promptTitle.setTextColor(builder.getContext()
                    .getResources()
                    .getColorStateList(builder.titleTextColor));
        }

        if (0 != builder.titleTextSize) {
            promptTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext()
                    .getResources()
                    .getDimensionPixelSize(builder.titleTextSize));
        }

        TextView promptMessage = (TextView) mDialog.findViewById(R.id.tv_prompt_message);
        ViewHelper.safelySetText(promptMessage, builder.message, true);
        try {
            promptMessage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    try {
                        promptMessage.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (promptMessage.getLineCount() == 2) {
                            promptMessage.setGravity(Gravity.LEFT);
                        }
                    } catch (Exception e) {
                    }
                    return false;
                }
            });
        } catch (Exception e) {
        }
//        promptMessage.setText(builder.message);
        if (0 != builder.messageTextColor) {
            promptMessage.setTextColor(builder.getContext()
                    .getResources()
                    .getColorStateList(builder.messageTextColor));
        }

        if (0 != builder.messageTextSize) {
            promptMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext()
                    .getResources()
                    .getDimensionPixelSize(builder.messageTextSize));
        }

        if (Gravity.NO_GRAVITY != builder.messageGravity) {
            promptMessage.setGravity(builder.messageGravity);
        } else {
            promptMessage.setGravity(Gravity.CENTER);
        }

        if (0 != builder.messagePaddingBottom && 0 != builder.messagePaddingLeft && 0 != builder.messagePaddingRight && 0 != builder.messagePaddingTop) {
            promptMessage.setPadding((int) builder.getContext().getResources().getDimension(builder.messagePaddingLeft),
                    (int) builder.getContext().getResources().getDimension(builder.messagePaddingTop),
                    (int) builder.getContext().getResources().getDimension(builder.messagePaddingRight),
                    (int) builder.getContext().getResources().getDimension(builder.messagePaddingBottom));
        }

        TextView positiveBtn = (TextView) mDialog.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(builder.positiveBtnText)) {
            positiveBtn.setVisibility(View.VISIBLE);
            positiveBtn.setText(builder.positiveBtnText);
            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.mPositiveBtnListener != null) {
                        builder.mPositiveBtnListener.onClick(mDialog, BUTTON_POSITIVE);
                    }
                    if (builder.isAutoDismiss) {
                        mDialog.dismiss();
                    }
                }
            });

            if (0 != builder.positiveTextSize) {
                positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext()
                        .getResources()
                        .getDimensionPixelSize(builder.positiveTextSize));
            }
            if (0 != builder.positiveTextColor) {
                positiveBtn.setTextColor(builder.getContext()
                        .getResources()
                        .getColorStateList(builder.positiveTextColor));
            }
            if (0 != builder.positiveBackground) {
                positiveBtn.setBackgroundResource(builder.positiveBackground);
            }
        }

        TextView neutralBtn = (TextView) mDialog.findViewById(R.id.btn_neutral);
        if (!TextUtils.isEmpty(builder.neutralBtnText)) {
            neutralBtn.setVisibility(View.VISIBLE);
            neutralBtn.setText(builder.neutralBtnText);
            neutralBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.mNeutralBtnListener != null) {
                        builder.mNeutralBtnListener.onClick(mDialog, BUTTON_NEUTRAL);
                    }
                    if (builder.isAutoDismiss) {
                        mDialog.dismiss();
                    }
                }
            });

            if (0 != builder.neutralTextSize) {
                neutralBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext()
                        .getResources()
                        .getDimensionPixelSize(builder.neutralTextSize));
            }
            if (0 != builder.neutralTextColor) {
                neutralBtn.setTextColor(builder.getContext()
                        .getResources()
                        .getColorStateList(builder.neutralTextColor));
            }
            if (0 != builder.neutralBackground) {
                neutralBtn.setBackgroundResource(builder.neutralBackground);
            }
        }

        TextView negativeBtn = (TextView) mDialog.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(builder.negativeBtnText)) {
            negativeBtn.setVisibility(View.VISIBLE);
            negativeBtn.setText(builder.negativeBtnText);
            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.mNegativeBtnListener != null) {
                        builder.mNegativeBtnListener.onClick(mDialog, BUTTON_NEGATIVE);
                    }
                    if (builder.isAutoDismiss) {
                        mDialog.dismiss();
                    }
                }
            });
            if (0 != builder.negativeTextSize) {
                negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext()
                        .getResources()
                        .getDimensionPixelSize(builder.negativeTextSize));
            }
            if (0 != builder.negativeTextColor) {
                negativeBtn.setTextColor(builder.getContext()
                        .getResources()
                        .getColorStateList(builder.negativeTextColor));
            }
            if (0 != builder.negativeBackground) {
                negativeBtn.setBackgroundResource(builder.negativeBackground);
            }
        }

        if (null != builder.mOnCancelListener) {
            mDialog.setOnCancelListener(builder.mOnCancelListener);
        }

        if (null != builder.mOnDismissListener) {
            mDialog.setOnDismissListener(builder.mOnDismissListener);
        }

        if (null != builder.mOnShowListener) {
            mDialog.setOnShowListener(builder.mOnShowListener);
        }

        LinearLayout actionBtnContainer = (LinearLayout) mDialog.findViewById(R.id.ly_action_btn_container);
        actionBtnContainer.setOrientation(builder.orientation);
        if (0 != builder.separatorDrawable) {
            actionBtnContainer.setDividerDrawable(builder.getContext()
                    .getResources()
                    .getDrawable(builder.separatorDrawable));
            actionBtnContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }

        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.picker_view_scale_anim);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (DeviceUtils.getScreenWidth() * 0.773);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    public void cancel() {
        getDialog().cancel();
    }

    @Override
    public void dismiss() {
        getDialog().dismiss();
    }

    public boolean isShowing() {
        boolean isShowing = false;
        if (getDialog() != null) {
            isShowing = getDialog().isShowing();
        }
        return isShowing;
    }

    public void show() {
        getDialog().show();
    }

    public void hide() {
        getDialog().hide();
    }

    public static final class Builder {

        private final Context context;

        private boolean isCancelable;

        private boolean isAutoDismiss;

        private boolean isNoTitle;

        private boolean isCancelTouchOutSide;

        /**
         * 背景透明度
         */
        private float dimAmountAlpha = -1.0f;

        @ColorRes
        private int titleTextColor;

        @DimenRes
        private int titleTextSize;

        @ColorRes
        private int messageTextColor;

        @DimenRes
        private int messageTextSize;

        @DimenRes
        private int messagePaddingLeft;

        @DimenRes
        private int messagePaddingRight;

        @DimenRes
        private int messagePaddingTop;

        @DimenRes
        private int messagePaddingBottom;

        @DimenRes
        private int positiveTextSize;

        @ColorRes
        private int positiveTextColor;

        @DrawableRes
        private int positiveBackground;

        @DimenRes
        private int negativeTextSize;

        @ColorRes
        private int negativeTextColor;

        @DrawableRes
        private int negativeBackground;

        @DimenRes
        private int neutralTextSize;

        @ColorRes
        private int neutralTextColor;

        @DrawableRes
        private int neutralBackground;

        private int themeStyle;

        private int animStyle;

        private int width;// px 仅支持具体大小, 可更具屏幕百分比进行配置, 默认屏幕 80%

        private int height;// px 仅支持具体大小, 可更具屏幕百分比进行配置, 默认自适应

        private int gravity;

        private int messageGravity;

        private int orientation;// 底部按钮排列股则

        private int separatorDrawable;// 按钮分割装饰物

        private CharSequence title;

        private CharSequence message;

        private CharSequence positiveBtnText;

        private CharSequence neutralBtnText;

        private CharSequence negativeBtnText;

        private OnClickListener mPositiveBtnListener;

        private OnClickListener mNegativeBtnListener;

        private OnClickListener mNeutralBtnListener;

        private OnCancelListener mOnCancelListener;

        private OnDismissListener mOnDismissListener;

        private OnShowListener mOnShowListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public Builder setTheme(@StyleRes int themeStyle) {
            this.themeStyle = themeStyle;
            return this;
        }

        public Builder setWindowAnimations(@StyleRes int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        /**
         * 设置窗口背景透明度
         */
        public Builder setDimAmountAlpha(float alpha) {
            dimAmountAlpha = alpha;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        /**
         * @see Gravity
         */
        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(CharSequence title, @ColorRes int colorRes) {
            this.title = title;
            this.titleTextColor = colorRes;
            return this;
        }

        public Builder setTitle(CharSequence title, @ColorRes int colorRes, @DimenRes int dimenRes) {
            this.title = title;
            this.titleTextColor = colorRes;
            this.titleTextSize = dimenRes;
            return this;
        }

        public Builder setTitle(@StringRes int resId) {
            this.title = getContext().getText(resId);
            return this;
        }

        public Builder setTitle(@StringRes int titleRes, @ColorRes int colorRes) {
            this.title = getContext().getText(titleRes);
            this.titleTextColor = colorRes;
            return this;
        }

        public Builder setTitle(@StringRes int titleRes, @ColorRes int colorRes, @DimenRes int dimenRes) {
            this.title = getContext().getString(titleRes);
            this.titleTextColor = colorRes;
            this.titleTextSize = dimenRes;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(CharSequence message, @ColorRes int colorRes) {
            this.message = message;
            this.messageTextColor = colorRes;
            return this;
        }

        public Builder setMessage(CharSequence message, @ColorRes int colorRes, @DimenRes int dimenRes) {
            this.message = message;
            this.messageTextColor = colorRes;
            this.messageTextSize = dimenRes;
            return this;
        }

        public Builder setMessage(@StringRes int textId) {
            this.message = getContext().getText(textId);
            return this;
        }

        public Builder setMessage(@StringRes int textId, @ColorRes int colorRes, @DimenRes int dimenRes) {
            this.message = context.getResources().getText(textId);
            this.messageTextColor = colorRes;
            this.messageTextSize = dimenRes;
            return this;
        }

        public Builder setMessageGravity(int gravity) {
            this.messageGravity = gravity;
            return this;
        }

        public Builder setMessagePadding(@DimenRes int paddingLeft, @DimenRes int paddingTop, @DimenRes int paddingRight, @DimenRes int paddingBottom) {
            this.messagePaddingLeft = paddingLeft;
            this.messagePaddingTop = paddingTop;
            this.messagePaddingRight = paddingRight;
            this.messagePaddingBottom = paddingBottom;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            this.positiveBtnText = text;
            this.mPositiveBtnListener = listener;
            return this;
        }

        public Builder setPositiveButton(@StringRes int textId, final OnClickListener listener) {
            this.positiveBtnText = getContext().getText(textId);
            this.mPositiveBtnListener = listener;
            return this;
        }

        public Builder setPositiveBtnStyle(@DimenRes int textSize, @ColorRes int textColor, @DrawableRes int background) {
            this.positiveTextSize = textSize;
            this.positiveTextColor = textColor;
            this.positiveBackground = background;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            this.negativeBtnText = text;
            this.mNegativeBtnListener = listener;
            return this;
        }

        public Builder setNegativeBtnStyle(@DimenRes int textSize, @ColorRes int textColor, @DrawableRes int background) {
            this.negativeTextSize = textSize;
            this.negativeTextColor = textColor;
            this.negativeBackground = background;
            return this;
        }

        public Builder setNegativeButton(@StringRes int textId, final OnClickListener listener) {
            this.negativeBtnText = getContext().getText(textId);
            this.mNegativeBtnListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            this.neutralBtnText = text;
            this.mNeutralBtnListener = listener;
            return this;
        }

        public Builder setNeutralButton(@StringRes int textId, final OnClickListener listener) {
            this.neutralBtnText = getContext().getText(textId);
            this.mNeutralBtnListener = listener;
            return this;
        }

        public Builder setNeutralBtnStyle(@DimenRes int textSize, @ColorRes int textColor, @DrawableRes int background) {
            this.neutralTextSize = textSize;
            this.neutralTextColor = textColor;
            this.neutralBackground = background;
            return this;
        }

        /**
         * 按钮排列方向
         */
        public Builder setArrangeOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setSeparatorDrawable(@DrawableRes int drawable) {
            this.separatorDrawable = drawable;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnShowListener(OnShowListener onShowListener) {
            mOnShowListener = onShowListener;
            return this;
        }

        public Builder setAutoDismiss(boolean autoDismiss) {
            isAutoDismiss = autoDismiss;
            return this;
        }

        public Builder setNoTitle(boolean isNoTitle) {
            this.isNoTitle = isNoTitle;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.isCancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancel) {
            this.isCancelTouchOutSide = cancel;
            return this;
        }

        public PromptDialog create() {
            return new PromptDialog(this);
        }

        public PromptDialog show() {
            PromptDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

}
