package com.xiaoniu.cleanking.widget.rewrite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;

import androidx.appcompat.widget.AppCompatEditText;

import com.xiaoniu.cleanking.R;


/**
 * 有内容后贷清楚按钮的edittext
 */

public class ClearAbleEditText extends AppCompatEditText implements OnTouchListener,
        OnFocusChangeListener, TextWatcher {

    public interface Listener {
        void didClearText();
    }

    OnTextListener textListener;
    public void setOnTextListener(OnTextListener textListener){
        this.textListener=textListener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Drawable xD;
    private Listener listener;
    OnTextListener onTextListener;

    public ClearAbleEditText(Context context) {
        super(context);
        init();
    }

    public ClearAbleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearAbleEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTextListener(OnTextListener onTextListener){
        this.onTextListener = onTextListener;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.l = l;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener f) {
        this.f = f;
    }

    private OnTouchListener l;
    private OnFocusChangeListener f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            boolean tappedX = event.getX() > (getWidth() - getPaddingRight() - xD
                    .getIntrinsicWidth());
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setText("");
                    if (listener != null) {
                        listener.didClearText();
                    }
                }
                return true;
            }
        }
        return l != null && l.onTouch(v, event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(!TextUtils.isEmpty(getText()));
        } else {
            setClearIconVisible(false);
        }
        if (f != null) {
            f.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore,
                              int lengthAfter) {
        if (isFocused()) {
            setClearIconVisible(!TextUtils.isEmpty(text));
        }
        if(textListener!=null){
            textListener.onTextAfter();
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    private void init() {
        xD = getCompoundDrawables()[2];
        if (xD == null) {
            xD = getResources().getDrawable(R.mipmap.ic_delete);
        }
        xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable x = visible ? xD : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], x, getCompoundDrawables()[3]);
    }

    @Override
    public void afterTextChanged(Editable arg0) {
            if(onTextListener!=null){
                onTextListener.onTextAfter();
            }

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {

    }
}