package com.nsak.android.ui.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsak.android.App;
import com.nsak.android.R;


public class LabeledEditTextLayout extends LinearLayout {

    private TextView mLabelView;
    private TextView mErrorView;
    private EditText mTextView;
    private int mNextFocusDownId = -1;

    public LabeledEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.labeled_layout, this);

        mLabelView = (TextView) findViewById(R.id.text_label);
        mTextView = (EditText) findViewById(R.id.text_content);
        mErrorView = (TextView) findViewById(R.id.text_error);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabeledEditTextLayout);
        mLabelView.setText(a.getString(R.styleable.LabeledEditTextLayout_label));
        mTextView.setHint(a.getString(R.styleable.LabeledEditTextLayout_hint));
        int inputType = a.getInteger(R.styleable.LabeledEditTextLayout_inputType, InputType.TYPE_TEXT_VARIATION_NORMAL);
        if (inputType == 0x00200001) { // ip address
            mTextView.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        } else {
            mTextView.setInputType(inputType);
        }
        String id = a.getString(R.styleable.LabeledEditTextLayout_nextFocus);
        if (id != null) {
            mNextFocusDownId = getResourceId("id", id);
        }

        String text = a.getString(R.styleable.LabeledEditTextLayout_text);
        if (text != null) {
            mTextView.setText(text);
        }

        final int activeColor = a.getColor(R.styleable.LabeledEditTextLayout_activeLabelColor, getResources().getColor(R.color.white));
        mTextView.setTextAppearance(getContext(), a.getResourceId(R.styleable.LabeledEditTextLayout_inputStyle, R.style.LabeledTextEdit));
        mTextView.getBackground().setColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP);
        a.recycle();

        mTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int mainColor = getResources().getColor(R.color.grey_2);

                ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                        hasFocus ? mainColor : activeColor,
                        hasFocus ? activeColor : mainColor);

                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        mLabelView.setTextColor((Integer) animator.getAnimatedValue());
                    }

                });
                valueAnimator.setDuration(500);
                valueAnimator.start();
            }
        });
    }

    public EditText getTextView() {
        return mTextView;
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setError(String message) {
        mErrorView.setText(message);
    }

    @Override
    public int getNextFocusDownId() {
        return mNextFocusDownId;
    }

    public static int getResourceId(String type, String identifier) {
        return App.sInstance.getResources().getIdentifier(identifier, type,
                App.sInstance.getPackageName());
    }
}
