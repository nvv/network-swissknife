package com.nsak.android.ui.widget;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.nsak.android.R;

/**
 * @author Vlad Namashko
 */
public class PopupActionWindow extends PopupWindow {

    protected View mAnchor;
    protected View mLayout;

    public PopupActionWindow(View anchor, View layout) {
        super(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mLayout = layout;
        setContentView(mLayout);
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.PopupWindowAnimation);
        mAnchor = anchor;
    }

    public void show() {
        showAsDropDown(mAnchor, 0, -(mAnchor.getHeight() + (int) (10 * mAnchor.getResources().getDisplayMetrics().density)));
    }


    public void handleActionClick(int id, final Runnable action) {
        mLayout.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                action.run();
            }
        });
    }
}
